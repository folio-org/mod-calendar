package org.folio.calendar.integration.api.calendar;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
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
public abstract class BaseCalendarApiTest extends BaseApiAutoDatabaseTest {

  public static final String COLLECTION_CALENDAR_API_ROUTE = "/calendar/calendars";
  public static final String SINGLE_CALENDAR_API_ROUTE = "/calendar/calendars/{calendarId}";
  public static final String GET_ALL_DATES_API_ROUTE =
    "/calendar/dates/{servicePointId}/all-openings";
  public static final String GET_SURROUNDING_DATES_API_ROUTE =
    "/calendar/dates/{servicePointId}/surrounding-openings";

  @Autowired
  private CalendarMapper calendarMapper;

  /**
   * POST /calendar/calendars - Send a Calendar creation request
   * @param calendar the calendar to create
   * @return the Response
   */
  public Response sendCalendarCreationRequest(Calendar calendar) {
    return sendCalendarCreationRequest(calendar, "");
  }

  /**
   * POST /calendar/calendars - Send a Calendar creation request
   * @param calendar the calendar to create
   * @param userId the user to create the calendar as
   * @return the Response
   */
  public Response sendCalendarCreationRequest(Calendar calendar, UUID userId) {
    return sendCalendarCreationRequest(calendar, userId.toString());
  }

  /**
   * POST /calendar/calendars - Send a Calendar creation request
   * @param calendar the calendar to create
   * @param userId the user to create the calendar as
   * @return the Response
   */
  public Response sendCalendarCreationRequest(Calendar calendar, String userId) {
    return ra(ValidationSchema.REGULAR)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .header("x-okapi-user-id", userId)
      .body(calendarMapper.toDto(calendar))
      .post(getRequestUrl(COLLECTION_CALENDAR_API_ROUTE));
  }

  /**
   * GET /calendar/calendars/{ids} - Send a Calendar get request
   * @param id the calendar to get
   * @return the Response
   */
  public Response sendCalendarGetRequest(UUID id) {
    return ra(ValidationSchema.REGULAR)
      .pathParam("calendarId", id)
      .get(getRequestUrl(SINGLE_CALENDAR_API_ROUTE));
  }

  /**
   * DELETE /calendar/calendars/{id} - Send a Calendar delete request
   * @param id the calendar to delete
   * @return the Response
   */
  public Response sendCalendarDeleteRequest(UUID id) {
    // spec must be ignored as the validator improperly flags comma-separated UUIDs
    // as being illegal
    return ra(ValidationSchema.REGULAR)
      .pathParam("calendarId", id)
      .delete(getRequestUrl(SINGLE_CALENDAR_API_ROUTE));
  }

  /**
   * DELETE /calendar/calendars?id=... - Send a multi-Calendar delete request
   * @param id the calendar to delete
   * @return the Response
   */
  public Response sendMultiCalendarDeleteRequest(List<UUID> ids) {
    return ra(ValidationSchema.REGULAR)
      .queryParam("id", ids)
      .delete(getRequestUrl(COLLECTION_CALENDAR_API_ROUTE));
  }

  /**
   * PUT /calendar/calendars/{id} - Send a Calendar update request
   * @param id the ID of the calendar to overwrite
   * @param calendar the calendar to save
   * @return the Response
   */
  public Response sendCalendarUpdateRequest(UUID id, Calendar calendar) {
    return sendCalendarUpdateRequest(id, calendar, "");
  }

  /**
   * PUT /calendar/calendars/{id} - Send a Calendar update request
   * @param id the ID of the calendar to overwrite
   * @param calendar the calendar to save
   * @param userId the user ID who is performing the request
   * @return the Response
   */
  public Response sendCalendarUpdateRequest(UUID id, Calendar calendar, UUID userId) {
    return sendCalendarUpdateRequest(id, calendar, userId.toString());
  }

  /**
   * PUT /calendar/calendars/{id} - Send a Calendar update request
   * @param id the ID of the calendar to overwrite
   * @param calendar the calendar to save
   * @param userId the user ID who is performing the request
   * @return the Response
   */
  public Response sendCalendarUpdateRequest(UUID id, Calendar calendar, String userId) {
    return ra(ValidationSchema.REGULAR)
      .pathParam("calendarId", id.toString())
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .header("x-okapi-user-id", userId)
      .body(calendarMapper.toDto(calendar))
      .put(getRequestUrl(SINGLE_CALENDAR_API_ROUTE));
  }

