package org.folio.calendar.integration.calendar.periods.servicepointid.period.post;

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

class CreateCalendarConflictTest extends CreateCalendarAbstractTest {

  @Test
  void testOverlappingCalendars() {
    sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_C,
      Periods.PERIOD_FULL_EXAMPLE_C.getServicePointId()
    )
      .then()
      .statusCode(is(HttpStatus.CREATED.value()));

    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_D,
      Periods.PERIOD_FULL_EXAMPLE_D.getServicePointId()
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
          Periods.PERIOD_FULL_EXAMPLE_D.getStartDate(),
          Periods.PERIOD_FULL_EXAMPLE_D.getEndDate(),
          Periods.PERIOD_FULL_EXAMPLE_C.getName(),
          Periods.PERIOD_FULL_EXAMPLE_C.getStartDate(),
          Periods.PERIOD_FULL_EXAMPLE_C.getEndDate()
        )
      )
    );
  }

  @Test
  void testSameIdCalendars() {
    sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_D,
      Periods.PERIOD_FULL_EXAMPLE_D.getServicePointId()
    )
      .then()
      .statusCode(is(HttpStatus.CREATED.value()));

    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_E.withId(Periods.PERIOD_FULL_EXAMPLE_D.getId()),
      Periods.PERIOD_FULL_EXAMPLE_E.getServicePointId()
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
      "Error reports that the period with this ID already exists",
      error.getCode(),
      is(ErrorCode.INVALID_REQUEST)
    );
    assertThat(
      "Error message specifies collision",
      error.getMessage(),
      containsString(
        String.format("The period ID %s already exists", Periods.PERIOD_FULL_EXAMPLE_D.getId())
      )
    );
  }
}
