package org.folio.calendar.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.folio.calendar.testutils.TimeConstants;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class DateUtilsTest {

  @Test
  void testHourMinutesFormatting() {
    assertThat(
      DateUtils.toTimeString(TimeConstants.TIME_00_00),
      is(equalTo(TimeConstants.TIME_00_00_STRING_HH_mm))
    );
    assertThat(
      DateUtils.toTimeString(TimeConstants.TIME_04_00),
      is(equalTo(TimeConstants.TIME_04_00_STRING_HH_mm))
    );
    assertThat(
      DateUtils.toTimeString(TimeConstants.TIME_12_30),
      is(equalTo(TimeConstants.TIME_12_30_STRING_HH_mm))
    );
    assertThat(
      DateUtils.toTimeString(TimeConstants.TIME_14_00),
      is(equalTo(TimeConstants.TIME_14_00_STRING_HH_mm))
    );
    assertThat(
      DateUtils.toTimeString(TimeConstants.TIME_23_00),
      is(equalTo(TimeConstants.TIME_23_00_STRING_HH_mm))
    );
    assertThat(
      DateUtils.toTimeString(TimeConstants.TIME_23_59),
      is(equalTo(TimeConstants.TIME_23_59_STRING_HH_mm))
    );
  }

  @Test
  void testHourMinutesParsing() {
    assertThat(
      DateUtils.fromTimeString(TimeConstants.TIME_00_00_STRING_HH_mm),
      is(equalTo(TimeConstants.TIME_00_00))
    );
    assertThat(
      DateUtils.fromTimeString(TimeConstants.TIME_04_00_STRING_HH_mm),
      is(equalTo(TimeConstants.TIME_04_00))
    );
    assertThat(
      DateUtils.fromTimeString(TimeConstants.TIME_12_30_STRING_HH_mm),
      is(equalTo(TimeConstants.TIME_12_30))
    );
    assertThat(
      DateUtils.fromTimeString(TimeConstants.TIME_14_00_STRING_HH_mm),
      is(equalTo(TimeConstants.TIME_14_00))
    );
    assertThat(
      DateUtils.fromTimeString(TimeConstants.TIME_23_00_STRING_HH_mm),
      is(equalTo(TimeConstants.TIME_23_00))
    );
    assertThat(
      DateUtils.fromTimeString(TimeConstants.TIME_23_59_STRING_HH_mm),
      is(equalTo(TimeConstants.TIME_23_59))
    );
  }
}
