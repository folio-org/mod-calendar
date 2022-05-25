package org.folio.calendar.exception;

import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.error.CalendarOverlapErrorData;
import org.springframework.http.HttpStatus;

/**
 * An exception thrown whenever new data conflicts (e.g. duplicate IDs)
 */
public class DataConflictException extends AbstractCalendarException {

  public static final HttpStatus DEFAULT_STATUS_CODE = HttpStatus.CONFLICT;

  /**
   * Create an exception for data conflict with a message, error code, parameters, and extra data
   *
   * @param errorCode  The error code to report (defined by ErrorResponse)
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   * @param data       Data about the exception in a machine-readable format
   */
  public DataConflictException(
    ErrorCodeDTO errorCode,
    ExceptionParameters parameters,
    String message,
    CalendarOverlapErrorData data
  ) {
    super(null, parameters, DEFAULT_STATUS_CODE, errorCode, message, data);
  }
}
