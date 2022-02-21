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
   * @param cause      The cause of this exception
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   */
  public DataNotFoundException(Throwable cause, ExceptionParameters parameters, String message) {
    super(cause, parameters, HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, message);
  }

  /**
   * Create an exception for when data is not found with a message and parameters.
   *
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   */
  public DataNotFoundException(ExceptionParameters parameters, String message) {
    this(null, parameters, message);
  }
}
