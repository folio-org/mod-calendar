package org.folio.calendar.integration.calendar.periods.get;

import io.restassured.response.Response;
import java.util.UUID;
import org.folio.calendar.integration.calendar.periods.AbstractExistingCalendarTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Setup an environment for testing GET /calendar/periods/{servicePointId}/period.
 */
@ActiveProfiles("test")
public abstract class GetCalendarAbstractTest extends AbstractExistingCalendarTest {

  public static final String GET_CALENDAR_API_ROUTE = "/calendar/periods/%s/period";

  protected Response sendCalendarGetRequest(
    UUID servicePointId,
    boolean withOpeningDays,
    boolean showPast,
    boolean showExceptional
  ) {
    return ra()
      .queryParam("withOpeningDays", withOpeningDays)
      .queryParam("showPast", showPast)
      .queryParam("showExceptional", showExceptional)
      .get(getRequestUrl(String.format(GET_CALENDAR_API_ROUTE, servicePointId)));
  }
}
