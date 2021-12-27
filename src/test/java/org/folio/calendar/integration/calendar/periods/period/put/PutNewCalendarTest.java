package org.folio.calendar.integration.calendar.periods.period.put;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PutNewCalendarTest extends PutCalendarAbstractTest {

  @Test
  void testPutNewCalendar() {
    sendPutRequest(Periods.PERIOD_FULL_EXAMPLE_E, UUIDs.UUID_1, UUIDs.UUID_E)
      .then()
      .statusCode(is(HttpStatus.NO_CONTENT.value()));

    Response response = sendGetRequest(UUIDs.UUID_1, UUIDs.UUID_E);
    response.then().statusCode(is(HttpStatus.OK.value()));
    Period period = response.getBody().as(Period.class);
    assertThat(
      "The created period is the expected period",
      period,
      is(Periods.PERIOD_FULL_EXAMPLE_E)
    );
  }
}
