package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.folio.calendar.domain.dto.SingleDayOpeningDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningRangeDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.NormalOpenings;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.CalendarUtils;
import org.junit.jupiter.api.Test;

class CalendarUtilsNormalOpeningSplitTest {

  @Test
  void testNone() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().build(),
      map,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_01
    );

    assertThat(map.size(), is(0));
  }

  @Test
  void testRegularOpening() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().normalHour(NormalOpenings.MONDAY_00_00_TO_12_30).build(),
      map,
      // include a full week
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_07
    );

    assertThat(map.size(), is(1));
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_04,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_04)
          .allDay(false)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_12_30)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
  }

  @Test
  void testAllDayOpenings() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().normalHour(NormalOpenings.SUNDAY_MONDAY_ALL_DAY).build(),
      map,
      // include a full week
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_07
    );

    assertThat(map.size(), is(2));
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_03,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_03)
          .allDay(true)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_04,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_04)
          .allDay(true)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
  }

  @Test
  void testLargeOpeningSpan() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().normalHour(NormalOpenings.WEDNESDAY_23_00_TO_SUNDAY_23_59).build(),
      map,
      // include a full week
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_07
    );

    assertThat(map.size(), is(5));
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_06,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_06)
          .allDay(false)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_23_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_07,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_07)
          .allDay(true)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_01,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_01)
          .allDay(true)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
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
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_03,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_03)
          .allDay(true)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
  }

  @Test
  void testMultipleOpeningsWraparound() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().normalHour(NormalOpenings.MONDAY_23_00_TO_04_00_WRAPAROUND).build(),
      map,
      Dates.DATE_2021_01_03,
      Dates.DATE_2021_01_05
    );

    assertThat(map.size(), is(3));
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_03,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_03)
          .allDay(true)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_04,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_04)
          .allDay(false)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_04_00)
              .build()
          )
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_23_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_05,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_05)
          .allDay(true)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
  }
}
