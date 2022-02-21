package org.folio.calendar.integration;

import static org.folio.calendar.testutils.DateTimeHandler.isCurrentInstant;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.Error;
import org.folio.calendar.domain.dto.ErrorCode;
import org.folio.calendar.domain.dto.ErrorResponse;
import org.folio.calendar.integration.calendar.periods.post.CreateCalendarAbstractTest;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class ApiExceptionHandlerTest extends BaseApiTest {

  public static final String BAD_API_ROUTE = "/bad";

  public static final String VALID_API_ROUTE = CreateCalendarAbstractTest.CREATE_CALENDAR_API_ROUTE;

  @Tag("DB_USAGE_NONE")
  @Test
  void testInvalidGetApiRoute() {
    Response response = ra(false).get(getRequestUrl(BAD_API_ROUTE));

    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.NOT_FOUND.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    Error error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports the endpoint is not found (an invalid request)",
      error.getCode(),
      is(ErrorCode.INVALID_REQUEST)
    );
    assertThat(
      "Error message specified that the endpoint is unknown",
      error.getMessage(),
      containsString("This application does not know how to handle a ")
    );
  }

  @Tag("DB_USAGE_NONE")
  @Test
  void testInvalidPostApiRoute() {
    Response response = ra(false).post(getRequestUrl(BAD_API_ROUTE));

    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.NOT_FOUND.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    Error error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports the endpoint is not found (an invalid request)",
      error.getCode(),
      is(ErrorCode.INVALID_REQUEST)
    );
    assertThat(
      "Error message specified that the endpoint is unknown",
      error.getMessage(),
      containsString("This application does not know how to handle a ")
    );
  }

  @Tag("DB_USAGE_NONE")
  @Test
  void testInvalidMethodToValidApiRoute() {
    Response response = ra(false)
      .patch(getRequestUrl(String.format(VALID_API_ROUTE, UUIDs.UUID_0)));

    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.NOT_FOUND.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    Error error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports this method is an invalid request",
      error.getCode(),
      is(ErrorCode.INVALID_REQUEST)
    );
    assertThat(
      "Error message specified that the endpoint can not accept these requests",
      error.getMessage(),
      containsString("This endpoint does not accept PATCH requests")
    );
  }

  @Tag("DB_USAGE_NONE")
  @Test
  void testInvalidParametersToValidApiRoute() {
    Response response = ra(false)
      .post(getRequestUrl(String.format(VALID_API_ROUTE, UUIDs.UUID_INVALID)));

    response.then().statusCode(is(HttpStatus.BAD_REQUEST.value()));

    ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.BAD_REQUEST.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    Error error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports that there was an invalid parameter",
      error.getCode(),
      is(ErrorCode.INVALID_PARAMETER)
    );
    assertThat(
      "Error message specified that a parameter was not understood",
      error.getMessage(),
      containsString("One of the parameters was of the incorrect type")
    );
  }

  @Tag("DB_USAGE_NONE")
  @Test
  void testInvalidRequestBody() {
    Response response = ra(false)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body("{}")
      .post(getRequestUrl(String.format(VALID_API_ROUTE, UUIDs.UUID_0)));

    // for some reason, a NullPointerException occurs in the JSON parser; nothing we can control
    response.then().statusCode(is(HttpStatus.INTERNAL_SERVER_ERROR.value()));

    ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.INTERNAL_SERVER_ERROR.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    Error error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports that there was an internal server error",
      error.getCode(),
      is(ErrorCode.INTERNAL_SERVER_ERROR)
    );
    assertThat(
      "Error message specifies that an internal server error",
      error.getMessage(),
      containsString("Internal server error")
    );
  }
}
