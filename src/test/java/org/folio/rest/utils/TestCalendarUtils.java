package org.folio.rest.utils;

import org.folio.rest.jaxrs.model.Description;
import org.folio.rest.jaxrs.model.Event;
import org.folio.rest.jaxrs.model.OpeningDay;
import org.folio.rest.jaxrs.model.OpeningHour;
import org.junit.Test;

import java.time.DayOfWeek;
import java.util.*;

import static org.junit.Assert.*;

public class TestCalendarUtils {
  private static final int START_HOUR = 8;
  private static final int END_HOUR = 23;
  private static final int START_MINUTE = 0;
  private static final int END_MINUTE = 0;

  @Test
  public void testDayOfDate() {
    Date date = new Date();
    date.setTime(0);
    DayOfWeek dayOfDate = CalendarUtils.dayOfDate(date);
    assertEquals(DayOfWeek.THURSDAY, dayOfDate);
  }

  @Test
  public void testSeparateEvents() {

    Calendar startDate = Calendar.getInstance();
    startDate.set(2017, 0, 1, 0, 0, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(2017, 0, 7, 23, 59, 59);

    List<OpeningDay> openingDays = new ArrayList<>();
    for (OpeningDay.Day day : OpeningDay.Day.values()) {
      openingDays.add(createOpeningDay(day));
    }

    Description description = new Description()
      .withId(UUID.randomUUID().toString())
      .withDescription(UUID.randomUUID().toString())
      .withDescriptionType(Description.DescriptionType.OPENING_DAY)
      .withStartDate(startDate.getTime())
      .withEndDate(endDate.getTime())
      .withOpeningDays(openingDays);

    String generatedId = UUID.randomUUID().toString();

    List<Object> generatedEvents = CalendarUtils.separateEvents(description, generatedId);

    for (Object currentEvent : generatedEvents) {
      if (!(currentEvent instanceof Event)) {
        fail("Event object should have been generated.");
      }
      Event event = (Event) currentEvent;

      Calendar openingStart = Calendar.getInstance();
      openingStart.setTime(event.getStartDate());

      Calendar openingEnd = Calendar.getInstance();
      openingEnd.setTime(event.getEndDate());

      assertEquals(START_HOUR, openingStart.get(Calendar.HOUR_OF_DAY));
      assertEquals(START_MINUTE, openingStart.get(Calendar.MINUTE));
      assertEquals(END_HOUR, openingEnd.get(Calendar.HOUR_OF_DAY));
      assertEquals(END_MINUTE, openingEnd.get(Calendar.MINUTE));
      assertEquals(generatedId, event.getDescriptionId());
      assertEquals(Boolean.TRUE, event.getOpen());
      assertEquals(Boolean.FALSE, event.getAllDay());
      assertEquals(CalendarConstants.OPENING_DAY, event.getEventType());

      assertFalse(startDate.after(openingStart));
      assertFalse(endDate.before(openingEnd));
    }
  }

  private OpeningDay createOpeningDay(OpeningDay.Day day) {
    OpeningHour openingHour = new OpeningHour();
    Calendar startDate = Calendar.getInstance();
    startDate.set(1970, 0, 1, START_HOUR, START_MINUTE, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.set(1970, 0, 1, END_HOUR, END_MINUTE, 59);
    String startTime = CalendarUtils.TIME_FORMAT.format(startDate.getTime());
    String endTime = CalendarUtils.TIME_FORMAT.format(endDate.getTime());
    openingHour.setStartTime(startTime);
    openingHour.setEndTime(endTime);
    OpeningDay openingDay = new OpeningDay();
    openingDay.getOpeningHour().add(openingHour);
    openingDay.setDay(day);
    openingDay.setAllDay(Boolean.FALSE);
    openingDay.setOpen(Boolean.TRUE);

    return openingDay;
  }
}
