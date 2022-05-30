package org.folio.calendar.integration.api.openinghours.calendars.get;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
    assertThat(errorResponse.getErrors(), hasSize(1));

    ErrorDTO error = errorResponse.getErrors().get(0);
    assertThat(error.getCode(), is(ErrorCodeDTO.CALENDAR_NOT_FOUND));
    assertThat(error.getMessage(), is("No calendar was found with the specified query"));
    assertThat(
      ((LinkedHashMap<String, List<String>>) error.getData()).get("notFound"),
      containsInAnyOrder(UUIDs.UUID_0.toString(), UUIDs.UUID_1.toString())
    );
  }
}
