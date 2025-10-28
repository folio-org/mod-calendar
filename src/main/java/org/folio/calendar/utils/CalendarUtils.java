package org.folio.calendar.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.SingleDayOpeningCollectionDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningRangeDTO;
import org.folio.calendar.domain.dto.SurroundingOpeningsDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.types.Weekday;

@UtilityClass
public class CalendarUtils {

  /** Number of dates from a queried date to search at a time, to improve performance */
  private static final List<Integer> LAZY_SPLIT_AMOUNTS = List.of(
    7,
    30,
    180,
    365,
    Integer.MAX_VALUE
  );

  /**
   * Take a provided calendar and split it into a series of dates
   *
   * @param calendar       the calendar to split into dates
   * @param dates          the map of dates to fill (in-place)
   * @param boundStartDate the first date to include; if the calendar starts before this date, the
   *                       extra will be ignored
   * @param boundEndDate   the last date to include; if the calendar ends after this date, the extra
   *                       will be ignored
   */
  public static void splitCalendarIntoDates(
    Calendar calendar,
    Map<LocalDate, SingleDayOpeningDTO> dates,
    LocalDate boundStartDate,
    LocalDate boundEndDate
  ) {
    // limit start and end dates to the calendar range
    LocalDate startDate = DateUtils.max(boundStartDate, calendar.getStartDate());
    LocalDate endDate = DateUtils.min(boundEndDate, calendar.getEndDate());

    // add all exceptional dates to the map; other dates will be computed one at a time
    addExceptionsToDateMap(calendar, dates, startDate, endDate);

    Map<Weekday, List<TemporalRange<LocalTime, NormalOpening>>> normalOpenings = NormalOpeningUtils.initializeWeekdayMapOfRanges();
    calendar
      .getNormalHours()
      .forEach(normalOpening ->
        NormalOpeningUtils.fillWeekdayMapWithTimeTuples(normalOpenings, normalOpening)
      );

    // iterate through all other dates, checking normal openings
    addNormalOpeningsToDateMap(dates, startDate, endDate, normalOpenings);
  }

  /**
   * Take a provided calendar and split its exceptions into a series of dates
   *
   * @param calendar  the calendar with exceptions to split
   * @param dates     the map of dates to fill (in-place)
   * @param startDate the first date to include; if the calendar starts before this date, the extra
   *                  will be ignored
   * @param endDate   the last date to include; if the calendar ends after this date, the extra will
   *                  be ignored
   */
  protected static void addExceptionsToDateMap(
    Calendar calendar,
    Map<LocalDate, SingleDayOpeningDTO> dates,
    LocalDate startDate,
    LocalDate endDate
  ) {
    calendar
      .getExceptions()
      .forEach((ExceptionRange exception) ->
        DateUtils
          .getDateRangeStream(
            DateUtils.max(startDate, exception.getStartDate()),
            DateUtils.min(endDate, exception.getEndDate())
          )
          .forEach((LocalDate date) -> {
            List<SingleDayOpeningRangeDTO> openings = ExceptionRangeUtils
              .toTemporalRanges(exception.getOpenings())
              .map(dateTimeRange ->
                TemporalUtils.getLocalTimeSliceOfDateTimeRange(dateTimeRange, date)
              )
              .filter(Optional::isPresent)
              .map(Optional::get)
              .map(timeRange ->
                SingleDayOpeningRangeDTO
                  .builder()
                  .startTime(timeRange.getStart())
                  .endTime(timeRange.getEnd())
                  .build()
              )
              .toList();

            dates.put(
              date,
              SingleDayOpeningDTO
                .builder()
                .date(date)
                .exceptionName(exception.getName())
                .exceptional(true)
                .openings(openings)
                .open(!openings.isEmpty())
                .allDay(
                  openings.isEmpty() ||
                  (openings.size() == 1 && TimeUtils.isAllDay(openings.get(0)))
                )
                .build()
            );
          })
      );
  }

