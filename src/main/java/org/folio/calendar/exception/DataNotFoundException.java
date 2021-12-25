package org.folio.calendar.exception;

import org.folio.calendar.domain.dto.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * An exception thrown whenever something cannot be found
 */
public class DataNotFoundException extends AbstractCalendarException {

  /**
   * Create an exception for when data is not found with a cause, message, and parameters.
   *
   * @param parameters Parameters which caused this issue
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  public DataNotFoundException(
    Throwable cause,
    ExceptionParameters parameters,
    String message,
    Object... format
  ) {
    super(cause, parameters, HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, message, format);
  }

  /**
   * Create an exception for when data is not found with a message and parameters.
   *
   * @param parameters Parameters which caused this issue
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  public DataNotFoundException(ExceptionParameters parameters, String message, Object... format) {
    this(null, parameters, message, format);
  }
}
