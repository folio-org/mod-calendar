package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.OpeningDayInfoRelativeConstants;
import org.folio.calendar.testconstants.OpeningHourRanges;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

/**
 * Please note that, since PeriodUtils.mergeInto WILL change its inputs,
 * anytime there is a risk of this happening, .toBuilder().build() is used to "clone" OpeningDayInfo
 * objects/constants that would otherwise be modified
 */
class PeriodUtilsMergeTest {

  @Test
  void testMergeEmpty() {
    Map<LocalDate, OpeningDayInfo> a = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>();

    PeriodUtils.mergeInto(a, b);

    assertThat("Merging two empty maps results in an empty map", a, is(equalTo(new HashMap<>())));
  }

  @Test
  void testMergeEmptyIntoSingle() {
    Map<LocalDate, OpeningDayInfo> full = Calendars.CALENDAR_FULL_EXAMPLE_E.getDailyNormalOpenings(
      null,
      null
    );
    Map<LocalDate, OpeningDayInfo> empty = new HashMap<>();

    Map<LocalDate, OpeningDayInfo> a = new HashMap<>(full);
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>(empty);
    PeriodUtils.mergeInto(a, b);

    assertThat("Merging a empty map into a full map results in the full map", a, is(equalTo(full)));
  }

  @Test
  void testMergeSingleIntoEmpty() {
    Map<LocalDate, OpeningDayInfo> full = Calendars.CALENDAR_FULL_EXAMPLE_E.getDailyNormalOpenings(
      null,
      null
    );
    Map<LocalDate, OpeningDayInfo> empty = new HashMap<>();

    Map<LocalDate, OpeningDayInfo> a = new HashMap<>(empty);
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>(full);
    PeriodUtils.mergeInto(a, b);

    assertThat("Merging a full map into a empty map results in the full map", a, is(equalTo(full)));
  }

  @Test
  void testMergeTwoSeparate() {
    Map<LocalDate, OpeningDayInfo> a = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>();

    a.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30.toBuilder().build()
    );
    b.put(
      Dates.DATE_2021_01_02,
      OpeningDayInfoRelativeConstants.OPEN_04_00_TO_14_59.toBuilder().build()
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();
    expected.putAll(a);
    expected.putAll(b);

    PeriodUtils.mergeInto(a, b);

    assertThat(
      "Merging two unrelated maps creates a map containing all elements of each",
      a,
      is(equalTo(expected))
    );
  }

  @Test
  void testMergeTwoOverlappingOpenings() {
    Map<LocalDate, OpeningDayInfo> a = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>();

    a.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30.toBuilder().build()
    );
    b.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_23_00_TO_23_59.toBuilder().build()
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();
    expected.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59
    );

    PeriodUtils.mergeInto(a, b);

    assertThat(
      "Merging two overlapping maps of openings creates a map with a single date containing all elements of each",
      a,
      is(equalTo(expected))
    );
  }

  @Test
  void testMergeOpeningIntoClosure() {
    Map<LocalDate, OpeningDayInfo> a = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>();

    a.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.CLOSED_ALL_DAY.toBuilder().build()
    );
    b.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_04_00_TO_14_59.toBuilder().build()
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();
    expected.put(Dates.DATE_2021_01_01, OpeningDayInfoRelativeConstants.OPEN_04_00_TO_14_59);

    PeriodUtils.mergeInto(a, b);

    assertThat(
      "Merging an opening into a closure creates a map with a single date containing just the opening",
      a,
      is(equalTo(expected))
    );
  }

  @Test
  void testMergeClosureIntoOpening() {
    Map<LocalDate, OpeningDayInfo> a = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>();

    a.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_04_00_TO_14_59.toBuilder().build()
    );
    b.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.CLOSED_ALL_DAY.toBuilder().build()
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();
    expected.put(Dates.DATE_2021_01_01, OpeningDayInfoRelativeConstants.OPEN_04_00_TO_14_59);

    PeriodUtils.mergeInto(a, b);

    assertThat(
      "Merging a closure into an opening creates a map with a single date containing just the opening",
      a,
      is(equalTo(expected))
    );
  }

  @Test
  void testMergeClosureIntoClosure() {
    Map<LocalDate, OpeningDayInfo> a = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>();

    a.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.CLOSED_ALL_DAY.toBuilder().build()
    );
    b.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.CLOSED_ALL_DAY.toBuilder().build()
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();
    expected.put(Dates.DATE_2021_01_01, OpeningDayInfoRelativeConstants.CLOSED_ALL_DAY);

    PeriodUtils.mergeInto(a, b);

    assertThat(
      "Merging a closure into a closure creates a map with a single date containing just the closure",
      a,
      is(equalTo(expected))
    );
  }

  @Test
  void testMergePartialIntoAllDay() {
    Map<LocalDate, OpeningDayInfo> a = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>();

    a.put(Dates.DATE_2021_01_01, OpeningDayInfoRelativeConstants.OPEN_ALL_DAY.toBuilder().build());
    b.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30.toBuilder().build()
    );

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();
    expected.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_ALL_DAY.withOpeningHour(
        Arrays.asList(OpeningHourRanges.ALL_DAY, OpeningHourRanges.RANGE_00_00_TO_12_30)
      )
    );

    PeriodUtils.mergeInto(a, b);

    assertThat(
      "Merging a partial opening into an all day opening creates a map indicating an all-day opening",
      a,
      is(equalTo(expected))
    );
  }

  @Test
  void testMergeAllDayIntoPartial() {
    Map<LocalDate, OpeningDayInfo> a = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>();

    a.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30.toBuilder().build()
    );
    b.put(Dates.DATE_2021_01_01, OpeningDayInfoRelativeConstants.OPEN_ALL_DAY.toBuilder().build());

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();
    expected.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30
        .withAllDay(true)
        .withOpeningHour(
          Arrays.asList(OpeningHourRanges.ALL_DAY, OpeningHourRanges.RANGE_00_00_TO_12_30)
        )
    );

    PeriodUtils.mergeInto(a, b);

    assertThat(
      "Merging an all day opening into a partial opening creates a map indicating an all-day opening",
      a,
      is(equalTo(expected))
    );
  }

  @Test
  void testMergeAllDayIntoAllDay() {
    Map<LocalDate, OpeningDayInfo> a = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> b = new HashMap<>();

    a.put(Dates.DATE_2021_01_01, OpeningDayInfoRelativeConstants.OPEN_ALL_DAY.toBuilder().build());
    b.put(Dates.DATE_2021_01_01, OpeningDayInfoRelativeConstants.OPEN_ALL_DAY.toBuilder().build());

    Map<LocalDate, OpeningDayInfo> expected = new HashMap<>();
    expected.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.OPEN_ALL_DAY.withOpeningHour(
        Arrays.asList(OpeningHourRanges.ALL_DAY)
      )
    );

    PeriodUtils.mergeInto(a, b);

    assertThat(
      "Merging an all day opening into an all day opening creates a map indicating an all-day opening without duplicates",
      a,
      is(equalTo(expected))
    );
  }
}
