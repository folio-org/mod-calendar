package org.folio.calendar.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
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
import org.folio.calendar.repository.CustomOffsetPageRequest;
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
   * @param count     the number of calendars available (may not be equal to size, due to pagination)
   * @return a calendar collection object ready for an API response
   */
  protected CalendarCollectionDTO calendarsToCalendarCollection(
    Collection<Calendar> calendars,
    Integer count
  ) {
    List<CalendarDTO> transformedCalendars = calendars
      .stream()
      .map(calendarMapper::toDto)
      .collect(Collectors.toList());
    return CalendarCollectionDTO
      .builder()
      .calendars(transformedCalendars)
      .totalRecords(count)
      .build();
  }

  /**
   * Get all the calendars based on a list of ids.  If not all calendars are
   * found, a {@link DataNotFoundException DataNotFoundException} is thrown
   *
   * @param calendarIds a {@link java.util.List List} of calendars to search for
   * @return a {@link java.util.List List} of {@link java.util.Calendar Calendar}s
   */
  public List<Calendar> getCalendarsForIdList(Set<UUID> calendarIds) {
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
    return calendars;
  }

  /**
   * Get all the calendars based on a list of ids.  If not all calendars are
   * found, a {@link DataNotFoundException DataNotFoundException} is thrown
   *
   * @param servicePointIds a list of service point UUIDs to search
   * @param startDate the date which returned results will end before
   * @param endDate the date which returned results will not start after
   * @param limit the maximum number of calendars to return
   * @param offset the number of calendars to skip over
   * @return a {@link CalendarCollectionDTO CalendarCollectionDTO} with found calendars
   */
  public CalendarCollectionDTO getCalendarCollectionForServicePointsOrDateRange(
    @CheckForNull List<UUID> servicePointIds,
    LocalDate startDate,
    LocalDate endDate,
    Integer offset,
    Integer limit
  ) {
    return calendarsToCalendarCollection(
      calendarRepository.findWithServicePointsDateRangeAndPagination(
        servicePointIds != null,
        servicePointIds,
        startDate,
        endDate,
        new CustomOffsetPageRequest(offset, limit)
      ),
      calendarRepository.countWithServicePointsDateRangeAndPagination(
        servicePointIds != null,
        servicePointIds,
        startDate,
        endDate
      )
    );
  }

  /**
   * Get all the calendars based on a list of ids.  If not all calendars are
   * found, a {@link DataNotFoundException DataNotFoundException} is thrown
   *
   * @param calendarIds a {@link java.util.List List} of calendars to search for
   * @return a {@link CalendarCollectionDTO CalendarCollectionDTO} with found calendars
   */
  public CalendarCollectionDTO getCalendarCollectionForIdList(Set<UUID> calendarIds) {
    List<Calendar> calendars = getCalendarsForIdList(calendarIds);
    return calendarsToCalendarCollection(calendars, calendars.size());
  }

  /**
   * Insert (or update) a calendar to the database
   *
   * @param calendar the calendar to insert/update/save
   */
  public void saveCalendar(Calendar calendar) {
    this.calendarRepository.save(calendar);
  }

  /**
   * Delete a calendar by its ID
   *
   * @param calendar the calendar to delete
   */
  public void deleteCalendar(Calendar calendar) {
    this.calendarRepository.deleteCascadingById(calendar.getId());
  }
}
