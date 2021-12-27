package org.folio.calendar.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.controller.CalendarController;
import org.folio.calendar.domain.dto.ErrorCode;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.dto.PeriodCollection;
import org.folio.calendar.domain.dto.Weekday;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.folio.calendar.exception.DataConflictException;
import org.folio.calendar.exception.DataNotFoundException;
import org.folio.calendar.exception.ExceptionParameters;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.repository.CalendarRepository;
import org.folio.calendar.repository.PeriodQueryFilter;
import org.folio.calendar.utils.DateUtils;
import org.folio.calendar.utils.PeriodCollectionUtils;
import org.folio.calendar.utils.PeriodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A Service class for calendar-related API calls
 */
@Service
@Log4j2
public class CalendarService {

  private final CalendarRepository calendarRepository;

  @Autowired
  CalendarService(CalendarRepository calendarRepository) {
    this.calendarRepository = calendarRepository;
  }

  /**
   * Get all the calendars for a certain service point with normal hours
   *
   * @param servicePointId the service point
   * @return a {@link java.util.List List} of {@link java.util.Calendar Calendar}s associated with the service point
   */
  public List<Calendar> getCalendarsWithNormalHoursForServicePoint(UUID servicePointId) {
    return this.calendarRepository.findByServicePointId(servicePointId)
      .stream()
      .filter(calendar -> !calendar.getNormalHours().isEmpty())
      .collect(Collectors.toList());
  }

  /**
   * Get all the calendars for a certain service point with exceptions
   *
   * @param servicePointId the service point
   * @return a {@link java.util.List List} of {@link java.util.Calendar Calendar}s associated with the service point
   */
  public List<Calendar> getCalendarsWithExceptionsForServicePoint(UUID servicePointId) {
    return this.calendarRepository.findByServicePointId(servicePointId)
      .stream()
      .filter(calendar -> !calendar.getExceptions().isEmpty())
      .collect(Collectors.toList());
  }

  @Transactional
  public void replaceCalendar(Calendar old, Calendar replacement) {
    this.deleteCalendar(old);
    this.insertCalendar(replacement);
  }

  @Transactional
  public void replaceCalendar(Calendar old, Period replacement, UUID servicePointId) {
    log.info("Deleting old");
    this.deleteCalendar(old);
    log.info("Creating new");
    this.insertCalendar(
        Calendar
          .builder()
          .startDate(LocalDate.of(2021, Month.JULY, 4))
          .endDate(LocalDate.of(2021, Month.SEPTEMBER, 22))
          .build()
          .withId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
          // .withServicePoints(
          //   Set.of(
          //     new ServicePointCalendarAssignment(
          //       UUID.fromString("00000000-0000-0000-0000-000000000000"),
          //       UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
          //     )
          //   )
          // )
          .withName("screams")
          .withNormalHours(
            Set.of(
              NormalOpening
                .builder()
                .startDay(Weekday.SUNDAY)
                .startTime(LocalTime.of(0, 0))
                .endDay(Weekday.MONDAY)
                .endTime(LocalTime.of(23, 59))
                .build()
            )
          )
      );
    // this.createCalendarFromPeriod(replacement, servicePointId);
    log.info("done");
  }

  /**
   * Delete a calendar by its ID
   *
   * @param calendarId the calendar to delete
   */
  public void deleteCalendar(Calendar calendar) {
    this.calendarRepository.deleteCascadingById(calendar.getId());
  }

  /**
   * Insert (or update) a calendar to the database
   *
   * @param calendar the calendar to insert
   */
  public void insertCalendar(Calendar calendar) {
    this.calendarRepository.save(calendar);
  }

  /**
   * Create a calendar from a given period
   *
   * @param period a {@link org.folio.calendar.domain.dto.Period Period}
   * @return the created {@link org.folio.calendar.domain.entity.Calendar Calendar}
   */
  public Calendar createCalendarFromPeriod(Period period, UUID servicePointId) {
    this.checkPeriod(period, servicePointId);

    if (this.calendarRepository.existsById(period.getId())) {
      throw new DataConflictException(
        new ExceptionParameters("period", period),
        "The period ID %s already exists",
        period.getId()
      );
    }

    // basic info
    Calendar.CalendarBuilder calendarBuilder = Calendar
      .builder()
      .id(period.getId())
      .name(period.getName())
      .startDate(period.getStartDate())
      .endDate(period.getEndDate());

    // assign starting service point
    ServicePointCalendarAssignment servicePointAssignment = ServicePointCalendarAssignment
      .builder()
      .servicePointId(period.getServicePointId())
      .build();
    calendarBuilder = calendarBuilder.servicePoint(servicePointAssignment);

    // create hours
    if (PeriodUtils.areOpeningsExceptional(period.getOpeningDays())) {
      calendarBuilder.exceptions(
        PeriodUtils.convertOpeningDayRelativeToExceptionRanges(
          period.getStartDate(),
          period.getEndDate(),
          period.getOpeningDays()
        )
      );
    } else {
      calendarBuilder.normalHours(
        PeriodUtils.convertOpeningDayRelativeToNormalOpening(period.getOpeningDays())
      );
    }

    Calendar calendar = calendarBuilder.build();

    this.insertCalendar(calendar);

    return calendar;
  }

