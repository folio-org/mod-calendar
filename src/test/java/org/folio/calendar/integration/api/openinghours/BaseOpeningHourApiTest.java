package org.folio.calendar.integration.api.openinghours;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
public abstract class BaseOpeningHourApiTest extends BaseApiAutoDatabaseTest {

  public static final String GET_SEARCH_CALENDAR_API_ROUTE = "/opening-hours/calendars";
  public static final String CREATE_CALENDAR_API_ROUTE = "/opening-hours/calendars";
  public static final String GET_CALENDAR_API_ROUTE = "/opening-hours/calendars/{calendarIds}";
  public static final String PUT_CALENDAR_API_ROUTE = "/opening-hours/calendars/{calendarId}";
  public static final String DELETE_CALENDAR_API_ROUTE = "/opening-hours/calendars/{calendarIds}";
  public static final String GET_ALL_DATES_API_ROUTE =
    "/opening-hours/dates/{servicePointId}/all-openings";

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
    return ra(ids.size() > 1 ? ValidationSchema.NONE : ValidationSchema.OPENING_HOURS)
      .pathParam("calendarIds", ids.stream().map(UUID::toString).collect(Collectors.joining(",")))
      .get(getRequestUrl(GET_CALENDAR_API_ROUTE));
  }

  /**
   * DELETE /opening-hours/calendars/{ids*} - Send a Calendar delete request
   * @param ids the calendars to delete
   * @return the Response
   */
  public Response sendCalendarDeleteRequest(List<UUID> ids) {
    // spec must be ignored as the validator improperly flags comma-separated UUIDs
    // as being illegal
    return ra(ids.size() > 1 ? ValidationSchema.NONE : ValidationSchema.OPENING_HOURS)
      .pathParam("calendarIds", ids.stream().map(UUID::toString).collect(Collectors.joining(",")))
      .delete(getRequestUrl(DELETE_CALENDAR_API_ROUTE));
  }

  /**
   * PUT /opening-hours/calendars/{id} - Send a Calendar update request
   * @param id the ID of the calendar to overwrite
   * @param calendar the calendar to save
   * @return the Response
   */
  public Response sendCalendarUpdateRequest(UUID id, Calendar calendar) {
    return ra(ValidationSchema.OPENING_HOURS)
      .pathParam("calendarId", id.toString())
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(calendarMapper.toDto(calendar))
      .put(getRequestUrl(PUT_CALENDAR_API_ROUTE));
  }

  /**
   * Parameters for {@link #sendCalendarSearchRequest sendCalendarSearchRequest}.
   * Builder usage is recommended to make parameters more descriptive
   */
  @Data
  @Builder
  public static class SearchRequestParameters {

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
   * GET /opening-hours/calendars : Get all calendars
   * Get all calendars that match the given query
   *
   * @param parameters The parameters, built into a {@link SearchRequestParameters SearchRequestParameters}
   * @return The query results (status code 200)
   *         or Invalid request or parameters (status code 400)
   *         or Internal server error (status code 500)
   */
  public Response sendCalendarSearchRequest(SearchRequestParameters parameters) {
    RequestSpecification request = ra(ValidationSchema.OPENING_HOURS);
    if (parameters.getServicePointIds() != null && !parameters.getServicePointIds().isEmpty()) {
      // disable validation for when there are multiple service points
      // as the validator does not support multiple UUIDs as an array
      if (parameters.getServicePointIds().size() > 1) {
        request = ra(ValidationSchema.NONE);
      }
      request =
        request.queryParam(
          "servicePointId",
          parameters
            .getServicePointIds()
            .stream()
            .map(UUID::toString)
            .collect(Collectors.joining(","))
        );
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
    return request.get(getRequestUrl(GET_SEARCH_CALENDAR_API_ROUTE));
  }

  /**
   * GET /opening-hours/dates/{servicePointId}/all-openings : Daily opening information
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
    RequestSpecification ra = ra(ValidationSchema.OPENING_HOURS)
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
}
