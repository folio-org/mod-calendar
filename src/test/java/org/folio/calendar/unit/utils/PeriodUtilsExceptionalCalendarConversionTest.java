package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Names;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

class PeriodUtilsExceptionalCalendarConversionTest {

  @Test
  void testFullExceptionalCalendarConversionA() {
    Calendar calendar = PeriodUtils.toCalendar(Periods.PERIOD_FULL_EXCEPTIONAL_A);
    calendar.clearIds();
    // legacy exceptions have no names
    calendar.getExceptions().forEach(e -> e.setName(Names.NAME_1));
    assertThat(
      "A converted closure exceptional period is represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_A),
      is(Calendars.CALENDAR_FULL_EXCEPTIONAL_A)
    );
  }

  @Test
  void testFullExceptionalCalendarConversionB() {
    Calendar calendar = PeriodUtils.toCalendar(Periods.PERIOD_FULL_EXCEPTIONAL_B);
    calendar.clearIds();
    assertThat(
      "A converted all-day opening exceptional period is represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_B),
      is(Calendars.CALENDAR_FULL_EXCEPTIONAL_B)
    );
  }

  @Test
  void testFullExceptionalCalendarConversionC() {
    Calendar calendar = PeriodUtils.toCalendar(Periods.PERIOD_FULL_EXCEPTIONAL_C);
    calendar.clearIds();
    assertThat(
      "A converted partial-day opening exceptional period is represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_C),
      is(Calendars.CALENDAR_FULL_EXCEPTIONAL_C)
    );
  }

  @Test
  void testFullExceptionalCalendarConversionD() {
    Calendar calendar = PeriodUtils.toCalendar(Periods.PERIOD_FULL_EXCEPTIONAL_D);
    calendar.clearIds();
    assertThat(
      "A converted partial-day opening exceptional period is represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_D),
      is(Calendars.CALENDAR_FULL_EXCEPTIONAL_D)
    );
  }
}
