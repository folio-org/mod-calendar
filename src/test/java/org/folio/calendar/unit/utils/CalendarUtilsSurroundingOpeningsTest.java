package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.List;
import org.folio.calendar.domain.dto.SingleDayOpeningDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningRangeDTO;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Names;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.CalendarUtils;
import org.junit.jupiter.api.Test;

class CalendarUtilsSurroundingOpeningsTest {

  @Test
  void testNoCalendars() {
    List<SingleDayOpeningDTO> openings = CalendarUtils
      .getSurroundingOpenings(Arrays.asList(), Dates.DATE_2021_01_02)
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_01)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_02)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_03)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build()
      )
    );
  }

  @Test
  void testEmptyCalendar() {
    List<SingleDayOpeningDTO> openings = CalendarUtils
      .getSurroundingOpenings(
        Arrays.asList(Calendars.CALENDAR_2021_01_01_TO_2021_07_04),
        Dates.DATE_2021_01_02
      )
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_01)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_02)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_03)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build()
      )
    );
  }

  @Test
  void testEmptySurroundingCalendars() {
    List<SingleDayOpeningDTO> openings = CalendarUtils
      .getSurroundingOpenings(
        Arrays.asList(
          Calendars.CALENDAR_2021_01_01_TO_2021_01_04,
          Calendars.CALENDAR_2021_04_30_TO_2021_09_22
        ),
        Dates.DATE_2021_01_06
      )
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_05)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_06)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_07)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build()
      )
    );
  }

  @Test
  void testInsideCalendar1() {
    List<SingleDayOpeningDTO> openings = CalendarUtils
      .getSurroundingOpenings(
        Arrays.asList(Calendars.CALENDAR_FULL_EXAMPLE_A),
        Dates.DATE_2021_01_05
      )
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_04)
          .open(true)
          .allDay(false)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_04_00)
              .endTime(Times.TIME_14_59)
              .build()
          )
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_05)
          .open(true)
          .allDay(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_06)
          .open(true)
          .allDay(false)
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
  }

  @Test
  void testInsideCalendar2() {
    List<SingleDayOpeningDTO> openings = CalendarUtils
      .getSurroundingOpenings(
        Arrays.asList(Calendars.CALENDAR_FULL_EXAMPLE_B),
        Dates.DATE_2021_01_05
      )
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      anyOf(
        hasItem(
          SingleDayOpeningDTO
            .builder()
            .date(Dates.DATE_2021_01_04)
            .open(true)
            .allDay(false)
            .opening(
              SingleDayOpeningRangeDTO
                .builder()
                .startTime(Times.TIME_00_00)
                .endTime(Times.TIME_12_30)
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
        ),
        hasItem(
          SingleDayOpeningDTO
            .builder()
            .date(Dates.DATE_2021_01_04)
            .open(true)
            .allDay(false)
            .opening(
              SingleDayOpeningRangeDTO
                .builder()
                .startTime(Times.TIME_23_00)
                .endTime(Times.TIME_23_59)
                .build()
            )
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
      )
    );
    assertThat(
      openings,
      hasItem(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_05)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build()
      )
    );
    assertThat(
      openings,
      hasItem(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_07)
          .open(true)
          .allDay(true)
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
  void testBeforeCalendar() {
    List<SingleDayOpeningDTO> openings = CalendarUtils
      .getSurroundingOpenings(
        Arrays.asList(Calendars.CALENDAR_FULL_EXAMPLE_E),
        Dates.DATE_2021_01_05
      )
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_04)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_05)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_03_18)
          .open(true)
          .allDay(true)
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
  void testAfterCalendar() {
    List<SingleDayOpeningDTO> openings = CalendarUtils
      .getSurroundingOpenings(
        Arrays.asList(Calendars.CALENDAR_FULL_EXAMPLE_C.withEndDate(Dates.DATE_2021_01_07)),
        Dates.DATE_2021_04_30
      )
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_07)
          .open(true)
          .allDay(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_04_30)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_05_01)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build()
      )
    );
  }

  @Test
  void testInsideExceptionalClosure() {
    List<SingleDayOpeningDTO> openings = CalendarUtils
      .getSurroundingOpenings(
        Arrays.asList(Calendars.CALENDAR_COMBINED_EXAMPLE_D),
        Dates.DATE_2021_08_12
      )
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_07_01)
          .open(true)
          .allDay(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_08_12)
          .open(false)
          .allDay(true)
          .exceptional(true)
          .exceptionName(Names.NAME_1)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_08_19)
          .open(true)
          .allDay(true)
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
  void testBeforeCalendarWithExceptionalClosure() {
    List<SingleDayOpeningDTO> openings = CalendarUtils
      .getSurroundingOpenings(
        Arrays.asList(Calendars.CALENDAR_COMBINED_EXAMPLE_E),
        Dates.DATE_2021_01_05
      )
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_04)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_05)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_08_19)
          .open(true)
          .allDay(true)
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