  /**
   * Take a provided map of weekdays/time ranges representing normal openings and split them into
   * concrete dates for each opening
   *
   * @param dates          the map of dates to fill (in-place)
   * @param startDate      the first date to include; if the calendar starts before this date, the
   *                       extra will be ignored
   * @param endDate        the last date to include; if the calendar ends after this date, the extra
   *                       will be ignored
   * @param normalOpenings a map of weekdays to their time ranges
   */
  protected static void addNormalOpeningsToDateMap(
    Map<LocalDate, SingleDayOpeningDTO> dates,
    LocalDate startDate,
    LocalDate endDate,
    Map<Weekday, List<TemporalRange<LocalTime, NormalOpening>>> normalOpenings
  ) {
    DateUtils
      .getDateRangeStream(startDate, endDate)
      .forEach((LocalDate date) -> {
        List<TemporalRange<LocalTime, NormalOpening>> openings = normalOpenings.get(
          Weekday.from(date)
        );
        if (!openings.isEmpty()) {
          // put if absent will only add this date if it was not added above (from an exception)
          dates.putIfAbsent(
            date,
            SingleDayOpeningDTO
              .builder()
              .date(date)
              .exceptional(false)
              .openings(
                openings
                  .stream()
                  .map(timeRange ->
                    SingleDayOpeningRangeDTO
                      .builder()
                      .startTime(timeRange.getStart())
                      .endTime(timeRange.getEnd())
                      .build()
                  )
                  .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                  .toList()
              )
              .open(true)
              .allDay(TimeUtils.isAllDay(openings))
              .build()
          );
        }
      });
  }

  /**
   * Take a map of dates and fill in any empty slots with {@link SingleDayOpeningDTO
   * SingleDayOpeningDTOs} representing regular closures
   *
   * @param dates     the map to fill (in-place)
   * @param startDate the first date to consider
   * @param endDate   the last date to consider
   */
  public static void fillClosedDates(
    Map<LocalDate, SingleDayOpeningDTO> dates,
    LocalDate startDate,
    LocalDate endDate
  ) {
    DateUtils
      .getDateRangeStream(startDate, endDate)
      .forEach((LocalDate date) ->
        dates.putIfAbsent(
          date,
          SingleDayOpeningDTO
            .builder()
            .date(date)
            .exceptional(false)
            .openings(new ArrayList<>())
            .open(false)
            .allDay(true)
            .build()
        )
      );
  }

  /**
   * Convert a map of dates to {@link SingleDayOpeningDTO SingleDayOpeningDTOs} into a
   * {@link SingleDayOpeningCollectionDTO SingleDayOpeningCollectionDTO} with pagination
   *
   * @param dates  the map of dates to convert
   * @param offset the number of items to skip, for pagination
   * @param limit  the maximum number of items to include, for pagination
   * @return a DTO collection ready for API return
   */
  public static SingleDayOpeningCollectionDTO openingMapToCollection(
    Map<LocalDate, SingleDayOpeningDTO> dates,
    int offset,
    int limit
  ) {
    return SingleDayOpeningCollectionDTO
      .builder()
      .dates(dates.entrySet().stream().map(Map.Entry::getValue).skip(offset).limit(limit).toList())
      .totalRecords(dates.size())
      .build();
  }

