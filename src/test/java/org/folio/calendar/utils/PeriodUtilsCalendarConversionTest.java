package org.folio.calendar.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Periods;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class PeriodUtilsCalendarConversionTest {

  @Test
  void testCalendarWithNoServicePoints() {
    assertThrows(
      "A calendar with no service points cannot be made into a Period",
      IllegalArgumentException.class,
      () -> PeriodUtils.toPeriod(Calendars.CALENDAR_WITH_NO_SERVICE_POINTS)
    );
  }

  @Test
  void testCalendarWithMultipleServicePoints() {
    assertThrows(
      "A calendar with more than one service point cannot be made into a Period",
      IllegalArgumentException.class,
      () -> PeriodUtils.toPeriod(Calendars.CALENDAR_WITH_TWO_SERVICE_POINTS)
    );
  }

  @Test
  void testFullCalendarConversionA() {
    assertThat(
      "A converted calendar with openings across many days is represented equivalently as a legacy period",
      PeriodUtils.toPeriod(Calendars.CALENDAR_FULL_EXAMPLE_A),
      is(Periods.PERIOD_FULL_EXAMPLE_A)
    );
  }

  @Test
  void testFullCalendarConversionB() {
    assertThat(
      "A converted calendar with openings on few days is represented equivalently as a legacy period",
      PeriodUtils.toPeriod(Calendars.CALENDAR_FULL_EXAMPLE_B),
      is(Periods.PERIOD_FULL_EXAMPLE_B)
    );
  }
}
