package org.folio.calendar.unit.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.testconstants.NormalOpenings;
import org.junit.jupiter.api.Test;

class NormalOpeningAdjacencyTest {

  @Test
  void testAdjacentAllDays() {
    assertThat(
      "Adjacent all day openings are adjacent",
      NormalOpening.adjacent(NormalOpenings.MONDAY_ALL_DAY, NormalOpenings.TUESDAY_ALL_DAY),
      is(true)
    );
  }

  @Test
  void testIdenticalOpeningsAreNotAdjacent() {
    assertThat(
      "Identical openings are not adjacent",
      NormalOpening.adjacent(NormalOpenings.MONDAY_ALL_DAY, NormalOpenings.MONDAY_ALL_DAY),
      is(false)
    );
  }

  @Test
  void testSmoothingSameDayOverlaps() {
    assertThat(
      "Same-day overlaps are smoothed to adjacent",
      NormalOpening.adjacent(
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
      NormalOpening.adjacent(
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
      NormalOpening.adjacent(
        NormalOpenings.TUESDAY_ALL_DAY,
        NormalOpenings.WEDNESDAY_23_00_TO_FRIDAY_23_59
      ),
      is(false)
    );
  }
}
