package org.folio.calendar.integration.api.calendar.periods.servicepointid.period.post;

import io.restassured.response.Response;
import java.util.UUID;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.integration.BaseApiAutoDatabaseTest;
import org.springframework.http.MediaType;

public abstract class CreateCalendarAbstractTest extends BaseApiAutoDatabaseTest {

  public static final String CREATE_CALENDAR_API_ROUTE = "/calendar/periods/%s/period";

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
}
