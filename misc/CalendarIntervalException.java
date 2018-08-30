package org.folio.rest.impl;

/**
 * @author mtornai
 */
public class CalendarIntervalException extends Exception {
  public CalendarIntervalException() {
  }

  public CalendarIntervalException(String message) {
    super(message);
  }

  public CalendarIntervalException(String message, Throwable cause) {
    super(message, cause);
  }

  public CalendarIntervalException(Throwable cause) {
    super(cause);
  }

  public CalendarIntervalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
