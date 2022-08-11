package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.entity.ExceptionHour;

@UtilityClass
public class ExceptionHours {

  public static final ExceptionHour OPEN_ALL_DAY_JAN_1_THRU_JAN_4 = ExceptionHour
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .startTime(Times.TIME_00_00)
    .endDate(Dates.DATE_2021_01_04)
    .endTime(Times.TIME_23_59)
    .build();
  public static final ExceptionHour OPEN_ALL_DAY_JAN_1 = ExceptionHour
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .startTime(Times.TIME_00_00)
    .endDate(Dates.DATE_2021_01_01)
    .endTime(Times.TIME_23_59)
    .build();
  public static final ExceptionHour OPEN_00_00_TO_14_59_JAN_1 = ExceptionHour
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .startTime(Times.TIME_00_00)
    .endDate(Dates.DATE_2021_01_01)
    .endTime(Times.TIME_14_59)
    .build();
  public static final ExceptionHour OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2 = ExceptionHour
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .startTime(Times.TIME_00_00)
    .endDate(Dates.DATE_2021_01_02)
    .endTime(Times.TIME_14_59)
    .build();
  public static final ExceptionHour OPEN_00_00_TO_14_59_JAN_2_THRU_JAN_3 = ExceptionHour
    .builder()
    .startDate(Dates.DATE_2021_01_02)
    .startTime(Times.TIME_00_00)
    .endDate(Dates.DATE_2021_01_03)
    .endTime(Times.TIME_14_59)
    .build();
  public static final ExceptionHour OPEN_00_00_TO_14_59_JAN_3_THRU_JAN_4 = ExceptionHour
    .builder()
    .startDate(Dates.DATE_2021_01_03)
    .startTime(Times.TIME_00_00)
    .endDate(Dates.DATE_2021_01_04)
    .endTime(Times.TIME_14_59)
    .build();
  public static final ExceptionHour OPEN_04_00_TO_14_59_JAN_1 = OPEN_00_00_TO_14_59_JAN_1.withStartTime(
    Times.TIME_04_00
  );
  public static final ExceptionHour OPEN_04_00_TO_14_59_JAN_2 = OPEN_04_00_TO_14_59_JAN_1
    .withStartDate(Dates.DATE_2021_01_02)
    .withEndDate(Dates.DATE_2021_01_02);
  public static final ExceptionHour OPEN_04_00_TO_14_59_JAN_3 = OPEN_04_00_TO_14_59_JAN_1
    .withStartDate(Dates.DATE_2021_01_03)
    .withEndDate(Dates.DATE_2021_01_03);
  public static final ExceptionHour OPEN_04_00_TO_14_59_JAN_4 = OPEN_04_00_TO_14_59_JAN_1
    .withStartDate(Dates.DATE_2021_01_04)
    .withEndDate(Dates.DATE_2021_01_04);
  public static final ExceptionHour OPEN_15_00_TO_23_00_JAN_4 = OPEN_04_00_TO_14_59_JAN_4
    .withStartTime(Times.TIME_15_00)
    .withEndTime(Times.TIME_23_00);
  public static final ExceptionHour OPEN_18_12_TO_23_00_JAN_4 = OPEN_04_00_TO_14_59_JAN_4
    .withStartTime(Times.TIME_18_12)
    .withEndTime(Times.TIME_23_00);
  public static final ExceptionHour OPEN_15_00_TO_23_59_JAN_4 = OPEN_04_00_TO_14_59_JAN_4
    .withStartTime(Times.TIME_15_00)
    .withEndTime(Times.TIME_23_59);
  public static final ExceptionHour OPEN_ALL_DAY_MAR_16_TO_APR_30 = ExceptionHour
    .builder()
    .startDate(Dates.DATE_2021_03_16)
    .startTime(Times.TIME_00_00)
    .endDate(Dates.DATE_2021_04_30)
    .endTime(Times.TIME_23_59)
    .build();
}
