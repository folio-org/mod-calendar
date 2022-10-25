package org.folio.calendar.testconstants;

import java.util.ArrayList;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.legacy.dto.PeriodDTO;

/**
 * A series of Periods for testing
 */
@UtilityClass
public class Periods {

  public static final PeriodDTO PERIOD_2021_01_01_TO_2021_01_01 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_01_01)
    .endDate(Dates.LDATE_2021_01_01)
    .build();
  public static final PeriodDTO PERIOD_2021_01_01_TO_2021_01_04 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_01_01)
    .endDate(Dates.LDATE_2021_01_04)
    .build();
  public static final PeriodDTO PERIOD_2021_01_01_TO_2021_04_30 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_01_01)
    .endDate(Dates.LDATE_2021_04_30)
    .build();
  public static final PeriodDTO PERIOD_2021_01_01_TO_2021_05_01 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_01_01)
    .endDate(Dates.LDATE_2021_05_01)
    .build();
  public static final PeriodDTO PERIOD_2021_01_01_TO_2021_07_04 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_01_01)
    .endDate(Dates.LDATE_2021_07_04)
    .build();
  public static final PeriodDTO PERIOD_2021_01_01_TO_2021_12_31 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_01_01)
    .endDate(Dates.LDATE_2021_12_31)
    .build();
  public static final PeriodDTO PERIOD_2021_01_02_TO_2021_01_02 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_01_02)
    .endDate(Dates.LDATE_2021_01_02)
    .build();
  public static final PeriodDTO PERIOD_2021_03_16_TO_2021_03_16 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_03_16)
    .endDate(Dates.LDATE_2021_03_16)
    .build();
  public static final PeriodDTO PERIOD_2021_03_16_TO_2021_04_30 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_03_16)
    .endDate(Dates.LDATE_2021_04_30)
    .build();
  public static final PeriodDTO PERIOD_2021_03_16_TO_2021_05_01 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_03_16)
    .endDate(Dates.LDATE_2021_05_01)
    .build();
  public static final PeriodDTO PERIOD_2021_04_30_TO_2021_04_30 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_04_30)
    .endDate(Dates.LDATE_2021_04_30)
    .build();
  public static final PeriodDTO PERIOD_2021_04_30_TO_2021_09_22 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_04_30)
    .endDate(Dates.LDATE_2021_09_22)
    .build();
  public static final PeriodDTO PERIOD_2021_05_01_TO_2021_09_22 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_05_01)
    .endDate(Dates.LDATE_2021_09_22)
    .build();
  public static final PeriodDTO PERIOD_2021_07_04_TO_2021_09_22 = PeriodDTO
    .builder()
    .startDate(Dates.LDATE_2021_07_04)
    .endDate(Dates.LDATE_2021_09_22)
    .build();

  public static final PeriodDTO PERIOD_WITH_NO_OPENINGS_NOR_EXCEPTIONS = PERIOD_2021_01_01_TO_2021_12_31
    .withId(UUIDs.UUID_A)
    .withServicePointId(UUIDs.UUID_0)
    .withName(Names.NAME_1)
    .withOpeningDays(new ArrayList<>());

  public static final PeriodDTO PERIOD_FULL_EXAMPLE_A = PERIOD_2021_01_01_TO_2021_12_31
    .withId(UUIDs.UUID_A)
    .withServicePointId(UUIDs.UUID_0)
    .withName(Names.NAME_1)
    .withOpeningDays(
      Arrays.asList(
        OpeningDayRelativeConstants.SUNDAY_OPEN_ALL_DAY,
        OpeningDayRelativeConstants.MONDAY_OPEN_04_00_TO_14_59,
        OpeningDayRelativeConstants.TUESDAY_OPEN_ALL_DAY,
        OpeningDayRelativeConstants.WEDNESDAY_OPEN_23_00_TO_23_59,
        OpeningDayRelativeConstants.THURSDAY_OPEN_ALL_DAY,
        OpeningDayRelativeConstants.FRIDAY_OPEN_ALL_DAY,
        OpeningDayRelativeConstants.SATURDAY_OPEN_ALL_DAY
      )
    );

  public static final PeriodDTO PERIOD_FULL_EXAMPLE_B = PERIOD_2021_01_01_TO_2021_12_31
    .withId(UUIDs.UUID_B)
    .withServicePointId(UUIDs.UUID_2)
    .withName(Names.NAME_2)
    .withOpeningDays(
      Arrays.asList(
        OpeningDayRelativeConstants.MONDAY_OPEN_00_00_TO_12_30_AND_23_00_TO_23_59,
        OpeningDayRelativeConstants.THURSDAY_OPEN_ALL_DAY
      )
    );

  public static final PeriodDTO PERIOD_FULL_EXAMPLE_C = PERIOD_2021_01_01_TO_2021_07_04
    .withId(UUIDs.UUID_C)
    .withServicePointId(UUIDs.UUID_1)
    .withName(Names.NAME_3)
    .withOpeningDays(
      Arrays.asList(
        OpeningDayRelativeConstants.MONDAY_OPEN_00_00_TO_12_30_AND_23_00_TO_23_59,
        OpeningDayRelativeConstants.THURSDAY_OPEN_ALL_DAY
      )
    );

  public static final PeriodDTO PERIOD_FULL_EXAMPLE_D = PERIOD_2021_05_01_TO_2021_09_22
    .withId(UUIDs.UUID_D)
    .withServicePointId(UUIDs.UUID_1)
    .withName(Names.NAME_4)
    .withOpeningDays(
      Arrays.asList(
        OpeningDayRelativeConstants.MONDAY_OPEN_00_00_TO_12_30_AND_23_00_TO_23_59,
        OpeningDayRelativeConstants.THURSDAY_OPEN_ALL_DAY
      )
    );

  public static final PeriodDTO PERIOD_FULL_EXAMPLE_E = PERIOD_2021_03_16_TO_2021_04_30
    .withId(UUIDs.UUID_E)
    .withServicePointId(UUIDs.UUID_1)
    .withName(Names.NAME_5)
    .withOpeningDays(
      Arrays.asList(
        OpeningDayRelativeConstants.MONDAY_OPEN_00_00_TO_12_30_AND_23_00_TO_23_59,
        OpeningDayRelativeConstants.THURSDAY_OPEN_ALL_DAY
      )
    );

  public static final PeriodDTO PERIOD_FULL_EXAMPLE_F = PERIOD_2021_01_01_TO_2021_04_30
    .withId(UUIDs.UUID_A)
    .withServicePointId(UUIDs.UUID_0)
    .withName(Names.NAME_1)
    .withOpeningDays(
      Arrays.asList(
        OpeningDayRelativeConstants.SUNDAY_OPEN_ALL_DAY,
        OpeningDayRelativeConstants.MONDAY_OPEN_04_00_TO_14_59,
        OpeningDayRelativeConstants.TUESDAY_OPEN_ALL_DAY,
        OpeningDayRelativeConstants.WEDNESDAY_OPEN_23_00_TO_23_59,
        OpeningDayRelativeConstants.THURSDAY_OPEN_ALL_DAY,
        OpeningDayRelativeConstants.FRIDAY_OPEN_ALL_DAY,
        OpeningDayRelativeConstants.SATURDAY_OPEN_ALL_DAY
      )
    );

  public static final PeriodDTO PERIOD_FULL_EXAMPLE_G = PERIOD_2021_05_01_TO_2021_09_22
    .withId(UUIDs.UUID_B)
    .withServicePointId(UUIDs.UUID_0)
    .withName(Names.NAME_2)
    .withOpeningDays(
      Arrays.asList(
        OpeningDayRelativeConstants.MONDAY_OPEN_00_00_TO_12_30_AND_23_00_TO_23_59,
        OpeningDayRelativeConstants.THURSDAY_OPEN_ALL_DAY
      )
    );

  public static final PeriodDTO PERIOD_FULL_EXCEPTIONAL_A = PERIOD_2021_01_01_TO_2021_12_31
    .withId(UUIDs.UUID_A)
    .withServicePointId(UUIDs.UUID_0)
    .withName(Names.NAME_1)
    .withOpeningDays(Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_CLOSED));

  public static final PeriodDTO PERIOD_FULL_EXCEPTIONAL_B = PERIOD_2021_01_01_TO_2021_01_04
    .withId(UUIDs.UUID_B)
    .withServicePointId(UUIDs.UUID_0)
    .withName(Names.NAME_2)
    .withOpeningDays(Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_ALL_DAY));

  public static final PeriodDTO PERIOD_FULL_EXCEPTIONAL_C = PERIOD_2021_01_01_TO_2021_01_04
    .withId(UUIDs.UUID_C)
    .withServicePointId(UUIDs.UUID_5)
    .withName(Names.NAME_3)
    .withOpeningDays(Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_04_00_TO_14_59));

  public static final PeriodDTO PERIOD_FULL_EXCEPTIONAL_D = PERIOD_2021_01_01_TO_2021_01_01
    .withId(UUIDs.UUID_D)
    .withServicePointId(UUIDs.UUID_9)
    .withName(Names.NAME_4)
    .withOpeningDays(Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_00_00_TO_14_59));

  public static final PeriodDTO PERIOD_FULL_EXCEPTIONAL_E = PERIOD_2021_01_01_TO_2021_01_04
    .withId(UUIDs.UUID_E)
    .withServicePointId(UUIDs.UUID_0)
    .withName(Names.NAME_1)
    .withOpeningDays(Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_CLOSED));

  public static final PeriodDTO PERIOD_FULL_EXCEPTIONAL_G = PERIOD_2021_07_04_TO_2021_09_22
    .withId(UUIDs.UUID_0)
    .withServicePointId(UUIDs.UUID_0)
    .withName(Names.NAME_3)
    .withOpeningDays(Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_CLOSED));
}
