package org.folio.calendar.integration;

/**
 * Enum for potential usage of the database in integration tests.
 * Used to dirty the cache as needed
 */
public enum DatabaseUsage {
  NONE("DB_USAGE_NONE"),
  IDEMPOTENT("DB_USAGE_IDEMPOTENT"),
  NORMAL("DB_USAGE_NORMAL"); // default

  public String value;

  DatabaseUsage(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
