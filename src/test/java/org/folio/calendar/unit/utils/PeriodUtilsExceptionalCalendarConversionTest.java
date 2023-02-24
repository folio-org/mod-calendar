package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

class PeriodUtilsExceptionalCalendarConversionTest {

  @Test
  void testFullExceptionalCalendarConversionA() {
    Calendar calendar = PeriodUtils
      .toCalendars(Arrays.asList(Periods.PERIOD_FULL_EXCEPTIONAL_A))
      .get(0);
    calendar.clearIds();

    assertThat(
      "A converted closure exceptional period is represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_A),
      is(Calendars.CALENDAR_FULL_EXCEPTIONAL_A)
    );
  }

  @Test
  void testFullExceptionalCalendarConversionB() {
    Calendar calendar = PeriodUtils
      .toCalendars(Arrays.asList(Periods.PERIOD_FULL_EXCEPTIONAL_B))
      .get(0);
    calendar.clearIds();
    assertThat(
      "A converted all-day opening exceptional period is represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_B),
      is(Calendars.CALENDAR_FULL_EXCEPTIONAL_B)
    );
  }

  @Test
  void testFullExceptionalCalendarConversionC() {
    Calendar calendar = PeriodUtils
      .toCalendars(Arrays.asList(Periods.PERIOD_FULL_EXCEPTIONAL_C))
      .get(0);
    calendar.clearIds();
    assertThat(
      "A converted partial-day opening exceptional period is represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_C),
      is(Calendars.CALENDAR_FULL_EXCEPTIONAL_C)
    );
  }

  @Test
  void testFullExceptionalCalendarConversionD() {
    Calendar calendar = PeriodUtils
      .toCalendars(Arrays.asList(Periods.PERIOD_FULL_EXCEPTIONAL_D))
      .get(0);
    calendar.clearIds();
    assertThat(
      "A converted partial-day opening exceptional period is represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_D),
      is(Calendars.CALENDAR_FULL_EXCEPTIONAL_D)
    );
  }
}
