package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.folio.calendar.domain.dto.SingleDayOpeningDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.NormalOpenings;
import org.folio.calendar.utils.CalendarUtils;
import org.junit.jupiter.api.Test;

class CalendarUtilsSplitDateLimitTest {

  @Test
  void testInclusiveRestriction() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    // fills the entire range
    CalendarUtils.splitCalendarIntoDates(
      Calendar
        .builder()
        .normalHour(NormalOpenings.MONDAY_23_00_TO_04_00_WRAPAROUND)
        .startDate(Dates.DATE_2021_01_01)
        .endDate(Dates.DATE_2021_01_07)
        .build(),
      map,
      Dates.DATE_2021_01_03,
      Dates.DATE_2021_01_05
    );

    assertThat(map.size(), is(3));
  }

  @Test
  void testExclusiveRestriction() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    // fills the entire range
    CalendarUtils.splitCalendarIntoDates(
      Calendar
        .builder()
        .normalHour(NormalOpenings.MONDAY_23_00_TO_04_00_WRAPAROUND)
        .startDate(Dates.DATE_2021_01_03)
        .endDate(Dates.DATE_2021_01_05)
        .build(),
      map,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_07
    );

    assertThat(map.size(), is(3));
  }

  @Test
  void testPartialRestriction() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    // fills the entire range
    CalendarUtils.splitCalendarIntoDates(
      Calendar
        .builder()
        .normalHour(NormalOpenings.MONDAY_23_00_TO_04_00_WRAPAROUND)
        .startDate(Dates.DATE_2021_01_01)
        .endDate(Dates.DATE_2021_01_05)
        .build(),
      map,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_07
    );

    assertThat(map.size(), is(5));
  }

  @Test
  void testOutOfBounds() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    // fills the entire range
    CalendarUtils.splitCalendarIntoDates(
      Calendar
        .builder()
        .normalHour(NormalOpenings.MONDAY_23_00_TO_04_00_WRAPAROUND)
        .startDate(Dates.DATE_2021_01_01)
        .endDate(Dates.DATE_2021_01_05)
        .build(),
      map,
      Dates.DATE_2021_03_16,
      Dates.DATE_2021_04_30
    );

    assertThat(map.size(), is(0));
  }
}
