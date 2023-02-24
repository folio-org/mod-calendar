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

class PeriodUtilsCalendarConversionTest {

  @Test
  void testFullCalendarConversionA() {
    Calendar calendar = PeriodUtils
      .toCalendars(Arrays.asList(Periods.PERIOD_FULL_EXAMPLE_A))
      .get(0)
      .withId(UUIDs.UUID_A);
    calendar.clearIds();
    assertThat(
      "A converted period with openings across many days is represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_A),
      is(Calendars.CALENDAR_FULL_EXAMPLE_A)
    );
  }

  @Test
  void testFullCalendarConversionB() {
    Calendar calendar = PeriodUtils
      .toCalendars(Arrays.asList(Periods.PERIOD_FULL_EXAMPLE_B))
      .get(0)
      .withId(UUIDs.UUID_B);
    calendar.clearIds();
    assertThat(
      "A converted period with openings on few days is represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_B),
      is(Calendars.CALENDAR_FULL_EXAMPLE_B)
    );
  }

  @Test
  void testEmptyCalendarConversion() {
    Calendar calendar = PeriodUtils
      .toCalendars(Arrays.asList(Periods.PERIOD_WITH_NO_OPENINGS_NOR_EXCEPTIONS))
      .get(0);
    calendar.clearIds();
    assertThat(
      "A converted period with no openings nor exceptions represented equivalently as a calendar",
      calendar.withId(UUIDs.UUID_A),
      is(Calendars.CALENDAR_WITH_NO_OPENINGS_NOR_EXCEPTIONS)
    );
  }
}
