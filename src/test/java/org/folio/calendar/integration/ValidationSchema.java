package org.folio.calendar.integration;

/**
 * Enum for different types of validation schemas, either none or regular
 */
public enum ValidationSchema {
  NONE("NO_VALIDATION"),
  REGULAR("VALIDATE_API");

  public String value;

  ValidationSchema(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
