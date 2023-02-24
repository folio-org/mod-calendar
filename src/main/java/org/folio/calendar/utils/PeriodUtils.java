package org.folio.calendar.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.Calendar.CalendarBuilder;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.ExceptionRange.ExceptionRangeBuilder;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.NormalOpening.NormalOpeningBuilder;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.folio.calendar.domain.legacy.dto.OpeningDayInfoDTO;
import org.folio.calendar.domain.legacy.dto.OpeningDayRelativeDTO;
import org.folio.calendar.domain.legacy.dto.OpeningDayRelativeWeekdaysDTO;
import org.folio.calendar.domain.legacy.dto.OpeningHourRangeDTO;
import org.folio.calendar.domain.legacy.dto.PeriodDTO;

/**
 * Utilities for acting on {@link org.folio.calendar.domain.dto.Period} objects.
 * Primarily kept for legacy reasons to facilitate the conversion of old RMB Period
 * objects to the new Calendar format
 */
@Log4j2
@UtilityClass
public class PeriodUtils {

  /**
   * Determine if a Period is intended for an exception or a calendar (distinct in legacy, but both use Periods)
   *
   * @param period the period to test
   * @return if the period refers to an exception or normal opening
   */
  public static boolean isExceptional(PeriodDTO period) {
    return period.getOpeningDays().stream().anyMatch(opening -> opening.getWeekdays() == null);
  }

  /**
   * Convert exception period openings to exceptions.  Due to the simple nature of legacy exceptions,
   * this will result in exactly one {@link org.folio.calendar.domain.entity.ExceptionRange}.
   *
   * @param period the period to convert
   * @return the corresponding {@code ExceptionRange}
   */
  public static ExceptionRange convertExceptionalPeriodToExceptionRanges(PeriodDTO period) {
    UUID exceptionId = UUID.randomUUID();

    ExceptionRangeBuilder builder = ExceptionRange
      .builder()
      .id(exceptionId)
      .name(period.getName())
      .startDate(period.getStartDate().getValue())
      .endDate(period.getEndDate().getValue());

    List<OpeningDayRelativeDTO> openings = period.getOpeningDays();

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
      return builder.build();
    }

    // open all day
    if (Boolean.TRUE.equals(opening.isAllDay())) {
      builder =
        builder.opening(
          ExceptionHour
            .builder()
            .startDate(period.getStartDate().getValue())
            .startTime(TimeConstants.TIME_MIN)
            .endDate(period.getEndDate().getValue())
            .endTime(TimeConstants.TIME_MAX)
            .build()
        );
    } else {
      for (LocalDate date : DateUtils.getDateRange(
        period.getStartDate().getValue(),
        period.getEndDate().getValue()
      )) {
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

    return builder.build();
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

  protected static Calendar convertOpeningPeriodToCalendar(PeriodDTO period) {
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
    calendarBuilder.normalHours(
      convertOpeningDayRelativeDTOToNormalOpening(period.getOpeningDays())
    );

    return calendarBuilder.build();
  }

  public static List<Calendar> toCalendars(List<PeriodDTO> periods) {
    log.debug("Converting periods: {}", periods);

    List<Calendar> calendars = periods
      .stream()
      .filter(period -> !isExceptional(period))
      .map(PeriodUtils::convertOpeningPeriodToCalendar)
      .collect(Collectors.toCollection(ArrayList::new));
    List<PeriodDTO> exceptions = periods.stream().filter(PeriodUtils::isExceptional).toList();

    log.info("Found {} calendars and {} exceptions", calendars.size(), exceptions.size());
    log.debug("Calendars: {}", calendars);
    log.debug("Exceptions: {}", exceptions);

    for (PeriodDTO exceptionPeriod : exceptions) {
      ExceptionRange exception = convertExceptionalPeriodToExceptionRanges(exceptionPeriod);
      log.info(
        "Searching for a calendar to host exception {} on service point {}",
        exception,
        exceptionPeriod.getServicePointId()
      );

      Optional<Calendar> parentCalendar = calendars
        .stream()
        // ensure we're only looking at calendars for this SP
        .filter((Calendar cal) ->
          cal
            .getServicePoints()
            .stream()
            .anyMatch(spa -> spa.getServicePointId().equals(exceptionPeriod.getServicePointId()))
        )
        // calendar must be within the date range of this exception
        .filter((Calendar cal) ->
          DateUtils.containsRange(
            exception.getStartDate(),
            exception.getEndDate(),
            cal.getStartDate(),
            cal.getEndDate()
          )
        )
        // calendar must not already have an exception with this date range
        .filter((Calendar cal) -> {
          if (
            cal
              .getExceptions()
              .stream()
              .anyMatch(otherException ->
                DateUtils.overlaps(
                  exception.getStartDate(),
                  exception.getEndDate(),
                  otherException.getStartDate(),
                  otherException.getEndDate()
                )
              )
          ) {
            // theoretically, this indicates an unrecoverable issue, since
            // there should only be able to be one exception per date per SP
            log.info(
              "Calendar {} seems like where this exception belongs, but another exception conflicted...",
              cal
            );
            return false;
          }
          return true;
        })
        .findAny();

      if (parentCalendar.isPresent()) {
        Set<ExceptionRange> newExceptionSet = new HashSet<>(parentCalendar.get().getExceptions());
        newExceptionSet.add(exception);

        parentCalendar.get().setExceptions(newExceptionSet);

        log.info("Exception stored in calendar {}", parentCalendar.get().getName());
      } else {
        log.error(
          "Could not find a parent for exception {}; creating orphaned exception",
          exceptionPeriod
        );
        // this goes at the back of the list so that, in the event
        // an orphaned exception cannot be added, it will be excluded
        // rather than the original calendar
        calendars.add(
          Calendar
            .builder()
            .name(String.format("Orphaned exception (%s)", exception.getName()))
            .servicePoint(
              ServicePointCalendarAssignment
                .builder()
                .servicePointId(exceptionPeriod.getServicePointId())
                .build()
            )
            .startDate(exception.getStartDate())
            .endDate(exception.getEndDate())
            .exception(exception)
            .build()
        );
      }
    }
    return calendars;
  }
}
