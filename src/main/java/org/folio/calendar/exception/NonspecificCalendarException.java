package org.folio.calendar.exception;

import org.folio.calendar.domain.dto.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * An exception to be used whenever nothing more specific has been written. This
 * will primarily be used by {@link org.folio.calendar.controller.ApiExceptionHandler ApiExceptionHandler} for Spring exceptions
 * ({@link org.springframework.web.method.annotation.MethodArgumentTypeMismatchException MethodArgumentTypeMismatchException},
 * {@link org.springframework.web.bind.MissingRequestValueException MissingRequestValueException}) and any otherwise unhandled {@link
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
    ErrorCode errorCode,
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
   * @param statusCode The Spring HTTP status code ({@link org.springframework.http.HttpStatus HttpStatus})
   * @param errorCode  An error code as described in the ErrorResponse API type
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  public NonspecificCalendarException(
    Exception cause,
    HttpStatus statusCode,
    ErrorCode errorCode,
    String message,
    Object... format
  ) {
    super(cause, statusCode, errorCode, message, format);
  }
}
