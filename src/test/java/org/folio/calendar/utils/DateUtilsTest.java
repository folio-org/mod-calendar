package org.folio.calendar.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.Times;
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

  @Test
  void testNonOverlappingMultiDayCalendars() {
    assertThat(
      "Distinct multi-day calendars (1 then 2) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_07_04_TO_2021_09_22
      ),
      is(false)
    );
    assertThat(
      "Distinct multi-day calendars (2 then 1) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_07_04_TO_2021_09_22,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day calendars (1 then 2) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_05_01_TO_2021_09_22
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day calendars (2 then 1) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_05_01_TO_2021_09_22,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(false)
    );
  }

  @Test
  void testNonOverlappingSingleDayCalendars() {
    assertThat(
      "Distinct single-day calendars (1 then 2) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Calendars.CALENDAR_2021_03_16_TO_2021_03_16
      ),
      is(false)
    );
    assertThat(
      "Distinct single-day calendars (2 then 1) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_03_16_TO_2021_03_16,
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent single-day calendars (1 then 2) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Calendars.CALENDAR_2021_01_02_TO_2021_01_02
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent single-day calendars (2 then 1) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_02_TO_2021_01_02,
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01
      ),
      is(false)
    );
  }

  @Test
  void testOverlappingIdenticalSingleDayCalendars() {
    assertThat(
      "Identical single day calendars should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01
      ),
      is(true)
    );
    assertThat(
      "Identical single day calendars should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
  }

  @Test
  void testOverlappingSingleAndMultiDayCalendars() {
    assertThat(
      "Single calendar at start (1) of multi-day calendar (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Single calendar at end (1) of multi-day calendar (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_04_30_TO_2021_04_30,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Single calendar in the middle (1) of multi-day calendar (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_03_16_TO_2021_03_16,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1) with single day calendar at start (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1) with single day calendar at end (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_04_30_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1) with single day calendar in the middle (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_03_16_TO_2021_03_16
      ),
      is(true)
    );
  }

  @Test
  void testOverlappingMultiDayCalendars() {
    assertThat(
      "Multi-day calendar (1,AB) contained inside multi-day calendar (2,CD) should overlap (-C-A-B-D-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01,
        Calendars.CALENDAR_2021_01_01_TO_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) enclosing multi-day calendar (2,CD) should overlap (-A-C-D-B-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_07_04,
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) starting with and enclosing multi-day calendar (2,CD) should overlap (-A=C-D-B-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_07_04,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) starting with and being enclosed by multi-day calendar (2,CD) should overlap (-A=C-B-D-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_01_01_TO_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) ending with and enclosing multi-day calendar (2,CD) should overlap (-A-C-D=B-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_05_01,
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) ending with and being enclosed by multi-day calendar (2,CD) should overlap (-C-A-B=D-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01,
        Calendars.CALENDAR_2021_01_01_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) fully and only overlapping start of multi-day calendar (2,CD) should overlap (-A-C-B-D-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) fully and only overlapping end of multi-day calendar (2,CD) should overlap (-C-A-D-B-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
  }

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

  @Test
  void testNonOverlappingMultiDayPeriodCalendars() {
    Calendar.builder().startDate(Dates.DATE_2021_01_01).endDate(Dates.DATE_2021_01_01).build();
    assertThat(
      "Distinct multi-day period and calendar (1 then 2) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_07_04_TO_2021_09_22
      ),
      is(false)
    );
    assertThat(
      "Distinct multi-day calendar and period (1 then 2) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_07_04_TO_2021_09_22
      ),
      is(false)
    );
    assertThat(
      "Distinct multi-day period and calendar (2 then 1) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_07_04_TO_2021_09_22,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(false)
    );
    assertThat(
      "Distinct multi-day calendar and period (2 then 1) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_07_04_TO_2021_09_22,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day period and calendar (1 then 2) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_05_01_TO_2021_09_22
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day calendar and period (1 then 2) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_05_01_TO_2021_09_22
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day period and calendar (2 then 1) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_05_01_TO_2021_09_22,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent multi-day calendar and period (2 then 1) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_05_01_TO_2021_09_22,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(false)
    );
  }

  @Test
  void testNonOverlappingSingleDayPeriodCalendars() {
    assertThat(
      "Distinct single-day period and calendar (1 then 2) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_01_01,
        Calendars.CALENDAR_2021_03_16_TO_2021_03_16
      ),
      is(false)
    );
    assertThat(
      "Distinct single-day calendar and period (1 then 2) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Periods.PERIOD_2021_03_16_TO_2021_03_16
      ),
      is(false)
    );
    assertThat(
      "Distinct single-day period and calendar (2 then 1) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_03_16_TO_2021_03_16,
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01
      ),
      is(false)
    );
    assertThat(
      "Distinct single-day calendar and period (2 then 1) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_03_16_TO_2021_03_16,
        Periods.PERIOD_2021_01_01_TO_2021_01_01
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent single-day period and calendar (1 then 2) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_01_01,
        Calendars.CALENDAR_2021_01_02_TO_2021_01_02
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent single-day calendar and period (1 then 2) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Periods.PERIOD_2021_01_02_TO_2021_01_02
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent single-day period and calendar (2 then 1) do not overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_02_TO_2021_01_02,
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01
      ),
      is(false)
    );
    assertThat(
      "Distinct adjacent single-day calendar and period (2 then 1) do not overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_02_TO_2021_01_02,
        Periods.PERIOD_2021_01_01_TO_2021_01_01
      ),
      is(false)
    );
  }

  @Test
  void testOverlappingIdenticalSingleDayPeriodCalendars() {
    assertThat(
      "Identical single day period and calendar should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_01_01,
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01
      ),
      is(true)
    );
    assertThat(
      "Identical single day calendar and period should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Periods.PERIOD_2021_01_01_TO_2021_01_01
      ),
      is(true)
    );
    assertThat(
      "Identical single day period and calendar should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Identical single day calendar and period should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
  }

  @Test
  void testOverlappingSingleAndMultiDayPeriodCalendars() {
    assertThat(
      "Single period at start (1) of multi-day calendar (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_01_01,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Single calendar at start (1) of multi-day period (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Single period at end (1) of multi-day calendar (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_04_30_TO_2021_04_30,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Single calendar at end (1) of multi-day period (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_04_30_TO_2021_04_30,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Single period in the middle (1) of multi-day calendar (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_03_16_TO_2021_03_16,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Single calendar in the middle (1) of multi-day period (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_03_16_TO_2021_03_16,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1) with single day calendar at start (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1) with single day period at start (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_01_01_TO_2021_01_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1) with single day calendar at end (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_04_30_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1) with single day period at end (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_04_30_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1) with single day calendar in the middle (2) should overlap",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_03_16_TO_2021_03_16
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1) with single day period in the middle (2) should overlap",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_03_16_TO_2021_03_16
      ),
      is(true)
    );
  }

  @Test
  void testOverlappingMultiDayPeriodCalendars() {
    assertThat(
      "Multi-day period (1,AB) contained inside multi-day calendar (2,CD) should overlap (-C-A-B-D-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_03_16_TO_2021_05_01,
        Calendars.CALENDAR_2021_01_01_TO_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) contained inside multi-day period (2,CD) should overlap (-C-A-B-D-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01,
        Periods.PERIOD_2021_01_01_TO_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) enclosing multi-day calendar (2,CD) should overlap (-A-C-D-B-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_07_04,
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) enclosing multi-day period (2,CD) should overlap (-A-C-D-B-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_07_04,
        Periods.PERIOD_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) starting with and enclosing multi-day calendar (2,CD) should overlap (-A=C-D-B-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_07_04,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) starting with and enclosing multi-day period (2,CD) should overlap (-A=C-D-B-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_07_04,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) starting with and being enclosed by multi-day calendar (2,CD) should overlap (-A=C-B-D-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_01_01_TO_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) starting with and being enclosed by multi-day period (2,CD) should overlap (-A=C-B-D-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_01_01_TO_2021_07_04
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) ending with and enclosing multi-day calendar (2,CD) should overlap (-A-C-D=B-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_05_01,
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) ending with and enclosing multi-day period (2,CD) should overlap (-A-C-D=B-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_05_01,
        Periods.PERIOD_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) ending with and being enclosed by multi-day calendar (2,CD) should overlap (-C-A-B=D-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_03_16_TO_2021_05_01,
        Calendars.CALENDAR_2021_01_01_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) ending with and being enclosed by multi-day period (2,CD) should overlap (-C-A-B=D-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01,
        Periods.PERIOD_2021_01_01_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) fully and only overlapping start of multi-day calendar (2,CD) should overlap (-A-C-B-D-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) fully and only overlapping start of multi-day period (2,CD) should overlap (-A-C-B-D-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30,
        Periods.PERIOD_2021_03_16_TO_2021_05_01
      ),
      is(true)
    );
    assertThat(
      "Multi-day period (1,AB) fully and only overlapping end of multi-day calendar (2,CD) should overlap (-C-A-D-B-)",
      DateUtils.overlaps(
        Periods.PERIOD_2021_03_16_TO_2021_05_01,
        Calendars.CALENDAR_2021_01_01_TO_2021_04_30
      ),
      is(true)
    );
    assertThat(
      "Multi-day calendar (1,AB) fully and only overlapping end of multi-day period (2,CD) should overlap (-C-A-D-B-)",
      DateUtils.overlaps(
        Calendars.CALENDAR_2021_03_16_TO_2021_05_01,
        Periods.PERIOD_2021_01_01_TO_2021_04_30
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
