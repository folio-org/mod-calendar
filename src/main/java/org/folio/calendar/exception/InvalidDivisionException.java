package org.folio.calendar.exception;

import org.folio.calendar.domain.dto.ErrorCode;

/**
 * An exception thrown by the /hello POST endpoint where an invalid parameter was passed, causing the inability to divide.
 */
public class InvalidDivisionException extends AbstractCalendarException {

  /**
   * Create an exception for when the /hello POST endpoint fails to divide
   */
  public InvalidDivisionException(ArithmeticException e, ExceptionParameters parameters) {
    super(e, parameters, ErrorCode.BAD_ARITHMETIC, e.getMessage());
  }
}
