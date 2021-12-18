package org.folio.calendar.service;

import java.util.List;
import java.util.UUID;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.folio.calendar.exception.ExceptionParameters;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.repository.CalendarRepository;
import org.folio.calendar.utils.PeriodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A Service class for calendar-related API calls
 */
@Service
public final class CalendarService {

  private final CalendarRepository calendarRepository;

  @Autowired
  CalendarService(CalendarRepository calendarRepository) {
    this.calendarRepository = calendarRepository;
  }

  /**
   * Get all the calendars for a certain service point
   *
   * @param servicePointId the service point
   * @return a {@link java.util.List List} of {@link java.util.Calendar Calendar}s associated with the service point
   */
  public List<Calendar> getAllCalendarsForServicePoint(UUID servicePointId) {
    return this.calendarRepository.findByServicePointId(servicePointId);
  }

  /**
   * Create a calendar from a given period
   *
   * @param period a {@link org.folio.calendar.domain.dto.Period Period}
   * @return the created {@link org.folio.calendar.domain.entity.Calendar Calendar}
   */
  public Calendar createCalendarFromPeriod(Period period) {
    if (this.calendarRepository.existsById(period.getId())) {
      throw new InvalidDataException(
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
      .calendarId(period.getId())
      .servicePointId(period.getServicePointId())
      .build();
    calendarBuilder = calendarBuilder.servicePoint(servicePointAssignment);

    // create hours
    calendarBuilder.normalHours(
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(period.getOpeningDays(), period.getId())
    );

    Calendar calendar = calendarBuilder.build();
    this.calendarRepository.save(calendar);

    return calendar;
  }
}
