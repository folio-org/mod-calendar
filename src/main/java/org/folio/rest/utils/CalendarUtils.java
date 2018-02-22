package org.folio.rest.utils;

import org.apache.commons.lang3.BooleanUtils;
import org.folio.rest.jaxrs.model.Description;
import org.folio.rest.jaxrs.model.Description.DescriptionType;
import org.folio.rest.jaxrs.model.Event;
import org.folio.rest.jaxrs.model.OpeningDay;
import org.folio.rest.jaxrs.model.OpeningHour;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

import static java.util.Calendar.DAY_OF_MONTH;

public class CalendarUtils {

  public static final String DAY_PATTERN = "EEEE";

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

      startCal.add(DAY_OF_MONTH, 1);
    }

    return events;
  }

  private static List<Event> createEvents(OpeningDay openingDay, Calendar startCal, Description entity, String generatedId) {
    Calendar currentStartDate = Calendar.getInstance();
    currentStartDate.setTimeInMillis(startCal.getTimeInMillis());

    Calendar currentEndDate = Calendar.getInstance();
    currentEndDate.setTimeInMillis(startCal.getTimeInMillis());

    List<Event> events = new ArrayList<>();
    String eventType = CalendarConstants.OPENING_DAY;
    if (entity.getDescriptionType() != null && entity.getDescriptionType() == DescriptionType.EXCLUSION) {
      eventType = CalendarConstants.EXCLUSION;
    }

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
        .withEventType(eventType)
        .withAllDay(allDay)
        .withOpen(open)
        .withStartDate(currentStartDate.getTime())
        .withEndDate(currentEndDate.getTime()));
    } else {
      for (OpeningHour opening : openingDay.getOpeningHour()) {
        currentStartDate.set(Calendar.HOUR_OF_DAY, opening.getStartHour());
        currentStartDate.set(Calendar.MINUTE, opening.getStartMinute());
        currentEndDate.set(Calendar.HOUR_OF_DAY, opening.getEndHour());
        currentEndDate.set(Calendar.MINUTE, opening.getEndMinute());
        events.add(new Event()
          .withDescriptionId(generatedId)
          .withEventType(eventType)
          .withAllDay(allDay)
          .withOpen(open)
          .withStartDate(currentStartDate.getTime())
          .withEndDate(currentEndDate.getTime()));
      }
    }

    return events;
  }

  private static Map<DayOfWeek, OpeningDay> getOpeningDays(Description entity) {

    Map<DayOfWeek, OpeningDay> openingDays = new HashMap<>();

    for (OpeningDay openingDay : entity.getOpeningDays()) {

      switch (openingDay.getDay()) {
        case MONDAY: {
          openingDays.put(DayOfWeek.MONDAY, openingDay);
          break;
        }
        case TUESDAY: {
          openingDays.put(DayOfWeek.TUESDAY, openingDay);
          break;
        }
        case WEDNESDAY: {
          openingDays.put(DayOfWeek.WEDNESDAY, openingDay);
          break;
        }
        case THURSDAY: {
          openingDays.put(DayOfWeek.THURSDAY, openingDay);
          break;
        }
        case FRIDAY: {
          openingDays.put(DayOfWeek.FRIDAY, openingDay);
          break;
        }
        case SATURDAY: {
          openingDays.put(DayOfWeek.SATURDAY, openingDay);
          break;
        }
        case SUNDAY: {
          openingDays.put(DayOfWeek.SUNDAY, openingDay);
          break;
        }
      }
    }

    return openingDays;
  }

}