  /**
   * Extracts one calendar at a time out of the provided calendars, splitting it into dates, until at
   * least one opening is found. The opening will not be returned directly, but placed into the
   * provided {@code map}.
   *
   * @param map              the map to store/retrieve opening information in/from
   * @param calendarIterator an iterator of calendars to examine
   * @param focalDate        the date to search around
   * @param direction        the direction to search in
   * @param fallbackDate     the date to use if no opening is found
   */
  public static SingleDayOpeningDTO getFirstOpeningFromCalendarList(
    SortedMap<LocalDate, SingleDayOpeningDTO> map,
    Iterator<Calendar> calendarIterator,
    LocalDate focalDate,
    SearchDirection direction,
    LocalDate fallbackDate
  ) {
    Predicate<LocalDate> isInRange;
    Function<Calendar, LocalDate> dateHinter;
    Function<SortedMap<LocalDate, SingleDayOpeningDTO>, LocalDate> keyGetter;

    switch (direction) {
      case FORWARD -> {
        isInRange = openingDate -> openingDate.isAfter(focalDate);
        dateHinter = calendar -> DateUtils.max(focalDate, calendar.getStartDate());
        keyGetter = SortedMap::firstKey;
      }
      case BACKWARD -> {
        isInRange = openingDate -> openingDate.isBefore(focalDate);
        dateHinter = calendar -> DateUtils.min(focalDate, calendar.getEndDate());
        keyGetter = SortedMap::lastKey;
      }
      default -> throw new IllegalArgumentException("SearchDirection.BOTH is not supported here");
    }
    // find dates < date with openings while there are more calendars to consume
    // and a date has not been found yet
    while (calendarIterator.hasNext() && map.isEmpty()) {
      Calendar currentCalendar = calendarIterator.next();

      SortedMap<LocalDate, SingleDayOpeningDTO> additionalDates = lazySplitCalendarIntoDates(
        currentCalendar,
        dateHinter.apply(currentCalendar),
        direction,
        false
      );

      // remove exceptional closures for surrounding openings
      additionalDates
        .entrySet()
        .removeIf(entry -> !entry.getValue().isOpen() || !isInRange.test(entry.getValue().getDate())
        );
      map.putAll(additionalDates);
    }

    if (map.isEmpty()) {
      return SingleDayOpeningDTO
        .builder()
        .date(fallbackDate)
        .open(false)
        .allDay(true)
        .exceptional(false)
        .build();
    } else {
      return map.get(keyGetter.apply(map));
    }
  }

  /**
   * Take a provided calendar and split it into a series of dates, inserting it into the provided map
   * until the map contains at least one date before and after {@code focalDate}, or the calendar is
   * exhausted.
   *
   * @param calendar  the calendar to split into dates
   * @param focalDate the date to find adjacent openings for
   */
  public static SortedMap<LocalDate, SingleDayOpeningDTO> lazySplitCalendarIntoDates(
    Calendar calendar,
    LocalDate focalDate,
    SearchDirection direction,
    boolean includeClosures
  ) {
    SortedMap<LocalDate, SingleDayOpeningDTO> dates = new TreeMap<>();

    // insert focal date first, to make first/last key operations safe
    splitCalendarIntoDates(calendar, dates, focalDate, focalDate);
    dates.putIfAbsent(focalDate, null);

    // calculate previous
    if (direction == SearchDirection.BACKWARD || direction == SearchDirection.BOTH) {
      LocalDate oldestSearchDate = focalDate;
      for (int lookbackAmount : LAZY_SPLIT_AMOUNTS) {
        LocalDate newStartRange = DateUtils.max(
          focalDate.minusDays(lookbackAmount),
          calendar.getStartDate()
        );

        // already found one, or cannot go back any further
        if (dates.firstKey().isBefore(focalDate) || newStartRange.isEqual(oldestSearchDate)) {
          break;
        }

        splitCalendarIntoDates(calendar, dates, newStartRange, oldestSearchDate.minusDays(1));
        if (!includeClosures) {
          dates.headMap(focalDate).entrySet().removeIf(entry -> !entry.getValue().isOpen());
        } else if (
          // if we retain closures, we need more complex logic to ensure we don't break before
          // finding an opening, if available. if closures are filtered out (as above), the regular
          // `firstKey` check suffices.
          dates
            .headMap(oldestSearchDate)
            .values()
            .stream()
            .anyMatch(o -> Boolean.TRUE.equals(o.isOpen()))
        ) {
          break;
        }
        oldestSearchDate = newStartRange;
      }
    }

    // calculate next
    if (direction == SearchDirection.FORWARD || direction == SearchDirection.BOTH) {
      LocalDate newestSearchDate = focalDate;
      for (int lookaheadAmount : LAZY_SPLIT_AMOUNTS) {
        LocalDate newEndRange = DateUtils.min(
          focalDate.plusDays(lookaheadAmount),
          calendar.getEndDate()
        );

        // already found one, or cannot go forward any further
        if (dates.lastKey().isAfter(focalDate) || newEndRange.isEqual(newestSearchDate)) {
          break;
        }

        splitCalendarIntoDates(calendar, dates, newestSearchDate.plusDays(1), newEndRange);
        if (!includeClosures) {
          dates
            .tailMap(focalDate.plusDays(1))
            .entrySet()
            .removeIf(entry -> !entry.getValue().isOpen());
        } else if (
          // if we retain closures, we need more complex logic to ensure we don't break before
          // finding an opening, if available. if closures are filtered out (as above), the regular
          // `lastKey` check suffices.
          dates
            .tailMap(newestSearchDate.plusDays(1))
            .values()
            .stream()
            .anyMatch(o -> Boolean.TRUE.equals(o.isOpen()))
        ) {
          break;
        }
        newestSearchDate = newEndRange;
      }
    }

    if (
      dates.get(focalDate) == null ||
      (!includeClosures && dates.get(focalDate).getOpenings().isEmpty())
    ) {
      dates.remove(focalDate);
    }

    return dates;
  }

