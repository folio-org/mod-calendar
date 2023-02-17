package org.folio.calendar.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningCollectionDTO;
import org.folio.calendar.domain.dto.SurroundingOpeningsDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.rest.resource.CalendarApi;
import org.folio.calendar.service.CalendarService;
import org.folio.calendar.service.CalendarValidationService;
import org.folio.spring.FolioExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main Calendar API controller
 */
@Log4j2
@RestController
@RequestMapping(value = "/")
public class CalendarController implements CalendarApi {

  @Autowired
  private CalendarValidationService calendarValidationService;

  @Autowired
  private CalendarService calendarService;

  @Autowired
  private CalendarMapper calendarMapper;

  @Autowired
  private FolioExecutionContext folioExecutionContext;

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarDTO> createCalendar(CalendarDTO calendarDto) {
    Calendar calendar = calendarMapper.fromDto(calendarDto);
    calendarValidationService.validate(calendar);

    log.info("createCalendar: Calendar passed validation, saving...");

    calendar.clearIds();
    calendar.setId(UUID.randomUUID());

    calendar.setCreatedDate(Instant.now());
    calendar.setUpdatedDate(Instant.now());
    calendar.setCreatedByUserId(folioExecutionContext.getUserId());
    calendar.setUpdatedByUserId(folioExecutionContext.getUserId());

    calendarService.saveCalendar(calendar);

    return new ResponseEntity<>(calendarMapper.toDto(calendar), HttpStatus.CREATED);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarDTO> getCalendar(UUID calendarId) {
    return new ResponseEntity<>(calendarService.getCalendarById(calendarId), HttpStatus.OK);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarCollectionDTO> searchCalendars(
    List<UUID> calendarIds,
    List<UUID> servicePointIds,
    LocalDate startDate,
    LocalDate endDate,
    Integer offset,
    Integer limit
  ) {
    return new ResponseEntity<>(
      calendarService.getCalendarCollectionForIdsServicePointsOrDateRange(
        calendarIds,
        servicePointIds,
        startDate,
        endDate,
        offset,
        limit
      ),
      HttpStatus.OK
    );
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarDTO> updateCalendar(UUID calendarId, CalendarDTO calendarDto) {
    // ensure the ID currently exists, as this method throws 404 otherwise
    Calendar originalCalendar = calendarMapper.fromDto(calendarService.getCalendarById(calendarId));

    Calendar calendar = calendarMapper.fromDto(calendarDto);
    calendarValidationService.validate(calendar, Arrays.asList(calendarId));

    log.info("updateCalendar: Calendar passed validation, saving...");

    calendar.clearIds();
    calendar.setId(calendarId);

    // ensure new request cannot update this metadata
    calendar.setCreatedDate(originalCalendar.getCreatedDate());
    calendar.setCreatedByUserId(originalCalendar.getCreatedByUserId());

    calendar.setUpdatedDate(Instant.now());
    calendar.setUpdatedByUserId(folioExecutionContext.getUserId());

    calendarService.saveCalendar(calendar);

    return new ResponseEntity<>(calendarMapper.toDto(calendar), HttpStatus.OK);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Void> deleteCalendar(UUID calendarId) {
    // getting the calendar first ensures that non-existent calendars will result in 404
    calendarService.deleteCalendarById(calendarService.getCalendarById((calendarId)).getId());

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Void> deleteCalendars(List<UUID> calendarIds) {
    // getting the calendars ensure that non-existent calendars will result in 404
    calendarService
      .getCalendarsForIdList(new HashSet<>(calendarIds))
      .stream()
      .map(Calendar::getId)
      .forEach(calendarService::deleteCalendarById);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<SingleDayOpeningCollectionDTO> getAllOpenings(
    UUID servicePointId,
    LocalDate startDate,
    LocalDate endDate,
    Boolean includeClosed,
    Integer offset,
    Integer limit
  ) {
    return new ResponseEntity<>(
      calendarService.getDailyOpeningCollection(
        servicePointId,
        startDate,
        endDate,
        includeClosed,
        offset,
        limit
      ),
      HttpStatus.OK
    );
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<SurroundingOpeningsDTO> getSurroundingOpenings(
    UUID servicePointId,
    LocalDate date
  ) {
    return new ResponseEntity<>(
      calendarService.getSurroundingOpenings(servicePointId, date),
      HttpStatus.OK
    );
  }
}
