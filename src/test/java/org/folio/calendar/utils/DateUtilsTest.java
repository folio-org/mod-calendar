package org.folio.calendar.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.folio.calendar.testutils.Times;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class DateUtilsTest {

  @Test
  void testHourMinutesFormatting() {
    assertThat(
      DateUtils.toTimeString(Times.TIME_00_00),
      is(equalTo(Times.TIME_00_00_STRING_HH_mm))
    );
    assertThat(
      DateUtils.toTimeString(Times.TIME_04_00),
      is(equalTo(Times.TIME_04_00_STRING_HH_mm))
    );
    assertThat(
      DateUtils.toTimeString(Times.TIME_12_30),
      is(equalTo(Times.TIME_12_30_STRING_HH_mm))
    );
    assertThat(
      DateUtils.toTimeString(Times.TIME_14_00),
      is(equalTo(Times.TIME_14_00_STRING_HH_mm))
    );
    assertThat(
      DateUtils.toTimeString(Times.TIME_23_00),
      is(equalTo(Times.TIME_23_00_STRING_HH_mm))
    );
    assertThat(
      DateUtils.toTimeString(Times.TIME_23_59),
      is(equalTo(Times.TIME_23_59_STRING_HH_mm))
    );
  }

  @Test
  void testHourMinutesParsing() {
    assertThat(
      DateUtils.fromTimeString(Times.TIME_00_00_STRING_HH_mm),
      is(equalTo(Times.TIME_00_00))
    );
    assertThat(
      DateUtils.fromTimeString(Times.TIME_04_00_STRING_HH_mm),
      is(equalTo(Times.TIME_04_00))
    );
    assertThat(
      DateUtils.fromTimeString(Times.TIME_12_30_STRING_HH_mm),
      is(equalTo(Times.TIME_12_30))
    );
    assertThat(
      DateUtils.fromTimeString(Times.TIME_14_00_STRING_HH_mm),
      is(equalTo(Times.TIME_14_00))
    );
    assertThat(
      DateUtils.fromTimeString(Times.TIME_23_00_STRING_HH_mm),
      is(equalTo(Times.TIME_23_00))
    );
    assertThat(
      DateUtils.fromTimeString(Times.TIME_23_59_STRING_HH_mm),
      is(equalTo(Times.TIME_23_59))
    );
  }
}
