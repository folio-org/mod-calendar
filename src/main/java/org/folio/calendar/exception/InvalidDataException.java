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
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  public InvalidDataException(
    ErrorCode errorCode,
    ExceptionParameters parameters,
    String message,
    Object... format
  ) {
    super(parameters, HttpStatus.UNPROCESSABLE_ENTITY, errorCode, message, format);
  }

  /**
   * Create an exception for invalid data with a message and parameters.
   *
   * @param parameters Parameters which caused this issue
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  public InvalidDataException(ExceptionParameters parameters, String message, Object... format) {
    this(ErrorCode.INVALID_REQUEST, parameters, message, format);
  }
}
