package org.folio.calendar.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Periods;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class DateUtilsOverlapMixedTest {

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
  void testNonOverlappingPeriodList() {
    assertThat(
      "A period should not overlap with a list of empty periods",
      DateUtils.overlapsPeriodList(Periods.PERIOD_2021_01_01_TO_2021_04_30, Arrays.asList()),
      is(nullValue())
    );
    assertThat(
      "A period should not overlap with a list of a single distinct period",
      DateUtils.overlapsPeriodList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(Periods.PERIOD_2021_07_04_TO_2021_09_22)
      ),
      is(nullValue())
    );
    assertThat(
      "A period should not overlap with a list of a multiple, self-overlapping periods",
      DateUtils.overlapsPeriodList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(
          Periods.PERIOD_2021_07_04_TO_2021_09_22,
          Periods.PERIOD_2021_05_01_TO_2021_09_22
        )
      ),
      is(nullValue())
    );
  }

  @Test
  void testOverlappingPeriodList() {
    assertThat(
      "A period should overlap with a list of a single overlapping periods",
      DateUtils.overlapsPeriodList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(Periods.PERIOD_2021_03_16_TO_2021_05_01)
      ),
      is(Periods.PERIOD_2021_03_16_TO_2021_05_01)
    );
    assertThat(
      "A period should overlap with a list of a non-overlapping periods and one overlapping",
      DateUtils.overlapsPeriodList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(
          Periods.PERIOD_2021_07_04_TO_2021_09_22,
          Periods.PERIOD_2021_03_16_TO_2021_05_01
        )
      ),
      is(Periods.PERIOD_2021_03_16_TO_2021_05_01)
    );
    assertThat(
      "A period should overlap with a list of multiple overlapping periods",
      DateUtils.overlapsPeriodList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(
          Periods.PERIOD_2021_01_01_TO_2021_01_01,
          Periods.PERIOD_2021_03_16_TO_2021_03_16
        )
      ),
      is(notNullValue())
    );
    assertThat(
      "A period should not overlap with a list of a multiple, self-overlapping periods",
      DateUtils.overlapsPeriodList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(
          Periods.PERIOD_2021_07_04_TO_2021_09_22,
          Periods.PERIOD_2021_05_01_TO_2021_09_22
        )
      ),
      is(nullValue())
    );
  }

  @Test
  void testNonOverlappingPeriodWithCalendarList() {
    assertThat(
      "A period should not overlap with a list of empty calendars",
      DateUtils.overlapsCalendarList(Periods.PERIOD_2021_01_01_TO_2021_04_30, Arrays.asList()),
      is(nullValue())
    );
    assertThat(
      "A period should not overlap with a list of a single distinct calendar",
      DateUtils.overlapsCalendarList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(Calendars.CALENDAR_2021_07_04_TO_2021_09_22)
      ),
      is(nullValue())
    );
    assertThat(
      "A period should not overlap with a list of a multiple, self-overlapping calendars",
      DateUtils.overlapsCalendarList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(
          Calendars.CALENDAR_2021_07_04_TO_2021_09_22,
          Calendars.CALENDAR_2021_05_01_TO_2021_09_22
        )
      ),
      is(nullValue())
    );
  }

  @Test
  void testOverlappingPeriodWithCalendarList() {
    assertThat(
      "A period should overlap with a list of a single overlapping calendars",
      DateUtils.overlapsCalendarList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(Calendars.CALENDAR_2021_03_16_TO_2021_05_01)
      ),
      is(Calendars.CALENDAR_2021_03_16_TO_2021_05_01)
    );
    assertThat(
      "A period should overlap with a list of a non-overlapping calendars and one overlapping",
      DateUtils.overlapsCalendarList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(
          Calendars.CALENDAR_2021_07_04_TO_2021_09_22,
          Calendars.CALENDAR_2021_03_16_TO_2021_05_01
        )
      ),
      is(Calendars.CALENDAR_2021_03_16_TO_2021_05_01)
    );
    assertThat(
      "A period should overlap with a list of multiple overlapping calendars",
      DateUtils.overlapsCalendarList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(
          Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
          Calendars.CALENDAR_2021_03_16_TO_2021_03_16
        )
      ),
      is(notNullValue())
    );
    assertThat(
      "A period should not overlap with a list of a multiple, self-overlapping periods",
      DateUtils.overlapsPeriodList(
        Periods.PERIOD_2021_01_01_TO_2021_04_30,
        Arrays.asList(
          Periods.PERIOD_2021_07_04_TO_2021_09_22,
          Periods.PERIOD_2021_05_01_TO_2021_09_22
        )
      ),
      is(nullValue())
    );
  }
}
