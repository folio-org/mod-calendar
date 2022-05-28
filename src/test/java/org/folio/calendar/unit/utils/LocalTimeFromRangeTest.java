package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.LocalTimeFromRange;
import org.junit.jupiter.api.Test;

class LocalTimeFromRangeTest {

  static final LocalTimeFromRange<Object> START_04_00 = new LocalTimeFromRange<>(
    Times.TIME_04_00,
    null,
    true
  );
  static final LocalTimeFromRange<Object> START_15_00 = new LocalTimeFromRange<>(
    Times.TIME_15_00,
    null,
    true
  );
  static final LocalTimeFromRange<Object> END_04_00 = new LocalTimeFromRange<>(
    Times.TIME_04_00,
    null,
    false
  );
  static final LocalTimeFromRange<Object> END_15_00 = new LocalTimeFromRange<>(
    Times.TIME_15_00,
    null,
    false
  );

  @Test
  void testCompare() {
    // we do not care about self -> self as that order is indeterminate and
    // stable sort is not needed for the use case
    assertThat(START_04_00.compareTo(START_15_00), is(lessThan(0)));
    assertThat(START_04_00.compareTo(END_04_00), is(lessThan(0)));
    assertThat(START_04_00.compareTo(END_15_00), is(lessThan(0)));

    assertThat(START_15_00.compareTo(START_04_00), is(greaterThan(0)));
    assertThat(START_15_00.compareTo(END_04_00), is(greaterThan(0)));
    assertThat(START_15_00.compareTo(END_15_00), is(lessThan(0)));

    assertThat(END_04_00.compareTo(START_04_00), is(greaterThan(0)));
    assertThat(END_04_00.compareTo(START_15_00), is(lessThan(0)));
    assertThat(END_04_00.compareTo(END_15_00), is(lessThan(0)));

    assertThat(END_15_00.compareTo(START_04_00), is(greaterThan(0)));
    assertThat(END_15_00.compareTo(START_15_00), is(greaterThan(0)));
    assertThat(END_15_00.compareTo(END_04_00), is(greaterThan(0)));
  }
}
