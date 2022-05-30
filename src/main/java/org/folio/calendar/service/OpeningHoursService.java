package org.folio.calendar.service;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A Service class for calendar-related API calls
 */
@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class OpeningHoursService {

  private final CalendarRepository calendarRepository;

  /**
   * Get all the calendars for a certain service point
   *
   * @param servicePointId the service point
   * @return a {@link java.util.List List} of {@link java.util.Calendar Calendar}s
   */
  public List<Calendar> getCalendarsForServicePoint(UUID servicePointId) {
    return this.calendarRepository.findByServicePointId(servicePointId);
  }

  /**
   * Insert (or update) a calendar to the database
   *
   * @param calendar the calendar to insert/update/save
   */
  public void saveCalendar(Calendar calendar) {
    if (calendar.getId() == null) {
      calendar.setId(UUID.randomUUID());
    }
    this.calendarRepository.save(calendar);
  }
}
