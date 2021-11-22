package org.folio.calendar.controller;

import static org.apache.logging.log4j.Level.ERROR;
import static org.apache.logging.log4j.Level.INFO;

import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.ErrorResponse;
import org.folio.calendar.domain.dto.ErrorResponse.ErrorCodeEnum;
import org.folio.calendar.exception.AbstractCalendarException;
import org.folio.calendar.exception.NonspecificCalendarException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Log4j2
@RestControllerAdvice
public class ApiExceptionHandler {

  /**
   * Handles exceptions from our application
   *
   * @param exception {@link AbstractCalendarException} object
   * @see AbstractCalendarException
   * @return ResponseEntity<ErrorResponse>
   */
  @ExceptionHandler(AbstractCalendarException.class)
  public ResponseEntity<ErrorResponse> handleCalendarException(
    AbstractCalendarException exception
  ) {
    log.log(INFO, exception);
    return exception.getErrorResponseEntity();
  }

  /**
   * Handles improperly typed parameters
   *
   * @param exception {@link Exception} object
   * @return {@link ResponseEntity} with {@link ErrorResponse} body.
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleGenericInvalidParameter(
    MethodArgumentTypeMismatchException exception
  ) {
    log.log(INFO, exception);
    return new NonspecificCalendarException(
      exception,
      ErrorCodeEnum.INVALID_PARAMETER,
      "One of the parameters was of the incorrect type (%s)",
      exception.getMessage()
    )
      .getErrorResponseEntity();
  }

  /**
   * Handles entirely missing parameters
   *
   * @param exception {@link Exception} object
   * @return {@link ResponseEntity} with {@link ErrorResponse} body.
   */
  @ExceptionHandler(MissingRequestValueException.class)
  public ResponseEntity<ErrorResponse> handleGenericMissingParameter(
    MissingRequestValueException exception
  ) {
    log.log(INFO, exception);
    return new NonspecificCalendarException(
      exception,
      ErrorCodeEnum.INVALID_PARAMETER,
      "One of the parameters was missing or null (%s)",
      exception.getMessage()
    )
      .getErrorResponseEntity();
  }

  /**
   * Handles all uncaught exceptions.
   *
   * @param exception {@link Exception} object
   * @return {@link ResponseEntity} with {@link ErrorResponse} body.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception exception) {
    log.log(ERROR, exception);

    return new NonspecificCalendarException(
      exception,
      HttpStatus.INTERNAL_SERVER_ERROR,
      ErrorCodeEnum.INTERNAL_SERVER_ERROR,
      "Internal server error (%s): %s",
      exception.getClass().getSimpleName(),
      exception.getMessage()
    )
      .getErrorResponseEntity();
  }
}
