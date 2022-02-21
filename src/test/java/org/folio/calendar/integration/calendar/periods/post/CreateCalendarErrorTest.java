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
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CreateCalendarErrorTest extends CreateCalendarAbstractTest {

  @Tag("DB_USAGE_NONE")
  @Test
  void testInvalidServicePointId() {
    Response response = sendCalendarCreationRequest(Periods.PERIOD_FULL_EXAMPLE_A, UUIDs.UUID_F);

    response.then().statusCode(is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.UNPROCESSABLE_ENTITY.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    Error error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports an invalid request was made",
      error.getCode(),
      is(ErrorCode.INVALID_REQUEST)
    );
    assertThat(
      "Error message specified service point mismatch",
      error.getMessage(),
      containsString(
        String.format(
          "The service point ID in the URL (%s) did not match the one in the payload (%s)",
          UUIDs.UUID_F,
          Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
        )
      )
    );
  }

  @Tag("DB_USAGE_NONE")
  @Test
  void testEmptyCalendarName() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_A.withName(""),
      Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
    );

    response.then().statusCode(is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.UNPROCESSABLE_ENTITY.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    Error error = errorResponse.getErrors().get(0);

    assertThat("Error reports that no name was provided", error.getCode(), is(ErrorCode.NO_NAME));
    assertThat(
      "Error message specified missing name error",
      error.getMessage(),
      containsString(String.format("The provided name (\"%s\") was empty", ""))
    );
  }

  @Tag("DB_USAGE_NONE")
  @Test
  void testWhitespaceCalendarName() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_A.withName(" \t "), // space tab space
      Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
    );

    response.then().statusCode(is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.UNPROCESSABLE_ENTITY.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    Error error = errorResponse.getErrors().get(0);

    assertThat("Error reports that no name was provided", error.getCode(), is(ErrorCode.NO_NAME));
    assertThat(
      "Error message specified missing name error",
      error.getMessage(),
      containsString(String.format("The provided name (\"%s\") was empty", " \t "))
    );
  }

  @Tag("DB_USAGE_NONE")
  @Test
  void testInvalidCalendarDates() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_A
        .withStartDate(Dates.DATE_2021_12_31)
        .withEndDate(Dates.DATE_2021_01_01),
      Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
    );

    response.then().statusCode(is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.UNPROCESSABLE_ENTITY.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    Error error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports that the date range was invalid",
      error.getCode(),
      is(ErrorCode.INVALID_DATE_RANGE)
    );
    assertThat(
      "Error message specified invalid date range",
      error.getMessage(),
      containsString(
        String.format(
          "The start date (%s) was after the end date (%s)",
          Dates.DATE_2021_12_31,
          Dates.DATE_2021_01_01
        )
      )
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
