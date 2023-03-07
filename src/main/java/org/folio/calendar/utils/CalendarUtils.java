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

  /**
   * Take a provided calendar and split it into a series of dates
   * @param calendar the calendar to split into dates
   * @param dates the map of dates to fill (in-place)
   * @param boundStartDate the first date to include; if the calendar starts
   *        before this date, the extra will be ignored
   * @param boundEndDate the last date to include; if the calendar ends after
   *        this date, the extra will be ignored
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
   * @param calendar the calendar with exceptions to split
   * @param dates the map of dates to fill (in-place)
   * @param startDate the first date to include; if the calendar starts
   *        before this date, the extra will be ignored
   * @param endDate the last date to include; if the calendar ends after
   *        this date, the extra will be ignored
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
   * Take a provided map of weekdays/time ranges representing normal openings
   * and split them into concrete dates for each opening
   * @param dates the map of dates to fill (in-place)
   * @param startDate the first date to include; if the calendar starts
   *        before this date, the extra will be ignored
   * @param endDate the last date to include; if the calendar ends after
   *        this date, the extra will be ignored
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
   * Take a map of dates and fill in any empty slots with
   * {@link SingleDayOpeningDTO SingleDayOpeningDTOs} representing regular
   * closures
   * @param dates the map to fill (in-place)
   * @param startDate the first date to consider
   * @param endDate the last date to consider
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
   * Convert a map of dates to {@link SingleDayOpeningDTO SingleDayOpeningDTOs}
   * into a {@link SingleDayOpeningCollectionDTO SingleDayOpeningCollectionDTO}
   * with pagination
   * @param dates the map of dates to convert
   * @param offset the number of items to skip, for pagination
   * @param limit the maximum number of items to include, for pagination
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
   * Extracts one calendar at a time out of the provided calendars, splitting
   * it into dates, until at least one opening is found.  The opening will not
   * be returned directly, but placed into the provided {@code map}.
   *
   * @param map the map to store opening information in
   * @param calendarIterator an iterator of calendars to examine
   */
  protected static SingleDayOpeningDTO getFirstOpeningFromCalendarList(
    SortedMap<LocalDate, SingleDayOpeningDTO> map,
    Iterator<Calendar> calendarIterator,
    LocalDate fallbackDate,
    Function<SortedMap<LocalDate, SingleDayOpeningDTO>, LocalDate> keyGetter,
    Predicate<LocalDate> inRange
  ) {
    // find dates < date with openings while there are more calendars to consume
    // and a date has not been found yet
    while (calendarIterator.hasNext() && map.isEmpty()) {
      Calendar currentCalendar = calendarIterator.next();

      SortedMap<LocalDate, SingleDayOpeningDTO> additionalDates = new TreeMap<>();
      splitCalendarIntoDates(
        currentCalendar,
        additionalDates,
        currentCalendar.getStartDate(),
        currentCalendar.getEndDate()
      );

      // remove exceptional closures for surrounding openings
      additionalDates
        .entrySet()
        .removeIf(entry -> !entry.getValue().isOpen() || !inRange.test(entry.getValue().getDate()));
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
   * Query a list of calendars for openings surrounding a given date
   *
   * @param calendars the list of calendars to examine
   * @param date the date to query
   * @return an {@link SurroundingOpeningsDTO SurroundingOpeningsDTO}
   *         representing the opening information
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
      CalendarUtils.splitCalendarIntoDates(
        currentCalendar,
        dates,
        currentCalendar.getStartDate(),
        currentCalendar.getEndDate()
      );

      // if the calendar is closed on the queried date, do not overwrite the closure above
      if (dates.containsKey(date)) {
        current = dates.get(date);
      }

      // remove exceptional closures for surrounding openings
      dates.entrySet().removeIf(entry -> !entry.getValue().isOpen());
      // start at the calendar directly after for > date
      dateIndex++;
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
      new ArrayDeque<>(calendars.subList(0, dateIndex)).descendingIterator(),
      date.minusDays(1),
      SortedMap::lastKey,
      openingDate -> openingDate.isBefore(date)
    );
    SingleDayOpeningDTO next = CalendarUtils.getFirstOpeningFromCalendarList(
      afterDateMap,
      // subList from [dateIndex, end)
      calendars.listIterator(dateIndex),
      date.plusDays(1),
      SortedMap::firstKey,
      openingDate -> openingDate.isAfter(date)
    );

    return SurroundingOpeningsDTO
      .builder()
      .opening(previous)
      .opening(current)
      .opening(next)
      .build();
  }
}
