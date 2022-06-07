package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.folio.calendar.domain.dto.SingleDayOpeningDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningRangeDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.ExceptionRanges;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.CalendarUtils;
import org.junit.jupiter.api.Test;

class CalendarUtilsExceptionSplitTest {

  @Test
  void testNoExceptions() {
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
  void testOutOfRange() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().exception(ExceptionRanges.CLOSED_JAN_3_TO_JAN_4).build(),
      map,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_01
    );

    assertThat(map.size(), is(0));
  }

  @Test
  void testClosure() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().exception(ExceptionRanges.CLOSED_JAN_3_TO_JAN_4).build(),
      map,
      Dates.DATE_2021_01_04,
      Dates.DATE_2021_01_04
    );

    assertThat(map.size(), is(1));
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_04,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_04)
          .allDay(true)
          .open(false)
          .exceptional(true)
          .exceptionName(ExceptionRanges.CLOSED_JAN_3_TO_JAN_4.getName())
          .build()
      )
    );
  }

  @Test
  void testAllDayOpening() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().exception(ExceptionRanges.OPEN_ALL_DAY_JAN_1_THRU_JAN_4).build(),
      map,
      Dates.DATE_2021_01_04,
      Dates.DATE_2021_01_04
    );

    assertThat(map.size(), is(1));
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
          .exceptional(true)
          .exceptionName(ExceptionRanges.OPEN_ALL_DAY_JAN_1_THRU_JAN_4.getName())
          .build()
      )
    );
  }

  @Test
  void testSomeOpenDays() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().exception(ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_AND_JAN_4).build(),
      map,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_04
    );

    assertThat(map.size(), is(4));
    assertThat(
      map,
      allOf(
        hasEntry(
          Dates.DATE_2021_01_01,
          SingleDayOpeningDTO
            .builder()
            .date(Dates.DATE_2021_01_01)
            .allDay(false)
            .open(true)
            .opening(
              SingleDayOpeningRangeDTO
                .builder()
                .startTime(Times.TIME_04_00)
                .endTime(Times.TIME_14_59)
                .build()
            )
            .exceptional(true)
            .exceptionName(ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_AND_JAN_4.getName())
            .build()
        ),
        hasEntry(
          Dates.DATE_2021_01_02,
          SingleDayOpeningDTO
            .builder()
            .date(Dates.DATE_2021_01_02)
            .allDay(true)
            .open(false)
            .exceptional(true)
            .exceptionName(ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_AND_JAN_4.getName())
            .build()
        ),
        hasEntry(
          Dates.DATE_2021_01_03,
          SingleDayOpeningDTO
            .builder()
            .date(Dates.DATE_2021_01_03)
            .allDay(true)
            .open(false)
            .exceptional(true)
            .exceptionName(ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_AND_JAN_4.getName())
            .build()
        ),
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
                .startTime(Times.TIME_04_00)
                .endTime(Times.TIME_14_59)
                .build()
            )
            .exceptional(true)
            .exceptionName(ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_AND_JAN_4.getName())
            .build()
        )
      )
    );
  }

  @Test
  void testStartOfDayOpening() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().exception(ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1).build(),
      map,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_04
    );

    assertThat(map.size(), is(1));
    assertThat(
      map,
      hasEntry(
        Dates.DATE_2021_01_01,
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_01)
          .allDay(false)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_14_59)
              .build()
          )
          .exceptional(true)
          .exceptionName(ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1.getName())
          .build()
      )
    );
  }

  @Test
  void testEndOfDayOpening() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar.builder().exception(ExceptionRanges.OPEN_15_00_TO_23_59_JAN_4).build(),
      map,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_04
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
              .startTime(Times.TIME_15_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(true)
          .exceptionName(ExceptionRanges.OPEN_15_00_TO_23_59_JAN_4.getName())
          .build()
      )
    );
  }

  @Test
  void testMultipleOpenings() {
    Map<LocalDate, SingleDayOpeningDTO> map = new HashMap<>();
    CalendarUtils.splitCalendarIntoDates(
      Calendar
        .builder()
        .exception(ExceptionRanges.OPEN_04_00_TO_14_59_AND_18_12_TO_23_00_JAN_4)
        .build(),
      map,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_04
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
              .startTime(Times.TIME_04_00)
              .endTime(Times.TIME_14_59)
              .build()
          )
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_18_12)
              .endTime(Times.TIME_23_00)
              .build()
          )
          .exceptional(true)
          .exceptionName(ExceptionRanges.OPEN_04_00_TO_14_59_AND_18_12_TO_23_00_JAN_4.getName())
          .build()
      )
    );
  }
}
