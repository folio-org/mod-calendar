package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.utils.DateUtils;
import org.junit.jupiter.api.Test;

class DateUtilsDateRangeTest {

  @Test
  void testGetDateRange() {
    assertThat(
      DateUtils.getDateRange(Dates.DATE_2021_01_01, Dates.DATE_2021_01_01),
      contains(Dates.DATE_2021_01_01)
    );
    assertThat(
      DateUtils.getDateRange(Dates.DATE_2021_01_01, Dates.DATE_2021_01_02),
      contains(Dates.DATE_2021_01_01, Dates.DATE_2021_01_02)
    );
    assertThat(
      DateUtils.getDateRange(Dates.DATE_2021_01_01, Dates.DATE_2021_01_04),
      contains(
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_03,
        Dates.DATE_2021_01_04
      )
    );
  }

  @Test
  void testImproper() {
    assertThat(DateUtils.getDateRange(Dates.DATE_2021_01_04, Dates.DATE_2021_01_01), hasSize(0));
    assertThat(DateUtils.getDateRange(Dates.DATE_2021_01_04, Dates.DATE_2021_01_03), hasSize(0));
  }
}
