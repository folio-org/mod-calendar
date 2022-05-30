package org.folio.calendar.integration.api.calendar.periods.servicepointid.period.periodid.get;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GetSpecificCalendarTest extends GetSpecificCalendarAbstractTest {

  @Test
  void testGetNormalHoursCalendar() {
    Response response = sendGetRequest(UUIDs.UUID_0, UUIDs.UUID_A);
    response.then().statusCode(is(HttpStatus.OK.value()));
    Period period = response.getBody().as(Period.class);
    assertThat(
      "The returned period is the expected period",
      period,
      is(Periods.PERIOD_FULL_EXAMPLE_F)
    );
  }

  @Test
  void testGetExceptionalCalendar() {
    Response response = sendGetRequest(UUIDs.UUID_0, UUIDs.UUID_F);
    response.then().statusCode(is(HttpStatus.OK.value()));
    Period period = response.getBody().as(Period.class);
    assertThat(
      "The returned period is the expected period",
      period,
      is(Periods.PERIOD_FULL_EXCEPTIONAL_F)
    );
  }
}
