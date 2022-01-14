package org.folio.calendar.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayConcrete;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.domain.dto.OpeningDayRelativeWeekdays;
import org.folio.calendar.domain.dto.OpeningHourRange;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.dto.Weekday;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.ExceptionRange.ExceptionRangeBuilder;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.NormalOpening.NormalOpeningBuilder;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.folio.calendar.domain.types.LegacyPeriodDate;

/**
 * Utilities for acting on {@link org.folio.calendar.domain.dto.Period} objects
 */
@UtilityClass
public class PeriodUtils {

  /**
   * Determine if a a list of OpeningDayRelative is intended for an exception or a calendar (distinct in legacy, although both use Periods)
   *
   * @param openings a list of {@link org.folio.calendar.domain.dto.OpeningDayRelative} objects
   * @return if the list refers to an exception or normal opening
   */
  public static boolean areOpeningsExceptional(Iterable<OpeningDayRelative> openings) {
    for (OpeningDayRelative opening : openings) {
      if (opening.getWeekdays() == null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Convert period openings to exceptions.  Due to the simple nature of legacy exceptions,
   * this will result in exactly one {@link org.folio.calendar.domain.entity.ExceptionRange}.
   *
   * @param startDate the first day of the exception
   * @param endDate the last day of the exception
   * @param openings a list of {@link org.folio.calendar.domain.dto.OpeningDayRelative} objects
   * @return a {@link java.util.List} of the corresponding {@code ExceptionRange}
   */
  public static List<ExceptionRange> convertOpeningDayRelativeToExceptionRanges(
    LocalDate startDate,
    LocalDate endDate,
    List<OpeningDayRelative> openings
  ) {
    UUID exceptionId = UUID.randomUUID();

    ExceptionRangeBuilder builder = ExceptionRange
      .builder()
      .id(exceptionId)
      .startDate(startDate)
      .endDate(endDate);

    if (openings.size() != 1) {
      throw new IllegalArgumentException(
        "Provided legacy exception information must have exactly one opening"
      );
    }

    OpeningDayInfo opening = openings.get(0).getOpeningDay();

    List<OpeningHourRange> openingHourList = opening.getOpeningHour();
    if (openingHourList == null) {
      throw new IllegalArgumentException(
        "An opening was provided to convertOpeningDayRelativeToExceptionRanges with no opening days"
      );
    }

    if (openingHourList.size() != 1) {
      throw new IllegalArgumentException(
        "Provided legacy exception information must have exactly one set of opening hours"
      );
    }

    OpeningHourRange openingHours = openingHourList.get(0);

    if (!Boolean.TRUE.equals(opening.isOpen())) {
      // no time information implies closure
      builder =
        builder.opening(ExceptionHour.builder().startDate(startDate).endDate(endDate).build());
    } else if (Boolean.TRUE.equals(opening.isAllDay())) {
      builder =
        builder.opening(
          ExceptionHour
            .builder()
            .startDate(startDate)
            .startTime(TimeConstants.TIME_MIN)
            .endDate(endDate)
            .endTime(TimeConstants.TIME_MAX)
            .build()
        );
    } else {
      for (LocalDate date : DateUtils.getDateRange(startDate, endDate)) {
        builder =
          builder.opening(
            ExceptionHour
              .builder()
              .startDate(date)
              .startTime(DateUtils.fromTimeString(openingHours.getStartTime()))
              .endDate(date)
              .endTime(DateUtils.fromTimeString(openingHours.getEndTime()))
              .build()
          );
      }
    }

    return Arrays.asList(builder.build());
  }

  /**
   * Convert period openings to normalized openings, consolidating as necessary
   *
   * @param openings a list of {@link org.folio.calendar.domain.dto.OpeningDayRelative} objects
   * @return a {@link java.util.List} of {@code NormalOpening}s
   */
  // allow multiple continue statements in for loop
  @SuppressWarnings("java:S135")
  public static List<NormalOpening> convertOpeningDayRelativeToNormalOpening(
    Iterable<OpeningDayRelative> openings
  ) {
    List<NormalOpening> normalizedOpenings = new ArrayList<>();

    NormalOpeningBuilder builder = NormalOpening.builder();

    for (OpeningDayRelative opening : openings) {
      OpeningDayInfo openingInfo = opening.getOpeningDay();

      OpeningDayRelativeWeekdays weekdays = opening.getWeekdays();

      if (weekdays == null) {
        throw new IllegalArgumentException(
          "Invalid opening passed to convertOpeningDayRelativeToNormalOpening"
        );
      }

      builder = builder.startDay(weekdays.getDay()).endDay(weekdays.getDay());

      // we do not create NormalOpenings for closures
      if (Boolean.FALSE.equals(openingInfo.isOpen())) {
        continue;
      }

      if (Boolean.TRUE.equals(openingInfo.isAllDay())) {
        normalizedOpenings.add(
          builder.startTime(TimeConstants.TIME_MIN).endTime(TimeConstants.TIME_MAX).build()
        );
        continue;
      }

      for (OpeningHourRange hourRange : openingInfo.getOpeningHour()) {
        // legacy version preserves error states; we will discard them silently
        // hourRange.getStartTime() is after hourRange.getEndTime()
        if (hourRange.getStartTime().compareTo(hourRange.getEndTime()) > 0) {
          continue;
        }

        normalizedOpenings.add(
          builder
            .startTime(DateUtils.fromTimeString(hourRange.getStartTime()))
            .endTime(DateUtils.fromTimeString(hourRange.getEndTime()))
            .build()
        );
      }
    }

    return consolidateNormalOpenings(normalizedOpenings);
  }

  /**
   * Consolidate adjacent normal openings into one cohesive list.
   * The list MUST BE sorted.  Small overlaps will be handled, however, they must not span multiple weekdays.
   * @param normalOpenings list of sorted and canonicalized openings to consolidate
   * @return consolidated openings
   */
  private static List<NormalOpening> consolidateNormalOpenings(List<NormalOpening> normalOpenings) {
    // consolidation
    for (int i = normalOpenings.size() - 1; i >= 0; i--) {
      // no more to consolidate
      if (i == 0 && normalOpenings.size() == 1) {
        break;
      }

      NormalOpening former;
      NormalOpening latter;

      if (i == 0) {
        former = normalOpenings.get(normalOpenings.size() - 1);
        latter = normalOpenings.get(i);
      } else {
        former = normalOpenings.get(i - 1);
        latter = normalOpenings.get(i);
      }

      // merge if touching each other, including across days
      if (NormalOpening.adjacent(former, latter)) {
        if (i == 0) {
          normalOpenings.set(normalOpenings.size() - 1, NormalOpening.merge(former, latter));
          normalOpenings.remove(0);
        } else {
          normalOpenings.set(i - 1, NormalOpening.merge(former, latter));
          normalOpenings.remove(i);
        }
      }
    }

    return normalOpenings;
  }

  /**
   * Get a list of {@link org.folio.calendar.domain.dto.OpeningDayRelative}s from {@link org.folio.calendar.domain.entity.NormalOpening}s, for conversion to a legacy Period.
   * Days which are fully closed will not be included.
   *
   * @param normalHours the list of normal hours to convert
   * @return a list of OpeningDayRelative
   */
  public static List<OpeningDayRelative> getOpeningDayRelativeFromNormalOpenings(
    Iterable<NormalOpening> normalHours
  ) {
    // convert contiguous NormalOpenings into single-weekday groups
    Map<Weekday, List<OpeningHourRange>> openings = new EnumMap<>(Weekday.class);

    for (NormalOpening opening : normalHours) {
      Map<Weekday, OpeningHourRange> openingRanges = opening.splitIntoWeekdays();

      for (Map.Entry<Weekday, OpeningHourRange> range : openingRanges.entrySet()) {
        if (!openings.containsKey(range.getKey())) {
          openings.put(range.getKey(), new ArrayList<>());
        }
        openings.get(range.getKey()).add(range.getValue());
      }
    }

    // build NormalOpenings into OpeningDayRelative
    List<OpeningDayRelative> openingDays = new ArrayList<>();

    for (Map.Entry<Weekday, List<OpeningHourRange>> entry : openings.entrySet()) {
      OpeningDayInfo.OpeningDayInfoBuilder openingDayInfoBuilder = OpeningDayInfo
        .builder()
        .open(true)
        .allDay(false)
        .exceptional(false);

      if (entry.getValue().size() == 1 && entry.getValue().get(0).equals(TimeConstants.ALL_DAY)) {
        openingDayInfoBuilder = openingDayInfoBuilder.allDay(true).openingHour(entry.getValue());
      } else {
        List<OpeningHourRange> ranges = entry.getValue();
        // ensure that the opening hours are in ascending order
        ranges.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
        openingDayInfoBuilder = openingDayInfoBuilder.openingHour(ranges);
      }

      openingDays.add(
        OpeningDayRelative
          .builder()
          .weekdays(new OpeningDayRelativeWeekdays(entry.getKey()))
          .openingDay(openingDayInfoBuilder.build())
          .build()
      );
    }

    return openingDays;
  }

  /**
   * Get a list of {@link org.folio.calendar.domain.dto.OpeningDayRelative}s from {@link org.folio.calendar.domain.entity.NormalOpening}s, for conversion to a legacy Period.
   * There MUST be at least one exception in the set.
   *
   * @param exceptions the list of exceptions to convert (should only be one)
   * @return the equivalent {@code OpeningDayRelative}
   */
  public static OpeningDayRelative getOpeningDayRelativeFromExceptionRanges(
    Set<ExceptionRange> exceptions
  ) {
    // Set has no native get
    ExceptionRange exception = new ArrayList<ExceptionRange>(exceptions).get(0);
    ExceptionHour opening = new ArrayList<ExceptionHour>(exception.getOpenings()).get(0);

    OpeningDayInfo.OpeningDayInfoBuilder openingDayInfoBuilder = OpeningDayInfo
      .builder()
      .exceptional(true);

    if (opening.getStartTime() == null) {
      openingDayInfoBuilder =
        openingDayInfoBuilder
          .allDay(true)
          .open(false)
          .openingHour(
            Arrays.asList(
              OpeningHourRange
                .builder()
                .startTime(TimeConstants.TIME_MIN_STRING)
                .endTime(TimeConstants.TIME_MAX_STRING)
                .build()
            )
          );
    } else {
      openingDayInfoBuilder =
        openingDayInfoBuilder
          .allDay(false)
          .open(true)
          .openingHour(
            Arrays.asList(
              OpeningHourRange
                .builder()
                .startTime(DateUtils.toTimeString(opening.getStartTime()))
                .endTime(DateUtils.toTimeString(opening.getEndTime()))
                .build()
            )
          );
      if (
        TimeConstants.TIME_MIN.equals(opening.getStartTime()) &&
        TimeConstants.TIME_MAX.equals(opening.getEndTime())
      ) {
        openingDayInfoBuilder = openingDayInfoBuilder.allDay(true);
      }
    }

    return OpeningDayRelative.builder().openingDay(openingDayInfoBuilder.build()).build();
  }

  /**
   * Convert a modern {@link org.folio.calendar.domain.entity.Calendar Calendar} to a legacy {@link org.folio.calendar.domain.dto.Period Period} object
   *
   * @param calendar the {@link org.folio.calendar.domain.entity.Calendar Calendar} to convert
   * @return the equivalent {@link org.folio.calendar.domain.dto.Period Period}
   */
  public static Period toPeriod(Calendar calendar) {
    Period.PeriodBuilder builder = Period
      .builder()
      .id(calendar.getId())
      .name(calendar.getName())
      .startDate(LegacyPeriodDate.from(calendar.getStartDate()))
      .endDate(LegacyPeriodDate.from(calendar.getEndDate()));

    // passing ServicePointCalendarAssignment[] to toArray causes cast
    ServicePointCalendarAssignment[] servicePoints = calendar
      .getServicePoints()
      .toArray(new ServicePointCalendarAssignment[0]);

    if (servicePoints.length < 1) {
      throw new IllegalArgumentException(
        String.format(
          "Calendar %s must have at least one service point to be converted to a Period!",
          calendar.getId()
        )
      );
    } else if (servicePoints.length > 1) {
      throw new IllegalArgumentException(
        String.format(
          "Calendar %s must have only one service point to be converted to a Period!",
          calendar.getId()
        )
      );
    }
    builder = builder.servicePointId(servicePoints[0].getServicePointId());

    if (!calendar.getNormalHours().isEmpty() && !calendar.getExceptions().isEmpty()) {
      throw new IllegalArgumentException(
        String.format(
          "Calendar %s must have only exceptions or normal openings to be converted to a Period!",
          calendar.getId()
        )
      );
    }

    if (calendar.getNormalHours().isEmpty() && calendar.getExceptions().isEmpty()) {
      builder.openingDays(new ArrayList<>());
    } else if (!calendar.getNormalHours().isEmpty()) {
      builder =
        builder.openingDays(getOpeningDayRelativeFromNormalOpenings(calendar.getNormalHours()));
    } else {
      if (calendar.getExceptions().size() > 1) {
        throw new IllegalArgumentException(
          String.format(
            "Calendar %s must have only one exception to be converted to a Period!",
            calendar.getId()
          )
        );
      }

      builder =
        builder.openingDays(
          Arrays.asList(getOpeningDayRelativeFromExceptionRanges(calendar.getExceptions()))
        );
    }

    return builder.build();
  }

  /**
   * Merge a Map of OpeningDayInfo into another
   * @param current the map to be merged into
   * @param toAdd the map to be consumed
   */
  public static void mergeInto(
    Map<LocalDate, OpeningDayInfo> current,
    Map<LocalDate, OpeningDayInfo> toAdd
  ) {
    for (Entry<LocalDate, OpeningDayInfo> entry : toAdd.entrySet()) {
      if (!current.containsKey(entry.getKey())) {
        current.put(entry.getKey(), entry.getValue());
      } else {
        if (Boolean.FALSE.equals(entry.getValue().isOpen())) {
          continue;
        }

        OpeningDayInfo thisOpening = current.get(entry.getKey());

        if (Boolean.FALSE.equals(thisOpening.isOpen())) {
          current.put(entry.getKey(), entry.getValue());
        } else {
          List<OpeningHourRange> newOpenings = new ArrayList<>(thisOpening.getOpeningHour());
          newOpenings.addAll(entry.getValue().getOpeningHour());
          thisOpening.setOpeningHour(newOpenings);

          thisOpening.setAllDay(thisOpening.isAllDay() || entry.getValue().isAllDay());
        }
      }
    }
  }

  /**
   * Build a list of {@link OpeningDayConcrete OpeningDayConcrete} objects for the dates and openings provided, taking into account the boolean options
   * @param normalOpenings The list of normal openings
   * @param exceptions The list of any exceptional openings
   * @param firstDate The first date to include
   * @param lastDate The last date to include
   * @param includeClosedDays If closed days should be included
   * @param actualOpening If exceptions should override normal openings
   * @see org.folio.calendar.controller.CalendarController#getDateOpenings
   * @return a list of concrete opening days, guaranteed to be sorted by date
   */
  public List<OpeningDayConcrete> buildOpeningDayConcreteCollection(
    Map<LocalDate, OpeningDayInfo> normalOpenings,
    Map<LocalDate, OpeningDayInfo> exceptions,
    LocalDate firstDate,
    LocalDate lastDate,
    boolean includeClosedDays,
    boolean actualOpening
  ) {
    // no calendars were given
    if (firstDate == null || lastDate == null) {
      return new ArrayList<>();
    }

    List<OpeningDayConcrete> result = new ArrayList<>();

    for (LocalDate date : DateUtils.getDateRange(firstDate, lastDate)) {
      if (exceptions.containsKey(date)) {
        result.add(
          OpeningDayConcrete
            .builder()
            .date(LegacyPeriodDate.from(date))
            .openingDay(exceptions.get(date))
            .build()
        );
        if (Boolean.FALSE.equals(actualOpening) && normalOpenings.containsKey(date)) {
          result.add(
            OpeningDayConcrete
              .builder()
              .date(LegacyPeriodDate.from(date))
              .openingDay(normalOpenings.get(date))
              .build()
          );
        }
      } else if (normalOpenings.containsKey(date)) {
        result.add(
          OpeningDayConcrete
            .builder()
            .date(LegacyPeriodDate.from(date))
            .openingDay(normalOpenings.get(date))
            .build()
        );
      } else if (includeClosedDays) {
        result.add(
          OpeningDayConcrete
            .builder()
            .date(LegacyPeriodDate.from(date))
            .openingDay(TimeConstants.ALL_DAY_CLOSURE)
            .build()
        );
      }
    }

    return result;
  }
}
