package org.folio.calendar.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.error.CalendarNotFoundErrorData;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.domain.request.Parameters;
import org.folio.calendar.domain.request.TranslationKey;
import org.folio.calendar.exception.DataNotFoundException;
import org.folio.calendar.exception.ExceptionParameters;
import org.folio.calendar.i18n.TranslationService;
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
  private final CalendarMapper calendarMapper;
  private final TranslationService translationService;

  /**
   * Convert a set of calendars to a {@link CalendarCollectionDTO CalendarCollectionDTO}
   * @param calendars the calendars to convert
   * @return a calendar collection object ready for an API response
   */
  protected CalendarCollectionDTO calendarsToCalendarCollection(Collection<Calendar> calendars) {
    List<CalendarDTO> transformedCalendars = calendars
      .stream()
      .map(calendarMapper::toDto)
      .collect(Collectors.toList());
    return CalendarCollectionDTO
      .builder()
      .calendars(transformedCalendars)
      .totalRecords(calendars.size())
      .build();
  }

  /**
   * Get all the calendars based on a list of ids
   *
   * @param calendarIds a {@link java.util.List List} of calendars to search for
   * @return a {@link java.util.List List} of {@link java.util.Calendar Calendar}s
   */
  public CalendarCollectionDTO getCalendarsForIdList(Set<UUID> calendarIds) {
    List<Calendar> calendars = this.calendarRepository.findByIds(calendarIds);
    List<UUID> foundIds = calendars.stream().map(Calendar::getId).collect(Collectors.toList());
    if (calendars.size() != calendarIds.size()) {
      throw new DataNotFoundException(
        new ExceptionParameters(Parameters.QUERY, calendarIds),
        translationService.format(TranslationKey.ERROR_CALENDAR_NOT_FOUND),
        new CalendarNotFoundErrorData(
          calendarIds
            .stream()
            .filter(query -> !foundIds.contains(query))
            .collect(Collectors.toList())
        )
      );
    }
    return calendarsToCalendarCollection(calendars);
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
