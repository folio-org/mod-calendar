package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.TimeConstants;
import org.folio.calendar.utils.TimeUtils;
import org.junit.jupiter.api.Test;

class TimeConstantsTest {

  @Test
  void testMinTimeIsMidnight() {
    assertThat("TIME_MIN is LocalTime.MIN", TimeConstants.TIME_MIN, is(equalTo(LocalTime.MIN)));
    assertThat("TIME_MIN is 00:00", TimeConstants.TIME_MIN, is(equalTo(Times.TIME_00_00)));
    assertThat(
      "TIME_MIN_STRING is \"00:00\"",
      TimeConstants.TIME_MIN_STRING,
      is(equalTo(Times.TIME_00_00_STRING_HH_mm))
    );
    assertThat(
      "TIME_MIN converts to String \"00:00\"",
      TimeUtils.toTimeString(TimeConstants.TIME_MIN),
      is(equalTo(Times.TIME_00_00_STRING_HH_mm))
    );
    assertThat(
      "TIME_MIN_STRING converts to LocalTime 00:00",
      TimeUtils.fromTimeString(TimeConstants.TIME_MIN_STRING),
      is(equalTo(Times.TIME_00_00))
    );
  }

  @Test
  void testMaxTimeIs23Hours59Minutes() {
    assertThat(
      "TIME_MAX is LocalTime.MAX (ignoring seconds)",
      TimeConstants.TIME_MAX,
      is(equalTo(LocalTime.MAX.truncatedTo(ChronoUnit.MINUTES)))
    );
    assertThat("TIME_MAX is 23:59", TimeConstants.TIME_MAX, is(equalTo(Times.TIME_23_59)));
    assertThat(
      "TIME_MAX_STRING is \"23:59\"",
      TimeConstants.TIME_MAX_STRING,
      is(equalTo(Times.TIME_23_59_STRING_HH_mm))
    );
    assertThat(
      "TIME_MAX converts to String \"23:59\"",
      TimeUtils.toTimeString(TimeConstants.TIME_MAX),
      is(equalTo(Times.TIME_23_59_STRING_HH_mm))
    );
    assertThat(
      "TIME_MAX_STRING converts to LocalTime 23:59",
      TimeUtils.fromTimeString(TimeConstants.TIME_MAX_STRING),
      is(equalTo(Times.TIME_23_59))
    );
  }
}
