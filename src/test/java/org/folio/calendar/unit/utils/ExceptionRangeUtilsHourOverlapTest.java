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
import org.folio.calendar.testconstants.ExceptionHours;
import org.folio.calendar.utils.ExceptionRangeUtils;
import org.junit.jupiter.api.Test;

class ExceptionRangeUtilsHourOverlapTest {

  @Test
  void testNoOverlaps() {
    assertThat(ExceptionRangeUtils.getHourOverlaps(new ArrayList<>()), isEmpty());
    assertThat(
      ExceptionRangeUtils.getHourOverlaps(
        Arrays.asList(ExceptionHours.OPEN_ALL_DAY_JAN_1_THRU_JAN_4)
      ),
      isEmpty()
    );
    assertThat(
      ExceptionRangeUtils.getHourOverlaps(
        Arrays.asList(
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1,
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_2_THRU_JAN_3,
          ExceptionHours.OPEN_04_00_TO_14_59_JAN_4,
          ExceptionHours.OPEN_15_00_TO_23_00_JAN_4
        )
      ),
      isEmpty()
    );
  }

  @Test
  void testSingleOverlaps() {
    assertThat(
      ExceptionRangeUtils.getHourOverlaps(
        Arrays.asList(
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1,
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2
        )
      ),
      isPresentAnd(
        containsInAnyOrder(
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1,
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2
        )
      )
    );
    assertThat(
      ExceptionRangeUtils.getHourOverlaps(
        Arrays.asList(
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1,
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1
        )
      ),
      isPresentAnd(hasItem(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1))
    );
    assertThat(
      ExceptionRangeUtils.getHourOverlaps(
        Arrays.asList(
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1,
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2,
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_3_THRU_JAN_4
        )
      ),
      isPresentAnd(
        allOf(
          hasItems(
            ExceptionHours.OPEN_00_00_TO_14_59_JAN_1,
            ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2
          ),
          not(hasItem(ExceptionHours.OPEN_00_00_TO_14_59_JAN_3_THRU_JAN_4))
        )
      )
    );
  }

  @Test
  void testMultipleOverlaps() {
    assertThat(
      ExceptionRangeUtils.getHourOverlaps(
        Arrays.asList(
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1,
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2,
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_3_THRU_JAN_4,
          ExceptionHours.OPEN_ALL_DAY_JAN_1_THRU_JAN_4
        )
      ),
      isPresentAnd(
        containsInAnyOrder(
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1,
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2,
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_3_THRU_JAN_4,
          ExceptionHours.OPEN_ALL_DAY_JAN_1_THRU_JAN_4
        )
      )
    );
  }
}
