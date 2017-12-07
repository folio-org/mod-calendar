package org.folio.rest.utils;

import org.folio.rest.jaxrs.model.*;

import java.util.*;

import static org.folio.rest.utils.CalendarConstants.OPENING_DAYS;

public class CalendarService {

  public static List<Object> separateEvents(Description entity, String generatedId) {

    List<Object> events = new ArrayList<>();
    Calendar startCal = Calendar.getInstance();
    startCal.setTimeInMillis(entity.getStartDate().getTime());
    startCal.set(Calendar.HOUR, entity.getStartHour());
    startCal.set(Calendar.MINUTE, entity.getStartMinute());

    Calendar endCal = Calendar.getInstance();
    endCal.setTimeInMillis(entity.getEndDate().getTime());
    endCal.set(Calendar.HOUR, entity.getEndHour());
    endCal.set(Calendar.MINUTE, entity.getEndMinute());

    if (!entity.getDaysIncluded().getAllDay()) {
      List<Calendar> datesToSave = CalendarUtils.itarateDates(startCal, endCal, entity.getDaysIncluded());
      for (Calendar dates : datesToSave) {
        Event event = new Event();
        event.setAllDay(entity.getDaysIncluded().getAllDay());
        event.setId(entity.getId());
        event.setStartDate(dates.getTime());
        dates.set(Calendar.HOUR, entity.getEndHour());
        dates.set(Calendar.MINUTE, entity.getEndMinute());
        event.setEndDate(dates.getTime());
        event.setEventType(OPENING_DAYS);
        event.setId(generatedId);
        events.add(event);
      }
    }

    if(entity.getDaysIncluded().getAllDay() || events.size() == 0) {
      Event event = new Event();
      event.setAllDay(entity.getDaysIncluded().getAllDay());
      event.setId(entity.getId());
      event.setStartDate(startCal.getTime());
      event.setEndDate(endCal.getTime());
      event.setEventType(OPENING_DAYS);
      event.setId(generatedId);
      events.add(event);
    }

    return events;
  }


}
