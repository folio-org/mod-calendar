package org.folio.calendar.integration.api.calendar.periods.servicepointid.period.periodid.put;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.testconstants.Names;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PutOverwriteCalendarTest extends PutCalendarAbstractTest {

  @Test
  void testPutOverwriteCalendar() {
    sendPutRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_D.withServicePointId(UUIDs.UUID_1),
      UUIDs.UUID_1,
      UUIDs.UUID_D
    )
      .then()
      .statusCode(is(HttpStatus.NO_CONTENT.value()));

    Response response = sendGetRequest(UUIDs.UUID_1, UUIDs.UUID_D);
    response.then().statusCode(is(HttpStatus.OK.value()));
    Period period = response.getBody().as(Period.class);
    assertThat(
      "The newly overwritten period is the expected period",
      period,
      is(Periods.PERIOD_FULL_EXCEPTIONAL_D.withServicePointId(UUIDs.UUID_1))
    );
  }

  @Test
  void testPutOverwriteSameNormalCalendar() {
    sendPutRequest(Periods.PERIOD_FULL_EXAMPLE_D.withName(Names.NAME_3), UUIDs.UUID_1, UUIDs.UUID_D)
      .then()
      .statusCode(is(HttpStatus.NO_CONTENT.value()));

    Response response = sendGetRequest(UUIDs.UUID_1, UUIDs.UUID_D);
    response.then().statusCode(is(HttpStatus.OK.value()));
    Period period = response.getBody().as(Period.class);
    assertThat(
      "The newly overwritten period is the expected period",
      period,
      is(Periods.PERIOD_FULL_EXAMPLE_D.withName(Names.NAME_3))
    );
  }

  @Test
  void testPutOverwriteSameExceptionalCalendar() {
    sendPutRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_C.withName(Names.NAME_2),
      UUIDs.UUID_5,
      UUIDs.UUID_C
    )
      .then()
      .statusCode(is(HttpStatus.NO_CONTENT.value()));

    Response response = sendGetRequest(UUIDs.UUID_5, UUIDs.UUID_C);
    response.then().statusCode(is(HttpStatus.OK.value()));
    Period period = response.getBody().as(Period.class);
    assertThat(
      "The newly overwritten period is the expected period",
      period,
      is(Periods.PERIOD_FULL_EXCEPTIONAL_C.withName(Names.NAME_2))
    );
  }

  @Test
  void testPutOverwriteDifferentIdCalendar() {
    // change (1,D) to (1,3)
    sendPutRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_D.withServicePointId(UUIDs.UUID_1).withId(UUIDs.UUID_3),
      UUIDs.UUID_1,
      UUIDs.UUID_D
    )
      .then()
      .statusCode(is(HttpStatus.NO_CONTENT.value()));

    Response response = sendGetRequest(UUIDs.UUID_1, UUIDs.UUID_3);
    response.then().statusCode(is(HttpStatus.OK.value()));
    Period period = response.getBody().as(Period.class);
    assertThat(
      "The newly written period is the expected period",
      period,
      is(Periods.PERIOD_FULL_EXCEPTIONAL_D.withServicePointId(UUIDs.UUID_1).withId(UUIDs.UUID_3))
    );

    // original is deleted
    response = sendGetRequest(UUIDs.UUID_1, UUIDs.UUID_D);
    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));
  }
}
