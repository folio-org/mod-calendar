package org.folio.calendar.exception;

import org.folio.calendar.domain.dto.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * An exception thrown whenever new data conflicts (e.g. duplicate IDs)
 */
public class DataConflictException extends AbstractCalendarException {

  /**
   * Create an exception for data conflict with a message, error code, and parameters.
   *
   * @param errorCode  The error code to report (defined by ErrorResponse)
   * @param parameters Parameters which caused this issue
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  public DataConflictException(
    ErrorCode errorCode,
    ExceptionParameters parameters,
    String message,
    Object... format
  ) {
    super(null, parameters, HttpStatus.CONFLICT, errorCode, message, format);
  }

  /**
   * Create an exception for data conflict with a message, error code, and parameters.
   *
   * @param parameters Parameters which caused this issue
   * @param message    A printf-style string for the error message
   * @param format     Formatting for the printf style message
   * @see String#format
   */
  public DataConflictException(ExceptionParameters parameters, String message, Object... format) {
    this(ErrorCode.INVALID_REQUEST, parameters, message, format);
  }
}
