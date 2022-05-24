package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.DateUtils;
import org.junit.jupiter.api.Test;

class DateUtilsOverlapTimeRangesTest {

  @Test
  void testNonOverlappingTimeRanges() {
    assertThat(
      "Distinct time ranges (1 then 2) do not overlap",
      DateUtils.overlaps(Times.TIME_00_00, Times.TIME_04_00, Times.TIME_12_30, Times.TIME_14_00),
      is(false)
    );
    assertThat(
      "Distinct time ranges (2 then 1) do not overlap",
      DateUtils.overlaps(Times.TIME_12_30, Times.TIME_14_00, Times.TIME_00_00, Times.TIME_04_00),
      is(false)
    );
  }

  @Test
  void testOverlappingSameTimeRange() {
    assertThat(
      "The same time on all range edges (1-1 and 1-1) does overlap",
      DateUtils.overlaps(Times.TIME_00_00, Times.TIME_00_00, Times.TIME_00_00, Times.TIME_00_00),
      is(true)
    );
  }

  @Test
  void testOverlappingRanges() {
    assertThat(
      "Overlapping time ranges (1 then 2) do overlap",
      DateUtils.overlaps(Times.TIME_00_00, Times.TIME_12_30, Times.TIME_04_00, Times.TIME_14_00),
      is(true)
    );
    assertThat(
      "Overlapping time ranges (2 then 1) do overlap",
      DateUtils.overlaps(Times.TIME_04_00, Times.TIME_14_00, Times.TIME_00_00, Times.TIME_12_30),
      is(true)
    );
  }
}
