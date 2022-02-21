package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.utils.DateUtils;
import org.junit.jupiter.api.Test;

class DateUtilsOverlapPeriodsTest {

  @Test
  void testNonOverlappingMultiDayPeriods() {
    Calendar.builder().startDate(Dates.DATE_2021_01_01).endDate(Dates.DATE_2021_01_01).build();
    assertThat(
      "Distinct multi-day periods (1 then 2) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_07_04_TO_2021_09_22
      ),
      is(false)
    );
    assertThat(
      "Distinct multi-day periods (2 then 1) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_07_04_TO_2021_09_22,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day periods (1 then 2) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_05_01_TO_2021_09_22
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day periods (2 then 1) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_05_01_TO_2021_09_22,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(false)
    );
  }

  @Test
  void testNonOverlappingSingleDayPeriods() {
    assertThat(
      "Distinct single-day periods (1 then 2) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_01_01,
        Periods.PERIOD_2021_03_16_TO_2021_03_16
      ),
      is(false)
    );
    assertThat(
      "Distinct single-day periods (2 then 1) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_03_16_TO_2021_03_16,
        Periods.PERIOD_2021_01_01_TO_2021_01_01
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent single-day periods (1 then 2) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_01_01,
        Periods.PERIOD_2021_01_02_TO_2021_01_02
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent single-day periods (2 then 1) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_02_TO_2021_01_02,
        Periods.PERIOD_2021_01_01_TO_2021_01_01
      ),
      is(false)
    );
  }

  @Test
  void testOverlappingIdenticalSingleDayPeriods() {
    assertThat(
      "Identical single day periods should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_01_01,
        Periods.PERIOD_2021_01_01_TO_2021_01_01
      ),
      is(true)
    );
    assertThat(
      "Identical single day periods should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
  }

  @Test
  void testOverlappingSingleAndMultiDayPeriods() {
    assertThat(
      "Single period at start (1) of multi-day period (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_01_01,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Single period at end (1) of multi-day period (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_04_30_TO_2021_04_30,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Single period in the middle (1) of multi-day period (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_03_16_TO_2021_03_16,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1) with single day period at start (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_01_01_TO_2021_01_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1) with single day period at end (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_04_30_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1) with single day period in the middle (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_03_16_TO_2021_03_16
      ),
      is(true)
    );
  }

  @Test
  void testOverlappingMultiDayPeriods() {
    assertThat(
      "Multi-day period (1,AB) contained inside multi-day period (2,CD) should overlap (-C-A-B-D-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_03_16_TO_2021_05_01,
        Periods.PERIOD_2021_01_01_TO_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) enclosing multi-day period (2,CD) should overlap (-A-C-D-B-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_07_04,
        Periods.PERIOD_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) starting with and enclosing multi-day period (2,CD) should overlap (-A=C-D-B-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_07_04,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) starting with and being enclosed by multi-day period (2,CD) should overlap (-A=C-B-D-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_01_01_TO_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) ending with and enclosing multi-day period (2,CD) should overlap (-A-C-D=B-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_05_01,
        Periods.PERIOD_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) ending with and being enclosed by multi-day period (2,CD) should overlap (-C-A-B=D-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_03_16_TO_2021_05_01,
        Periods.PERIOD_2021_01_01_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) fully and only overlapping start of multi-day period (2,CD) should overlap (-A-C-B-D-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) fully and only overlapping end of multi-day period (2,CD) should overlap (-C-A-D-B-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_03_16_TO_2021_05_01,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
  }
}
