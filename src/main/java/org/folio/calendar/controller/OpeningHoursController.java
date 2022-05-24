package org.folio.calendar.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.AdjacentOpeningsDTO;
import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningCollectionDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.domain.request.Parameters;
import org.folio.calendar.rest.resource.OpeningHoursApi;
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
  private OpeningHoursService openingHoursService;

  @Autowired
  private CalendarMapper calendarMapper;

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarDTO> createCalendar(CalendarDTO calendarDto) {
    log.warn(Parameters.CALENDAR.toString());
    Calendar calendar = calendarMapper.fromDto(calendarDto);
    openingHoursService.validate(calendar);

    log.info("createCalendar: Calendar passed validation, saving...");

    openingHoursService.saveCalendar(calendar);

    return new ResponseEntity<>(calendarMapper.toDto(calendar), HttpStatus.CREATED);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarCollectionDTO> getCalendars(List<UUID> calendars) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarCollectionDTO> getAllCalendars(
    List<UUID> servicePointId,
    LocalDate startDate,
    LocalDate endDate,
    Integer offset,
    Integer limit
  ) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalendarDTO> updateCalendar(UUID calendarId, CalendarDTO calendar) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Void> deleteCalendars(List<UUID> calendars) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<AdjacentOpeningsDTO> getAdjacentOpenings(
    UUID servicePointId,
    LocalDate date
  ) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