  /**
   * Query a list of calendars for openings surrounding a given date
   *
   * @param calendars the list of calendars to examine
   * @param date      the date to query
   * @return a {@link SurroundingOpeningsDTO SurroundingOpeningsDTO} representing the opening
   *         information
   */
  public static SurroundingOpeningsDTO getSurroundingOpenings(
    List<Calendar> calendars,
    LocalDate date
  ) {
    int dateIndex = Collections.binarySearch(
      calendars,
      Calendar.builder().startDate(date).endDate(date).build(),
      (Calendar a, Calendar b) -> {
        if (DateUtils.overlaps(a, b)) {
          return 0;
        }
        return a.getStartDate().compareTo(b.getStartDate());
      }
    );

    // assume closed
    SingleDayOpeningDTO current = SingleDayOpeningDTO
      .builder()
      .date(date)
      .open(false)
      .allDay(true)
      .exceptional(false)
      .build();

    SortedMap<LocalDate, SingleDayOpeningDTO> dates = new TreeMap<>();

    // the current date does fall inside a calendar
    if (dateIndex >= 0) {
      Calendar currentCalendar = calendars.get(dateIndex);
      dates.putAll(
        CalendarUtils.lazySplitCalendarIntoDates(currentCalendar, date, SearchDirection.BOTH, true)
      );

      // if the calendar is closed on the queried date, do not overwrite the closure above
      if (dates.containsKey(date)) {
        current = dates.get(date);
      }

      // remove exceptional closures for surrounding openings
      dates.entrySet().removeIf(entry -> !entry.getValue().isOpen());
    } else {
      // binary search returns next-greatest index by -(index) - 1
      // this undoes this, yielding the first place to look for > date
      dateIndex = -(dateIndex + 1);
    }

    SortedMap<LocalDate, SingleDayOpeningDTO> beforeDateMap = dates.headMap(date);
    SortedMap<LocalDate, SingleDayOpeningDTO> afterDateMap = dates.tailMap(date.plusDays(1));

    SingleDayOpeningDTO previous = CalendarUtils.getFirstOpeningFromCalendarList(
      beforeDateMap,
      // array deque needed to provide reversed sorting
      // subList from [0, dateIndex)
      new ArrayDeque<>(calendars.subList(0, Math.min(dateIndex + 1, calendars.size())))
        .descendingIterator(),
      date,
      SearchDirection.BACKWARD,
      date.minusDays(1)
    );
    SingleDayOpeningDTO next = CalendarUtils.getFirstOpeningFromCalendarList(
      afterDateMap,
      // subList from [dateIndex, end)
      calendars.listIterator(dateIndex),
      date,
      SearchDirection.FORWARD,
      date.plusDays(1)
    );

    return SurroundingOpeningsDTO
      .builder()
      .opening(previous)
      .opening(current)
      .opening(next)
      .build();
  }

  public enum SearchDirection {
    BOTH,
    BACKWARD,
    FORWARD,
  }
}
