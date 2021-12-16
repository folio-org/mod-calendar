package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.Weekday;
import org.folio.calendar.domain.entity.NormalOpening;

@UtilityClass
public class NormalOpenings {

  public static final NormalOpening SUNDAY_MONDAY_ALL_DAY = NormalOpening
    .builder()
    .startDay(Weekday.SUNDAY)
    .startTime(Times.TIME_00_00)
    .endDay(Weekday.MONDAY)
    .endTime(Times.TIME_23_59)
    .build();

  public static final NormalOpening MONDAY_ALL_DAY = NormalOpening
    .builder()
    .startDay(Weekday.MONDAY)
    .startTime(Times.TIME_00_00)
    .endDay(Weekday.MONDAY)
    .endTime(Times.TIME_23_59)
    .build();

  public static final NormalOpening MONDAY_04_00_TO_14_59 = NormalOpening
    .builder()
    .startDay(Weekday.MONDAY)
    .startTime(Times.TIME_04_00)
    .endDay(Weekday.MONDAY)
    .endTime(Times.TIME_14_59)
    .build();

  public static final NormalOpening MONDAY_04_00_TO_23_59 = NormalOpening
    .builder()
    .startDay(Weekday.MONDAY)
    .startTime(Times.TIME_04_00)
    .endDay(Weekday.MONDAY)
    .endTime(Times.TIME_23_59)
    .build();

  public static final NormalOpening MONDAY_04_00_TO_TUESDAY_12_30 = NormalOpening
    .builder()
    .startDay(Weekday.MONDAY)
    .startTime(Times.TIME_04_00)
    .endDay(Weekday.TUESDAY)
    .endTime(Times.TIME_12_30)
    .build();

  public static final NormalOpening TUESDAY_00_00_TO_12_30 = NormalOpening
    .builder()
    .startDay(Weekday.TUESDAY)
    .startTime(Times.TIME_00_00)
    .endDay(Weekday.TUESDAY)
    .endTime(Times.TIME_12_30)
    .build();

  public static final NormalOpening WEDNESDAY_23_00_TO_SUNDAY_23_59 = NormalOpening
    .builder()
    .startDay(Weekday.WEDNESDAY)
    .startTime(Times.TIME_23_00)
    .endDay(Weekday.SUNDAY)
    .endTime(Times.TIME_23_59)
    .build();
}
