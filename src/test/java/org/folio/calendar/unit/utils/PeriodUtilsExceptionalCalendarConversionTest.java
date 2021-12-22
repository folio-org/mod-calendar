package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class PeriodUtilsExceptionalCalendarConversionTest {

  @Test
  void testCalendarWithExceptionsAndNormalHours() {
    assertThrows(
      "A calendar with exceptions and normal hours cannot be made into a Period",
      IllegalArgumentException.class,
      () -> PeriodUtils.toPeriod(Calendars.CALENDAR_WITH_NORMAL_HOURS_AND_EXCEPTIONS)
    );
  }

  @Test
  void testCalendarWithMultipleExceptions() {
    assertThrows(
      "A calendar with multiple exceptions cannot be made into a Period",
      IllegalArgumentException.class,
      () -> PeriodUtils.toPeriod(Calendars.CALENDAR_WITH_MULTIPLE_EXCEPTIONS)
    );
  }

  @Test
  void testFullExceptionalCalendarConversionA() {
    assertThat(
      "A converted closure exceptional calendar is represented equivalently as a legacy period",
      PeriodUtils.toPeriod(Calendars.CALENDAR_FULL_EXCEPTIONAL_A),
      is(Periods.PERIOD_FULL_EXCEPTIONAL_A)
    );
  }

  @Test
  void testFullExceptionalCalendarConversionB() {
    assertThat(
      "A converted all-day opening exceptional calendar is represented equivalently as a legacy period",
      PeriodUtils.toPeriod(Calendars.CALENDAR_FULL_EXCEPTIONAL_B),
      is(Periods.PERIOD_FULL_EXCEPTIONAL_B)
    );
  }

  @Test
  void testFullExceptionalCalendarConversionC() {
    assertThat(
      "A converted partial-day opening exceptional calendar is represented equivalently as a legacy period",
      PeriodUtils.toPeriod(Calendars.CALENDAR_FULL_EXCEPTIONAL_C),
      is(Periods.PERIOD_FULL_EXCEPTIONAL_C)
    );
  }

  @Test
  void testFullExceptionalCalendarConversionD() {
    assertThat(
      "A converted partial-day opening exceptional calendar is represented equivalently as a legacy period",
      PeriodUtils.toPeriod(Calendars.CALENDAR_FULL_EXCEPTIONAL_D),
      is(Periods.PERIOD_FULL_EXCEPTIONAL_D)
    );
  }
}
