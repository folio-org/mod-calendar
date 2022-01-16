package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.utils.DateUtils;
import org.junit.jupiter.api.Test;

class DateUtilsContainsTest {

  @Test
  void testContainsMiddle() {
    assertThat(
      "A date is contained when it is between the start and end",
      DateUtils.contains(Dates.DATE_2021_01_02, Dates.DATE_2021_01_01, Dates.DATE_2021_01_03),
      is(true)
    );
  }

  @Test
  void testContainsEdges() {
    assertThat(
      "A date is contained when it is equal to the start",
      DateUtils.contains(Dates.DATE_2021_01_01, Dates.DATE_2021_01_01, Dates.DATE_2021_01_03),
      is(true)
    );
    assertThat(
      "A date is contained when it is equal to the end",
      DateUtils.contains(Dates.DATE_2021_01_03, Dates.DATE_2021_01_01, Dates.DATE_2021_01_03),
      is(true)
    );
  }

  @Test
  void testContainsSingle() {
    assertThat(
      "A date is contained when it is equal to the start and end",
      DateUtils.contains(Dates.DATE_2021_01_01, Dates.DATE_2021_01_01, Dates.DATE_2021_01_01),
      is(true)
    );
  }

  @Test
  void testBefore() {
    assertThat(
      "A date is not contained when it is before the start",
      DateUtils.contains(Dates.DATE_2021_01_01, Dates.DATE_2021_01_02, Dates.DATE_2021_01_03),
      is(false)
    );
  }

  @Test
  void testAfter() {
    assertThat(
      "A date is not contained when it is after the end",
      DateUtils.contains(Dates.DATE_2021_01_04, Dates.DATE_2021_01_02, Dates.DATE_2021_01_03),
      is(false)
    );
  }
}
