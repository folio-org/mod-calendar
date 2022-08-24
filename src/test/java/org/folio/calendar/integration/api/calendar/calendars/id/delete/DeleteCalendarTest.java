package org.folio.calendar.integration.api.calendar.calendars.id.delete;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.dto.ErrorDTO;
import org.folio.calendar.domain.dto.ErrorResponseDTO;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.integration.api.calendar.BaseCalendarApiTest;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class DeleteCalendarTest extends BaseCalendarApiTest {

  @Autowired
  private CalendarMapper calendarMapper;

  @Test
  @SuppressWarnings("unchecked")
  void testMissingDelete() {
    Response response = sendCalendarDeleteRequest(UUIDs.UUID_0);
    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);
    assertThat(errorResponse.getStatus(), is(equalTo(HttpStatus.NOT_FOUND.value())));
    assertThat("A single calendar not found is one error", errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);
    assertThat(error.getCode(), is(ErrorCodeDTO.CALENDAR_NOT_FOUND));
    assertThat(error.getMessage(), is("No calendar was found with the specified query"));

    List<String> missingUuids =
      ((LinkedHashMap<String, List<String>>) error.getData()).get("notFound");
    assertThat(missingUuids, hasSize(1));
    assertThat(missingUuids, containsInAnyOrder(UUIDs.UUID_0.toString()));
  }

  @Test
  void testSingleExistingDelete() {
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_A);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId1 = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();

    sendCalendarDeleteRequest(createdId1).then().statusCode(is(HttpStatus.NO_CONTENT.value()));

    sendCalendarGetRequest(createdId1).then().statusCode(is(HttpStatus.NOT_FOUND.value()));
  }

  @Test
  void testPartialDelete() {
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_A);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId1 = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();

    postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_C);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId2 = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();

    sendCalendarDeleteRequest(createdId1).then().statusCode(is(HttpStatus.NO_CONTENT.value()));

    sendCalendarGetRequest(createdId1).then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    sendCalendarGetRequest(createdId2).then().statusCode(is(HttpStatus.OK.value()));
  }
}
