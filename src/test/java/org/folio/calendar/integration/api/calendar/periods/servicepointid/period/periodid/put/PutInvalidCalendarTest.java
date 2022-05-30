package org.folio.calendar.integration.api.calendar.periods.servicepointid.period.periodid.put;

import static org.folio.calendar.testutils.DateTimeHandler.isCurrentInstant;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.dto.ErrorDTO;
import org.folio.calendar.domain.dto.ErrorResponseDTO;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("idempotent")
class PutInvalidCalendarTest extends PutCalendarAbstractTest {

  @Test
  void testPutInvalidCalendar() {
    Response response = sendPutRequest(
      Periods.PERIOD_FULL_EXAMPLE_A.withName(""),
      UUIDs.UUID_0,
      UUIDs.UUID_A
    );

    response.then().statusCode(is(HttpStatus.BAD_REQUEST.value()));

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.BAD_REQUEST.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports that no name was provided",
      error.getCode(),
      is(ErrorCodeDTO.CALENDAR_NO_NAME)
    );
    assertThat(
      "Error message specified missing name error",
      error.getMessage(),
      containsString("Please provide a non-empty calendar name")
    );
  }
}
