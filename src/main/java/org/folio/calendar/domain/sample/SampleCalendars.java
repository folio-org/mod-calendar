package org.folio.calendar.domain.sample;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.Weekday;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;
import org.folio.calendar.utils.DateUtils;

@UtilityClass
public class SampleCalendars {

  public static final LocalTime TIME_00_00 = LocalTime.of(0, 0);
  public static final LocalTime TIME_09_00 = LocalTime.of(9, 0);
  public static final LocalTime TIME_12_30 = LocalTime.of(12, 30);
  public static final LocalTime TIME_14_00 = LocalTime.of(14, 0);
  public static final LocalTime TIME_14_59 = LocalTime.of(14, 59);
  public static final LocalTime TIME_23_00 = LocalTime.of(23, 0);
  public static final LocalTime TIME_23_59 = LocalTime.of(23, 59);

  public static final int LONG_EXCEPTION_LENGTH = 3;

  /* Default service points:
      "3a40852d-49fd-4df2-a1f9-6e2641a6e91f" "Circ Desk 1",
      "c4c90014-c8c9-4ade-8f24-b5e313319f4b" "Circ Desk 2",
      "7c5abc9f-f3d7-4856-b8d7-6712462ca007" "Online",
  */
  public static final UUID SAMPLE_SERVICE_POINT_CIRC_DESK_1 = UUID.fromString(
    "3a40852d-49fd-4df2-a1f9-6e2641a6e91f"
  );
  public static final UUID SAMPLE_SERVICE_POINT_CIRC_DESK_2 = UUID.fromString(
    "c4c90014-c8c9-4ade-8f24-b5e313319f4b"
  );
  public static final UUID SAMPLE_SERVICE_POINT_CIRC_DESK_ONLINE = UUID.fromString(
    "7c5abc9f-f3d7-4856-b8d7-6712462ca007"
  );

  public List<Calendar> getSampleCalendars() {
    List<Calendar> calendars = new ArrayList<>();

    LocalDate lastMonth = DateUtils.getCurrentDate().minusMonths(1);
    LocalDate thisMonth = DateUtils.getCurrentDate();
    LocalDate nextMonth = DateUtils.getCurrentDate().plusMonths(1);

    LocalDate lastMonthStart = lastMonth.with(TemporalAdjusters.firstDayOfMonth());
    LocalDate thisMonthStart = thisMonth.with(TemporalAdjusters.firstDayOfMonth());
    LocalDate nextMonthStart = nextMonth.with(TemporalAdjusters.firstDayOfMonth());

    LocalDate lastMonthEnd = lastMonth.with(TemporalAdjusters.lastDayOfMonth());
    LocalDate nextMonthEnd = nextMonth.with(TemporalAdjusters.lastDayOfMonth());

    String lastMonthName = lastMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
    String thisMonthName = thisMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
    String nextMonthName = nextMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US);

    calendars.add(getServicePointOneCalendarOne(lastMonthStart, lastMonthEnd, lastMonthName));
    calendars.add(
      getServicePointOneCalendarTwo(thisMonthStart, nextMonthEnd, thisMonthName, nextMonthName)
    );
    calendars.add(getServicePointTwoCalendar(nextMonthStart, nextMonthEnd));
    calendars.add(getOnlineCalendar(lastMonthStart, nextMonthEnd));

