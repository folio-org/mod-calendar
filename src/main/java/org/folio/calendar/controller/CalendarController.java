package org.folio.calendar.controller;

import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.dto.PeriodCollection;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.exception.DataNotFoundException;
import org.folio.calendar.repository.PeriodQueryFilter;
import org.folio.calendar.rest.resource.CalendarApi;
import org.folio.calendar.service.CalendarService;
import org.folio.calendar.utils.PeriodUtils;
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
public final class CalendarController implements CalendarApi {

  public static final String PARAMETER_NAME_PERIOD = "period";
  public static final String PARAMETER_NAME_SERVICE_POINT_ID = "servicePointId";
  public static final String PARAMETER_NAME_PERIOD_ID = "periodId";

  @Autowired
  public CalendarService calendarService;

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Period> addNewPeriod(
    String xOkapiTenant,
    UUID servicePointId,
    Period period
  ) {
    Calendar calendar = this.calendarService.createCalendarFromPeriod(period, servicePointId);

    return new ResponseEntity<>(PeriodUtils.toPeriod(calendar), HttpStatus.CREATED);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<PeriodCollection> getPeriodsForServicePoint(
    String xOkapiTenant,
    UUID servicePointId,
    Boolean withOpeningDays,
    Boolean showPast,
    Boolean showExceptional
  ) {
    PeriodQueryFilter filter;
    if (Boolean.TRUE.equals(showExceptional)) {
      filter = PeriodQueryFilter.EXCEPTIONS;
    } else {
      filter = PeriodQueryFilter.NORMAL_HOURS;
    }

    return new ResponseEntity<>(
      this.calendarService.getPeriods(
          servicePointId,
          filter,
          Boolean.TRUE.equals(showPast),
          Boolean.TRUE.equals(withOpeningDays)
        ),
      HttpStatus.OK
    );
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Period> getPeriodById(
    String xOkapiTenant,
    UUID servicePointId,
    UUID periodId
  ) {
    return new ResponseEntity<>(
      PeriodUtils.toPeriod(this.calendarService.getCalendarById(servicePointId, periodId)),
      HttpStatus.OK
    );
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Void> deletePeriodById(
    String xOkapiTenant,
    UUID servicePointId,
    UUID periodId
  ) {
    Calendar calendar = this.calendarService.getCalendarById(servicePointId, periodId);

    this.calendarService.deleteCalendar(calendar);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("java:S1141")
  public ResponseEntity<Void> updatePeriodById(
    String xOkapiTenant,
    UUID servicePointId,
    UUID periodId,
    Period period
  ) {
    try {
      Calendar originalCalendar = this.calendarService.getCalendarById(periodId);

      this.calendarService.replaceCalendar(originalCalendar, period, servicePointId);
    } catch (DataNotFoundException exception) {
      log.info("Current calendar does not exist; creating new one");
      log.info(exception);

      this.calendarService.createCalendarFromPeriod(period, servicePointId);
    }

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
