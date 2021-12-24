package org.folio.calendar.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.domain.dto.OpeningDayRelativeWeekdays;
import org.folio.calendar.domain.dto.OpeningHourRange;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.dto.Weekday;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;

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
   * @param calendarId the ID of a calendar which the created {@link org.folio.calendar.domain.entity.NormalOpening} objects should be associated with
   * @return a {@link java.util.List} of the corresponding {@code ExceptionRange}
   */
  public static List<ExceptionRange> convertOpeningDayRelativeToExceptionRanges(
    LocalDate startDate,
    LocalDate endDate,
    List<OpeningDayRelative> openings,
    UUID calendarId
  ) {
    UUID exceptionId = UUID.randomUUID();

    ExceptionRange.ExceptionRangeBuilder builder = ExceptionRange
      .builder()
      .id(exceptionId)
      .calendarId(calendarId)
      .startDate(startDate)
      .endDate(endDate);

    if (openings.size() != 1) {
      throw new IllegalArgumentException(
        "Provided legacy exception information must have exactly one opening"
      );
    }

    OpeningDayInfo opening = openings.get(0).getOpeningDay();

    if (opening.getOpeningHour().size() != 1) {
      throw new IllegalArgumentException(
        "Provided legacy exception information must have exactly one set of opening hours"
      );
    }

    OpeningHourRange openingHours = opening.getOpeningHour().get(0);

    if (!Boolean.TRUE.equals(opening.isOpen())) {
      // no time information implies closure
      builder =
        builder.opening(
          ExceptionHour
            .builder()
            .exceptionId(exceptionId)
            .startDate(startDate)
            .endDate(endDate)
            .build()
        );
    } else if (Boolean.TRUE.equals(opening.isAllDay())) {
      builder =
        builder.opening(
          ExceptionHour
            .builder()
            .exceptionId(exceptionId)
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
              .exceptionId(exceptionId)
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
   * @param calendarId the ID of a calendar which the created {@link org.folio.calendar.domain.entity.NormalOpening} objects should be associated with
   * @return a {@link java.util.List} of {@code NormalOpening}s
   */
  // allow multiple continue statements in for loop
  @SuppressWarnings("java:S135")
  public static List<NormalOpening> convertOpeningDayRelativeToNormalOpening(
    Iterable<OpeningDayRelative> openings,
    UUID calendarId
  ) {
    List<NormalOpening> normalizedOpenings = new ArrayList<>();

    NormalOpening.NormalOpeningBuilder builder = NormalOpening.builder().calendarId(calendarId);

    for (OpeningDayRelative opening : openings) {
      OpeningDayInfo openingInfo = opening.getOpeningDay();

      builder =
        builder.startDay(opening.getWeekdays().getDay()).endDay(opening.getWeekdays().getDay());

      // we do not create NormalOpenings for closures
      if (!openingInfo.isOpen()) {
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
   * Get a list of {@link org.folio.calendar.domain.dto.OpeningDayRelative}s from {@link org.folio.calendar.domain.entity.NormalOpening}s, for conversion to a legacy Period
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
   * Get a list of {@link org.folio.calendar.domain.dto.OpeningDayRelative}s from {@link org.folio.calendar.domain.entity.NormalOpening}s, for conversion to a legacy Period
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
      .startDate(calendar.getStartDate())
      .endDate(calendar.getEndDate());

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

    if (!calendar.getNormalHours().isEmpty()) {
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
}
