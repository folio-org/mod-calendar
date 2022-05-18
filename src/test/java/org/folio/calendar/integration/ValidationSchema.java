package org.folio.calendar.integration;

/**
 * Enum for different types of validation schemas, either none, legacy (/calendar), or opening_hours (new)
 */
public enum ValidationSchema {
  NONE("NO_VALIDATION"),
  LEGACY("VALIDATE_LEGACY_CALENDAR"),
  OPENING_HOURS("VALIDATE_NEW_OPENING_HOURS");

  public String value;

  ValidationSchema(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
