package org.folio.rest.utils;

import org.apache.commons.lang3.BooleanUtils;
import org.folio.rest.jaxrs.model.Description;
import org.folio.rest.jaxrs.model.Event;
import org.folio.rest.jaxrs.model.OpeningDay;
import org.folio.rest.jaxrs.model.OpeningHour;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

public class CalendarUtils {

  public static final String DAY_PATTERN = "EEEE";

  private static final String TIME_PATTERN = "HH:mm:ss.SSS'Z'";
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern(TIME_PATTERN);

  private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_PATTERN).withZoneUTC();

  public static final DateTimeFormatter BASIC_DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

  private CalendarUtils() {
  }

  public static DayOfWeek dayOfDate(Date inputDate) {
    return DayOfWeek.valueOf(new SimpleDateFormat(DAY_PATTERN, Locale.ENGLISH).format(inputDate).toUpperCase());
  }

  public static List<Object> separateEvents(Description entity, String generatedId) {
    List<Object> events = new ArrayList<>();

    Calendar startCal = Calendar.getInstance();
    startCal.setTimeInMillis(entity.getStartDate().getTime());
    startCal.set(Calendar.SECOND, 0);
    startCal.set(Calendar.MILLISECOND, 0);

    Calendar endCal = Calendar.getInstance();
    endCal.setTimeInMillis(entity.getEndDate().getTime());
    endCal.set(Calendar.SECOND, 0);
    endCal.set(Calendar.MILLISECOND, 1);

    Map<DayOfWeek, OpeningDay> openingDays = getOpeningDays(entity);

    while (startCal.before(endCal)) {
      OpeningDay openingDay = openingDays.get(dayOfDate(startCal.getTime()));

      List<Event> event = createEvents(openingDay, startCal, entity, generatedId);
      events.addAll(event);

      startCal.add(Calendar.DAY_OF_MONTH, 1);
    }

    return events;
  }

  private static List<Event> createEvents(OpeningDay openingDay, Calendar startCal, Description entity, String generatedId) {
    Calendar currentStartDate = Calendar.getInstance();
    currentStartDate.setTimeInMillis(startCal.getTimeInMillis());

    Calendar currentEndDate = Calendar.getInstance();
    currentEndDate.setTimeInMillis(startCal.getTimeInMillis());

    List<Event> events = new ArrayList<>();
    String eventType = entity.getDescriptionType().toString();

    boolean allDay = true;
    boolean open = false;
    if (openingDay != null) {
      allDay = openingDay.getAllDay();
      open = BooleanUtils.isTrue(openingDay.getOpen());
    }

    if (openingDay == null || openingDay.getAllDay() || BooleanUtils.isFalse(openingDay.getOpen())
      || openingDay.getOpeningHour() == null) {
      currentStartDate.set(Calendar.HOUR_OF_DAY, 0);
      currentStartDate.set(Calendar.MINUTE, 0);
      currentEndDate.set(Calendar.HOUR_OF_DAY, 23);
      currentEndDate.set(Calendar.MINUTE, 59);
      events.add(new Event()
        .withDescriptionId(generatedId)
        .withCalendarId(entity.getCalendarId())
        .withEventType(eventType)
        .withAllDay(allDay)
        .withOpen(open)
        .withStartDate(currentStartDate.getTime())
        .withEndDate(currentEndDate.getTime())
        .withActive(true));
    } else {
      for (OpeningHour opening : openingDay.getOpeningHour()) {
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal.setTimeInMillis(DateTime.parse(opening.getStartTime(), TIME_FORMATTER).getMillis());
        cal2.setTimeInMillis(DateTime.parse(opening.getEndTime(), TIME_FORMATTER).getMillis());

        currentStartDate.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        currentStartDate.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        currentEndDate.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
        currentEndDate.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
        events.add(new Event()
          .withDescriptionId(generatedId)
          .withCalendarId(entity.getCalendarId())
          .withEventType(eventType)
          .withAllDay(allDay)
          .withOpen(open)
          .withStartDate(currentStartDate.getTime())
          .withEndDate(currentEndDate.getTime())
          .withActive(true));
      }
    }

    return events;
  }

  private static Map<DayOfWeek, OpeningDay> getOpeningDays(Description entity) {

    EnumMap openingDays = new EnumMap(DayOfWeek.class);

    for (OpeningDay openingDay : entity.getOpeningDays()) {
      openingDays.put(DayOfWeek.valueOf(openingDay.getDay().toString()), openingDay);
    }

    return openingDays;
  }

}
