package org.folio.calendar.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.folio.calendar.testutils.Dates;
import org.folio.calendar.testutils.Times;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class DateUtilsTest {

  @Test
  void testNonOverlappingMultiDayDateRanges() {
    assertThat(
      "Distinct multi-day ranges (1 then 2) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_12_31
      ),
      is(false)
    );
    assertThat(
      "Distinct multi-day ranges (2 then 1) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_12_31,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_02
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day ranges (1 then 2) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_03,
        Dates.DATE_2021_01_04
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day ranges (2 then 1) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_03,
        Dates.DATE_2021_01_04,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_02
      ),
      is(false)
    );
  }

  @Test
  void testNonOverlappingSingleDayDateRanges() {
    assertThat(
      "Distinct multi-day ranges (1 then 2) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_12_07
      ),
      is(false)
    );
    assertThat(
      "Distinct multi-day ranges (2 then 1) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_01
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day ranges (1 then 2) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_03,
        Dates.DATE_2021_01_03
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day ranges (2 then 1) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_03,
        Dates.DATE_2021_01_03,
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_02
      ),
      is(false)
    );
  }

  @Test
  void testOverlappingIdenticalSingleDayDateRanges() {
    assertThat(
      "Identical single day date ranges should overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_02
      ),
      is(true)
    );
    assertThat(
      "Identical single day date ranges should overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_12_07
      ),
      is(true)
    );
  }

  @Test
  void testOverlappingSingleAndMultiDayDateRanges() {
    assertThat(
      "Single date range at start (1) of multi-day range (2) should overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_03_16
      ),
      is(true)
    );
    assertThat(
      "Single date range at end (1) of multi-day range (2) should overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_03_16
      ),
      is(true)
    );
    assertThat(
      "Single date range in the middle (1) of multi-day range (2) should overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day range (1) with single date range at start (2) should overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day range (1) with single date range at end (2) should overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_03_16
      ),
      is(true)
    );
    assertThat(
      "Multi-day range (1) with single date range in the middle (2) should overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_05_01,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_03_16
      ),
      is(true)
    );
  }

  @Test
  void testOverlappingMultiDayRanges() {
    assertThat(
      "Multi-day range (1,AB) contained inside multi-day range (2,CD) should overlap (-C-A-B-D-)",
      DateUtils.overlaps(
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_05_01,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day range (1,AB) enclosing multi-day range (2,CD) should overlap (-A-C-D-B-)",
      DateUtils.overlaps(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_07_04,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day range (1,AB) starting with and enclosing multi-day range (2,CD) should overlap (-A=C-D-B-)",
      DateUtils.overlaps(
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_07_04,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day range (1,AB) starting with and being enclosed by multi-day range (2,CD) should overlap (-A=C-B-D-)",
      DateUtils.overlaps(
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_05_01,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day range (1,AB) ending with and enclosing multi-day range (2,CD) should overlap (-A-C-D=B-)",
      DateUtils.overlaps(
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_07_04,
        Dates.DATE_2021_05_01,
        Dates.DATE_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day range (1,AB) ending with and being enclosed by multi-day range (2,CD) should overlap (-C-A-B=D-)",
      DateUtils.overlaps(
        Dates.DATE_2021_05_01,
        Dates.DATE_2021_07_04,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day range (1,AB) fully and only overlapping start of multi-day range (2,CD) should overlap (-A-C-B-D-)",
      DateUtils.overlaps(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_05_01,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day range (1,AB) fully and only overlapping end of multi-day range (2,CD) should overlap (-C-A-D-B-)",
      DateUtils.overlaps(
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_07_04,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_05_01
      ),
      is(true)
    );
  }

  @Test
  void testHourMinutesFormatting() {
    assertThat(
      "00:00 converts LocalTime to String",
      DateUtils.toTimeString(Times.TIME_00_00),
      is(equalTo(Times.TIME_00_00_STRING_HH_mm))
    );
    assertThat(
      "04:00 converts LocalTime to String",
      DateUtils.toTimeString(Times.TIME_04_00),
      is(equalTo(Times.TIME_04_00_STRING_HH_mm))
    );
    assertThat(
      "12:30 converts LocalTime to String",
      DateUtils.toTimeString(Times.TIME_12_30),
      is(equalTo(Times.TIME_12_30_STRING_HH_mm))
    );
    assertThat(
      "14:00 converts LocalTime to String",
      DateUtils.toTimeString(Times.TIME_14_00),
      is(equalTo(Times.TIME_14_00_STRING_HH_mm))
    );
    assertThat(
      "23:00 converts LocalTime to String",
      DateUtils.toTimeString(Times.TIME_23_00),
      is(equalTo(Times.TIME_23_00_STRING_HH_mm))
    );
    assertThat(
      "23:59 converts LocalTime to String",
      DateUtils.toTimeString(Times.TIME_23_59),
      is(equalTo(Times.TIME_23_59_STRING_HH_mm))
    );
  }

  @Test
  void testHourMinutesParsing() {
    assertThat(
      "00:00 converts String to LocalTime",
      DateUtils.fromTimeString(Times.TIME_00_00_STRING_HH_mm),
      is(equalTo(Times.TIME_00_00))
    );
    assertThat(
      "04:00 converts String to LocalTime",
      DateUtils.fromTimeString(Times.TIME_04_00_STRING_HH_mm),
      is(equalTo(Times.TIME_04_00))
    );
    assertThat(
      "12:30 converts String to LocalTime",
      DateUtils.fromTimeString(Times.TIME_12_30_STRING_HH_mm),
      is(equalTo(Times.TIME_12_30))
    );
    assertThat(
      "14:00 converts String to LocalTime",
      DateUtils.fromTimeString(Times.TIME_14_00_STRING_HH_mm),
      is(equalTo(Times.TIME_14_00))
    );
    assertThat(
      "23:00 converts String to LocalTime",
      DateUtils.fromTimeString(Times.TIME_23_00_STRING_HH_mm),
      is(equalTo(Times.TIME_23_00))
    );
    assertThat(
      "23:59 converts String to LocalTime",
      DateUtils.fromTimeString(Times.TIME_23_59_STRING_HH_mm),
      is(equalTo(Times.TIME_23_59))
    );
  }
}
