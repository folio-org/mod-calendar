package org.folio.calendar.exception;

import org.folio.calendar.domain.dto.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * An exception thrown whenever invalid data is presented (such as empty fields, start date &gt; end date, etc)
 */
public class InvalidDataException extends AbstractCalendarException {

  /**
   * Create an exception for invalid data with a message, error code, and parameters.
   *
   * @param errorCode  The error code to report (defined by ErrorResponse)
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   */
  public InvalidDataException(ErrorCode errorCode, ExceptionParameters parameters, String message) {
    super(null, parameters, HttpStatus.BAD_REQUEST, errorCode, message, null);
  }

  /**
   * Create an exception for invalid data with a message and parameters.
   *
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   */
  public InvalidDataException(ExceptionParameters parameters, String message) {
    this(ErrorCode.INVALID_REQUEST, parameters, message);
  }

  /**
   * Create an exception for invalid data with a cause, message, and parameters.
   *
   * @param cause      The cause of the exception
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   */
  public InvalidDataException(Throwable cause, ExceptionParameters parameters, String message) {
    super(cause, parameters, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST, message, null);
  }
}