  /**
   * Get a list of periods based on a filter (for exceptional/normal openings), optionally including past and opening day information
   *
   * @param servicePointId the service point which these periods apply to
   * @param filter a {@link PeriodQueryFilter PeriodQueryFilter} denoting how to filter these results
   * @param showPast if past periods should be included
   * @param withOpeningDays if {@link OpeningDayRelative OpeningDayRelative} information should be included
   * @return a {@link PeriodCollection PeriodCollection} of matching periods
   */
  // allow a method to be if/else'd on a boolean
  @SuppressWarnings("java:S2301")
  public PeriodCollection getPeriods(
    UUID servicePointId,
    PeriodQueryFilter filter,
    boolean showPast,
    boolean withOpeningDays
  ) {
    List<Calendar> calendars;
    if (showPast) {
      calendars = this.calendarRepository.findByServicePointId(servicePointId);
    } else {
      calendars =
        this.calendarRepository.findByServicePointIdOnOrAfterDate(
            servicePointId,
            DateUtils.getCurrentDate()
          );
    }
    return PeriodCollectionUtils.getPeriodsFromCalendarList(calendars, filter, withOpeningDays);
  }

  /**
   * Get a calendar by a given UUID
   *
   * @param id UUID to search for
   * @return found {@link Calendar} object
   */
  public Calendar getCalendarById(UUID id) {
    return this.calendarRepository.findById(id)
      .orElseThrow(() ->
        new DataNotFoundException(
          new ExceptionParameters(CalendarController.PARAMETER_NAME_PERIOD_ID, id),
          "No calendar was found with ID %s",
          id
        )
      );
  }

  /**
   * Get a calendar by a given UUID and service point
   *
   * @param servicePointId service point UUID that the calendar must apply to
   * @param periodId ID to search for
   * @return found {@link Calendar} object
   */
  public Calendar getCalendarById(UUID servicePointId, UUID periodId) {
    try {
      Calendar calendar = this.getCalendarById(periodId);

      if (
        calendar
          .getServicePoints()
          .stream()
          .map(ServicePointCalendarAssignment::getServicePointId)
          .noneMatch(id -> id.equals(servicePointId))
      ) {
        throw new DataNotFoundException(
          new ExceptionParameters(
            CalendarController.PARAMETER_NAME_SERVICE_POINT_ID,
            servicePointId,
            CalendarController.PARAMETER_NAME_PERIOD_ID,
            periodId
          ),
          "The period requested does exist, however, is not assigned to service point %s",
          servicePointId
        );
      }

      return calendar;
    } catch (EntityNotFoundException exception) {
      throw new DataNotFoundException(
        exception,
        new ExceptionParameters(CalendarController.PARAMETER_NAME_PERIOD_ID, periodId),
        "No calendar was found with ID %s",
        periodId
      );
    }
  }

  /**
   * Check that a period is valid and insertable
   * @param period period to verify
   * @param servicePointId service point this calendar will be initially assigned to
   * @throws org.folio.calendar.exception.AbstractCalendarException if it cannot be inserted
   */
  public void checkPeriod(Period period, UUID servicePointId) {
    if (period.getName().trim().isEmpty()) {
      throw new InvalidDataException(
        ErrorCode.NO_NAME,
        new ExceptionParameters(
          CalendarController.PARAMETER_NAME_SERVICE_POINT_ID,
          servicePointId,
          CalendarController.PARAMETER_NAME_PERIOD,
          period
        ),
        "The provided name (\"%s\") was empty",
        period.getName()
      );
    }
    if (period.getStartDate().isAfter(period.getEndDate())) {
      throw new InvalidDataException(
        ErrorCode.INVALID_DATE_RANGE,
        new ExceptionParameters(
          CalendarController.PARAMETER_NAME_SERVICE_POINT_ID,
          servicePointId,
          CalendarController.PARAMETER_NAME_PERIOD,
          period
        ),
        "The start date (%s) was after the end date (%s)",
        period.getStartDate(),
        period.getEndDate()
      );
    }
    if (!servicePointId.equals(period.getServicePointId())) {
      throw new InvalidDataException(
        new ExceptionParameters(
          CalendarController.PARAMETER_NAME_SERVICE_POINT_ID,
          servicePointId,
          CalendarController.PARAMETER_NAME_PERIOD,
          period
        ),
        "The service point ID in the URL (%s) did not match the one in the payload (%s)",
        servicePointId,
        period.getServicePointId()
      );
    }

    Calendar overlapped = null;
    if (!PeriodUtils.areOpeningsExceptional(period.getOpeningDays())) {
      overlapped =
        DateUtils.overlapsCalendarList(
          period,
          getCalendarsWithNormalHoursForServicePoint(servicePointId)
        );
    } else {
      overlapped =
        DateUtils.overlapsCalendarList(
          period,
          getCalendarsWithExceptionsForServicePoint(servicePointId)
        );
    }

    if (overlapped != null) {
      throw new DataConflictException(
        ErrorCode.OVERLAPPING_CALENDAR,
        new ExceptionParameters(
          CalendarController.PARAMETER_NAME_SERVICE_POINT_ID,
          servicePointId,
          CalendarController.PARAMETER_NAME_PERIOD,
          period
        ),
        "This period (%s to %s) overlaps with another calendar (\"%s\" from %s to %s)",
        period.getStartDate(),
        period.getEndDate(),
        overlapped.getName(),
        overlapped.getStartDate(),
        overlapped.getEndDate()
      );
    }
  }
}
