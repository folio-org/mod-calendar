package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.utils.DateUtils;
import org.junit.jupiter.api.Test;

class DateUtilsOverlapDateRangesTest {

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
      "Distinct single-day ranges (1 then 2) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_12_07
      ),
      is(false)
    );
    assertThat(
      "Distinct single-day ranges (2 then 1) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_12_07,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_01
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent single-day ranges (1 then 2) do not overlap",
      DateUtils.overlaps(
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_03,
        Dates.DATE_2021_01_03
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent single-day ranges (2 then 1) do not overlap",
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
  void testOverlappingMultiDayDateRanges() {
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
}
