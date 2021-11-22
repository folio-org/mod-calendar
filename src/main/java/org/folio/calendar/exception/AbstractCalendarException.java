package org.folio.calendar.exception;

import java.util.Date;
import lombok.Getter;
import org.folio.calendar.domain.dto.ErrorResponse;
import org.folio.calendar.domain.dto.ErrorResponse.ErrorCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Abstract calendar exception, to be implemented by more concrete exceptions thrown from our application.  {@link NonspecificCalendarException} should be used for otherwise unknown errors (e.g. generic Exception or Spring-related exceptions.)
 */
public abstract class AbstractCalendarException extends RuntimeException {

  @Getter
  protected final ErrorCodeEnum errorCode;

  @Getter
  protected final HttpStatus statusCode;

  /**
   * Create an AbstractCalendarException with the given error code, message, and
   * format. This constructor assumes a HTTP code of 400 Bad Request
   *
   * @param errorCode An error code as described in the ErrorResponse API type
   * @param message   A printf-style string for the error message
   * @param format    Formatting for the printf style message
   * @see             String#format
   */
  protected AbstractCalendarException(ErrorCodeEnum errorCode, String message, Object... format) {
    super(String.format(message, format));
    this.errorCode = errorCode;
    this.statusCode = HttpStatus.BAD_REQUEST;
  }

  /**
   * Create an AbstractCalendarException with the given HTTP status code, error
   * code, message, and format.
   *
   * @param statusCode The Spring HTTP status code ({@link HttpStatus})
   * @param errorCode An error code as described in the ErrorResponse API type
   * @param message   A printf-style string for the error message
   * @param format    Formatting for the printf style message
   * @see String#format
   */
  protected AbstractCalendarException(
    HttpStatus statusCode,
    ErrorCodeEnum errorCode,
    String message,
    Object... format
  ) {
    super(String.format(message, format));
    this.errorCode = errorCode;
    this.statusCode = statusCode;
  }

  /**
   * Create a standardized error response for the rest API
   *
   * @return An ErrorResponse for API return
   */
  protected ErrorResponse getErrorResponse() {
    ErrorResponse error = new ErrorResponse();
    error.setTimestamp(new Date());
    error.setErrorCode(this.getErrorCode());
    error.setErrorMessage(
      String.format("%s: %s", this.getClass().getSimpleName(), this.getMessage())
    );
    error.setStatus(this.getStatusCode().value());
    return error;
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
