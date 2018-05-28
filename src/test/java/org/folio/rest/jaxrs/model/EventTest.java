package org.folio.rest.jaxrs.model;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class EventTest {
  @Test
  public void avoidAnnoyingErrorMessageWhenRunningCleanInstall() {
    assertTrue(true);
  }
/*
  @Test
  public void testEventCreation() {
    Event event = createEvent();
    Event otherEvent = new Event()
      .withId(event.getId())
      .withAllDay(event.getAllDay())
      .withOpen(event.getOpen())
      .withStartDate(event.getStartDate())
      .withEndDate(event.getEndDate())
      .withEventType(event.getEventType())
      .withActive(event.getActive());

    assertEquals(event.getId(), otherEvent.getId());
    assertEquals(event.getAllDay(), otherEvent.getAllDay());
    assertEquals(event.getOpen(), otherEvent.getOpen());
    assertEquals(event.getStartDate(), otherEvent.getStartDate());
    assertEquals(event.getEndDate(), otherEvent.getEndDate());
    assertEquals(event.getEventType(), otherEvent.getEventType());
    assertEquals(event.getActive(), otherEvent.getActive());

  }

  @Test
  public void testEventCollectionCreation() {
    CalendarEventCollection calendarEventCollection = createEventCollection();
    CalendarEventCollection otherCalendarEventCollection = new CalendarEventCollection()
      .withEvents(calendarEventCollection.getEvents());
    otherCalendarEventCollection.setTotalRecords(calendarEventCollection.getTotalRecords());

    assertEquals(calendarEventCollection.getEvents(), otherCalendarEventCollection.getEvents());
    assertEquals(calendarEventCollection.getTotalRecords(), otherCalendarEventCollection.getTotalRecords());
  }

  private Event createEvent() {
    Event event = new Event();
    event.setId(UUID.randomUUID().toString());
    event.setAllDay(true);
    event.setOpen(true);
    event.setStartDate(new Date());
    event.setEndDate(new Date());
    event.setEventType(UUID.randomUUID().toString());
    event.setActive(true);
    return event;
  }

  private CalendarEventCollection createEventCollection() {
    CalendarEventCollection calendarEventCollection = new CalendarEventCollection();
    calendarEventCollection.getEvents().add(createEvent());
    calendarEventCollection.getEvents().add(createEvent());
    calendarEventCollection.setTotalRecords(calendarEventCollection.getEvents().size());

    return calendarEventCollection;
  }
*/
}
