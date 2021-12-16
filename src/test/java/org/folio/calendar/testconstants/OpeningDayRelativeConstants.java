package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayRelative;

@UtilityClass
public class OpeningDayRelativeConstants {

  public static final OpeningDayRelative MONDAY_CLOSED = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.CLOSED_ALL_DAY)
    .build();
  public static final OpeningDayRelative MONDAY_INVALID = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_INVALID)
    .build();
  public static final OpeningDayRelative MONDAY_OPEN_ALL_DAY = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelative MONDAY_OPEN_00_00_TO_12_30_AND_23_00_TO_23_59 = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59)
    .build();
  public static final OpeningDayRelative MONDAY_OPEN_04_00_TO_14_59 = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_04_00_TO_14_59)
    .build();
  public static final OpeningDayRelative MONDAY_OPEN_15_00_TO_23_59 = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.MONDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_15_00_TO_23_59)
    .build();
  public static final OpeningDayRelative TUESDAY_OPEN_ALL_DAY = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.TUESDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelative TUESDAY_OPEN_00_00_TO_12_30 = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.TUESDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30)
    .build();
  public static final OpeningDayRelative WEDNESDAY_OPEN_23_00_TO_23_59 = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.WEDNESDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_23_00_TO_23_59)
    .build();
  public static final OpeningDayRelative THURSDAY_OPEN_ALL_DAY = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.THURSDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelative FRIDAY_OPEN_ALL_DAY = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.FRIDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelative SATURDAY_OPEN_ALL_DAY = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.SATURDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
  public static final OpeningDayRelative SUNDAY_OPEN_ALL_DAY = OpeningDayRelative
    .builder()
    .weekdays(OpeningDayRelativeWeekdaysConstants.SUNDAY)
    .openingDay(OpeningDayInfoRelativeConstants.OPEN_ALL_DAY)
    .build();
}
