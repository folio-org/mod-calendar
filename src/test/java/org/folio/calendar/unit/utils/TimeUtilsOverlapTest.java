package org.folio.calendar.unit.utils;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import org.folio.calendar.domain.types.Weekday;
import org.folio.calendar.testconstants.NormalOpenings;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.LocalTimeFromRange;
import org.folio.calendar.utils.NormalOpeningUtils;
import org.folio.calendar.utils.TimeRange;
import org.folio.calendar.utils.TimeUtils;
import org.junit.jupiter.api.Test;

class TimeUtilsOverlapTest {

  static final EnumMap<Weekday, List<TimeRange>> SUNDAY_MONDAY_ALL_DAY = new EnumMap<>(
    Weekday.class
  );
  static final EnumMap<Weekday, List<TimeRange>> MONDAY_00_00_TO_12_30 = new EnumMap<>(
    Weekday.class
  );
  static final EnumMap<Weekday, List<TimeRange>> MONDAY_04_00_TO_14_59 = new EnumMap<>(
    Weekday.class
  );
  static final EnumMap<Weekday, List<TimeRange>> MONDAY_04_00_TO_23_59 = new EnumMap<>(
    Weekday.class
  );
  static final EnumMap<Weekday, List<TimeRange>> MONDAY_23_00_TO_23_59 = new EnumMap<>(
    Weekday.class
  );
  static final EnumMap<Weekday, List<TimeRange>> MONDAY_04_00_TO_TUESDAY_12_30 = new EnumMap<>(
    Weekday.class
  );
  static final EnumMap<Weekday, List<TimeRange>> TUESDAY_ALL_DAY = new EnumMap<>(Weekday.class);

  static {
    // NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
    //   SUNDAY_MONDAY_ALL_DAY,
    //   NormalOpenings.SUNDAY_MONDAY_ALL_DAY
    // );
    NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
      MONDAY_00_00_TO_12_30,
      NormalOpenings.MONDAY_00_00_TO_12_30
    );
    // NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
    //   MONDAY_04_00_TO_14_59,
    //   NormalOpenings.MONDAY_04_00_TO_14_59
    // );
    // NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
    //   MONDAY_04_00_TO_23_59,
    //   NormalOpenings.MONDAY_04_00_TO_23_59
    // );
    // NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
    //   MONDAY_23_00_TO_23_59,
    //   NormalOpenings.MONDAY_23_00_TO_23_59
    // );
    // NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
    //   MONDAY_04_00_TO_TUESDAY_12_30,
    //   NormalOpenings.MONDAY_04_00_TO_TUESDAY_12_30
    // );
    // NormalOpeningUtils.fillWeekdayMapWithTimeTuples(
    //   TUESDAY_ALL_DAY,
    //   NormalOpenings.TUESDAY_ALL_DAY
    // );
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
}
