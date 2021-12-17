package org.folio.calendar.controller;

import static org.folio.calendar.testutils.DateTimeHandler.isCurrentInstant;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import java.util.UUID;
import org.folio.calendar.domain.dto.Error;
import org.folio.calendar.domain.dto.ErrorCode;
import org.folio.calendar.domain.dto.ErrorResponse;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CalendarControllerTest extends BaseApiTest {

  public static final String CREATE_CALENDAR_API_ROUTE = "/calendar/periods/%s/period";

  /**
   * Send a Calendar creation request
   * @param calendar calendar to create, as a legacy Period
   * @param servicePointId service point to assign
   * @return the Response
   */
  public Response sendCalendarCreationRequest(Period calendar, UUID servicePointId) {
    return ra()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(calendar)
      .post(getRequestUrl(String.format(CREATE_CALENDAR_API_ROUTE, servicePointId)));
  }

  @Test
  void testCalendarACreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_A,
      Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXAMPLE_A))
    );
  }

  @Test
  void testCalendarBCreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_B,
      Periods.PERIOD_FULL_EXAMPLE_B.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXAMPLE_B))
    );
  }

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
}
