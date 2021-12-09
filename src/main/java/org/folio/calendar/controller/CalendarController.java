package org.folio.calendar.controller;

import java.util.UUID;
import org.folio.calendar.domain.dto.ErrorCode;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.exception.ExceptionParameters;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.rest.resource.CalendarApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class CalendarController implements CalendarApi {

  @Override
  public ResponseEntity<Period> addNewPeriod(
    String xOkapiTenant,
    UUID servicePointId,
    Period period
  ) {
    if (period.getName().trim().isEmpty()) {
      ExceptionParameters parameters = new ExceptionParameters();
      parameters.addParameter("servicePointId", servicePointId);
      parameters.addParameter("period", period);

      throw new InvalidDataException(
        ErrorCode.NO_NAME,
        parameters,
        "The provided name (\"%s\") was empty",
        period.getName()
      );
    }
    if (period.getStartDate().isAfter(period.getEndDate())) {
      ExceptionParameters parameters = new ExceptionParameters();
      parameters.addParameter("servicePointId", servicePointId);
      parameters.addParameter("period", period);

      throw new InvalidDataException(
        ErrorCode.INVALID_DATE_RANGE,
        parameters,
        "The start date (%s) was after the end date (%s)",
        period.getStartDate(),
        period.getEndDate()
      );
    }

    return new ResponseEntity<>(null, HttpStatus.NOT_IMPLEMENTED);
  }
}
