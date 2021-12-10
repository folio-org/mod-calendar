package org.folio.calendar.utils;

import java.time.LocalTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeConstants {

  /**
   * The first minute in the day
   */
  public static final LocalTime DAY_MIN = LocalTime.of(0, 0);
  /**
   * The last minute in the day
   */
  public static final LocalTime DAY_MAX = LocalTime.of(23, 59);
}
