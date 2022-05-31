package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.testconstants.NormalOpenings;
import org.folio.calendar.utils.NormalOpeningUtils;
import org.junit.jupiter.api.Test;

class NormalOpeningUtilsAdjacencyTest {

  @Test
  void testAdjacentAllDays() {
    assertThat(
      "Adjacent all day openings are adjacent",
      NormalOpeningUtils.adjacent(NormalOpenings.MONDAY_ALL_DAY, NormalOpenings.TUESDAY_ALL_DAY),
      is(true)
    );
  }

  @Test
  void testIdenticalOpeningsAreNotAdjacent() {
    assertThat(
      "Identical openings are not adjacent",
      NormalOpeningUtils.adjacent(NormalOpenings.MONDAY_ALL_DAY, NormalOpenings.MONDAY_ALL_DAY),
      is(false)
    );
  }

  @Test
  void testSmoothingSameDayOverlaps() {
    assertThat(
      "Same-day overlaps are smoothed to adjacent",
      NormalOpeningUtils.adjacent(
        NormalOpenings.MONDAY_04_00_TO_23_59,
        NormalOpenings.MONDAY_23_00_TO_23_59
      ),
      is(true)
    );
  }

  @Test
  void testDistinctSameDaysAreNotSmoothed() {
    assertThat(
      "Same-day openings that do not overlap and do not touch are not smoothed to adjacent",
      NormalOpeningUtils.adjacent(
        NormalOpenings.MONDAY_00_00_TO_12_30,
        NormalOpenings.MONDAY_23_00_TO_23_59
      ),
      is(false)
    );
  }

  @Test
  void testDistinctAcrossDays() {
    assertThat(
      "Openings across days that are not adjacent should not be adjacent",
      NormalOpeningUtils.adjacent(
        NormalOpenings.TUESDAY_ALL_DAY,
        NormalOpenings.WEDNESDAY_23_00_TO_FRIDAY_23_59
      ),
      is(false)
    );
  }
}
