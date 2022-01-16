package org.folio.calendar.unit.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.OpeningDayInfoRelativeConstants;
import org.junit.jupiter.api.Test;

class CalendarNormalDailyConversionTest {

  @Test
  void testGetAllDailyNormalOpenings() {
    Map<LocalDate, OpeningDayInfo> result = Calendars.CALENDAR_FULL_EXAMPLE_E.getDailyNormalOpenings(
      null,
      null
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();

    LocalDate mondays[] = {
      LocalDate.of(2021, 3, 22),
      LocalDate.of(2021, 3, 29),
      LocalDate.of(2021, 4, 5),
      LocalDate.of(2021, 4, 12),
      LocalDate.of(2021, 4, 19),
      LocalDate.of(2021, 4, 26),
    };
    for (LocalDate monday : mondays) {
      expected.put(monday, OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59);
    }
    LocalDate thursdays[] = {
      LocalDate.of(2021, 3, 18),
      LocalDate.of(2021, 3, 25),
      LocalDate.of(2021, 4, 1),
      LocalDate.of(2021, 4, 8),
      LocalDate.of(2021, 4, 15),
      LocalDate.of(2021, 4, 22),
      LocalDate.of(2021, 4, 29),
    };
    for (LocalDate thursday : thursdays) {
      expected.put(thursday, OpeningDayInfoRelativeConstants.OPEN_ALL_DAY);
    }
    assertThat(
      "Calendar E produces the expected map of normal openings",
      result,
      is(equalTo(expected))
    );
  }

  @Test
  void testGetSubsetOfDailyNormalOpenings() {
    Map<LocalDate, OpeningDayInfo> result = Calendars.CALENDAR_FULL_EXAMPLE_E.getDailyNormalOpenings(
      LocalDate.of(2021, 4, 5),
      LocalDate.of(2021, 4, 12)
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();

    LocalDate mondays[] = { LocalDate.of(2021, 4, 5), LocalDate.of(2021, 4, 12) };
    for (LocalDate monday : mondays) {
      expected.put(monday, OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59);
    }
    LocalDate thursdays[] = { LocalDate.of(2021, 4, 8) };
    for (LocalDate thursday : thursdays) {
      expected.put(thursday, OpeningDayInfoRelativeConstants.OPEN_ALL_DAY);
    }
    assertThat(
      "Calendar E produces the expected subset map of normal openings",
      result,
      is(equalTo(expected))
    );
  }

  @Test
  void testGetOpenStartSubsetOfDailyNormalOpenings() {
    Map<LocalDate, OpeningDayInfo> result = Calendars.CALENDAR_FULL_EXAMPLE_E.getDailyNormalOpenings(
      null,
      LocalDate.of(2021, 4, 12)
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();

    LocalDate mondays[] = {
      LocalDate.of(2021, 3, 22),
      LocalDate.of(2021, 3, 29),
      LocalDate.of(2021, 4, 5),
      LocalDate.of(2021, 4, 12),
    };
    for (LocalDate monday : mondays) {
      expected.put(monday, OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59);
    }
    LocalDate thursdays[] = {
      LocalDate.of(2021, 3, 18),
      LocalDate.of(2021, 3, 25),
      LocalDate.of(2021, 4, 1),
      LocalDate.of(2021, 4, 8),
    };
    for (LocalDate thursday : thursdays) {
      expected.put(thursday, OpeningDayInfoRelativeConstants.OPEN_ALL_DAY);
    }
    assertThat(
      "Calendar E produces the expected subset map of normal openings",
      result,
      is(equalTo(expected))
    );
  }

  @Test
  void testGetOpenEndSubsetOfDailyNormalOpenings() {
    Map<LocalDate, OpeningDayInfo> result = Calendars.CALENDAR_FULL_EXAMPLE_E.getDailyNormalOpenings(
      LocalDate.of(2021, 4, 9),
      null
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();

    LocalDate mondays[] = {
      LocalDate.of(2021, 4, 12),
      LocalDate.of(2021, 4, 19),
      LocalDate.of(2021, 4, 26),
    };
    for (LocalDate monday : mondays) {
      expected.put(monday, OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59);
    }
    LocalDate thursdays[] = {
      LocalDate.of(2021, 4, 15),
      LocalDate.of(2021, 4, 22),
      LocalDate.of(2021, 4, 29),
    };
    for (LocalDate thursday : thursdays) {
      expected.put(thursday, OpeningDayInfoRelativeConstants.OPEN_ALL_DAY);
    }
    assertThat(
      "Calendar E produces the expected subset map of normal openings",
      result,
      is(equalTo(expected))
    );
  }

  @Test
  void testGetEmptySubsetOfDailyNormalOpenings() {
    Map<LocalDate, OpeningDayInfo> result = Calendars.CALENDAR_FULL_EXAMPLE_E.getDailyNormalOpenings(
      LocalDate.of(2021, 4, 9),
      LocalDate.of(2021, 4, 11)
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();

    assertThat(
      "Calendar E produces the expected map of empty normal openings when the date range excludes all",
      result,
      is(equalTo(expected))
    );
  }
}
