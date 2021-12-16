package org.folio.calendar.testutils;

import java.time.LocalTime;

/**
 * A series of LocalTime and related conversions for testing
 */
public class TimeConstants {

  public static final LocalTime TIME_00_00 = LocalTime.of(0, 0);
  public static final LocalTime TIME_04_00 = LocalTime.of(4, 0);
  public static final LocalTime TIME_12_30 = LocalTime.of(12, 30);
  public static final LocalTime TIME_14_00 = LocalTime.of(14, 0);
  public static final LocalTime TIME_23_00 = LocalTime.of(23, 0);
  public static final LocalTime TIME_23_59 = LocalTime.of(23, 59);

  public static final String TIME_00_00_STRING_HH_mm = "00:00";
  public static final String TIME_04_00_STRING_HH_mm = "04:00";
  public static final String TIME_12_30_STRING_HH_mm = "12:30";
  public static final String TIME_14_00_STRING_HH_mm = "14:00";
  public static final String TIME_23_00_STRING_HH_mm = "23:00";
  public static final String TIME_23_59_STRING_HH_mm = "23:59";
}
