package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
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

class CalendarUtilsFillClosedDatesTest {

  @Test
  void testEmpty() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.fillClosedDates(map, Dates.DATE_2021_01_01, Dates.DATE_2021_01_02);

    assertThat(map.size(), is(2));
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_01,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_01)
          .allDay(true)
          .open(false)
          .exceptional(false)
          .build()
      )
    );
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_02,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_02)
          .allDay(true)
          .open(false)
          .exceptional(false)
          .build()
      )
    );
  }

  @Test
  void testAlreadyFilled() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    // fills the entire range
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().normalHour(NormalOpenings.MONDAY_23_00_TO_04_00_WRAPAROUND).build(),
      map,
      Dates.DATE_2021_01_03,
      Dates.DATE_2021_01_05
    );

    Map<LocalDate, SingleDayOpeningDTO> beforeFill = new HashMap<>(map);
    CalendarUtils.fillClosedDates(map, Dates.DATE_2021_01_03, Dates.DATE_2021_01_05);

    assertThat(map, is(equalTo(beforeFill)));
  }
}
