package org.folio.jaxrs.model;

import org.folio.rest.jaxrs.model.CalendarEventCollection;
import org.folio.rest.jaxrs.model.Event;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CalendarTest {

  @Test
  public void testEventCreation() {
    Event event = createEvent();
    Event otherEvent = new Event()
      .withId(event.getId())
      .withAllDay(event.getAllDay())
      .withStartDate(event.getStartDate())
      .withEndDate(event.getEndDate())
      .withEventType(event.getEventType());

    assertEquals(event.getId(), otherEvent.getId());
    assertEquals(event.getAllDay(), otherEvent.getAllDay());
    assertEquals(event.getStartDate(), otherEvent.getStartDate());
    assertEquals(event.getEndDate(), otherEvent.getEndDate());
    assertEquals(event.getEventType(), otherEvent.getEventType());

  }

  @Test
  public void testEventCollectionCreation() {
    CalendarEventCollection calendarEventCollection = createEventCollection();
    CalendarEventCollection otherCalendarEventCollection = new CalendarEventCollection()
      .withEvents(calendarEventCollection.getEvents());
    otherCalendarEventCollection.setTotalRecords(calendarEventCollection.getTotalRecords());

    assertEquals(calendarEventCollection.getEvents(), otherCalendarEventCollection.getEvents());
  }

  private Event createEvent() {
    Event event = new Event();
    event.setId(UUID.randomUUID().toString());
    event.setAllDay(true);
    event.setStartDate(new Date());
    event.setEndDate(new Date());
    event.setEventType(UUID.randomUUID().toString());
    return event;
  }

  private CalendarEventCollection createEventCollection() {
    CalendarEventCollection calendarEventCollection = new CalendarEventCollection();
    calendarEventCollection.getEvents().add(createEvent());
    calendarEventCollection.getEvents().add(createEvent());
    calendarEventCollection.setTotalRecords(calendarEventCollection.getEvents().size());

    return calendarEventCollection;
  }


}
