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
   * @param message    A string for the error message
   */
  public DataConflictException(
    ErrorCode errorCode,
    ExceptionParameters parameters,
    String message
  ) {
    super(null, parameters, HttpStatus.CONFLICT, errorCode, message);
  }

  /**
   * Create an exception for data conflict with a message and parameters.
   *
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   */
  public DataConflictException(ExceptionParameters parameters, String message) {
    this(ErrorCode.INVALID_REQUEST, parameters, message);
  }
}
