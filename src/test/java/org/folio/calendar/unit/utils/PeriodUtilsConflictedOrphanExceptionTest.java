package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.Arrays;
import java.util.List;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

class PeriodUtilsConflictedOrphanExceptionTest {

  @Test
  void testDoubleOrphan() {
    List<Calendar> calendars = PeriodUtils.toCalendars(
      Arrays.asList(Periods.PERIOD_FULL_EXCEPTIONAL_A, Periods.PERIOD_FULL_EXCEPTIONAL_A)
    );
    calendars.forEach(Calendar::clearIds);

    assertThat(
      "Two calendars are returned, one for each found",
      calendars,
      contains(
        Calendars.CALENDAR_FULL_EXCEPTIONAL_A.withId(null),
        Calendars.CALENDAR_FULL_EXCEPTIONAL_A.withId(null)
      )
    );
  }
}
