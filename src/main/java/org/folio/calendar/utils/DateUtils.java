package org.folio.calendar.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.entity.Calendar;

/**
 * Utilities for times, dates, and date ranges
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
   * Get all LocalDates between two dates (inclusive)
   *
   * @param startDate the first date in the range
   * @param endDate the last date in the range
   * @return all dates between the start and end dates
   * @see <a href="https://stackoverflow.com/questions/40671689/how-to-build-a-list-of-localdate-from-a-given-range">
   * Stack Overflow post regarding LocalDate/Stream solutions</a>
   */
  public static List<LocalDate> getDateRange(LocalDate startDate, LocalDate endDate) {
    // the end date for datesUtil is exclusive; adding one makes it inclusive
    return startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
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
   * Check that two inclusive local time pairs overlap (within the same day)
   *
   * @param start1 Start of time range 1
   * @param end1 End of time range 1
   * @param start2 Start of time range 2
   * @param end2 End of time range 2
   * @return if they overlap
   */
  public static boolean overlaps(
    LocalTime start1,
    LocalTime end1,
    LocalTime start2,
    LocalTime end2
  ) {
    // False if: 2 starts after 1 OR 2 ends before 1 starts
    return !(start2.isAfter(end1) || end2.isBefore(start1));
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
   * @param period1 Period 1
   * @param period2 Period 2
   * @return if they overlap
   */
  public static boolean overlaps(Period period1, Period period2) {
    return overlaps(
      period1.getStartDate().getValue(),
      period1.getEndDate().getValue(),
      period2.getStartDate().getValue(),
      period2.getEndDate().getValue()
    );
  }

  /**
   * Check that a period and calendar overlap
   *
   * @param period Period
   * @param calendar Calendar
   * @return if they overlap
   */
  public static boolean overlaps(Period period, Calendar calendar) {
    return overlaps(calendar, period);
  }

  /**
   * Check that a calendar and period overlap
   *
   * @param calendar Calendar
   * @param period Period
   * @return if they overlap
   */
  public static boolean overlaps(Calendar calendar, Period period) {
    return overlaps(
      calendar.getStartDate(),
      calendar.getEndDate(),
      period.getStartDate().getValue(),
      period.getEndDate().getValue()
    );
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
   * Check that the given period overlaps with a list of others.  Note that this does not check overlaps within otherPeriods.
   *
   * @param period Period to check
   * @param otherPeriods List of other periods
   * @return the overlapped period, or null if no overlap
   */
  @CheckForNull
  public static Period overlapsPeriodList(Period period, Iterable<Period> otherPeriods) {
    for (Period other : otherPeriods) {
      if (overlaps(period, other)) {
        return other;
      }
    }
    return null;
  }

  /**
   * Check that the given period does not overlap with a list of Calendars
   *
   * @param period Period to check
   * @param calendars Calendars
   * @return the overlapped calendar, or null if no overlap
   */
  @CheckForNull
  public static Calendar overlapsCalendarList(Period period, Iterable<Calendar> calendars) {
    for (Calendar other : calendars) {
      if (overlaps(period, other)) {
        return other;
      }
    }
    return null;
  }

  /**
   * Convert a LocalTime to a string of the format HH:mm
   *
   * @param time the {@link java.time.LocalTime LocalTime} to convert
   * @return the formatted String (HH:mm)
   */
  public static String toTimeString(LocalTime time) {
    return time.format(TimeConstants.TIME_FORMATTER);
  }

  /**
   * Convert a string of the format HH:mm to a LocalTime
   *
   * @param time the formatted String (HH:mm)
   * @return a parsed {@link java.time.LocalTime LocalTime}
   */
  public static LocalTime fromTimeString(String time) {
    return LocalTime.parse(time, TimeConstants.TIME_FORMATTER);
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
