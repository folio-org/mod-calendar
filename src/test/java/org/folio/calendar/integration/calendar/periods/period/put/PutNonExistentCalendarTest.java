package org.folio.calendar.integration.calendar.periods.period.put;

import static org.folio.calendar.testutils.DateTimeHandler.isCurrentInstant;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.Error;
import org.folio.calendar.domain.dto.ErrorCode;
import org.folio.calendar.domain.dto.ErrorResponse;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PutNonExistentCalendarTest extends PutCalendarAbstractTest {

  @Test
  void testPutOverwriteCalendar() {
    Response response = sendGetRequest(UUIDs.UUID_1, UUIDs.UUID_D);
    response.then().statusCode(is(HttpStatus.OK.value()));
    Period period = response.getBody().as(Period.class);
    assertThat(
      "The current period is the original period",
      period,
      is(Periods.PERIOD_FULL_EXAMPLE_D)
    );

    sendPutRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_D.withServicePointId(UUIDs.UUID_1),
      UUIDs.UUID_1,
      UUIDs.UUID_D
    )
      .then()
      .statusCode(is(HttpStatus.NO_CONTENT.value()));

    response = sendGetRequest(UUIDs.UUID_1, UUIDs.UUID_D);
    response.then().statusCode(is(HttpStatus.OK.value()));
    period = response.getBody().as(Period.class);
    assertThat(
      "The newly overwritten period is the expected period",
      period,
      is(Periods.PERIOD_FULL_EXCEPTIONAL_D.withServicePointId(UUIDs.UUID_1))
    );
  }
}
