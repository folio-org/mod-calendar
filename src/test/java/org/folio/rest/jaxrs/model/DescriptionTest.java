package org.folio.rest.jaxrs.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class DescriptionTest {

  @Test
  public void testDescriptionCreation() {
    Description description = createDescription();
    Description otherDescription = new Description()
      .withId(description.getId())
      .withDescription(description.getDescription())
      .withDescriptionType(description.getDescriptionType())
      .withStartDate(description.getStartDate())
      .withEndDate(description.getEndDate())
      .withTwelveHour(description.getTwelveHour())
      .withOpeningDays(description.getOpeningDays())
      .withCreationDate(description.getCreationDate())
      .withCreatedBy(description.getCreatedBy());

    assertEquals(description.getId(), otherDescription.getId());
    assertEquals(description.getDescription(), otherDescription.getDescription());
    assertEquals(description.getDescriptionType(), otherDescription.getDescriptionType());
    assertEquals(description.getStartDate(), otherDescription.getStartDate());
    assertEquals(description.getEndDate(), otherDescription.getEndDate());
    assertEquals(description.getTwelveHour(), otherDescription.getTwelveHour());
    assertEquals(description.getOpeningDays(), otherDescription.getOpeningDays());
    assertEquals(description.getCreationDate(), otherDescription.getCreationDate());
    assertEquals(description.getCreatedBy(), otherDescription.getCreatedBy());

  }

  @Test
  public void testDescriptionCollectionCreation() {
    CalendarEventDescriptionCollection collection = new CalendarEventDescriptionCollection();
    List<Description> descriptions = new ArrayList<>();
    descriptions.add(createDescription());
    collection.setDescriptions(descriptions);
    collection.setTotalRecords(descriptions.size());

    CalendarEventDescriptionCollection otherCollection = new CalendarEventDescriptionCollection()
      .withDescriptions(collection.getDescriptions())
      .withTotalRecords(collection.getTotalRecords());

    assertEquals(collection.getDescriptions(), otherCollection.getDescriptions());
    assertEquals(collection.getTotalRecords(), otherCollection.getTotalRecords());
  }

  @Test
  public void testOpeningDayCreation() {
    OpeningDay openingDay = createOpeningDay();
    OpeningDay otherOpeningDay = new OpeningDay()
      .withDay(openingDay.getDay())
      .withStartHour(openingDay.getStartHour())
      .withStartMinute(openingDay.getStartMinute())
      .withEndHour(openingDay.getEndHour())
      .withEndMinute(openingDay.getEndMinute())
      .withAllDay(openingDay.getAllDay())
      .withOpen(openingDay.getOpen());

    assertEquals(openingDay.getDay(), otherOpeningDay.getDay());
    assertEquals(openingDay.getStartHour(), otherOpeningDay.getStartHour());
    assertEquals(openingDay.getStartMinute(), otherOpeningDay.getStartMinute());
    assertEquals(openingDay.getEndHour(), otherOpeningDay.getEndHour());
    assertEquals(openingDay.getEndMinute(), otherOpeningDay.getEndMinute());
    assertEquals(openingDay.getAllDay(), otherOpeningDay.getAllDay());
    assertEquals(openingDay.getOpen(), otherOpeningDay.getOpen());
  }

  private Description createDescription() {
    Description description = new Description();
    List<OpeningDay> openingDays = new ArrayList<>();
    openingDays.add(createOpeningDay());
    description.setId(UUID.randomUUID().toString());
    description.setDescription(UUID.randomUUID().toString());
    description.setDescriptionType(Description.DescriptionType.OPENING_DAY);
    description.setStartDate(new Date());
    description.setEndDate(new Date());
    description.setTwelveHour(Boolean.FALSE);
    description.setOpeningDays(openingDays);
    description.setCreationDate(new Date());
    description.setCreatedBy(UUID.randomUUID().toString());

    return description;
  }

  private OpeningDay createOpeningDay() {
    OpeningDay openingDay = new OpeningDay();
    openingDay.setDay(OpeningDay.Day.MONDAY);
    openingDay.setStartHour(0);
    openingDay.setStartMinute(0);
    openingDay.setEndHour(23);
    openingDay.setEndMinute(59);
    openingDay.setAllDay(Boolean.FALSE);
    openingDay.setOpen(Boolean.TRUE);

    return openingDay;
  }
}
