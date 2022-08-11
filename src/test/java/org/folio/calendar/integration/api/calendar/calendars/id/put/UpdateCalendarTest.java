package org.folio.calendar.integration.api.calendar.calendars.id.put;

import static org.exparity.hamcrest.date.InstantMatchers.within;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.dto.ErrorDTO;
import org.folio.calendar.domain.dto.ErrorResponseDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.integration.api.calendar.BaseCalendarApiTest;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class UpdateCalendarTest extends BaseCalendarApiTest {

  @Autowired
  private CalendarMapper calendarMapper;

  @Test
  @SuppressWarnings("unchecked")
  void testMissingUpdate() {
    Response response = sendCalendarUpdateRequest(
      UUIDs.UUID_0,
      Calendars.CALENDAR_COMBINED_EXAMPLE_A
    );
    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);
    assertThat(errorResponse.getStatus(), is(equalTo(HttpStatus.NOT_FOUND.value())));
    assertThat("Only one error was relevant", errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);
    assertThat(error.getCode(), is(ErrorCodeDTO.CALENDAR_NOT_FOUND));
    assertThat(error.getMessage(), is("No calendar was found with the specified query"));

    List<String> missingUuids =
      ((LinkedHashMap<String, List<String>>) error.getData()).get("notFound");
    assertThat(missingUuids, hasSize(1));
    assertThat(missingUuids, containsInAnyOrder(UUIDs.UUID_0.toString()));
  }

  @Test
  void testExistingUpdate() {
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_A);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();

    Response putResponse = sendCalendarUpdateRequest(
      createdId,
      Calendars.CALENDAR_COMBINED_EXAMPLE_B
    );
    putResponse.then().statusCode(is(HttpStatus.OK.value()));
    Calendar result = calendarMapper.fromDto(putResponse.getBody().as(CalendarDTO.class));

    assertThat(
      "The newly updated calendar is as expected",
      result.withoutMetadata(),
      is(equalTo(Calendars.CALENDAR_COMBINED_EXAMPLE_B.withId(createdId)))
    );
  }

  @Test
  void testIdAndMetadataUpdate() {
    Response postResponse = sendCalendarCreationRequest(
      Calendars.CALENDAR_COMBINED_EXAMPLE_A,
      UUIDs.UUID_A
    );
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();
    Instant createdTimestamp = calendarMapper
      .fromDto(postResponse.getBody().as(CalendarDTO.class))
      .getCreatedDate();

    Response putResponse = sendCalendarUpdateRequest(
      createdId,
      Calendars.CALENDAR_COMBINED_EXAMPLE_B
        .withId(UUIDs.UUID_0)
        .withCreatedByUserId(UUIDs.UUID_B)
        .withCreatedDate(Instant.parse("2000-01-01T00:00:00.00Z")),
      UUIDs.UUID_C
    );
    putResponse.then().statusCode(is(HttpStatus.OK.value()));
    Calendar result = calendarMapper.fromDto(putResponse.getBody().as(CalendarDTO.class));

    assertThat(
      "The newly updated calendar is as expected with its ID ignored",
      result.withoutMetadata(),
      is(equalTo(Calendars.CALENDAR_COMBINED_EXAMPLE_B.withId(createdId)))
    );
    assertThat(
      "The newly updated calendar had a false creation timestamp ignored",
      result.getCreatedDate(),
      is(within(1, ChronoUnit.SECONDS, createdTimestamp))
    );
    assertThat(
      "The newly updated calendar had a false creation user ID ignored",
      result.getCreatedByUserId(),
      is(UUIDs.UUID_A)
    );
    assertThat(
      "The newly updated calendar has a proper updated timestamp",
      result.getUpdatedDate(),
      is(greaterThan(result.getCreatedDate()))
    );
    assertThat(
      "The newly updated calendar has a proper updated user ID",
      result.getUpdatedByUserId(),
      is(UUIDs.UUID_C)
    );
  }

  @Test
  void testBadUpdateSchema() {
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_A);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();

    Response putResponse = sendCalendarUpdateRequest(
      createdId,
      Calendars.CALENDAR_COMBINED_EXAMPLE_B.withName("")
    );
    putResponse.then().statusCode(is(HttpStatus.BAD_REQUEST.value()));
    ErrorResponseDTO errorResponse = putResponse.getBody().as(ErrorResponseDTO.class);

    assertThat(errorResponse.getErrors(), hasSize(1));
    assertThat(errorResponse.getErrors().get(0).getCode(), is(ErrorCodeDTO.CALENDAR_NO_NAME));
  }
}
