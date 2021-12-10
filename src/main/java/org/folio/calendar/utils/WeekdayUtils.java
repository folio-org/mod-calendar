package org.folio.calendar.utils;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.Weekday;

@UtilityClass
public class WeekdayUtils {

  /**
   * Get the weekday before the provided one
   * @param w a weekday
   * @return the day of the week before w
   */
  public static Weekday previous(Weekday w) {
    switch (w) {
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
        throw new IllegalArgumentException(
          "Weekday passed to WeekdayUtils::previous somehow is not in the enum"
        );
    }
  }

  /**
   * Get the weekday after the provided one
   * @param w a weekday
   * @return the day of the week after w
   */
  public static Weekday next(Weekday w) {
    switch (w) {
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
        throw new IllegalArgumentException(
          "Weekday passed to WeekdayUtils::next somehow is not in the enum"
        );
    }
  }
}
