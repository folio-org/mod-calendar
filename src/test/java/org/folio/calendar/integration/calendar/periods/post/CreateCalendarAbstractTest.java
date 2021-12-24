package org.folio.calendar.integration.calendar.periods.post;

import io.restassured.response.Response;
import java.util.UUID;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public abstract class CreateCalendarAbstractTest extends BaseApiTest {

  public static final String CREATE_CALENDAR_API_ROUTE = "/calendar/periods/%s/period";

  @Autowired
  protected CalendarRepository calendarRepository;

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
