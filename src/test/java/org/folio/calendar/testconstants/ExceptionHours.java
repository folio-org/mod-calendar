package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.entity.ExceptionHour;

@UtilityClass
public class ExceptionHours {

  public static final ExceptionHour CLOSED_ALL_YEAR = ExceptionHour
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_12_31)
    .startTime(null)
    .endTime(null)
    .build();
  public static final ExceptionHour OPEN_ALL_DAY_JAN_1_THRU_JAN_4 = ExceptionHour
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_04)
    .startTime(Times.TIME_00_00)
    .endTime(Times.TIME_23_59)
    .build();
  public static final ExceptionHour OPEN_00_00_TO_14_59_JAN_1 = ExceptionHour
    .builder()
    .startDate(Dates.DATE_2021_01_01)
    .endDate(Dates.DATE_2021_01_01)
    .startTime(Times.TIME_00_00)
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
}
