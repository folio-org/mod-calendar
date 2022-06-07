package org.folio.calendar.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningCollectionDTO;
import org.folio.calendar.domain.dto.SurroundingOpeningsDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.rest.resource.OpeningHoursApi;
import org.folio.calendar.service.CalendarValidationService;
import org.folio.calendar.service.OpeningHoursService;
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
public final class OpeningHoursController implements OpeningHoursApi {

  @Autowired
  private CalendarValidationService calendarValidationService;

  @Autowired
  private OpeningHoursService openingHoursService;

  @Autowired
  private CalendarMapper calendarMapper;

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarDTO> createCalendar(CalendarDTO calendarDto) {
    Calendar calendar = calendarMapper.fromDto(calendarDto);
    calendarValidationService.validate(calendar, new ArrayList<>());

    log.info("createCalendar: Calendar passed validation, saving...");

    calendar.clearIds();
    calendar.setId(UUID.randomUUID());
    openingHoursService.saveCalendar(calendar);

    return new ResponseEntity<>(calendarMapper.toDto(calendar), HttpStatus.CREATED);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarCollectionDTO> getCalendars(List<UUID> calendarIds) {
    return new ResponseEntity<>(
      openingHoursService.getCalendarCollectionForIdList(new HashSet<>(calendarIds)),
      HttpStatus.OK
    );
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarCollectionDTO> searchCalendars(
    List<UUID> servicePointIds,
    LocalDate startDate,
    LocalDate endDate,
    Integer offset,
    Integer limit
  ) {
    return new ResponseEntity<>(
      openingHoursService.getCalendarCollectionForServicePointsOrDateRange(
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
    // ensure the ID currently exists
    openingHoursService.getCalendarCollectionForIdList(Set.of(calendarId));

    Calendar calendar = calendarMapper.fromDto(calendarDto);
    calendarValidationService.validate(calendar, Arrays.asList(calendarId));

    log.info("updateCalendar: Calendar passed validation, saving...");

    calendar.clearIds();
    calendar.setId(calendarId);
    openingHoursService.saveCalendar(calendar);

    return new ResponseEntity<>(calendarMapper.toDto(calendar), HttpStatus.OK);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Void> deleteCalendars(List<UUID> calendarIds) {
    openingHoursService
      .getCalendarsForIdList(new HashSet<>(calendarIds))
      .forEach(openingHoursService::deleteCalendar);

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
      openingHoursService.getDailyOpeningCollection(
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
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
