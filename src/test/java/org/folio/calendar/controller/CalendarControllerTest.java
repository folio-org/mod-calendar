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
import org.folio.calendar.repository.CalendarRepository;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CalendarControllerTest extends BaseApiTest {

  public static final String CREATE_CALENDAR_API_ROUTE = "/calendar/periods/%s/period";

  @Autowired
  protected CalendarRepository calendarRepository;

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
  void testCalendarCCreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_C,
      Periods.PERIOD_FULL_EXAMPLE_C.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXAMPLE_C))
    );
  }

  @Test
  void testCalendarDCreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_D,
      Periods.PERIOD_FULL_EXAMPLE_D.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXAMPLE_D))
    );
  }

  @Test
  void testCalendarECreation() {
    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_E,
      Periods.PERIOD_FULL_EXAMPLE_E.getServicePointId()
    );
    response.then().statusCode(is(HttpStatus.CREATED.value()));
    Period result = response.getBody().as(Period.class);
    assertThat(
      "Returned period is the same as created one",
      result,
      is(equalTo(Periods.PERIOD_FULL_EXAMPLE_E))
    );
  }

  @Test
  void testMultipleServicePointCreation() {
    testCalendarACreation();
    testCalendarBCreation();
  }

  @Test
  void testMultipleCalendarsForSameServicePoint() {
    testCalendarDCreation();
    testCalendarECreation();
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
    testCalendarCCreation();

    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_D,
      Periods.PERIOD_FULL_EXAMPLE_D.getServicePointId()
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
    testCalendarDCreation();

    Response response = sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_E.withId(Periods.PERIOD_FULL_EXAMPLE_D.getId()),
      Periods.PERIOD_FULL_EXAMPLE_E.getServicePointId()
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
