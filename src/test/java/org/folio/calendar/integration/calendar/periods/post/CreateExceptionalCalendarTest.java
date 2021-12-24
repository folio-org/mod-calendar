package org.folio.calendar.integration.calendar.periods.post;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.testconstants.Periods;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CreateExceptionalCalendarTest extends CreateCalendarAbstractTest {

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
  void testExceptionalCalendarBCreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_B,
      Periods.PERIOD_FULL_EXCEPTIONAL_B.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXCEPTIONAL_B))
    );
  }

  @Test
  void testExceptionalCalendarCCreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_C,
      Periods.PERIOD_FULL_EXCEPTIONAL_C.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXCEPTIONAL_C))
    );
  }

  @Test
  void testExceptionalCalendarDCreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_D,
      Periods.PERIOD_FULL_EXCEPTIONAL_D.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXCEPTIONAL_D))
    );
  }
}
