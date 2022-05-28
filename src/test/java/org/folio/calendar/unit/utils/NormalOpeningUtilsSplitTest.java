package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Map;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.types.Weekday;
import org.folio.calendar.testconstants.NormalOpenings;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.NormalOpeningUtils;
import org.folio.calendar.utils.TimeRange;
import org.junit.jupiter.api.Test;

class NormalOpeningUtilsSplitTest {

  @Test
  void testSingleDaySplittingPartialDay() {
    Map<Weekday, List<TimeRange<NormalOpening>>> map = NormalOpeningUtils.initializeWeekdayMapOfTimeRanges();
    NormalOpeningUtils.fillWeekdayMapWithTimeTuples(map, NormalOpenings.MONDAY_00_00_TO_12_30);
    assertThat(map.get(Weekday.SUNDAY), is(empty()));
    assertThat(
      map.get(Weekday.MONDAY),
      containsInAnyOrder(
        new TimeRange<>(NormalOpenings.MONDAY_00_00_TO_12_30, Times.TIME_00_00, Times.TIME_12_30)
      )
    );
    assertThat(map.get(Weekday.TUESDAY), is(empty()));
    assertThat(map.get(Weekday.WEDNESDAY), is(empty()));
    assertThat(map.get(Weekday.THURSDAY), is(empty()));
    assertThat(map.get(Weekday.FRIDAY), is(empty()));
    assertThat(map.get(Weekday.SATURDAY), is(empty()));
  }

  @Test
  void testSingleDaySplittingAllDay() {
    Map<Weekday, List<TimeRange<NormalOpening>>> map = NormalOpeningUtils.initializeWeekdayMapOfTimeRanges();
    NormalOpeningUtils.fillWeekdayMapWithTimeTuples(map, NormalOpenings.MONDAY_ALL_DAY);
    assertThat(map.get(Weekday.SUNDAY), is(empty()));
    assertThat(
      map.get(Weekday.MONDAY),
      containsInAnyOrder(
        new TimeRange<>(NormalOpenings.MONDAY_ALL_DAY, Times.TIME_00_00, Times.TIME_23_59)
      )
    );
    assertThat(map.get(Weekday.TUESDAY), is(empty()));
    assertThat(map.get(Weekday.WEDNESDAY), is(empty()));
    assertThat(map.get(Weekday.THURSDAY), is(empty()));
    assertThat(map.get(Weekday.FRIDAY), is(empty()));
    assertThat(map.get(Weekday.SATURDAY), is(empty()));
  }

  @Test
  void testMultipleDaySplitting() {
    Map<Weekday, List<TimeRange<NormalOpening>>> map = NormalOpeningUtils.initializeWeekdayMapOfTimeRanges();
    NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
      map,
      NormalOpenings.WEDNESDAY_23_00_TO_FRIDAY_23_59
    );
    assertThat(map.get(Weekday.SUNDAY), is(empty()));
    assertThat(map.get(Weekday.MONDAY), is(empty()));
    assertThat(map.get(Weekday.TUESDAY), is(empty()));
    assertThat(
      map.get(Weekday.WEDNESDAY),
      containsInAnyOrder(
        new TimeRange<>(
          NormalOpenings.WEDNESDAY_23_00_TO_FRIDAY_23_59,
          Times.TIME_23_00,
          Times.TIME_23_59
        )
      )
    );
    assertThat(
      map.get(Weekday.THURSDAY),
      containsInAnyOrder(
        new TimeRange<>(
          NormalOpenings.WEDNESDAY_23_00_TO_FRIDAY_23_59,
          Times.TIME_00_00,
          Times.TIME_23_59
        )
      )
    );
    assertThat(
      map.get(Weekday.FRIDAY),
      containsInAnyOrder(
        new TimeRange<>(
          NormalOpenings.WEDNESDAY_23_00_TO_FRIDAY_23_59,
          Times.TIME_00_00,
          Times.TIME_23_59
        )
      )
    );
    assertThat(map.get(Weekday.SATURDAY), is(empty()));
  }
}
