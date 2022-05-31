package org.folio.calendar.integration.api.openinghours.calendars.get;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.dto.ErrorDTO;
import org.folio.calendar.domain.dto.ErrorResponseDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.integration.BaseApiAutoDatabaseTest;
import org.folio.calendar.integration.ValidationSchema;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class GetCalendarTest extends BaseApiAutoDatabaseTest {

  public static final String CREATE_CALENDAR_API_ROUTE = "/opening-hours/calendars";
  public static final String GET_CALENDAR_API_ROUTE = "/opening-hours/calendars/%s";

  @Autowired
  private CalendarMapper calendarMapper;

  /**
   * Send a Calendar creation request
   * @param calendar the calendar to create
   * @return the Response
   */
  public Response sendCalendarCreationRequest(Calendar calendar) {
    return ra(ValidationSchema.OPENING_HOURS)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(calendarMapper.toDto(calendar))
      .post(getRequestUrl(CREATE_CALENDAR_API_ROUTE));
  }

  /**
   * Send a Calendar creation request
   * @param calendar the calendar to create
   * @return the Response
   */
  public Response sendCalendarGetRequest(List<UUID> ids) {
    // spec must be ignored as the validator improperly flags comma-separated UUIDs
    // as being illegal
    return ra(ValidationSchema.NONE)
      .get(
        getRequestUrl(
          String.format(
            GET_CALENDAR_API_ROUTE,
            ids.stream().map(UUID::toString).collect(Collectors.joining(","))
          )
        )
      );
  }

  @Test
  @SuppressWarnings("unchecked")
  void testMissingGet() {
    Response response = sendCalendarGetRequest(Arrays.asList(UUIDs.UUID_0, UUIDs.UUID_1));
    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);
    assertThat(errorResponse.getStatus(), is(equalTo(HttpStatus.NOT_FOUND.value())));
    assertThat(
      "Multiple calendars not found is consolidated into one error",
      errorResponse.getErrors(),
      hasSize(1)
    );

    ErrorDTO error = errorResponse.getErrors().get(0);
    assertThat(error.getCode(), is(ErrorCodeDTO.CALENDAR_NOT_FOUND));
    assertThat(error.getMessage(), is("No calendar was found with the specified query"));

    List<String> missingUuids =
      ((LinkedHashMap<String, List<String>>) error.getData()).get("notFound");
    assertThat(missingUuids, hasSize(2));
    assertThat(missingUuids, containsInAnyOrder(UUIDs.UUID_0.toString(), UUIDs.UUID_1.toString()));
  }

  @Test
  void testSingleExistingGet() {
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_A);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId1 = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();

    Response getResponse = sendCalendarGetRequest(Arrays.asList(createdId1));
    getResponse.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO result = getResponse.getBody().as(CalendarCollectionDTO.class);

    assertThat(
      "Getting a single existing calendar results in one calendar returned",
      result.getTotalRecords(),
      is(equalTo(1))
    );
    assertThat(
      "Getting a single existing calendar results in one calendar returned",
      result.getCalendars(),
      hasSize(1)
    );
    assertThat(
      "Getting an existing calendar results in the proper calendar returned",
      result.getCalendars().stream().map(calendarMapper::fromDto).collect(Collectors.toList()),
      contains(Calendars.CALENDAR_COMBINED_EXAMPLE_A.withId(createdId1))
    );
  }

  @Test
  void testMultipleGet() {
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_A);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId1 = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();

    postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_C);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId2 = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();

    Response getResponse = sendCalendarGetRequest(Arrays.asList(createdId1, createdId2));
    getResponse.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO result = getResponse.getBody().as(CalendarCollectionDTO.class);

    assertThat(
      "Getting two existing calendars results in two calendars returned",
      result.getTotalRecords(),
      is(equalTo(2))
    );
    assertThat(
      "Getting two existing calendars results in two calendars returned",
      result.getCalendars(),
      hasSize(2)
    );
    assertThat(
      "Getting an existing calendar results in the proper calendar returned",
      result.getCalendars().stream().map(calendarMapper::fromDto).collect(Collectors.toList()),
      containsInAnyOrder(
        Calendars.CALENDAR_COMBINED_EXAMPLE_A.withId(createdId1),
        Calendars.CALENDAR_COMBINED_EXAMPLE_C.withId(createdId2)
      )
    );

    getResponse = sendCalendarGetRequest(Arrays.asList(createdId1, createdId2, createdId2));
    getResponse.then().statusCode(is(HttpStatus.OK.value()));
    result = getResponse.getBody().as(CalendarCollectionDTO.class);
    assertThat("Duplicate IDs are ignored appropriately", result.getTotalRecords(), is(equalTo(2)));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testPartialMissing() {
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_A);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();

    Response response = sendCalendarGetRequest(Arrays.asList(createdId, UUIDs.UUID_0));
    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);
    assertThat(errorResponse.getStatus(), is(equalTo(HttpStatus.NOT_FOUND.value())));
    assertThat(errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);
    assertThat(error.getCode(), is(ErrorCodeDTO.CALENDAR_NOT_FOUND));
    assertThat(error.getMessage(), is("No calendar was found with the specified query"));

    List<String> missingUuids =
      ((LinkedHashMap<String, List<String>>) error.getData()).get("notFound");
    assertThat(missingUuids, hasSize(1));
    assertThat(missingUuids, containsInAnyOrder(UUIDs.UUID_0.toString()));
  }
}
