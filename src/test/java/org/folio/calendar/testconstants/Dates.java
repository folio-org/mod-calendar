package org.folio.calendar.testconstants;

import java.time.LocalDate;
import java.time.Month;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.LegacyPeriodDate;

/**
 * A series of LocalDates for testing
 */
@UtilityClass
public class Dates {

  public static final LocalDate DATE_2021_01_01 = LocalDate.of(2021, Month.JANUARY, 1);
  public static final LocalDate DATE_2021_01_02 = LocalDate.of(2021, Month.JANUARY, 2);
  public static final LocalDate DATE_2021_01_03 = LocalDate.of(2021, Month.JANUARY, 3);
  public static final LocalDate DATE_2021_01_04 = LocalDate.of(2021, Month.JANUARY, 4);
  public static final LocalDate DATE_2021_03_16 = LocalDate.of(2021, Month.MARCH, 16);
  public static final LocalDate DATE_2021_04_30 = LocalDate.of(2021, Month.APRIL, 30);
  public static final LocalDate DATE_2021_05_01 = LocalDate.of(2021, Month.MAY, 1);
  public static final LocalDate DATE_2021_07_04 = LocalDate.of(2021, Month.JULY, 4);
  public static final LocalDate DATE_2021_09_22 = LocalDate.of(2021, Month.SEPTEMBER, 22);
  public static final LocalDate DATE_2021_11_31 = LocalDate.of(2021, Month.NOVEMBER, 30);
  public static final LocalDate DATE_2021_12_07 = LocalDate.of(2021, Month.DECEMBER, 7);
  public static final LocalDate DATE_2021_12_30 = LocalDate.of(2021, Month.DECEMBER, 30);
  public static final LocalDate DATE_2021_12_31 = LocalDate.of(2021, Month.DECEMBER, 31);

  public static final LegacyPeriodDate LDATE_2021_01_01 = LegacyPeriodDate.from(DATE_2021_01_01);
  public static final LegacyPeriodDate LDATE_2021_01_02 = LegacyPeriodDate.from(DATE_2021_01_02);
  public static final LegacyPeriodDate LDATE_2021_01_03 = LegacyPeriodDate.from(DATE_2021_01_03);
  public static final LegacyPeriodDate LDATE_2021_01_04 = LegacyPeriodDate.from(DATE_2021_01_04);
  public static final LegacyPeriodDate LDATE_2021_03_16 = LegacyPeriodDate.from(DATE_2021_03_16);
  public static final LegacyPeriodDate LDATE_2021_04_30 = LegacyPeriodDate.from(DATE_2021_04_30);
  public static final LegacyPeriodDate LDATE_2021_05_01 = LegacyPeriodDate.from(DATE_2021_05_01);
  public static final LegacyPeriodDate LDATE_2021_07_04 = LegacyPeriodDate.from(DATE_2021_07_04);
  public static final LegacyPeriodDate LDATE_2021_09_22 = LegacyPeriodDate.from(DATE_2021_09_22);
  public static final LegacyPeriodDate LDATE_2021_11_31 = LegacyPeriodDate.from(DATE_2021_11_31);
  public static final LegacyPeriodDate LDATE_2021_12_07 = LegacyPeriodDate.from(DATE_2021_12_07);
  public static final LegacyPeriodDate LDATE_2021_12_30 = LegacyPeriodDate.from(DATE_2021_12_30);
  public static final LegacyPeriodDate LDATE_2021_12_31 = LegacyPeriodDate.from(DATE_2021_12_31);
}
