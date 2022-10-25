package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.legacy.dto.OpeningDayRelativeDTO;

@UtilityClass
public class OpeningDayRelativeConstants {

  public static final OpeningDayRelativeDTO MONDAY_CLOSED = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.CLOSED_ALL_DAY)
    .build();
  public static final OpeningDayRelativeDTO MONDAY_INVALID = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_INVALID)
    .build();
  public static final OpeningDayRelativeDTO MONDAY_OPEN_ALL_DAY = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelativeDTO MONDAY_OPEN_00_00_TO_12_30_AND_23_00_TO_23_59 = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59)
    .build();
  public static final OpeningDayRelativeDTO MONDAY_OPEN_04_00_TO_14_59 = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_04_00_TO_14_59)
    .build();
  public static final OpeningDayRelativeDTO MONDAY_OPEN_15_00_TO_23_59 = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_15_00_TO_23_59)
    .build();
  public static final OpeningDayRelativeDTO TUESDAY_OPEN_ALL_DAY = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.TUESDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelativeDTO TUESDAY_OPEN_00_00_TO_12_30 = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.TUESDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30)
    .build();
  public static final OpeningDayRelativeDTO WEDNESDAY_OPEN_23_00_TO_23_59 = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.WEDNESDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_23_00_TO_23_59)
    .build();
  public static final OpeningDayRelativeDTO THURSDAY_OPEN_ALL_DAY = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.THURSDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelativeDTO FRIDAY_OPEN_ALL_DAY = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.FRIDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelativeDTO SATURDAY_OPEN_ALL_DAY = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.SATURDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelativeDTO SUNDAY_OPEN_ALL_DAY = OpeningDayRelativeDTO
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.SUNDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();

  public static final OpeningDayRelativeDTO EXCEPTIONAL_CLOSED = OpeningDayRelativeDTO
    .builder()
    .weekdays(null)
    .openingDay(OpeningDayInfoRelativeConstants.EXCEPTIONAL_CLOSED_ALL_DAY)
    .build();
  public static final OpeningDayRelativeDTO EXCEPTIONAL_OPEN_ALL_DAY = OpeningDayRelativeDTO
    .builder()
    .weekdays(null)
    .openingDay(OpeningDayInfoRelativeConstants.EXCEPTIONAL_OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelativeDTO EXCEPTIONAL_OPEN_00_00_TO_14_59 = OpeningDayRelativeDTO
    .builder()
    .weekdays(null)
    .openingDay(OpeningDayInfoRelativeConstants.EXCEPTIONAL_OPEN_00_00_TO_14_59)
    .build();
  public static final OpeningDayRelativeDTO EXCEPTIONAL_OPEN_04_00_TO_14_59 = OpeningDayRelativeDTO
    .builder()
    .weekdays(null)
    .openingDay(OpeningDayInfoRelativeConstants.EXCEPTIONAL_OPEN_04_00_TO_14_59)
    .build();
  public static final OpeningDayRelativeDTO EXCEPTIONAL_INVALID_MULTIPLE_OPENINGS = OpeningDayRelativeDTO
    .builder()
    .weekdays(null)
    .openingDay(OpeningDayInfoRelativeConstants.EXCEPTIONAL_OPEN_00_00_TO_12_30_AND_23_00_TO_23_59)
    .build();
  public static final OpeningDayRelativeDTO EXCEPTIONAL_INVALID_NULL_OPENING = OpeningDayRelativeDTO
    .builder()
    .weekdays(null)
    .openingDay(OpeningDayInfoRelativeConstants.OPENING_NULL)
    .build();
}
