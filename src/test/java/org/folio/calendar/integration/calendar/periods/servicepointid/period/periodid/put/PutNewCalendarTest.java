package org.folio.calendar.integration.calendar.periods.servicepointid.period.periodid.put;

import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
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
    // no calendar should actually be created
    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));
  }
}
