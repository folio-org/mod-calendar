package org.folio.calendar.exception;

import java.time.Instant;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.NonNull;
import org.folio.calendar.domain.dto.Error;
import org.folio.calendar.domain.dto.ErrorCode;
import org.folio.calendar.domain.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Abstract calendar exception, to be implemented by more concrete exceptions thrown from our application.
 * {@link NonspecificCalendarException} should be used for otherwise unknown errors (e.g. generic Exception or Spring-related exceptions.)
 */
public abstract class AbstractCalendarException extends RuntimeException {

  private static final HttpStatus DEFAULT_STATUS_CODE = HttpStatus.BAD_REQUEST;

  @Getter
  protected final ErrorCode errorCode;

  @Getter
  protected final HttpStatus statusCode;

  @Getter
  @NonNull
  protected final ExceptionParameters parameters;

  /**
   * Create an AbstractCalendarException with the given error code, message, and
   * format. This constructor assumes a HTTP code of 400 Bad Request
   *
   * @param errorCode An error code as described in the ErrorResponse API type
   * @param message   A printf-style string for the error message
   * @param format    Formatting for the printf style message
   * @see String#format
   */
  protected AbstractCalendarException(ErrorCode errorCode, String message, Object... format) {
    this(null, null, DEFAULT_STATUS_CODE, errorCode, message, format);
  }

  /**
   * Create an AbstractCalendarException with the given error code, message, and
   * format. This constructor assumes a HTTP code of 400 Bad Request
   *
   * @param errorCode An error code as described in the ErrorResponse API type
   * @param parameters The parameters causing the exception
   * @param message   A printf-style string for the error message
   * @param format    Formatting for the printf style message
   * @see String#format
   */
  protected AbstractCalendarException(
    ErrorCode errorCode,
    ExceptionParameters parameters,
    String message,
    Object... format
  ) {
    this(null, parameters, DEFAULT_STATUS_CODE, errorCode, message, format);
  }

  /**
   * Create an AbstractCalendarException with the given causing exception, error
   * code, message, and format. This constructor assumes a HTTP code of 400 Bad
   * Request
   *
   * @param cause     The exception which caused this (may be null)
   * @param errorCode An error code as described in the ErrorResponse API type
   * @param message   A printf-style string for the error message
   * @param format    Formatting for the printf style message
   * @see String#format
   */
  protected AbstractCalendarException(
    Throwable cause,
    ErrorCode errorCode,
    String message,
    Object... format
  ) {
    this(cause, DEFAULT_STATUS_CODE, errorCode, message, format);
  }

  /**
   * Create an AbstractCalendarException with the given causing exception, error
   * code, message, and format. This constructor assumes a HTTP code of 400 Bad
   * Request
   *
   * @param cause     The exception which caused this (may be null)
   * @param parameters The parameters causing the exception
   * @param errorCode An error code as described in the ErrorResponse API type
   * @param message   A printf-style string for the error message
   * @param format    Formatting for the printf style message
   * @see String#format
   */
  protected AbstractCalendarException(
    Throwable cause,
    ExceptionParameters parameters,
    ErrorCode errorCode,
    String message,
    Object... format
  ) {
    this(cause, parameters, DEFAULT_STATUS_CODE, errorCode, message, format);
  }

  /**
   * Create an AbstractCalendarException with the given causing exception, error
   * code, message, and format. This constructor assumes a HTTP code of 400 Bad
   * Request
   *
   * @param statusCode The Spring HTTP status code ({@link HttpStatus})
   * @param errorCode  An error code as described in the ErrorResponse API type
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  protected AbstractCalendarException(
    HttpStatus statusCode,
    ErrorCode errorCode,
    String message,
    Object... format
  ) {
    this(null, null, statusCode, errorCode, message, format);
  }

  /**
   * Create an AbstractCalendarException with the given causing exception, error
   * code, message, and format. This constructor assumes a HTTP code of 400 Bad
   * Request
   *
   * @param parameters The parameters causing the exception
   * @param statusCode The Spring HTTP status code ({@link HttpStatus})
   * @param errorCode  An error code as described in the ErrorResponse API type
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  protected AbstractCalendarException(
    ExceptionParameters parameters,
    HttpStatus statusCode,
    ErrorCode errorCode,
    String message,
    Object... format
  ) {
    this(null, parameters, statusCode, errorCode, message, format);
  }

  /**
   * Create an AbstractCalendarException with the given HTTP status code, error
   * code, message, and format.
   *
   * @param cause      The exception which caused this (may be null)
   * @param statusCode The Spring HTTP status code ({@link HttpStatus})
   * @param errorCode  An error code as described in the ErrorResponse API type
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  protected AbstractCalendarException(
    Throwable cause,
    HttpStatus statusCode,
    ErrorCode errorCode,
    String message,
    Object... format
  ) {
    this(cause, null, statusCode, errorCode, message, format);
  }

  /**
   * Create an AbstractCalendarException with the given HTTP status code, error
   * code, message, and format.
   *
   * @param cause      The exception which caused this (may be null)
   * @param parameters The parameters causing the exception
   * @param statusCode The Spring HTTP status code ({@link HttpStatus})
   * @param errorCode  An error code as described in the ErrorResponse API type
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  protected AbstractCalendarException(
    Throwable cause,
    ExceptionParameters parameters,
    HttpStatus statusCode,
    ErrorCode errorCode,
    String message,
    Object... format
  ) {
    super(String.format(message, format), cause);
    if (parameters == null) {
      parameters = new ExceptionParameters();
    }
    this.parameters = parameters;
    this.errorCode = errorCode;
    this.statusCode = statusCode;
  }

  /**
   * Create a standardized error response for the rest API
   *
   * @return An ErrorResponse for API return
   */
  protected ErrorResponse getErrorResponse() {
    ErrorResponse response = new ErrorResponse();
    response.setTimestamp(Instant.now());
    response.setStatus(this.getStatusCode().value());

    // Can only have one exception at a time
    Error error = new Error();
    error.setCode(this.getErrorCode());
    error.setMessage(String.format("%s: %s", this.getClass().getSimpleName(), this.getMessage()));
    for (StackTraceElement frame : this.getStackTrace()) {
      error.addTraceItem(frame.toString());
    }
    if (this.getCause() != null) {
      error.addTraceItem("----------------- CAUSED BY -----------------");
      error.addTraceItem(this.getCause().getMessage());
      for (StackTraceElement frame : this.getCause().getStackTrace()) {
        error.addTraceItem(frame.toString());
      }
    }
    for (Entry<String, Object> parameter : this.getParameters().getMap().entrySet()) {
      error.putParametersItem(parameter.getKey(), parameter.getValue());
    }

    response.addErrorsItem(error);

    return response;
  }

  /**
   * Get a ResponseEntity to be returned to the API
   *
   * @return {@link ResponseEntity} with {@link ErrorResponse} body.
   */
  public ResponseEntity<ErrorResponse> getErrorResponseEntity() {
    return new ResponseEntity<>(this.getErrorResponse(), this.getStatusCode());
  }
}
