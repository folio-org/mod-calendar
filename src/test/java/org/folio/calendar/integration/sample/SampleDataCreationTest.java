package org.folio.calendar.integration.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.integration.api.calendar.BaseCalendarApiTest;
import org.junit.jupiter.api.Test;

class SampleDataCreationTest extends AbstractSampleTest {

  @Test
  void testLoadSample() {
    CalendarCollectionDTO collection = ra()
      .get(getRequestUrl(BaseCalendarApiTest.COLLECTION_CALENDAR_API_ROUTE))
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(
      "The returned collection includes sample calendars",
      collection.getCalendars(),
      is(not(hasSize(0)))
    );
  }

  @Test
  void testMultipleLoads() {
    int totalRecords = ra()
      .get(getRequestUrl(BaseCalendarApiTest.COLLECTION_CALENDAR_API_ROUTE))
      .getBody()
      .as(CalendarCollectionDTO.class)
      .getTotalRecords();

    this.loadSample(); // second time; first is automatically there

    assertThat(
      "No additional data was loaded when samples were loaded twice",
      ra()
        .get(getRequestUrl(BaseCalendarApiTest.COLLECTION_CALENDAR_API_ROUTE))
        .getBody()
        .as(CalendarCollectionDTO.class)
        .getTotalRecords(),
      is(equalTo(totalRecords))
    );
  }
}
