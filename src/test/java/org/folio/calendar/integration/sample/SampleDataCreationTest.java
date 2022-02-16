package org.folio.calendar.integration.sample;

import static org.folio.calendar.integration.calendar.periods.get.GetPeriodAbstractTest.GET_PERIOD_API_ROUTE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.folio.calendar.domain.dto.OpeningDayConcreteCollection;
import org.junit.jupiter.api.Test;

class SampleDataCreationTest extends AbstractSampleTest {

  @Test
  void testLoadSample() {
    OpeningDayConcreteCollection collection = ra()
      .get(getRequestUrl(GET_PERIOD_API_ROUTE))
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "The returned collection includes sample calendars",
      collection.getOpeningPeriods(),
      is(not(hasSize(0)))
    );
  }

  @Test
  void testMultipleLoads() {
    int totalRecords = ra()
      .get(getRequestUrl(GET_PERIOD_API_ROUTE))
      .getBody()
      .as(OpeningDayConcreteCollection.class)
      .getTotalRecords();

    this.loadSample(); // second time; first is automatically there

    assertThat(
      "No additional data was loaded when samples were loaded twice",
      ra()
        .get(getRequestUrl(GET_PERIOD_API_ROUTE))
        .getBody()
        .as(OpeningDayConcreteCollection.class)
        .getTotalRecords(),
      is(equalTo(totalRecords))
    );
  }
}
