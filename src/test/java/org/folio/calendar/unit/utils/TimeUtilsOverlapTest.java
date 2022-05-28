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
import java.util.List;
import java.util.Map;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.types.Weekday;
import org.folio.calendar.testconstants.NormalOpenings;
import org.folio.calendar.utils.NormalOpeningUtils;
import org.folio.calendar.utils.TimeRange;
import org.folio.calendar.utils.TimeUtils;
import org.junit.jupiter.api.Test;

class TimeUtilsOverlapTest {

  static final Map<Weekday, List<TimeRange<NormalOpening>>> SUNDAY_MONDAY_ALL_DAY = NormalOpeningUtils.initializeWeekdayMapOfTimeRanges();
  static final Map<Weekday, List<TimeRange<NormalOpening>>> MONDAY_00_00_TO_12_30 = NormalOpeningUtils.initializeWeekdayMapOfTimeRanges();
  static final Map<Weekday, List<TimeRange<NormalOpening>>> MONDAY_04_00_TO_14_59 = NormalOpeningUtils.initializeWeekdayMapOfTimeRanges();
  static final Map<Weekday, List<TimeRange<NormalOpening>>> MONDAY_04_00_TO_23_59 = NormalOpeningUtils.initializeWeekdayMapOfTimeRanges();
  static final Map<Weekday, List<TimeRange<NormalOpening>>> MONDAY_23_00_TO_23_59 = NormalOpeningUtils.initializeWeekdayMapOfTimeRanges();

  static {
    NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
      SUNDAY_MONDAY_ALL_DAY,
      NormalOpenings.SUNDAY_MONDAY_ALL_DAY
    );
    NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
      MONDAY_00_00_TO_12_30,
      NormalOpenings.MONDAY_00_00_TO_12_30
    );
    NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
      MONDAY_04_00_TO_14_59,
      NormalOpenings.MONDAY_04_00_TO_14_59
    );
    NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
      MONDAY_04_00_TO_23_59,
      NormalOpenings.MONDAY_04_00_TO_23_59
    );
    NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
      MONDAY_23_00_TO_23_59,
      NormalOpenings.MONDAY_23_00_TO_23_59
    );
  }

  @Test
  void testNoOverlaps() {
    assertThat(TimeUtils.getOverlaps(new ArrayList<>()), isEmpty());
    assertThat(
      TimeUtils.getOverlaps(new ArrayList<>(SUNDAY_MONDAY_ALL_DAY.get(Weekday.SUNDAY))),
      isEmpty()
    );
    assertThat(
      TimeUtils.getOverlaps(
        Arrays.asList(
          MONDAY_00_00_TO_12_30.get(Weekday.MONDAY).get(0),
          MONDAY_23_00_TO_23_59.get(Weekday.MONDAY).get(0)
        )
      ),
      isEmpty()
    );
  }

  @Test
  void testSingleOverlaps() {
    assertThat(
      TimeUtils.getOverlaps(
        Arrays.asList(
          SUNDAY_MONDAY_ALL_DAY.get(Weekday.MONDAY).get(0),
          MONDAY_23_00_TO_23_59.get(Weekday.MONDAY).get(0)
        )
      ),
      isPresentAnd(
        containsInAnyOrder(
          NormalOpenings.SUNDAY_MONDAY_ALL_DAY,
          NormalOpenings.MONDAY_23_00_TO_23_59
        )
      )
    );
    assertThat(
      TimeUtils.getOverlaps(
        Arrays.asList(
          SUNDAY_MONDAY_ALL_DAY.get(Weekday.MONDAY).get(0),
          SUNDAY_MONDAY_ALL_DAY.get(Weekday.MONDAY).get(0)
        )
      ),
      isPresentAnd(hasItem(NormalOpenings.SUNDAY_MONDAY_ALL_DAY))
    );
    assertThat(
      TimeUtils.getOverlaps(
        Arrays.asList(
          MONDAY_00_00_TO_12_30.get(Weekday.MONDAY).get(0),
          MONDAY_04_00_TO_14_59.get(Weekday.MONDAY).get(0),
          MONDAY_23_00_TO_23_59.get(Weekday.MONDAY).get(0)
        )
      ),
      isPresentAnd(
        allOf(
          hasItems(NormalOpenings.MONDAY_00_00_TO_12_30, NormalOpenings.MONDAY_04_00_TO_14_59),
          not(hasItem(NormalOpenings.MONDAY_23_00_TO_23_59))
        )
      )
    );
  }

  @Test
  void testMultipleOverlaps() {
    assertThat(
      TimeUtils.getOverlaps(
        Arrays.asList(
          MONDAY_00_00_TO_12_30.get(Weekday.MONDAY).get(0),
          MONDAY_04_00_TO_14_59.get(Weekday.MONDAY).get(0),
          MONDAY_04_00_TO_23_59.get(Weekday.MONDAY).get(0),
          MONDAY_23_00_TO_23_59.get(Weekday.MONDAY).get(0)
        )
      ),
      isPresentAnd(
        containsInAnyOrder(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_04_00_TO_14_59,
          NormalOpenings.MONDAY_04_00_TO_23_59,
          NormalOpenings.MONDAY_23_00_TO_23_59
        )
      )
    );
    // 23:00-23:59 self-conflicts
    assertThat(
      TimeUtils.getOverlaps(
        Arrays.asList(
          MONDAY_00_00_TO_12_30.get(Weekday.MONDAY).get(0),
          MONDAY_04_00_TO_14_59.get(Weekday.MONDAY).get(0),
          MONDAY_23_00_TO_23_59.get(Weekday.MONDAY).get(0),
          MONDAY_23_00_TO_23_59.get(Weekday.MONDAY).get(0)
        )
      ),
      isPresentAnd(
        containsInAnyOrder(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_04_00_TO_14_59,
          NormalOpenings.MONDAY_23_00_TO_23_59
        )
      )
    );
  }
}
