package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.OpeningDayInfoRelativeConstants;
import org.folio.calendar.utils.DateUtils;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

class PeriodUtilsDailyExceptionalConversionTest {

  @Test
  void testGetNoDailyExceptions() {
    Map<LocalDate, OpeningDayInfo> result = PeriodUtils.getDailyExceptionalOpenings(
      Calendars.CALENDAR_FULL_EXAMPLE_E,
      null,
      null
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();

    assertThat("Calendar E has no exceptional openings", result, is(equalTo(expected)));
  }

  @Test
  void testGetSubsetExceptionalClosures() {
    Map<LocalDate, OpeningDayInfo> result = PeriodUtils.getDailyExceptionalOpenings(
      Calendars.CALENDAR_FULL_EXCEPTIONAL_A,
      Dates.DATE_2021_12_30,
      null
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();

    for (LocalDate date : DateUtils.getDateRange(Dates.DATE_2021_12_30, Dates.DATE_2021_12_31)) {
      expected.put(date, OpeningDayInfoRelativeConstants.EXCEPTIONAL_CLOSED_ALL_DAY);
    }
    assertThat(
      "Exceptional calendar A produces the expected map of closures",
      result,
      is(equalTo(expected))
    );
  }

  @Test
  void testGetExceptionalClosures() {
    Map<LocalDate, OpeningDayInfo> result = PeriodUtils.getDailyExceptionalOpenings(
      Calendars.CALENDAR_FULL_EXCEPTIONAL_A,
      null,
      null
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();

    for (LocalDate date : DateUtils.getDateRange(Dates.DATE_2021_01_01, Dates.DATE_2021_12_31)) {
      expected.put(date, OpeningDayInfoRelativeConstants.EXCEPTIONAL_CLOSED_ALL_DAY);
    }
    assertThat(
      "Exceptional calendar A produces the expected map of closures",
      result,
      is(equalTo(expected))
    );
  }
}
