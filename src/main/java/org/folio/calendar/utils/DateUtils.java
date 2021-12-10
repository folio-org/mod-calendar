package org.folio.calendar.utils;

import java.time.chrono.ChronoLocalDate;
import javax.annotation.CheckForNull;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.entity.Calendar;

@UtilityClass
public class DateUtils {

  /**
   * Check that two inclusive local date pairs overlap
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
   * @param period1 Period 1
   * @param period2 Period 2
   * @return if they overlap
   */
  public static boolean overlaps(Period period1, Period period2) {
    return overlaps(
      period1.getStartDate(),
      period1.getEndDate(),
      period2.getStartDate(),
      period2.getEndDate()
    );
  }

  /**
   * Check that a period and calendar overlap
   * @param period Period
   * @param calendar Calendar
   * @return if they overlap
   */
  public static boolean overlaps(Period period, Calendar calendar) {
    return overlaps(calendar, period);
  }

  /**
   * Check that a calendar and period overlap
   * @param calendar Calendar
   * @param period Period
   * @return if they overlap
   */
  public static boolean overlaps(Calendar calendar, Period period) {
    return overlaps(
      calendar.getStartDate(),
      calendar.getEndDate(),
      period.getStartDate(),
      period.getEndDate()
    );
  }

  /**
   * Check that two periods overlap
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
   * @param period Period to check
   * @param otherPeriods List of other periods
   * @return if they overlap
   */
  public static boolean overlapsPeriodList(Period period, Iterable<Period> otherPeriods) {
    for (Period other : otherPeriods) {
      if (overlaps(period, other)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check that the given period does not overlap with a list of Calendars
   * @param period Period to check
   * @param calendars Calendars
   * @return the overlapped calendar
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
}
