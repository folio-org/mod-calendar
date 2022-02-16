package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.utils.DateUtils;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DateUtilsGetCurrentDateTest {

  @BeforeEach
  void clearOverride() {
    DateUtils.setCurrentDateOverride(null);
  }

  @Test
  void testNoOverride() {
    assertThat(
      "Current date is same as LocalDate::now",
      DateUtils.getCurrentDate().toString(),
      is(equalTo(LocalDate.now().toString()))
    );
  }

  @Test
  void testOverride() {
    DateUtils.setCurrentDateOverride(Dates.DATE_2021_01_01);
    assertThat(
      "Current date is same as overridden",
      DateUtils.getCurrentDate(),
      is(equalTo(Dates.DATE_2021_01_01))
    );
  }
}
