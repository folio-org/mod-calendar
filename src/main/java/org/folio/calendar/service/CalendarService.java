package org.folio.calendar.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import javax.annotation.CheckForNull;
import lombok.AllArgsConstructor;
import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningCollectionDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningDTO;
import org.folio.calendar.domain.dto.SurroundingOpeningsDTO;
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
import org.folio.calendar.utils.CalendarUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * A Service class for calendar-related API calls
 */
@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CalendarService {

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
    List<CalendarDTO> transformedCalendars = calendars.stream().map(calendarMapper::toDto).toList();
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
    List<UUID> foundIds = calendars.stream().map(Calendar::getId).toList();
    if (calendars.size() != calendarIds.size()) {
      throw new DataNotFoundException(
        new ExceptionParameters(Parameters.QUERY, calendarIds),
        translationService.format(TranslationKey.ERROR_CALENDAR_NOT_FOUND),
        new CalendarNotFoundErrorData(
          calendarIds.stream().filter(query -> !foundIds.contains(query)).toList()
        )
      );
    }
    return calendars;
  }

  /**
   * Get all the calendars matching the given, optional, criteria.
   *
   * @param calendarIds a list of calendar IDs to consider
   * @param servicePointIds a list of service point UUIDs to search
   * @param startDate the date which returned results will end before
   * @param endDate the date which returned results will not start after
   * @param limit the maximum number of calendars to return
   * @param offset the number of calendars to skip over
   * @return a {@link CalendarCollectionDTO CalendarCollectionDTO} with found calendars
   */
  public CalendarCollectionDTO getCalendarCollectionForIdsServicePointsOrDateRange(
    @CheckForNull List<UUID> calendarIds,
    @CheckForNull List<UUID> servicePointIds,
    LocalDate startDate,
    LocalDate endDate,
    Integer offset,
    Integer limit
  ) {
    return calendarsToCalendarCollection(
      calendarRepository.findWithIdsServicePointsDateRangeAndPagination(
        calendarIds != null,
        calendarIds,
        servicePointIds != null,
        servicePointIds,
        startDate,
        endDate,
        new CustomOffsetPageRequest(offset, limit)
      ),
      calendarRepository.countWithIdsServicePointsDateRangeAndPagination(
        calendarIds != null,
        calendarIds,
        servicePointIds != null,
        servicePointIds,
        startDate,
        endDate
      )
    );
  }

  /**
   * Get the calendar corresponding to an ID.  If not
   * found, a {@link DataNotFoundException DataNotFoundException} is thrown
   *
   * @param calendarId the calendar ID to search for
   * @return a {@link CalendarDTO CalendarDTO} representing the found calendar
   */
  public CalendarDTO getCalendarById(UUID calendarId) {
    List<Calendar> calendars = getCalendarsForIdList(Set.of(calendarId));
    return calendarMapper.toDto(calendars.get(0));
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
   * @param calendarId the ID of the calendar to delete
   */
  public void deleteCalendarById(UUID calendarId) {
    this.calendarRepository.deleteCascadingById(calendarId);
  }

  /**
   * Get objects for each date within a calendar's range representing opening
   * or closure information
   *
   * @param servicePointId the service point ID to return opening information for
   * @param startDate the first date to include
   * @param endDate the last date to include
   * @param includeClosed whether or not closed dates should be included
   *        (exceptional closures are always included)
   * @param offset pagination offset
   * @param limit pagination limit
   * @return a {@link SingleDayOpeningCollectionDTO SingleDayOpeningCollectionDTO}
   * representing the dates' opening information
   */
  public SingleDayOpeningCollectionDTO getDailyOpeningCollection(
    UUID servicePointId,
    LocalDate startDate,
    LocalDate endDate,
    Boolean includeClosed,
    Integer offset,
    Integer limit
  ) {
    List<Calendar> relevantCalendars = calendarRepository.findWithIdsServicePointsDateRangeAndPagination(
      false,
      new ArrayList<>(),
      true,
      Arrays.asList(servicePointId),
      startDate,
      endDate,
      Pageable.unpaged()
    );

    // TreeMap makes sorting more efficient
    SortedMap<LocalDate, SingleDayOpeningDTO> dates = new TreeMap<>();
    relevantCalendars.forEach(calendar ->
      CalendarUtils.splitCalendarIntoDates(calendar, dates, startDate, endDate)
    );

    if (Boolean.TRUE.equals(includeClosed)) {
      CalendarUtils.fillClosedDates(dates, startDate, endDate);
    }

    return CalendarUtils.openingMapToCollection(dates, offset, limit);
  }

  /**
   * Get information on openings surrounding a given date
   *
   * @param servicePointId the service point to examine for openings
   * @param date the date to query
   * @return an {@link SurroundingOpeningsDTO SurroundingOpeningsDTO} representing opening information
   */
  public SurroundingOpeningsDTO getSurroundingOpenings(UUID servicePointId, LocalDate date) {
    List<Calendar> calendars = calendarRepository.findWithIdsServicePointsDateRangeAndPagination(
      false,
      new ArrayList<>(),
      true,
      Arrays.asList(servicePointId),
      null,
      null,
      Pageable.unpaged()
    );

    return CalendarUtils.getSurroundingOpenings(calendars, date);
  }
}
