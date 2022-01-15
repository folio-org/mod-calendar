package org.folio.calendar.controller;

import static org.apache.logging.log4j.Level.ERROR;
import static org.apache.logging.log4j.Level.INFO;

import java.util.Arrays;
import javax.servlet.ServletException;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.ErrorCode;
import org.folio.calendar.domain.dto.ErrorResponse;
import org.folio.calendar.exception.AbstractCalendarException;
import org.folio.calendar.exception.NonspecificCalendarException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Overall controller to handle exceptions and return proper API responses
 */
@Log4j2
@RestControllerAdvice
public class ApiExceptionHandler {

  /**
   * Handles exceptions from our application
   *
   * @param exception an {@link org.folio.calendar.exception.AbstractCalendarException AbstractCalendarException}
   * @see AbstractCalendarException
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponse ErrorResponse} body.
   */
  @ExceptionHandler(AbstractCalendarException.class)
  public ResponseEntity<ErrorResponse> handleCalendarException(
    AbstractCalendarException exception
  ) {
    log.log(INFO, exception);
    return exception.getErrorResponseEntity();
  }

  /**
   * Handles requests to endpoints that do not exist
   *
   * @param exception exception indicating that no handler exists for an endpoint
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponse ErrorResponse} body.
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException exception) {
    log.log(INFO, exception);
    return new NonspecificCalendarException(
      exception,
      HttpStatus.NOT_FOUND,
      ErrorCode.INVALID_REQUEST,
      "This application does not know how to handle a %s request to %s",
      exception.getHttpMethod(),
      exception.getRequestURL()
    )
      .getErrorResponseEntity();
  }

  /**
   * Handles requests to endpoints with unknown methods
   *
   * @param exception exception indicating that no handler exists for an endpoint with this method
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponse ErrorResponse} body.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleBadMethod(
    HttpRequestMethodNotSupportedException exception
  ) {
    log.log(INFO, exception);
    return new NonspecificCalendarException(
      exception,
      HttpStatus.METHOD_NOT_ALLOWED,
      ErrorCode.INVALID_REQUEST,
      "This endpoint does not accept %s requests to this endpoint (known are: %s)",
      exception.getMethod(),
      Arrays.toString(exception.getSupportedMethods())
    )
      .getErrorResponseEntity();
  }

  /**
   * Handles improperly typed parameters/requests
   *
   * @param exception exception indicating that the request could not be parsed
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponse ErrorResponse} body.
   */
  @ExceptionHandler(
    {
      ServletException.class,
      MethodArgumentTypeMismatchException.class,
      MissingRequestValueException.class,
      HttpMessageNotReadableException.class,
    }
  )
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception exception) {
    log.log(INFO, exception);
    return new NonspecificCalendarException(
      exception,
      ErrorCode.INVALID_PARAMETER,
      "One of the parameters was of the incorrect type (%s)",
      exception.getMessage()
    )
      .getErrorResponseEntity();
  }

  /**
   * Handles all uncaught exceptions.
   *
   * @param exception exceptions not otherwise caught
   * @return {@link org.springframework.http.ResponseEntity ResponseEntity} with {@link org.folio.calendar.domain.dto.ErrorResponse ErrorResponse} body.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception exception) {
    log.log(ERROR, exception);

    // As a note, NullPointerException can be thrown deep in the servlet code if parsing invalid JSON.
    // However, NPE is far too generic to catch and always attribute to bad input.
    return new NonspecificCalendarException(
      exception,
      HttpStatus.INTERNAL_SERVER_ERROR,
      ErrorCode.INTERNAL_SERVER_ERROR,
      "Internal server error (%s): %s",
      exception.getClass().getSimpleName(),
      exception.getMessage()
    )
      .getErrorResponseEntity();
  }
}