  /**
   * Parameters for {@link #sendCalendarSearchRequest sendCalendarSearchRequest}.
   * Builder usage is recommended to make parameters more descriptive
   */
  @Data
  @Builder
  public static class SearchRequestParameters {

    @Singular
    List<UUID> calendarIds;

    @Singular
    List<UUID> servicePointIds;

    LocalDate startDate;
    LocalDate endDate;
    Boolean includeClosedDays;
    Boolean actualOpening;
    Integer offset;
    Integer limit;
  }

  /**
   * GET /calendar/calendars : Get all calendars
   * Get all calendars that match the given query
   *
   * @param parameters The parameters, built into a {@link SearchRequestParameters SearchRequestParameters}
   * @return The query results (status code 200)
   *         or Invalid request or parameters (status code 400)
   *         or Internal server error (status code 500)
   */
  public Response sendCalendarSearchRequest(SearchRequestParameters parameters) {
    RequestSpecification request = ra(ValidationSchema.REGULAR);
    if (parameters.getCalendarIds() != null && !parameters.getCalendarIds().isEmpty()) {
      request = request.queryParam("id", parameters.getCalendarIds());
    }
    if (parameters.getServicePointIds() != null && !parameters.getServicePointIds().isEmpty()) {
      request = request.queryParam("servicePointId", parameters.getServicePointIds());
    }
    if (parameters.getStartDate() != null) {
      request = request.queryParam("startDate", parameters.getStartDate().toString());
    }
    if (parameters.getEndDate() != null) {
      request = request.queryParam("endDate", parameters.getEndDate().toString());
    }
    if (parameters.getIncludeClosedDays() != null) {
      request = request.queryParam("includeClosedDays", parameters.getIncludeClosedDays());
    }
    if (parameters.getActualOpening() != null) {
      request = request.queryParam("actualOpening", parameters.getActualOpening());
    }
    if (parameters.getOffset() != null) {
      request = request.queryParam("offset", parameters.getOffset());
    }
    if (parameters.getLimit() != null) {
      request = request.queryParam("limit", parameters.getLimit());
    }
    return request.get(getRequestUrl(COLLECTION_CALENDAR_API_ROUTE));
  }

  /**
   * GET /calendar/dates/{servicePointId}/all-openings : Daily opening information
   * Calculate the opening information for each date within a range
   *
   * @param servicePointId The service point to calculate openings on (required)
   * @param startDate The first date to include, inclusive (required)
   * @param endDate The last date to include, inclusive (required)
   * @param includeClosed Whether or not the results should include days where the service point is closed.  Exceptional closures will always be returned (required)
   * @param offset Skip a certain number of the first values; used for pagination (optional, default to 0)
   * @param limit The maximum number of elements returned in the response, used for pagination.  A limit of zero will not include any results (however, totalRecords will still be included) -- to include all results, use a large number such as 2147483647. (optional, default to 10)
   * @return The query results (status code 200)
   *         or Invalid request or parameters (status code 400)
   *         or Internal server error (status code 500)
   */
  public Response getAllOpenings(
    UUID servicePointId,
    LocalDate startDate,
    LocalDate endDate,
    Boolean includeClosed,
    Integer offset,
    Integer limit
  ) {
    RequestSpecification ra = ra(ValidationSchema.REGULAR)
      .pathParam("servicePointId", servicePointId.toString())
      .queryParam("startDate", startDate.toString())
      .queryParam("endDate", endDate.toString())
      .queryParam("includeClosed", includeClosed);

    if (offset != null) {
      ra = ra.queryParam("offset", offset);
    }
    if (limit != null) {
      ra = ra.queryParam("limit", limit);
    }

    return ra.get(getRequestUrl(GET_ALL_DATES_API_ROUTE));
  }

  /**
   * GET /calendar/dates/{servicePointId}/surrounding-openings/{date} : Surrounding openings
   * Calculate openings nearest to a given date for a specified service point
   *
   * @param servicePointId The service point to calculate openings on (required)
   * @param date The date to calculate openings around (required)
   * @return The query results (status code 200)
   *         or Invalid request or parameters (status code 400)
   *         or Internal server error (status code 500)
   */
  public Response getSurroundingOpenings(UUID servicePointId, LocalDate date) {
    return ra(ValidationSchema.REGULAR)
      .pathParam("servicePointId", servicePointId.toString())
      .queryParam("date", date.toString())
      .get(getRequestUrl(GET_SURROUNDING_DATES_API_ROUTE));
  }
}
