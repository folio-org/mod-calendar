package org.folio.calendar.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.folio.calendar.testutils.Times;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class TimeConstantsTest {

  @Test
  void testMinTimeIsMidnight() {
    assertThat(TimeConstants.TIME_MIN, is(equalTo(LocalTime.MIN)));
    assertThat(TimeConstants.TIME_MIN, is(equalTo(Times.TIME_00_00)));
    assertThat(TimeConstants.TIME_MIN_STRING, is(equalTo(Times.TIME_00_00_STRING_HH_mm)));
    assertThat(
      DateUtils.toTimeString(TimeConstants.TIME_MIN),
      is(equalTo(Times.TIME_00_00_STRING_HH_mm))
    );
    assertThat(
      DateUtils.fromTimeString(TimeConstants.TIME_MIN_STRING),
      is(equalTo(Times.TIME_00_00))
    );
  }

  @Test
  void testMaxTimeIs23Hours59Minutes() {
    assertThat(TimeConstants.TIME_MAX, is(equalTo(LocalTime.MAX.truncatedTo(ChronoUnit.MINUTES))));
    assertThat(TimeConstants.TIME_MAX, is(equalTo(Times.TIME_23_59)));
    assertThat(TimeConstants.TIME_MAX_STRING, is(equalTo(Times.TIME_23_59_STRING_HH_mm)));
    assertThat(
      DateUtils.toTimeString(TimeConstants.TIME_MAX),
      is(equalTo(Times.TIME_23_59_STRING_HH_mm))
    );
    assertThat(
      DateUtils.fromTimeString(TimeConstants.TIME_MAX_STRING),
      is(equalTo(Times.TIME_23_59))
    );
  }

  @Test
  void testAllDayRange() {
    assertThat(TimeConstants.ALL_DAY.getStartTime(), is(equalTo(Times.TIME_00_00_STRING_HH_mm)));
    assertThat(TimeConstants.ALL_DAY.getEndTime(), is(equalTo(Times.TIME_23_59_STRING_HH_mm)));
  }
}
