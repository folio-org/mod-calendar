package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.Period;

/**
 * A series of Periods for testing
 */
@UtilityClass
public class Periods {

  public static final Period PERIOD_2021_01_01_TO_2021_01_01 = Period
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_01)
    .build();
  public static final Period PERIOD_2021_01_01_TO_2021_04_30 = Period
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_04_30)
    .build();
  public static final Period PERIOD_2021_01_01_TO_2021_05_01 = Period
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_05_01)
    .build();
  public static final Period PERIOD_2021_01_01_TO_2021_07_04 = Period
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_07_04)
    .build();
  public static final Period PERIOD_2021_01_02_TO_2021_01_02 = Period
    .builder()
    .startDate(Dates.DATE_2021_01_02)
    .endDate(Dates.DATE_2021_01_02)
    .build();
  public static final Period PERIOD_2021_03_16_TO_2021_03_16 = Period
    .builder()
    .startDate(Dates.DATE_2021_03_16)
    .endDate(Dates.DATE_2021_03_16)
    .build();
  public static final Period PERIOD_2021_03_16_TO_2021_04_30 = Period
    .builder()
    .startDate(Dates.DATE_2021_03_16)
    .endDate(Dates.DATE_2021_04_30)
    .build();
  public static final Period PERIOD_2021_03_16_TO_2021_05_01 = Period
    .builder()
    .startDate(Dates.DATE_2021_03_16)
    .endDate(Dates.DATE_2021_05_01)
    .build();
  public static final Period PERIOD_2021_04_30_TO_2021_04_30 = Period
    .builder()
    .startDate(Dates.DATE_2021_04_30)
    .endDate(Dates.DATE_2021_04_30)
    .build();
  public static final Period PERIOD_2021_04_30_TO_2021_09_22 = Period
    .builder()
    .startDate(Dates.DATE_2021_04_30)
    .endDate(Dates.DATE_2021_09_22)
    .build();
  public static final Period PERIOD_2021_05_01_TO_2021_09_22 = Period
    .builder()
    .startDate(Dates.DATE_2021_05_01)
    .endDate(Dates.DATE_2021_09_22)
    .build();
  public static final Period PERIOD_2021_07_04_TO_2021_09_22 = Period
    .builder()
    .startDate(Dates.DATE_2021_07_04)
    .endDate(Dates.DATE_2021_09_22)
    .build();
}
