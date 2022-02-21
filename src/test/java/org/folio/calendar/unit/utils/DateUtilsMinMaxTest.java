package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.utils.DateUtils;
import org.junit.jupiter.api.Test;

public class DateUtilsMinMaxTest {

  @Test
  void testMinNull() {
    assertThat("min(null, null) is null", DateUtils.min(null, null), is(nullValue()));
    assertThat(
      "min(null, foo) is foo",
      DateUtils.min(null, Dates.DATE_2021_01_01),
      is(Dates.DATE_2021_01_01)
    );
    assertThat(
      "min(foo, null) is foo",
      DateUtils.min(Dates.DATE_2021_01_01, null),
      is(Dates.DATE_2021_01_01)
    );
  }

  @Test
  void testMinDifferent() {
    assertThat(
      "min(a, b) is a when a < b",
      DateUtils.min(Dates.DATE_2021_01_01, Dates.DATE_2021_01_02),
      is(Dates.DATE_2021_01_01)
    );
    assertThat(
      "min(b, a) is a when a < b",
      DateUtils.min(Dates.DATE_2021_01_02, Dates.DATE_2021_01_01),
      is(Dates.DATE_2021_01_01)
    );
  }

  @Test
  void testMinEqual() {
    assertThat(
      "min(a, a) is a",
      DateUtils.min(Dates.DATE_2021_01_01, Dates.DATE_2021_01_01),
      is(Dates.DATE_2021_01_01)
    );
  }

  @Test
  void testMaxNull() {
    assertThat("max(null, null) is null", DateUtils.max(null, null), is(nullValue()));
    assertThat(
      "max(null, foo) is foo",
      DateUtils.max(null, Dates.DATE_2021_01_01),
      is(Dates.DATE_2021_01_01)
    );
    assertThat(
      "max(foo, null) is foo",
      DateUtils.max(Dates.DATE_2021_01_01, null),
      is(Dates.DATE_2021_01_01)
    );
  }

  @Test
  void testMaxDifferent() {
    assertThat(
      "max(a, b) is b when a < b",
      DateUtils.max(Dates.DATE_2021_01_01, Dates.DATE_2021_01_02),
      is(Dates.DATE_2021_01_02)
    );
    assertThat(
      "max(b, a) is b when a < b",
      DateUtils.max(Dates.DATE_2021_01_02, Dates.DATE_2021_01_01),
      is(Dates.DATE_2021_01_02)
    );
  }

  @Test
  void testMaxEqual() {
    assertThat(
      "max(a, a) is a",
      DateUtils.max(Dates.DATE_2021_01_01, Dates.DATE_2021_01_01),
      is(Dates.DATE_2021_01_01)
    );
  }
}
