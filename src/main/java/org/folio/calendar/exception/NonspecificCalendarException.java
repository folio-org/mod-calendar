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
   * @param cause     The exception which caused this (should not be null due to
   *                  the ambiguity of this exception)
   * @param errorCode An error code as described in the ErrorResponse API type
   * @param message   A printf-style string for the error message
   * @param format    Formatting for the printf style message
   * @see String#format
   */
  public NonspecificCalendarException(
    Throwable cause,
    ErrorCodeEnum errorCode,
    String message,
    Object... format
  ) {
    super(cause, errorCode, message, format);
  }

  /**
   * Create an AbstractCalendarException with the given HTTP status code, error
   * code, message, and format.
   *
   * @param cause      The exception which caused this (should not be null due to the ambiguity of this exception)
   * @param statusCode The Spring HTTP status code ({@link HttpStatus})
   * @param errorCode  An error code as described in the ErrorResponse API type
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  public NonspecificCalendarException(
    Exception cause,
    HttpStatus statusCode,
    ErrorCodeEnum errorCode,
    String message,
    Object... format
  ) {
    super(cause, statusCode, errorCode, message, format);
  }
}