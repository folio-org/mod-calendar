package org.folio.calendar.utils;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.entity.Calendar;

/**
 * Utilities for dates and date ranges
 */
@UtilityClass
public class DateUtils {

  /**
   * Overrides the current date, as given by LocalDate.now() in getCurrentDate(), for testing purposes
   */
  @Getter
  @Setter
  protected static LocalDate currentDateOverride = null;

  /**
   * Get all LocalDates between two dates (inclusive).  If the range is
   * improper (ends before it starts), an empty list is returned.
   *
   * @param startDate the first date in the range
   * @param endDate the last date in the range
   * @return all dates between the start and end dates
   * @see <a href="https://stackoverflow.com/questions/40671689/how-to-build-a-list-of-localdate-from-a-given-range">
   * Stack Overflow post regarding LocalDate/Stream solutions</a>
   */
  public static List<LocalDate> getDateRange(LocalDate startDate, LocalDate endDate) {
    // the end date for datesUtil is exclusive; adding one makes it inclusive
    return getDateRangeStream(startDate, endDate).toList();
  }

  /**
   * Get all LocalDates between two dates (inclusive).  If the range is
   * improper (ends before it starts), an empty stream is returned.
   *
   * @param startDate the first date in the range
   * @param endDate the last date in the range
   * @return a stream providing all dates between the start and end dates
   * @see <a href="https://stackoverflow.com/questions/40671689/how-to-build-a-list-of-localdate-from-a-given-range">
   * Stack Overflow post regarding LocalDate/Stream solutions</a>
   */
  public static Stream<LocalDate> getDateRangeStream(LocalDate startDate, LocalDate endDate) {
    if (endDate.isBefore(startDate)) {
      return Stream.empty();
    }

    // the end date for datesUtil is exclusive; adding one makes it inclusive
    return startDate.datesUntil(endDate.plusDays(1));
  }

  /**
   * Check if a date is contained within a range
   * @param date the date to test
   * @param start the first date of the range, inclusive
   * @param end the last date of the range, inclusive
   * @return if the date is contained in the range
   */
  public static boolean contains(ChronoLocalDate date, ChronoLocalDate start, ChronoLocalDate end) {
    return !start.isAfter(date) && !end.isBefore(date);
  }

  /**
   * Check if a date range is contained within another (inclusive) range
   * @param testStart the first date of the range to test, inclusive
   * @param testEnd the last date of the range to test, inclusive
   * @param start the first date of the range, inclusive
   * @param end the last date of the range, inclusive
   * @return if the test range is contained within the other range
   */
  public static boolean containsRange(
    ChronoLocalDate testStart,
    ChronoLocalDate testEnd,
    ChronoLocalDate start,
    ChronoLocalDate end
  ) {
    return contains(testStart, start, end) && contains(testEnd, start, end);
  }

  /**
   * Check that two inclusive local date pairs overlap
   *
   * @param start1 Start of date range 1
   * @param end1 End of date range 1
   * @param start2 Start of date range 2
   * @param end2 End of date range 2
   * @return if they overlap
   */
  public static boolean overlaps(
    ChronoLocalDate start1,
    ChronoLocalDate end1,
    ChronoLocalDate start2,
    ChronoLocalDate end2
  ) {
    // False if: 2 starts after 1 OR 2 ends before 1 starts
    return !(start2.isAfter(end1) || end2.isBefore(start1));
  }

  /**
   * Check that two periods overlap
   *
   * @param calendar1 Calendar 1
   * @param calendar2 Calendar 2
   * @return if they overlap
   */
  public static boolean overlaps(Calendar calendar1, Calendar calendar2) {
    return overlaps(
      calendar1.getStartDate(),
      calendar1.getEndDate(),
      calendar2.getStartDate(),
      calendar2.getEndDate()
    );
  }

  /**
   * Get the current date
   * @return the current date
   */
  public static LocalDate getCurrentDate() {
    if (getCurrentDateOverride() == null) {
      return LocalDate.now();
    } else {
      return getCurrentDateOverride();
    }
  }

  /**
   * Get the smallest of two dates
   * @param a the first date
   * @param b the second date
   * @return the smaller date of the two
   */
  public static LocalDate min(LocalDate a, LocalDate b) {
    if (a == null) {
      return b;
    }
    if (b == null) {
      return a;
    }
    if (a.isBefore(b)) {
      return a;
    } else {
      return b;
    }
  }

  /**
   * Get the largest of two dates
   * @param a the first date
   * @param b the second date
   * @return the larger date of the two
   */
  public static LocalDate max(LocalDate a, LocalDate b) {
    if (a == null) {
      return b;
    }
    if (b == null) {
      return a;
    }
    if (a.isAfter(b)) {
      return a;
    } else {
      return b;
    }
  }
}
