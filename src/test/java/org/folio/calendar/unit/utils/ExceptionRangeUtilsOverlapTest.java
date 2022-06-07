package org.folio.calendar.unit.utils;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.Arrays;
import org.folio.calendar.testconstants.ExceptionRanges;
import org.folio.calendar.utils.ExceptionRangeUtils;
import org.junit.jupiter.api.Test;

class ExceptionRangeUtilsOverlapTest {

  @Test
  void testNoOverlaps() {
    assertThat(ExceptionRangeUtils.getOverlaps(new ArrayList<>()), isEmpty());
    assertThat(
      ExceptionRangeUtils.getOverlaps(
        Arrays.asList(ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31)
      ),
      isEmpty()
    );
    assertThat(
      ExceptionRangeUtils.getOverlaps(Arrays.asList(ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1)),
      isEmpty()
    );
    assertThat(
      ExceptionRangeUtils.getOverlaps(
        Arrays.asList(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02,
          ExceptionRanges.CLOSED_2021_01_03_TO_2021_01_04
        )
      ),
      isEmpty()
    );
  }

  @Test
  void testSingleOverlaps() {
    assertThat(
      ExceptionRangeUtils.getOverlaps(
        Arrays.asList(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02,
          ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1
        )
      ),
      isPresentAnd(
        containsInAnyOrder(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02,
          ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1
        )
      )
    );
    assertThat(
      ExceptionRangeUtils.getOverlaps(
        Arrays.asList(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02,
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02
        )
      ),
      isPresentAnd(hasItem(ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02))
    );
    assertThat(
      ExceptionRangeUtils.getOverlaps(
        Arrays.asList(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02,
          ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1,
          ExceptionRanges.CLOSED_2021_01_03_TO_2021_01_04
        )
      ),
      isPresentAnd(
        allOf(
          hasItems(
            ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02,
            ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1
          ),
          not(hasItem(ExceptionRanges.CLOSED_2021_01_03_TO_2021_01_04))
        )
      )
    );
  }

  @Test
  void testMultipleOverlaps() {
    assertThat(
      ExceptionRangeUtils.getOverlaps(
        Arrays.asList(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02,
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_04,
          ExceptionRanges.CLOSED_2021_01_03_TO_2021_01_04
        )
      ),
      isPresentAnd(
        containsInAnyOrder(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02,
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_04,
          ExceptionRanges.CLOSED_2021_01_03_TO_2021_01_04
        )
      )
    );
  }
}
