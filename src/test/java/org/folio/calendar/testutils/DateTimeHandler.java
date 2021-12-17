package org.folio.calendar.testutils;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.exparity.hamcrest.date.InstantMatchers.within;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;
import org.exparity.hamcrest.date.core.TemporalMatcher;

/**
 * Do some date/time-parsing related tasks
 */
@UtilityClass
public class DateTimeHandler {

  /**
   * Get timestamp as an Instant
   */
  public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_INSTANT;

  /**
   * Get the current date/time as an Instant
   *
   * @return the current date/time as an Instant
   */
  public static Instant getCurrentInstant() {
    return Instant.now();
  }

  /**
   * Parse a timestamp, returning an Instant
   *
   * @return the provided timestamp as an Instant
   */
  public static Instant parseTimestamp(String input) {
    return Instant.from(TIMESTAMP_FORMATTER.parse(input));
  }

  /**
   * Create a Matcher for an Instant to be compared to the current Instant
   *
   * @return a matcher for the current Instant, ± 1 minute
   */
  public static TemporalMatcher<Instant> isCurrentInstant() {
    return within(1, MINUTES, getCurrentInstant());
  }
}
