package org.folio.calendar.integration.api.openinghours;

import io.restassured.response.Response;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.integration.BaseApiAutoDatabaseTest;
import org.folio.calendar.integration.ValidationSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/**
 * An abstract class for any tests on the opening hours API.  This provides
 * full database support/teardown after each method (with {@code idempotent}
 * tag support, too) as well as a set of helper methods to generate requests.
 */
public abstract class BaseOpeningHourApiTest extends BaseApiAutoDatabaseTest {

  public static final String CREATE_CALENDAR_API_ROUTE = "/opening-hours/calendars";
  public static final String GET_CALENDAR_API_ROUTE = "/opening-hours/calendars/%s";
  public static final String DELETE_CALENDAR_API_ROUTE = "/opening-hours/calendars/%s";

  @Autowired
  private CalendarMapper calendarMapper;

  /**
   * POST /opening-hours/calendars - Send a Calendar creation request
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
   * GET /opening-hours/calendars/{ids*} - Send a Calendar get request
   * @param ids the calendars to get
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

  /**
   * DELETE /opening-hours/calendars/{ids*} - Send a Calendar delete request
   * @param ids the calendars to delete
   * @return the Response
   */
  public Response sendCalendarDeleteRequest(List<UUID> ids) {
    // spec must be ignored as the validator improperly flags comma-separated UUIDs
    // as being illegal
    return ra(ValidationSchema.NONE)
      .delete(
        getRequestUrl(
          String.format(
            DELETE_CALENDAR_API_ROUTE,
            ids.stream().map(UUID::toString).collect(Collectors.joining(","))
          )
        )
      );
  }
}
