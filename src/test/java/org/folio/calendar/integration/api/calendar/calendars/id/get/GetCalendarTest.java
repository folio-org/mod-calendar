package org.folio.calendar.integration.api.calendar.calendars.id.get;

import static org.exparity.hamcrest.date.InstantMatchers.within;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.response.Response;
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

class GetCalendarTest extends BaseCalendarApiTest {

  @Autowired
  private CalendarMapper calendarMapper;

  @Test
  @SuppressWarnings("unchecked")
  void testMissingGet() {
    Response response = sendCalendarGetRequest(UUIDs.UUID_0);
    response.then().statusCode(is(HttpStatus.NOT_FOUND.value()));

    ErrorResponseDTO errorResponse = response.getBody().as(ErrorResponseDTO.class);
    assertThat(errorResponse.getStatus(), is(equalTo(HttpStatus.NOT_FOUND.value())));
    assertThat("A calendar not found is one error", errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);
    assertThat(error.getCode(), is(ErrorCodeDTO.CALENDAR_NOT_FOUND));
    assertThat(error.getMessage(), is("No calendar was found with the specified query"));

    List<String> missingUuids =
      ((LinkedHashMap<String, List<String>>) error.getData()).get("notFound");
    assertThat(missingUuids, hasSize(1));
    assertThat(missingUuids, containsInAnyOrder(UUIDs.UUID_0.toString()));
  }

  @Test
  void testSingleExistingGet() {
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_A);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));
    UUID createdId1 = calendarMapper.fromDto(postResponse.getBody().as(CalendarDTO.class)).getId();

    Response getResponse = sendCalendarGetRequest(createdId1);
    getResponse.then().statusCode(is(HttpStatus.OK.value()));
    CalendarDTO result = getResponse.getBody().as(CalendarDTO.class);

    Calendar resultingCalendar = calendarMapper.fromDto(result);

    assertThat(
      "Getting an existing calendar results in the proper calendar returned",
      resultingCalendar.withoutMetadata(),
      is(Calendars.CALENDAR_COMBINED_EXAMPLE_A.withId(createdId1))
    );

    assertThat(
      "Newly created calendars have valid metadata",
      resultingCalendar.getCreatedDate(),
      is(notNullValue())
    );
    assertThat(
      "Newly created calendars have valid metadata",
      resultingCalendar.getUpdatedDate(),
      is(notNullValue())
    );
    assertThat(
      "Newly created calendars have valid metadata",
      resultingCalendar.getCreatedDate(),
      is(within(1, ChronoUnit.SECONDS, resultingCalendar.getUpdatedDate()))
    );
  }
}
