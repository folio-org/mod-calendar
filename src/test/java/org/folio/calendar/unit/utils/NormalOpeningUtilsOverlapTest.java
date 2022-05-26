package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.Arrays;
import org.folio.calendar.testconstants.NormalOpenings;
import org.folio.calendar.utils.NormalOpeningUtils;
import org.junit.jupiter.api.Test;

class NormalOpeningUtilsOverlapTest {

  @Test
  void testNoOverlaps() {
    assertThat(NormalOpeningUtils.getOverlaps(new ArrayList<>()), is(empty()));
    assertThat(
      NormalOpeningUtils.getOverlaps(Arrays.asList(NormalOpenings.SUNDAY_MONDAY_ALL_DAY)),
      is(empty())
    );
    assertThat(
      NormalOpeningUtils.getOverlaps(
        Arrays.asList(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_23_00_TO_23_59,
          NormalOpenings.TUESDAY_00_00_TO_12_30,
          NormalOpenings.WEDNESDAY_23_00_TO_SUNDAY_23_59
        )
      ),
      is(empty())
    );
  }

  @Test
  void testSingleOverlaps() {
    assertThat(
      NormalOpeningUtils.getOverlaps(
        Arrays.asList(NormalOpenings.SUNDAY_MONDAY_ALL_DAY, NormalOpenings.MONDAY_23_00_TO_23_59)
      ),
      containsInAnyOrder(NormalOpenings.SUNDAY_MONDAY_ALL_DAY, NormalOpenings.MONDAY_23_00_TO_23_59)
    );
    assertThat(
      NormalOpeningUtils.getOverlaps(
        Arrays.asList(NormalOpenings.SUNDAY_MONDAY_ALL_DAY, NormalOpenings.SUNDAY_MONDAY_ALL_DAY)
      ),
      hasItem(NormalOpenings.SUNDAY_MONDAY_ALL_DAY)
    );
    assertThat(
      NormalOpeningUtils.getOverlaps(
        Arrays.asList(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_04_00_TO_14_59,
          NormalOpenings.MONDAY_23_00_TO_23_59
        )
      ),
      allOf(
        hasItems(NormalOpenings.MONDAY_00_00_TO_12_30, NormalOpenings.MONDAY_04_00_TO_14_59),
        not(hasItem(NormalOpenings.MONDAY_23_00_TO_23_59))
      )
    );
  }

  @Test
  void testMultipleOverlaps() {
    assertThat(
      NormalOpeningUtils.getOverlaps(
        Arrays.asList(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_04_00_TO_14_59,
          NormalOpenings.MONDAY_04_00_TO_23_59,
          NormalOpenings.MONDAY_23_00_TO_23_59
        )
      ),
      containsInAnyOrder(
        NormalOpenings.MONDAY_00_00_TO_12_30,
        NormalOpenings.MONDAY_04_00_TO_14_59,
        NormalOpenings.MONDAY_04_00_TO_23_59,
        NormalOpenings.MONDAY_23_00_TO_23_59
      )
    );
    // 23:00-23:59 self-conflicts
    assertThat(
      NormalOpeningUtils.getOverlaps(
        Arrays.asList(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_04_00_TO_14_59,
          NormalOpenings.MONDAY_23_00_TO_23_59,
          NormalOpenings.MONDAY_23_00_TO_23_59
        )
      ),
      containsInAnyOrder(
        NormalOpenings.MONDAY_00_00_TO_12_30,
        NormalOpenings.MONDAY_04_00_TO_14_59,
        NormalOpenings.MONDAY_23_00_TO_23_59
      )
    );
  }
}
