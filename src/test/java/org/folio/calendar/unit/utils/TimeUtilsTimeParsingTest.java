package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.TimeUtils;
import org.junit.jupiter.api.Test;

class TimeUtilsTimeParsingTest {

  @Test
  void testHourMinutesFormatting() {
    assertThat(
      "00:00 converts LocalTime to String",
      TimeUtils.toTimeString(Times.TIME_00_00),
      is(equalTo(Times.TIME_00_00_STRING_HH_mm))
    );
    assertThat(
      "04:00 converts LocalTime to String",
      TimeUtils.toTimeString(Times.TIME_04_00),
      is(equalTo(Times.TIME_04_00_STRING_HH_mm))
    );
    assertThat(
      "12:30 converts LocalTime to String",
      TimeUtils.toTimeString(Times.TIME_12_30),
      is(equalTo(Times.TIME_12_30_STRING_HH_mm))
    );
    assertThat(
      "14:00 converts LocalTime to String",
      TimeUtils.toTimeString(Times.TIME_14_00),
      is(equalTo(Times.TIME_14_00_STRING_HH_mm))
    );
    assertThat(
      "23:00 converts LocalTime to String",
      TimeUtils.toTimeString(Times.TIME_23_00),
      is(equalTo(Times.TIME_23_00_STRING_HH_mm))
    );
    assertThat(
      "23:59 converts LocalTime to String",
      TimeUtils.toTimeString(Times.TIME_23_59),
      is(equalTo(Times.TIME_23_59_STRING_HH_mm))
    );
  }

  @Test
  void testHourMinutesParsing() {
    assertThat(
      "00:00 converts String to LocalTime",
      TimeUtils.fromTimeString(Times.TIME_00_00_STRING_HH_mm),
      is(equalTo(Times.TIME_00_00))
    );
    assertThat(
      "04:00 converts String to LocalTime",
      TimeUtils.fromTimeString(Times.TIME_04_00_STRING_HH_mm),
      is(equalTo(Times.TIME_04_00))
    );
    assertThat(
      "12:30 converts String to LocalTime",
      TimeUtils.fromTimeString(Times.TIME_12_30_STRING_HH_mm),
      is(equalTo(Times.TIME_12_30))
    );
    assertThat(
      "14:00 converts String to LocalTime",
      TimeUtils.fromTimeString(Times.TIME_14_00_STRING_HH_mm),
      is(equalTo(Times.TIME_14_00))
    );
    assertThat(
      "23:00 converts String to LocalTime",
      TimeUtils.fromTimeString(Times.TIME_23_00_STRING_HH_mm),
      is(equalTo(Times.TIME_23_00))
    );
    assertThat(
      "23:59 converts String to LocalTime",
      TimeUtils.fromTimeString(Times.TIME_23_59_STRING_HH_mm),
      is(equalTo(Times.TIME_23_59))
    );
  }
}
