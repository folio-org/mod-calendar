package org.folio.calendar.integration.calendar.periods.servicepointid.period.periodid.delete;

import static org.hamcrest.Matchers.is;

import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class DeleteCalendarTest extends DeleteCalendarAbstractTest {

  @Test
  void testDeleteNormalHoursCalendar() {
    sendDeleteRequest(UUIDs.UUID_0, UUIDs.UUID_A)
      .then()
      .statusCode(is(HttpStatus.NO_CONTENT.value()));

    sendGetRequest(UUIDs.UUID_0, UUIDs.UUID_A).then().statusCode(is(HttpStatus.NOT_FOUND.value()));
  }

  @Test
  void testDeleteExceptionalCalendar() {
    sendDeleteRequest(UUIDs.UUID_0, UUIDs.UUID_F)
      .then()
      .statusCode(is(HttpStatus.NO_CONTENT.value()));

    sendGetRequest(UUIDs.UUID_0, UUIDs.UUID_F).then().statusCode(is(HttpStatus.NOT_FOUND.value()));
  }
}
