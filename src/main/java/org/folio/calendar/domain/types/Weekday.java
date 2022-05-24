package org.folio.calendar.domain.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Days of the week enumeration
 */
@ToString
@AllArgsConstructor
public enum Weekday {
  /**
   * Sunday
   */
  SUNDAY("SUNDAY"),

  /**
   * Monday
   */
  MONDAY("MONDAY"),

  /**
   * Tuesday
   */
  TUESDAY("TUESDAY"),

  /**
   * Wednesday
   */
  WEDNESDAY("WEDNESDAY"),

  /**
   * Thursday
   */
  THURSDAY("THURSDAY"),

  /**
   * Friday
   */
  FRIDAY("FRIDAY"),

  /**
   * Saturday
   */
  SATURDAY("SATURDAY"),

  /**
   * This should never be used.  It is only for unit testing coverage of invalid values.
   */
  INVALID("INVALID");

  /**
   * All seven weekdays.  This is intentionally not public as no code should rely on a specific start/end of the week
   */
  protected static final Weekday[] WEEKDAYS = {
    Weekday.SUNDAY,
    Weekday.MONDAY,
    Weekday.TUESDAY,
    Weekday.WEDNESDAY,
    Weekday.THURSDAY,
    Weekday.FRIDAY,
    Weekday.SATURDAY,
  };

  private String value;

  /**
   * Get the weekday before the current one
   *
   * @return the day of the week before w
   */
  public Weekday previous() {
    switch (this) {
      case SUNDAY:
        return Weekday.SATURDAY;
      case MONDAY:
        return Weekday.SUNDAY;
      case TUESDAY:
        return Weekday.MONDAY;
      case WEDNESDAY:
        return Weekday.TUESDAY;
      case THURSDAY:
        return Weekday.WEDNESDAY;
      case FRIDAY:
        return Weekday.THURSDAY;
      case SATURDAY:
        return Weekday.FRIDAY;
      default:
        throw new IllegalArgumentException("Cannot get previous weekday of an invalid value");
    }
  }

  /**
   * Get the weekday after this one
   *
   * @return the day of the week after w
   */
  public Weekday next() {
    switch (this) {
      case SUNDAY:
        return Weekday.MONDAY;
      case MONDAY:
        return Weekday.TUESDAY;
      case TUESDAY:
        return Weekday.WEDNESDAY;
      case WEDNESDAY:
        return Weekday.THURSDAY;
      case THURSDAY:
        return Weekday.FRIDAY;
      case FRIDAY:
        return Weekday.SATURDAY;
      case SATURDAY:
        return Weekday.SUNDAY;
      default:
        throw new IllegalArgumentException("Cannot get next weekday of an invalid value");
    }
  }

  /**
   * Get the range of weekdays between two other weekdays, inclusive.
   *
   * @param start the start {@link org.folio.calendar.domain.dto.Weekday Weekday}
   * @param end the end {@link org.folio.calendar.domain.dto.Weekday Weekday}
   * @return a {@link java.util.List List} of weekdays between the start and end weekdays, inclusive
   */
  public static List<Weekday> getRange(Weekday start, Weekday end) {
    List<Weekday> list = new ArrayList<>();

    boolean started = false;

    // iterating twice to guarantee a start -> end range without wrapping
    for (int i = 0;; i++) {
      if (WEEKDAYS[i % WEEKDAYS.length] == start) {
        started = true;
      }
      if (started) {
        list.add(WEEKDAYS[i % WEEKDAYS.length]);
        if (WEEKDAYS[i % WEEKDAYS.length] == end) {
          break;
        }
      }
    }

    return list;
  }

  /**
   * Convert a {@link java.time.LocalDate LocalDate} to a {@code Weekday}
   *
   * @param date the date to get the {@code Weekday} of
   * @return the corresponding {@code Weekday}
   */
  public static Weekday from(LocalDate date) {
    switch (date.getDayOfWeek()) {
      case SUNDAY:
        return Weekday.SUNDAY;
      case MONDAY:
        return Weekday.MONDAY;
      case TUESDAY:
        return Weekday.TUESDAY;
      case WEDNESDAY:
        return Weekday.WEDNESDAY;
      case THURSDAY:
        return Weekday.THURSDAY;
      case FRIDAY:
        return Weekday.FRIDAY;
      case SATURDAY:
      default:
        return Weekday.SATURDAY;
    }
  }

  /**
   * Get a set of all weekdays.  The order of these elements is not guaranteed
   */
  public static Set<Weekday> getAll() {
    return Set.of(WEEKDAYS);
  }

  @JsonCreator
  public static Weekday from(String value) {
    for (Weekday b : Weekday.values()) {
      if (b.value.equals(value.toUpperCase())) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }

  @JsonValue
  public String getValue() {
    return this.value;
  }
}
