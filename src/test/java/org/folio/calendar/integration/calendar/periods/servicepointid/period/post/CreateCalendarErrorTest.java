package org.folio.calendar.integration.calendar.periods.servicepointid.period.post;

import static org.folio.calendar.testutils.DateTimeHandler.isCurrentInstant;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.dto.ErrorDTO;
import org.folio.calendar.domain.dto.ErrorResponseDTO;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("idempotent")
class CreateCalendarErrorTest extends CreateCalendarAbstractTest {

  @Test
  void testInvalidServicePointId() {
    Response response = sendCalendarCreationRequest(Periods.PERIOD_FULL_EXAMPLE_A, UUIDs.UUID_F);

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
      "Error reports an invalid request was made",
      error.getCode(),
      is(ErrorCodeDTO.INVALID_REQUEST)
    );
    assertThat(
      "Error message specified service point mismatch",
      error.getMessage(),
      containsString(
        String.format(
          "Service point ID in the URL (%s) does not match the payload (%s)",
          UUIDs.UUID_F,
          Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
        )
      )
    );
  }

  @Test
  void testEmptyCalendarName() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_A.withName(""),
      Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
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
      is(ErrorCodeDTO.NO_NAME)
    );
    assertThat(
      "Error message specified missing name error",
      error.getMessage(),
      containsString("Name cannot be empty or only whitespace")
    );
  }

  @Test
  void testWhitespaceCalendarName() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_A.withName(" \t "), // space tab space
      Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
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
      is(ErrorCodeDTO.NO_NAME)
    );
    assertThat(
      "Error message specified missing name error",
      error.getMessage(),
      containsString("Name cannot be empty or only whitespace")
    );
  }

  @Test
  void testInvalidCalendarDates() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_A
        .withStartDate(Dates.LDATE_2021_12_31)
        .withEndDate(Dates.LDATE_2021_01_01),
      Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
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
      "Error reports that the date range was invalid",
      error.getCode(),
      is(ErrorCodeDTO.INVALID_DATE_RANGE)
    );
    assertThat(
      "Error message specified invalid date range",
      error.getMessage(),
      containsString("cannot be after end date")
    );
  }

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

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.CONFLICT.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports that the date range was invalid",
      error.getCode(),
      is(ErrorCodeDTO.OVERLAPPING_CALENDAR)
    );
    assertThat(
      "Error message specified overlap information",
      error.getMessage(),
      containsString(
        String.format(
          "This calendar overlaps with another calendar (“%s”",
          Periods.PERIOD_FULL_EXAMPLE_C.getName()
        )
      )
    );
  }
}
