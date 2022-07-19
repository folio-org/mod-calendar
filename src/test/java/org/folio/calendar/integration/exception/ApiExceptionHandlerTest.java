package org.folio.calendar.integration.exception;

import static org.folio.calendar.testutils.DateTimeHandler.isCurrentInstant;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.dto.ErrorDTO;
import org.folio.calendar.domain.dto.ErrorResponseDTO;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.integration.ValidationSchema;
import org.folio.calendar.integration.api.calendar.BaseCalendarApiTest;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class ApiExceptionHandlerTest extends BaseApiTest {

  public static final String BAD_API_ROUTE = "/bad";

  public static final String VALID_API_ROUTE = BaseCalendarApiTest.GET_SEARCH_CALENDAR_API_ROUTE;

  @Test
  void testInvalidGetApiRoute() {
    Response response = ra(ValidationSchema.NONE).get(getRequestUrl(BAD_API_ROUTE));

    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.NOT_FOUND.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports the endpoint is not found (an invalid request)",
      error.getCode(),
      is(ErrorCodeDTO.INVALID_REQUEST)
    );
    assertThat(
      "Error message specified that the endpoint is unknown",
      error.getMessage(),
      containsString("This application does not know how to handle a ")
    );
  }

  @Test
  void testInvalidPostApiRoute() {
    Response response = ra(ValidationSchema.NONE).post(getRequestUrl(BAD_API_ROUTE));

    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.NOT_FOUND.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports the endpoint is not found (an invalid request)",
      error.getCode(),
      is(ErrorCodeDTO.INVALID_REQUEST)
    );
    assertThat(
      "Error message specified that the endpoint is unknown",
      error.getMessage(),
      containsString("This application does not know how to handle a ")
    );
  }

  @Test
  void testInvalidMethodToValidApiRoute() {
    Response response = ra(ValidationSchema.NONE)
      .patch(getRequestUrl(String.format(VALID_API_ROUTE, UUIDs.UUID_0)));

    response.then().statusCode(is(HttpStatus.METHOD_NOT_ALLOWED.value()));

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.METHOD_NOT_ALLOWED.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports this method is an invalid request",
      error.getCode(),
      is(ErrorCodeDTO.INVALID_REQUEST)
    );
    assertThat(
      "Error message specified that the endpoint can not accept these requests",
      error.getMessage(),
      containsString("This endpoint does not accept PATCH requests")
    );
  }

  @Test
  void testInvalidParametersToValidApiRoute() {
    Response response = ra(ValidationSchema.NONE)
      .post(getRequestUrl(String.format(VALID_API_ROUTE, UUIDs.UUID_INVALID)));

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
      "Error reports that there was an invalid parameter",
      error.getCode(),
      is(ErrorCodeDTO.INVALID_PARAMETER)
    );
    assertThat(
      "Error message specified that a parameter was not understood",
      error.getMessage(),
      containsString("One of the parameters was the incorrect type")
    );
  }

  @Test
  void testInvalidRequestBody() {
    Response response = ra(ValidationSchema.NONE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body("{}")
      .post(getRequestUrl(String.format(VALID_API_ROUTE, UUIDs.UUID_0)));

    // for some reason, a NullPointerException occurs in the JSON parser; nothing we can control
    response.then().statusCode(is(HttpStatus.INTERNAL_SERVER_ERROR.value()));

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);

    assertThat("Error timestamp is current", errorResponse.getTimestamp(), isCurrentInstant());
    assertThat(
      "Error HTTP code is correct",
      errorResponse.getStatus(),
      is(HttpStatus.INTERNAL_SERVER_ERROR.value())
    );
    assertThat("One error was returned", errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);

    assertThat(
      "Error reports that there was an internal server error",
      error.getCode(),
      is(ErrorCodeDTO.INTERNAL_SERVER_ERROR)
    );
    assertThat(
      "Error reports proper",
      error.getMessage(),
      containsString("An internal server error occurred")
    );
  }
}
