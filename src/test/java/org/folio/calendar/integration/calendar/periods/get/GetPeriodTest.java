package org.folio.calendar.integration.calendar.periods.get;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.OpeningDayConcreteCollection;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GetPeriodTest extends GetPeriodAbstractTest {

  private static final int NUM_TOTAL_PERIODS = 265;

  @Test
  void testGetAllPeriods() {
    Response response = sendRequest(null, null, null, null, null, null, null);
    response.then().statusCode(is(HttpStatus.OK.value()));

    OpeningDayConcreteCollection collection = response
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "The returned collection has ten elements",
      collection.getOpeningPeriods(),
      hasSize(10)
    );

    assertThat(
      "The returned collection has the proper number of total available",
      collection.getTotalRecords(),
      is(NUM_TOTAL_PERIODS)
    );
  }

  @Test
  void testGetSingleServicePointPeriods() {
    Response response = sendRequest(UUIDs.UUID_1, null, null, null, null, null, null);
    response.then().statusCode(is(HttpStatus.OK.value()));

    OpeningDayConcreteCollection collection = response
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "The returned collection has a subset of all of the service points",
      collection.getTotalRecords(),
      is(lessThan(NUM_TOTAL_PERIODS))
    );
  }

  @Test
  void testGetEmptyServicePointPeriods() {
    Response response = sendRequest(UUIDs.UUID_F, null, null, null, null, null, null);
    response.then().statusCode(is(HttpStatus.OK.value()));

    OpeningDayConcreteCollection collection = response
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "The returned collection has no opening information",
      collection.getOpeningPeriods(),
      hasSize(0)
    );
    assertThat(
      "The returned collection has no opening information",
      collection.getTotalRecords(),
      is(equalTo(0))
    );
  }

  @Test
  void testGetEmptyServicePointPeriodsWithStartLimit() {
    Response response = sendRequest(
      UUIDs.UUID_F,
      Dates.DATE_2021_01_01,
      null,
      null,
      null,
      null,
      null
    );
    response.then().statusCode(is(HttpStatus.OK.value()));

    OpeningDayConcreteCollection collection = response
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "The returned collection has no opening information",
      collection.getOpeningPeriods(),
      hasSize(0)
    );
    assertThat(
      "The returned collection has no opening information",
      collection.getTotalRecords(),
      is(equalTo(0))
    );
  }

  @Test
  void testGetEmptyServicePointPeriodsWithEndLimit() {
    Response response = sendRequest(
      UUIDs.UUID_F,
      null,
      Dates.DATE_2021_12_31,
      null,
      null,
      null,
      null
    );
    response.then().statusCode(is(HttpStatus.OK.value()));

    OpeningDayConcreteCollection collection = response
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "The returned collection has no opening information",
      collection.getOpeningPeriods(),
      hasSize(0)
    );
    assertThat(
      "The returned collection has no opening information",
      collection.getTotalRecords(),
      is(equalTo(0))
    );
  }

  @Test
  void testGetEmptyServicePointPeriodsWithDoubleLimit() {
    Response response = sendRequest(
      UUIDs.UUID_F,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_12_31,
      false,
      null,
      null,
      null
    );
    response.then().statusCode(is(HttpStatus.OK.value()));

    OpeningDayConcreteCollection collection = response
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "The returned collection has no opening information",
      collection.getOpeningPeriods(),
      hasSize(0)
    );
    assertThat(
      "The returned collection has no opening information",
      collection.getTotalRecords(),
      is(equalTo(0))
    );
  }

  @Test
  void testLimitStartDate() {
    Response response = sendRequest(null, Dates.DATE_2021_05_01, null, null, null, null, null);
    response.then().statusCode(is(HttpStatus.OK.value()));

    OpeningDayConcreteCollection collection = response
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "The returned collection has a subset of all of the service points",
      collection.getTotalRecords(),
      is(lessThan(NUM_TOTAL_PERIODS))
    );
  }

  @Test
  void testLimitEndDate() {
    Response response = sendRequest(null, null, Dates.DATE_2021_05_01, null, null, null, null);
    response.then().statusCode(is(HttpStatus.OK.value()));

    OpeningDayConcreteCollection collection = response
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "The returned collection has a subset of all of the service points",
      collection.getTotalRecords(),
      is(lessThan(NUM_TOTAL_PERIODS))
    );
  }

  @Test
  void testPagination() {
    OpeningDayConcreteCollection mainCollection = sendRequest(
      null,
      null,
      null,
      null,
      null,
      null,
      Integer.MAX_VALUE
    )
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    OpeningDayConcreteCollection firstTen = sendRequest(null, null, null, null, null, null, 10)
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    OpeningDayConcreteCollection secondTen = sendRequest(null, null, null, null, null, 10, 10)
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "First ten limited results should be equivalent to the first ten of the full list",
      firstTen.getOpeningPeriods(),
      is(mainCollection.getOpeningPeriods().subList(0, 10))
    );
    assertThat(
      "Second ten limited results should be equivalent to the second ten of the full list",
      secondTen.getOpeningPeriods(),
      is(mainCollection.getOpeningPeriods().subList(10, 20))
    );

    assertThat(
      "First ten still reports the correct totalRecords",
      firstTen.getTotalRecords(),
      is(equalTo(mainCollection.getTotalRecords()))
    );
    assertThat(
      "Second ten still reports the correct totalRecords",
      secondTen.getTotalRecords(),
      is(equalTo(mainCollection.getTotalRecords()))
    );
  }
}