    return calendars;
  }

  protected Calendar getServicePointOneCalendarOne(
    LocalDate lastMonthStart,
    LocalDate lastMonthEnd,
    String lastMonthName
  ) {
    return Calendar
      .builder()
      .id(UUID.randomUUID())
      .name(String.format("Circ Desk 1 %s Hours", lastMonthName))
      .startDate(lastMonthStart)
      .endDate(lastMonthEnd)
      .servicePoint(
        ServicePointCalendarAssignment
          .builder()
          .servicePointId(SAMPLE_SERVICE_POINT_CIRC_DESK_1)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.SUNDAY)
          .startTime(TIME_09_00)
          .endDay(Weekday.FRIDAY)
          .endTime(TIME_23_00)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.SATURDAY)
          .startTime(TIME_09_00)
          .endDay(Weekday.SATURDAY)
          .endTime(TIME_23_00)
          .build()
      )
      .exception(
        ExceptionRange
          .builder()
          .id(UUID.randomUUID())
          .startDate(lastMonthStart.plusDays(1))
          .endDate(lastMonthStart.plusDays(1))
          .build()
      )
      .build();
  }

  protected Calendar getServicePointOneCalendarTwo(
    LocalDate thisMonthStart,
    LocalDate nextMonthEnd,
    String thisMonthName,
    String nextMonthName
  ) {
    return Calendar
      .builder()
      .id(UUID.randomUUID())
      .name(String.format("Circ Desk 1 %s-%s Hours", thisMonthName, nextMonthName))
      .startDate(thisMonthStart)
      .endDate(nextMonthEnd)
      .servicePoint(
        ServicePointCalendarAssignment
          .builder()
          .servicePointId(SAMPLE_SERVICE_POINT_CIRC_DESK_1)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.MONDAY)
          .startTime(TIME_09_00)
          .endDay(Weekday.MONDAY)
          .endTime(TIME_23_00)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.TUESDAY)
          .startTime(TIME_09_00)
          .endDay(Weekday.TUESDAY)
          .endTime(TIME_23_00)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.WEDNESDAY)
          .startTime(TIME_09_00)
          .endDay(Weekday.WEDNESDAY)
          .endTime(TIME_23_00)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.THURSDAY)
          .startTime(TIME_09_00)
          .endDay(Weekday.THURSDAY)
          .endTime(TIME_23_00)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.FRIDAY)
          .startTime(TIME_09_00)
          .endDay(Weekday.FRIDAY)
          .endTime(TIME_23_00)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.SATURDAY)
          .startTime(TIME_09_00)
          .endDay(Weekday.SATURDAY)
          .endTime(TIME_12_30)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.SATURDAY)
          .startTime(TIME_14_00)
          .endDay(Weekday.SATURDAY)
          .endTime(TIME_23_00)
          .build()
      )
      .exception(
        ExceptionRange
          .builder()
          .id(UUID.randomUUID())
          .startDate(thisMonthStart.plusDays(1))
          .endDate(thisMonthStart.plusDays(LONG_EXCEPTION_LENGTH))
          .opening(
            ExceptionHour
              .builder()
              .startDate(thisMonthStart.plusDays(1))
              .startTime(TIME_00_00)
              .endDate(thisMonthStart.plusDays(LONG_EXCEPTION_LENGTH))
              .endTime(TIME_23_59)
              .build()
          )
          .build()
      )
      .build();
  }

  protected Calendar getServicePointTwoCalendar(LocalDate nextMonthStart, LocalDate nextMonthEnd) {
    return Calendar
      .builder()
      .id(UUID.randomUUID())
      .name("Limited Opening Period")
      .startDate(nextMonthStart)
      .endDate(nextMonthEnd)
      .servicePoint(
        ServicePointCalendarAssignment
          .builder()
          .servicePointId(SAMPLE_SERVICE_POINT_CIRC_DESK_2)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.MONDAY)
          .startTime(TIME_12_30)
          .endDay(Weekday.MONDAY)
          .endTime(TIME_14_59)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.WEDNESDAY)
          .startTime(TIME_12_30)
          .endDay(Weekday.WEDNESDAY)
          .endTime(TIME_14_59)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.FRIDAY)
          .startTime(TIME_12_30)
          .endDay(Weekday.FRIDAY)
          .endTime(TIME_14_59)
          .build()
      )
      .build();
  }

  protected Calendar getOnlineCalendar(LocalDate lastMonthStart, LocalDate nextMonthEnd) {
    return Calendar
      .builder()
      .id(UUID.randomUUID())
      .name("24/7 Online Access")
      .startDate(lastMonthStart)
      .endDate(nextMonthEnd)
      .servicePoint(
        ServicePointCalendarAssignment
          .builder()
          .servicePointId(SAMPLE_SERVICE_POINT_CIRC_DESK_ONLINE)
          .build()
      )
      .normalHour(
        NormalOpening
          .builder()
          .startDay(Weekday.SUNDAY)
          .startTime(TIME_00_00)
          .endDay(Weekday.SATURDAY)
          .endTime(TIME_23_59)
          .build()
      )
      .build();
  }
}
