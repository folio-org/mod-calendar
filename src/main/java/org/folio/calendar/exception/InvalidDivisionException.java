package org.folio.calendar.exception;

import org.folio.calendar.domain.dto.ErrorCode;

/**
 * An exception thrown by the /hello POST endpoint where an invalid parameter was passed, causing the inability to divide.
 */
public class InvalidDivisionException extends AbstractCalendarException {

  /**
   * Create an exception for when the /hello POST endpoint fails to divide
   *
   * @param e the causing {@link java.lang.ArithmeticException ArithmeticException}
   * @param parameters the parameters (as {@link org.folio.calendar.exception.ExceptionParameters ExceptionParameters}) which caused the error
   */
  public InvalidDivisionException(ArithmeticException e, ExceptionParameters parameters) {
    super(
      e,
      parameters,
      AbstractCalendarException.DEFAULT_STATUS_CODE,
      ErrorCode.BAD_ARITHMETIC,
      e.getMessage()
    );
  }
}
