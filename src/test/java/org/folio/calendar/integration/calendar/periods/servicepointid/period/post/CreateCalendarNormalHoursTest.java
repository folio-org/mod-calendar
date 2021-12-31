package org.folio.calendar.integration.calendar.periods.servicepointid.period.post;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.testconstants.Periods;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CreateCalendarNormalHoursTest extends CreateCalendarAbstractTest {

  @Test
  void testCalendarACreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_A,
      Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXAMPLE_A))
    );
  }

  @Test
  void testCalendarBCreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_B,
      Periods.PERIOD_FULL_EXAMPLE_B.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXAMPLE_B))
    );
  }

  @Test
  void testCalendarCCreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_C,
      Periods.PERIOD_FULL_EXAMPLE_C.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXAMPLE_C))
    );
  }

  @Test
  void testCalendarDCreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_D,
      Periods.PERIOD_FULL_EXAMPLE_D.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXAMPLE_D))
    );
  }

  @Test
  void testCalendarECreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_E,
      Periods.PERIOD_FULL_EXAMPLE_E.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXAMPLE_E))
    );
  }

  @Test
  void testExceptionalCalendarACreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_A,
      Periods.PERIOD_FULL_EXCEPTIONAL_A.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXCEPTIONAL_A))
    );
  }

  @Test
  void testMultipleServicePointCreation() {
    testCalendarACreation();
    testCalendarBCreation();
  }

  @Test
  void testMultipleCalendarsForSameServicePoint() {
    testCalendarDCreation();
    testCalendarECreation();
  }
}
