package org.folio.calendar.exception;

import org.folio.calendar.domain.dto.ErrorResponse.ErrorCodeEnum;
import org.springframework.http.HttpStatus;

/**
 * An exception to be used whenever nothing more specific has been written. This
 * will primarily be used by {@link ApiExceptionHandler} for Spring exceptions
 * ({@link MethodArgumentTypeMismatchException,
 * {@link MissingRequestValueException}) and any otherwise unhandled {@link
 * Exception}
 */
public class NonspecificCalendarException extends AbstractCalendarException {

  /**
   * Create an NonspecificCalendarException with the given error code, message,
   * and format. This constructor assumes a HTTP code of 400 Bad Request
   *
   * @param errorCode An error code as described in the ErrorResponse API type
   * @param message   A printf-style string for the error message
   * @param format    Formatting for the printf style message
   * @see String#format
   */
  public NonspecificCalendarException(ErrorCodeEnum errorCode, String message, Object... format) {
    super(errorCode, message, format);
  }

  /**
   * Create an AbstractCalendarException with the given HTTP status code, error
   * code, message, and format.
   *
   * @param statusCode The Spring HTTP status code ({@link HttpStatus})
   * @param errorCode  An error code as described in the ErrorResponse API type
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  public NonspecificCalendarException(
    HttpStatus statusCode,
    ErrorCodeEnum errorCode,
    String message,
    Object... format
  ) {
    super(statusCode, errorCode, message, format);
  }
}
