package org.folio.rest.utils;

import static org.folio.rest.utils.CalendarConstants.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.folio.rest.jaxrs.model.Description;
import org.folio.rest.jaxrs.model.Event;

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

    List<Calendar> datesToSave = CalendarUtils.itarateDates(startCal, endCal, entity.getDaysIncluded());
    for (Calendar dates : datesToSave) {
      Event event = new Event();
      event.setAllDay(false);
      event.setId(entity.getId());
      event.setStartDate(dates.getTime());
      dates.set(Calendar.HOUR, entity.getEndHour());
      dates.set(Calendar.MINUTE, entity.getEndMinute());
      event.setEndDate(dates.getTime());
      event.setEventType(OPENING_DAYS);
      event.setId(generatedId);
      events.add(event);
    }

    return events;
  }

}
