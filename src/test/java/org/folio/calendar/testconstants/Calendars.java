package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.entity.Calendar;

/**
 * A series of Calendars for testing
 */
@UtilityClass
public class Calendars {

  public static final Calendar CALENDAR_2021_01_01_TO_2021_01_01 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_01)
    .build();
  public static final Calendar CALENDAR_2021_01_01_TO_2021_04_31 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_04_31)
    .build();
  public static final Calendar CALENDAR_2021_01_01_TO_2021_05_01 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_05_01)
    .build();
  public static final Calendar CALENDAR_2021_03_16_TO_2021_05_01 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_03_16)
    .endDate(Dates.DATE_2021_05_01)
    .build();
  public static final Calendar CALENDAR_2021_03_16_TO_2021_04_31 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_03_16)
    .endDate(Dates.DATE_2021_04_31)
    .build();
  public static final Calendar CALENDAR_2021_05_01_TO_2021_09_22 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_05_01)
    .endDate(Dates.DATE_2021_09_22)
    .build();
  public static final Calendar CALENDAR_2021_04_31_TO_2021_09_22 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_04_31)
    .endDate(Dates.DATE_2021_09_22)
    .build();
}
