package org.folio.calendar.controller;

import java.util.UUID;
import org.folio.calendar.domain.dto.ErrorCode;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.exception.DataConflictException;
import org.folio.calendar.exception.ExceptionParameters;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.rest.resource.CalendarApi;
import org.folio.calendar.service.CalendarService;
import org.folio.calendar.utils.DateUtils;
import org.folio.calendar.utils.PeriodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main Calendar API controller
 */
@RestController
@RequestMapping(value = "/")
public final class CalendarController implements CalendarApi {

  private static final String PARAMETER_NAME_PERIOD = "period";
  private static final String PARAMETER_NAME_SERVICE_POINT_ID = "servicePointId";

  @Autowired
  private CalendarService calendarService;

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Period> addNewPeriod(
    String xOkapiTenant,
    UUID servicePointId,
    Period period
  ) {
    if (period.getName().trim().isEmpty()) {
      throw new InvalidDataException(
        ErrorCode.NO_NAME,
        new ExceptionParameters(
          PARAMETER_NAME_SERVICE_POINT_ID,
          servicePointId,
          PARAMETER_NAME_PERIOD,
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
          PARAMETER_NAME_SERVICE_POINT_ID,
          servicePointId,
          PARAMETER_NAME_PERIOD,
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
          PARAMETER_NAME_SERVICE_POINT_ID,
          servicePointId,
          PARAMETER_NAME_PERIOD,
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
          this.calendarService.getCalendarsWithNormalHoursForServicePoint(servicePointId)
        );
    } else {
      overlapped =
        DateUtils.overlapsCalendarList(
          period,
          this.calendarService.getCalendarsWithExceptionsForServicePoint(servicePointId)
        );
    }

    if (overlapped != null) {
      throw new DataConflictException(
        ErrorCode.OVERLAPPING_CALENDAR,
        new ExceptionParameters(
          PARAMETER_NAME_SERVICE_POINT_ID,
          servicePointId,
          PARAMETER_NAME_PERIOD,
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

    Calendar calendar = this.calendarService.createCalendarFromPeriod(period);

    return new ResponseEntity<>(PeriodUtils.toPeriod(calendar), HttpStatus.CREATED);
  }
}
