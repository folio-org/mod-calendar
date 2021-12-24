package org.folio.calendar.unit.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.repository.PeriodQueryFilter;
import org.folio.calendar.testconstants.Calendars;
import org.junit.jupiter.api.Test;

public class PeriodQueryFilterTest {

  @Test
  void testNormalHoursFilterValid() {
    assertThat(
      "A Calendar of normal hours should pass the normal hours filter",
      PeriodQueryFilter.NORMAL_HOURS.passes(Calendars.CALENDAR_FULL_EXAMPLE_A),
      is(equalTo(true))
    );
  }

  @Test
  void testNormalHoursFilterInvalid() {
    assertThat(
      "A Calendar of exceptional hours should not pass the normal hours filter",
      PeriodQueryFilter.NORMAL_HOURS.passes(Calendars.CALENDAR_FULL_EXCEPTIONAL_A),
      is(equalTo(false))
    );
  }

  @Test
  void testExceptionalFilterValid() {
    assertThat(
      "A Calendar of exceptional hours should pass the exceptional hours filter",
      PeriodQueryFilter.EXCEPTIONS.passes(Calendars.CALENDAR_FULL_EXCEPTIONAL_A),
      is(equalTo(true))
    );
  }

  @Test
  void testExceptionalFilterInvalid() {
    assertThat(
      "A Calendar of normal hours should not pass the exceptional hours filter",
      PeriodQueryFilter.EXCEPTIONS.passes(Calendars.CALENDAR_FULL_EXAMPLE_A),
      is(equalTo(false))
    );
  }
}
