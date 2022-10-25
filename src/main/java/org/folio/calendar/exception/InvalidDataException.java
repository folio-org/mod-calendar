package org.folio.calendar.exception;

import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.error.ErrorData;
import org.springframework.http.HttpStatus;

/**
 * An exception thrown whenever invalid data is presented (such as empty fields, start date &gt; end date, etc)
 */
public class InvalidDataException extends AbstractCalendarException {

  /**
   * Create an exception for invalid data with a message, error code, parameters, and extra data.
   *
   * @param errorCode  The error code to report (defined by ErrorResponse)
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   * @param data       Additional data to support richer error rendering
   */
  public InvalidDataException(
    ErrorCodeDTO errorCode,
    ExceptionParameters parameters,
    String message,
    ErrorData data
  ) {
    super(null, parameters, HttpStatus.BAD_REQUEST, errorCode, message, data);
  }

  /**
   * Create an exception for invalid data with a message, error code, and parameters.
   *
   * @param errorCode  The error code to report (defined by ErrorResponse)
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   */
  public InvalidDataException(
    ErrorCodeDTO errorCode,
    ExceptionParameters parameters,
    String message
  ) {
    this(errorCode, parameters, message, null);
  }

  /**
   * Create an exception for invalid data with a message and parameters.
   *
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   */
  public InvalidDataException(ExceptionParameters parameters, String message) {
    this(ErrorCodeDTO.INVALID_REQUEST, parameters, message);
  }

  /**
   * Create an exception for invalid data with a cause, message, and parameters.
   *
   * @param cause      The cause of the exception
   * @param parameters Parameters which caused this issue
   * @param message    A string for the error message
   */
  public InvalidDataException(Throwable cause, ExceptionParameters parameters, String message) {
    super(cause, parameters, HttpStatus.BAD_REQUEST, ErrorCodeDTO.INVALID_REQUEST, message, null);
  }
}
