package org.folio.calendar.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.testconstants.Calendars;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class DateUtilsOverlapCalendarsTest {

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
}
