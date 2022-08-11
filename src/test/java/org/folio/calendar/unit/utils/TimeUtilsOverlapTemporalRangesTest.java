package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalTime;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.TemporalRange;
import org.folio.calendar.utils.TimeUtils;
import org.junit.jupiter.api.Test;

class TimeUtilsOverlapTemporalRangesTest {

  @Test
  void testNonOverlappingLocalTimeSets() {
    assertThat(
      "Distinct time ranges (1 then 2) do not overlap",
      TimeUtils.overlaps(Times.TIME_00_00, Times.TIME_04_00, Times.TIME_12_30, Times.TIME_14_00),
      is(false)
    );
    assertThat(
      "Distinct time ranges (2 then 1) do not overlap",
      TimeUtils.overlaps(Times.TIME_12_30, Times.TIME_14_00, Times.TIME_00_00, Times.TIME_04_00),
      is(false)
    );
  }

  @Test
  void testOverlappingSameLocalTimeSet() {
    assertThat(
      "The same time on all range edges (1-1 and 1-1) does overlap",
      TimeUtils.overlaps(Times.TIME_00_00, Times.TIME_00_00, Times.TIME_00_00, Times.TIME_00_00),
      is(true)
    );
  }

  @Test
  void testOverlappingLocalTimeSets() {
    assertThat(
      "Overlapping time ranges (1 then 2) do overlap",
      TimeUtils.overlaps(Times.TIME_00_00, Times.TIME_12_30, Times.TIME_04_00, Times.TIME_14_00),
      is(true)
    );
    assertThat(
      "Overlapping time ranges (2 then 1) do overlap",
      TimeUtils.overlaps(Times.TIME_04_00, Times.TIME_14_00, Times.TIME_00_00, Times.TIME_12_30),
      is(true)
    );
  }

  @Test
  void testNonOverlappingTemporalRanges() {
    assertThat(
      "Distinct time ranges (1 then 2) do not overlap",
      TimeUtils.overlaps(
        new TemporalRange<LocalTime, Object>(Times.TIME_00_00, Times.TIME_04_00),
        new TemporalRange<LocalTime, Object>(Times.TIME_12_30, Times.TIME_14_00)
      ),
      is(false)
    );
    assertThat(
      "Distinct time ranges (2 then 1) do not overlap",
      TimeUtils.overlaps(
        new TemporalRange<LocalTime, Object>(Times.TIME_12_30, Times.TIME_14_00),
        new TemporalRange<LocalTime, Object>(Times.TIME_00_00, Times.TIME_04_00)
      ),
      is(false)
    );
  }

  @Test
  void testOverlappingSameTemporalRange() {
    assertThat(
      "The same time on all range edges (1-1 and 1-1) does overlap",
      TimeUtils.overlaps(
        new TemporalRange<LocalTime, Object>(Times.TIME_00_00, Times.TIME_00_00),
        new TemporalRange<LocalTime, Object>(Times.TIME_00_00, Times.TIME_00_00)
      ),
      is(true)
    );
  }

  @Test
  void testOverlappingTemporalRanges() {
    assertThat(
      "Overlapping time ranges (1 then 2) do overlap",
      TimeUtils.overlaps(
        new TemporalRange<LocalTime, Object>(Times.TIME_00_00, Times.TIME_12_30),
        new TemporalRange<LocalTime, Object>(Times.TIME_04_00, Times.TIME_14_00)
      ),
      is(true)
    );
    assertThat(
      "Overlapping time ranges (2 then 1) do overlap",
      TimeUtils.overlaps(
        new TemporalRange<LocalTime, Object>(Times.TIME_04_00, Times.TIME_14_00),
        new TemporalRange<LocalTime, Object>(Times.TIME_00_00, Times.TIME_12_30)
      ),
      is(true)
    );
  }
}
