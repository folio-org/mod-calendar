package org.folio.calendar.exception;

import org.folio.calendar.domain.dto.ErrorResponse.ErrorCodeEnum;

/**
 * An exception thrown by the /hello POST endpoint where an invalid parameter was passed, causing the inability to divide.
 */
public class InvalidDivisionException extends AbstractCalendarException {

  /**
   * Create an exception for when the /hello POST endpoint fails to divide
   */
  public InvalidDivisionException(ArithmeticException e) {
    super(e, ErrorCodeEnum.HELLO_POST_BAD_ARITHMETIC, e.getMessage());
  }
}
