package org.folio.calendar.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayInfoDTO;
import org.folio.calendar.domain.dto.OpeningDayRelativeDTO;
import org.folio.calendar.domain.dto.OpeningDayRelativeWeekdaysDTO;
import org.folio.calendar.domain.dto.OpeningHourRangeDTO;
import org.folio.calendar.domain.dto.PeriodDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.Calendar.CalendarBuilder;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.ExceptionRange.ExceptionRangeBuilder;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.NormalOpening.NormalOpeningBuilder;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;

/**
 * Utilities for acting on {@link org.folio.calendar.domain.dto.Period} objects
 */
@UtilityClass
public class PeriodUtils {

  /**
   * Determine if a a list of OpeningDayRelativeDTO is intended for an exception or a calendar (distinct in legacy, although both use Periods)
   *
   * @param openings a list of {@link org.folio.calendar.domain.dto.OpeningDayRelativeDTO} objects
   * @return if the list refers to an exception or normal opening
   */
  public static boolean areOpeningsExceptional(Iterable<OpeningDayRelativeDTO> openings) {
    for (OpeningDayRelativeDTO opening : openings) {
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
   * @param openings a list of {@link org.folio.calendar.domain.dto.OpeningDayRelativeDTO} objects
   * @return a {@link java.util.List} of the corresponding {@code ExceptionRange}
   */
  public static List<ExceptionRange> convertOpeningDayRelativeDTOToExceptionRanges(
    LocalDate startDate,
    LocalDate endDate,
    List<OpeningDayRelativeDTO> openings
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

    OpeningDayInfoDTO opening = openings.get(0).getOpeningDay();

    List<OpeningHourRangeDTO> openingHourList = opening.getOpeningHour();
    if (openingHourList == null) {
      throw new IllegalArgumentException(
        "An opening was provided to convertOpeningDayRelativeDTOToExceptionRanges with no opening days"
      );
    }

    if (openingHourList.size() != 1) {
      throw new IllegalArgumentException(
        "Provided legacy exception information must have exactly one set of opening hours"
      );
    }

    OpeningHourRangeDTO openingHours = openingHourList.get(0);

    if (!Boolean.TRUE.equals(opening.isOpen())) {
      // no time information implies closure
      // therefore, we want no openings
      return Arrays.asList(builder.build());
    }

    // open all day
    if (Boolean.TRUE.equals(opening.isAllDay())) {
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
              .startTime(TimeUtils.fromTimeString(openingHours.getStartTime()))
              .endDate(date)
              .endTime(TimeUtils.fromTimeString(openingHours.getEndTime()))
              .build()
          );
      }
    }

    return Arrays.asList(builder.build());
  }

  /**
   * Convert period openings to normalized openings, consolidating as necessary
   *
   * @param openings a list of {@link org.folio.calendar.domain.dto.OpeningDayRelativeDTO} objects
   * @return a {@link java.util.List} of {@code NormalOpening}s
   */
  // allow multiple continue statements in for loop
  @SuppressWarnings("java:S135")
  public static List<NormalOpening> convertOpeningDayRelativeDTOToNormalOpening(
    Iterable<OpeningDayRelativeDTO> openings
  ) {
    List<NormalOpening> normalizedOpenings = new ArrayList<>();

    NormalOpeningBuilder builder = NormalOpening.builder();

    for (OpeningDayRelativeDTO opening : openings) {
      OpeningDayInfoDTO openingInfo = opening.getOpeningDay();

      OpeningDayRelativeWeekdaysDTO weekdays = opening.getWeekdays();

      if (weekdays == null) {
        throw new IllegalArgumentException(
          "Invalid opening passed to convertOpeningDayRelativeDTOToNormalOpening"
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

      for (OpeningHourRangeDTO hourRange : openingInfo.getOpeningHour()) {
        // legacy version preserves error states; we will discard them silently
        // hourRange.getStartTime() is after hourRange.getEndTime()
        if (hourRange.getStartTime().compareTo(hourRange.getEndTime()) > 0) {
          continue;
        }

        normalizedOpenings.add(
          builder
            .startTime(TimeUtils.fromTimeString(hourRange.getStartTime()))
            .endTime(TimeUtils.fromTimeString(hourRange.getEndTime()))
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
      if (NormalOpeningUtils.adjacent(former, latter)) {
        if (i == 0) {
          normalOpenings.set(normalOpenings.size() - 1, NormalOpeningUtils.merge(former, latter));
          normalOpenings.remove(0);
        } else {
          normalOpenings.set(i - 1, NormalOpeningUtils.merge(former, latter));
          normalOpenings.remove(i);
        }
      }
    }

    return normalOpenings;
  }

  public static Calendar toCalendar(PeriodDTO period) {
    // basic info
    CalendarBuilder calendarBuilder = Calendar
      .builder()
      .name(period.getName())
      .startDate(period.getStartDate().getValue())
      .endDate(period.getEndDate().getValue());

    // assign starting service point
    ServicePointCalendarAssignment servicePointAssignment = ServicePointCalendarAssignment
      .builder()
      .servicePointId(period.getServicePointId())
      .build();
    calendarBuilder = calendarBuilder.servicePoint(servicePointAssignment);

    // create hours
    if (areOpeningsExceptional(period.getOpeningDays())) {
      calendarBuilder.exceptions(
        convertOpeningDayRelativeDTOToExceptionRanges(
          period.getStartDate().getValue(),
          period.getEndDate().getValue(),
          period.getOpeningDays()
        )
      );
    } else {
      calendarBuilder.normalHours(
        convertOpeningDayRelativeDTOToNormalOpening(period.getOpeningDays())
      );
    }

    return calendarBuilder.build();
  }
}
