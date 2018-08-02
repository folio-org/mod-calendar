package org.folio.rest.utils;

import org.apache.commons.lang.BooleanUtils;
import org.folio.rest.beans.ActualOpeningHours;
import org.folio.rest.beans.CalendarOpeningsRequestParameters;
import org.folio.rest.jaxrs.model.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Stream;

public class CalendarUtils {


  private static final String TIME_PATTERN = "HH:mm:ss.SSS'Z'";
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern(TIME_PATTERN);
  private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  private static final String DATE_PATTERN_SHORT = "yyyy-MM-dd";
  public static final DateTimeFormatter DATE_FORMATTER_SHORT = DateTimeFormat.forPattern(DATE_PATTERN_SHORT).withZoneUTC();
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_PATTERN).withZoneUTC();
  public static final String DAY_PATTERN = "EEEE";

  private CalendarUtils() {
  }

  public static DayOfWeek dayOfDate(Date inputDate) {
    return DayOfWeek.valueOf(new SimpleDateFormat(DAY_PATTERN, Locale.ENGLISH).format(inputDate).toUpperCase());
  }


  public static List<Object> separateEvents(OpeningPeriod_ entity, boolean isExceptional) {
    List<Object> actualOpeningHours = new ArrayList<>();

    Calendar startDay = Calendar.getInstance();
    startDay.setTimeInMillis(entity.getStartDate().getTime());
    startDay.set(Calendar.SECOND, 0);
    startDay.set(Calendar.MILLISECOND, 0);

    Calendar endDay = Calendar.getInstance();
    endDay.setTimeInMillis(entity.getEndDate().getTime());
    endDay.set(Calendar.SECOND, 0);
    endDay.set(Calendar.MILLISECOND, 1);

    if (isExceptional) {
      while (startDay.before(endDay)) {
        List<ActualOpeningHours> event = createEvents(entity.getOpeningDays().get(0).getOpeningDay(), startDay, entity.getId(), isExceptional);
        actualOpeningHours.addAll(event);
        startDay.add(Calendar.DAY_OF_MONTH, 1);
      }
    } else {
      Map<DayOfWeek, OpeningDay_> openingDays = getOpeningDays(entity);

      while (startDay.before(endDay)) {
        DayOfWeek dayOfWeek = dayOfDate(startDay.getTime());
        OpeningDay_ openingDay = openingDays.get(dayOfWeek);
        if (openingDay != null) {
          List<ActualOpeningHours> event = createEvents(openingDay.getOpeningDay(), startDay, entity.getId(), isExceptional);
          actualOpeningHours.addAll(event);
        }
        startDay.add(Calendar.DAY_OF_MONTH, 1);
      }
    }


    return actualOpeningHours;
  }

  private static Map<DayOfWeek, OpeningDay_> getOpeningDays(OpeningPeriod_ entity) {

    EnumMap openingDays = new EnumMap(DayOfWeek.class);

    for (OpeningDay_ openingDay : entity.getOpeningDays()) {
      openingDays.put(DayOfWeek.valueOf(openingDay.getWeekdays().getDay().toString()), openingDay);
    }

    return openingDays;
  }

  private static List<ActualOpeningHours> createEvents(OpeningDay openingDay, Calendar actualDay, String generatedId, boolean isExceptional) {
    Calendar currentStartDate = Calendar.getInstance();
    currentStartDate.setTimeInMillis(actualDay.getTimeInMillis());

    Calendar currentEndDate = Calendar.getInstance();
    currentEndDate.setTimeInMillis(actualDay.getTimeInMillis());

    boolean allDay = true;
    boolean open = false;
    if (openingDay != null) {
      allDay = openingDay.getAllDay();
      open = BooleanUtils.isTrue(openingDay.getOpen());
    }

    List<ActualOpeningHours> actualOpeningHours = new ArrayList<>();

    if (openingDay != null) {
      if (openingDay.getAllDay()) {
        ActualOpeningHours actualOpeningHour = new ActualOpeningHours();
        actualOpeningHour.setId(generatedId);
        actualOpeningHour.setOpeningId(generatedId);
        actualOpeningHour.setActualDay(currentStartDate.getTime());
        actualOpeningHour.setStartTime("00:00");
        actualOpeningHour.setEndTime("23:59");
        actualOpeningHour.setOpen(open);
        actualOpeningHour.setAllDay(allDay);
        actualOpeningHour.setExceptional(isExceptional);
        actualOpeningHours.add(actualOpeningHour);
      } else {
        for (OpeningHour openingHour : openingDay.getOpeningHour()) {
          ActualOpeningHours actualOpeningHour = new ActualOpeningHours();
          actualOpeningHour.setId(generatedId);
          actualOpeningHour.setOpeningId(generatedId);
          actualOpeningHour.setActualDay(currentStartDate.getTime());
          actualOpeningHour.setStartTime(openingHour.getStartTime());
          actualOpeningHour.setEndTime(openingHour.getEndTime());
          actualOpeningHour.setOpen(open);
          actualOpeningHour.setAllDay(allDay);
          actualOpeningHour.setExceptional(isExceptional);
          actualOpeningHours.add(actualOpeningHour);
        }
      }
    }

    return actualOpeningHours;
  }

  public static void addClosedDaysToOpenings(List<OpeningPeriod> openingPeriods, CalendarOpeningsRequestParameters calendarOpeningsRequestParameters) {
    String startDate = calendarOpeningsRequestParameters.getStartDate();
    String endDate = calendarOpeningsRequestParameters.getEndDate();
    openingPeriods.sort(Comparator.comparing(OpeningPeriod::getDate));
    if (startDate == null) {
      Calendar calendar = Calendar.getInstance();
      if (openingPeriods.stream().findFirst().isPresent()) {
        calendar.setTimeInMillis(openingPeriods.stream().findFirst().orElse(new OpeningPeriod()).getDate().getTime());
      }
      startDate = DATE_FORMATTER_SHORT.print(new DateTime(calendar));
    }
    if (endDate == null) {
      long count = openingPeriods.stream().count();
      Stream<OpeningPeriod> stream = openingPeriods.stream();
      Calendar calendar = Calendar.getInstance();
      if (!openingPeriods.isEmpty()) {
        calendar.setTimeInMillis(stream.skip(count - 1).findFirst().orElse(new OpeningPeriod()).getDate().getTime());
      }
      endDate = DATE_FORMATTER_SHORT.print(new DateTime(calendar));
    }

    Calendar startDay = Calendar.getInstance();
    startDay.setTimeInMillis(DateTime.parse(startDate, DATE_FORMATTER_SHORT).getMillis());
    startDay.set(Calendar.SECOND, 0);
    startDay.set(Calendar.MILLISECOND, 0);

    Calendar endDay = Calendar.getInstance();
    endDay.setTimeInMillis(DateTime.parse(endDate, DATE_FORMATTER_SHORT).getMillis());
    endDay.set(Calendar.SECOND, 0);
    endDay.set(Calendar.MILLISECOND, 1);


    while (startDay.before(endDay)) {
      OpeningPeriod openingPeriod = new OpeningPeriod();
      openingPeriod.setDate(startDay.getTime());
      if (openingPeriods.stream().anyMatch(o -> o.getDate().equals(openingPeriod.getDate()))) {
        startDay.add(Calendar.DAY_OF_MONTH, 1);
      } else {
        OpeningDay openingDay = new OpeningDay();
        openingDay.setOpen(false);
        openingDay.setAllDay(true);
        openingDay.setExceptional(false);
        List<OpeningHour> openingHours = new ArrayList<>();
        OpeningHour openingHour = new OpeningHour();
        openingHour.setStartTime("00:00");
        openingHour.setEndTime("23:59");
        openingHours.add(openingHour);
        openingDay.setOpeningHour(openingHours);
        openingPeriod.setOpeningDay(openingDay);
        openingPeriods.add(openingPeriod);
        startDay.add(Calendar.DAY_OF_MONTH, 1);
      }
    }
  }

  public static Date getDateWithoutHoursAndMinutes(Date date) {
    return date;
  }
}
