package org.folio.calendar.integration.calendar.periods.post;

import static org.folio.calendar.testutils.DateTimeHandler.isCurrentInstant;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.Error;
import org.folio.calendar.domain.dto.ErrorCode;
import org.folio.calendar.domain.dto.ErrorResponse;
import org.folio.calendar.testconstants.Periods;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CreateExceptionalCalendarErrorTest extends CreateCalendarAbstractTest {

  @Test
  void testOverlappingExceptionalCalendars() {
    sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_A,
      Periods.PERIOD_FULL_EXCEPTIONAL_A.getServicePointId()
    )
      .then()
      .statusCode(is(HttpStatus.CREATED.value()));

    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_B,
      Periods.PERIOD_FULL_EXCEPTIONAL_B.getServicePointId()
    );

    response.then().statusCode(is(HttpStatus.CONFLICT.value()));

    ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.CONFLICT.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    Error error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports that the date range was invalid",
      error.getCode(),
      is(ErrorCode.OVERLAPPING_CALENDAR)
    );
    assertThat(
      "Error message specified overlap information",
      error.getMessage(),
      containsString(
        String.format(
          "This period (%s to %s) overlaps with another calendar (\"%s\" from %s to %s)",
          Periods.PERIOD_FULL_EXCEPTIONAL_B.getStartDate(),
          Periods.PERIOD_FULL_EXCEPTIONAL_B.getEndDate(),
          Periods.PERIOD_FULL_EXCEPTIONAL_A.getName(),
          Periods.PERIOD_FULL_EXCEPTIONAL_A.getStartDate(),
          Periods.PERIOD_FULL_EXCEPTIONAL_A.getEndDate()
        )
      )
    );
  }
}
