package org.folio.calendar.testconstants;

import java.util.Set;
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
  public static final Calendar CALENDAR_2021_01_01_TO_2021_01_04 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_04)
    .build();
  public static final Calendar CALENDAR_2021_01_01_TO_2021_04_30 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_04_30)
    .build();
  public static final Calendar CALENDAR_2021_01_01_TO_2021_05_01 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_05_01)
    .build();
  public static final Calendar CALENDAR_2021_01_01_TO_2021_07_04 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_07_04)
    .build();
  public static final Calendar CALENDAR_2021_01_01_TO_2021_12_31 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_12_31)
    .build();
  public static final Calendar CALENDAR_2021_01_02_TO_2021_01_02 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_01_02)
    .endDate(Dates.DATE_2021_01_02)
    .build();
  public static final Calendar CALENDAR_2021_03_16_TO_2021_03_16 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_03_16)
    .endDate(Dates.DATE_2021_03_16)
    .build();
  public static final Calendar CALENDAR_2021_03_16_TO_2021_04_30 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_03_16)
    .endDate(Dates.DATE_2021_04_30)
    .build();
  public static final Calendar CALENDAR_2021_03_16_TO_2021_05_01 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_03_16)
    .endDate(Dates.DATE_2021_05_01)
    .build();
  public static final Calendar CALENDAR_2021_04_30_TO_2021_04_30 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_04_30)
    .endDate(Dates.DATE_2021_04_30)
    .build();
  public static final Calendar CALENDAR_2021_04_30_TO_2021_09_22 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_04_30)
    .endDate(Dates.DATE_2021_09_22)
    .build();
  public static final Calendar CALENDAR_2021_05_01_TO_2021_09_22 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_05_01)
    .endDate(Dates.DATE_2021_09_22)
    .build();
  public static final Calendar CALENDAR_2021_07_04_TO_2021_09_22 = Calendar
    .builder()
    .startDate(Dates.DATE_2021_07_04)
    .endDate(Dates.DATE_2021_09_22)
    .build();

  public static final Calendar CALENDAR_WITH_NO_SERVICE_POINTS = CALENDAR_2021_01_01_TO_2021_01_01.withServicePoints(
    Set.of()
  );

  public static final Calendar CALENDAR_WITH_TWO_SERVICE_POINTS = CALENDAR_2021_01_01_TO_2021_01_01
    .withId(UUIDs.UUID_0)
    .withServicePoints(
      Set.of(
        ServicePointCalendarAssignments.ASSIGNMENT_SP_0_TO_CAL_0,
        ServicePointCalendarAssignments.ASSIGNMENT_SP_1_TO_CAL_0
      )
    );

  public static final Calendar CALENDAR_FULL_EXAMPLE_A = CALENDAR_2021_01_01_TO_2021_12_31
    .withId(UUIDs.UUID_A)
    .withServicePoints(Set.of(ServicePointCalendarAssignments.ASSIGNMENT_SP_0_TO_CAL_A))
    .withName(Names.NAME_1)
    .withNormalHours(
      Set.of(
        NormalOpenings.MONDAY_04_00_TO_14_59,
        NormalOpenings.TUESDAY_ALL_DAY,
        NormalOpenings.WEDNESDAY_23_00_TO_SUNDAY_23_59
      )
    );

  public static final Calendar CALENDAR_FULL_EXAMPLE_B = CALENDAR_2021_01_01_TO_2021_12_31
    .withId(UUIDs.UUID_B)
    .withServicePoints(Set.of(ServicePointCalendarAssignments.ASSIGNMENT_SP_2_TO_CAL_B))
    .withName(Names.NAME_2)
    .withNormalHours(
      Set.of(
        NormalOpenings.MONDAY_00_00_TO_12_30,
        NormalOpenings.MONDAY_23_00_TO_23_59,
        NormalOpenings.THURSDAY_ALL_DAY
      )
    );

  public static final Calendar CALENDAR_FULL_EXAMPLE_C = CALENDAR_2021_01_01_TO_2021_07_04
    .withId(UUIDs.UUID_C)
    .withServicePoints(Set.of(ServicePointCalendarAssignments.ASSIGNMENT_SP_1_TO_CAL_C))
    .withName(Names.NAME_3)
    .withNormalHours(
      Set.of(
        NormalOpenings.MONDAY_00_00_TO_12_30,
        NormalOpenings.MONDAY_23_00_TO_23_59,
        NormalOpenings.THURSDAY_ALL_DAY
      )
    );

  public static final Calendar CALENDAR_FULL_EXAMPLE_D = CALENDAR_2021_05_01_TO_2021_09_22
    .withId(UUIDs.UUID_D)
    .withServicePoints(Set.of(ServicePointCalendarAssignments.ASSIGNMENT_SP_1_TO_CAL_D))
    .withName(Names.NAME_4)
    .withNormalHours(
      Set.of(
        NormalOpenings.MONDAY_00_00_TO_12_30,
        NormalOpenings.MONDAY_23_00_TO_23_59,
        NormalOpenings.THURSDAY_ALL_DAY
      )
    );

  public static final Calendar CALENDAR_FULL_EXAMPLE_E = CALENDAR_2021_03_16_TO_2021_04_30
    .withId(UUIDs.UUID_E)
    .withServicePoints(Set.of(ServicePointCalendarAssignments.ASSIGNMENT_SP_1_TO_CAL_E))
    .withName(Names.NAME_5)
    .withNormalHours(
      Set.of(
        NormalOpenings.MONDAY_00_00_TO_12_30,
        NormalOpenings.MONDAY_23_00_TO_23_59,
        NormalOpenings.THURSDAY_ALL_DAY
      )
    );

  public static final Calendar CALENDAR_WITH_NORMAL_HOURS_AND_EXCEPTIONS = CALENDAR_FULL_EXAMPLE_B.withExceptions(
    Set.of(ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4_CALENDAR_B)
  );

  public static final Calendar CALENDAR_WITH_MULTIPLE_EXCEPTIONS = CALENDAR_FULL_EXAMPLE_B
    .withNormalHours(Set.of())
    .withExceptions(
      Set.of(
        ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4_CALENDAR_B,
        ExceptionRanges.OPEN_ALL_DAY_JAN_1_THRU_JAN_4_CALENDAR_B
      )
    );

  public static final Calendar CALENDAR_FULL_EXCEPTIONAL_A = CALENDAR_2021_01_01_TO_2021_12_31
    .withId(UUIDs.UUID_A)
    .withServicePoints(Set.of(ServicePointCalendarAssignments.ASSIGNMENT_SP_0_TO_CAL_A))
    .withName(Names.NAME_1)
    .withNormalHours(Set.of())
    .withExceptions(
      Set.of(ExceptionRanges.CLOSED_ALL_YEAR_CALENDAR_0.withCalendarId(UUIDs.UUID_A))
    );

  public static final Calendar CALENDAR_FULL_EXCEPTIONAL_B = CALENDAR_2021_01_01_TO_2021_01_04
    .withId(UUIDs.UUID_B)
    .withServicePoints(Set.of(ServicePointCalendarAssignments.ASSIGNMENT_SP_0_TO_CAL_B))
    .withName(Names.NAME_2)
    .withNormalHours(Set.of())
    .withExceptions(
      Set.of(ExceptionRanges.OPEN_ALL_DAY_JAN_1_THRU_JAN_4_CALENDAR_A.withCalendarId(UUIDs.UUID_B))
    );

  public static final Calendar CALENDAR_FULL_EXCEPTIONAL_C = CALENDAR_2021_01_01_TO_2021_01_04
    .withId(UUIDs.UUID_C)
    .withServicePoints(Set.of(ServicePointCalendarAssignments.ASSIGNMENT_SP_5_TO_CAL_C))
    .withName(Names.NAME_3)
    .withNormalHours(Set.of())
    .withExceptions(
      Set.of(
        ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4_CALENDAR_B.withCalendarId(UUIDs.UUID_C)
      )
    );

  public static final Calendar CALENDAR_FULL_EXCEPTIONAL_D = CALENDAR_2021_01_01_TO_2021_01_01
    .withId(UUIDs.UUID_D)
    .withServicePoints(Set.of(ServicePointCalendarAssignments.ASSIGNMENT_SP_9_TO_CAL_D))
    .withName(Names.NAME_4)
    .withNormalHours(Set.of())
    .withExceptions(
      Set.of(ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1_CALENDAR_D.withCalendarId(UUIDs.UUID_D))
    );
}
