package org.folio.calendar.utils;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.exparity.hamcrest.date.LocalDateTimeMatchers.within;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
   * Get the current date/time.  Contrary to the name, this does not return an Instant but instead
   * an equivalent LocalDateTime in UTC due to lacking support in hamcrest-date.
   *
   * @return the current date/time as a LocalDateTime
   * @see https://github.com/eXparity/hamcrest-date/issues/37
   */
  public static final LocalDateTime getCurrentInstant() {
    return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
  }

  /**
   * Parse a timestamp, returning a LocalDateTime.  Instant would be preferred but is not implemented
   * due to lacking support in hamcrest-date
   *
   * @return the current date/time as a LocalDateTime
   * @see https://github.com/eXparity/hamcrest-date/issues/37
   */
  public static LocalDateTime parseTimestamp(String input) {
    Instant instant = Instant.from(TIMESTAMP_FORMATTER.parse(input));
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
  }

  /**
   * Create a Matcher for a LocalDateTime to compare to the current instant.  Instant would be
   * preferred but is not implemented due to lacking support in hamcrest-date
   *
   * @return the current date/time as a LocalDateTime
   * @see https://github.com/eXparity/hamcrest-date/issues/37
   */
  public static TemporalMatcher<LocalDateTime> isCurrentInstant() {
    return within(1, MINUTES, getCurrentInstant());
  }
}
