package org.folio.calendar.integration.api.calendar.periods.servicepointid.calculateopening.get;

import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.UUID;
import org.folio.calendar.integration.api.calendar.periods.AbstractExistingCalendarTest;
import org.junit.jupiter.api.Tag;

/**
 * Setup an environment for testing GET /calendar/periods/servicepointid/calculateopening.
 *
 * <ul>
 *   <li>On service point 0:</li>
 *   <li>
 *     <ul>
 *       <li>Calendar  2021-01-01 to 2021-04-30 ({@code PERIOD_FULL_EXAMPLE_F}, ID A)</li>
 *       <li>Calendar  2021-05-01 to 2021-09-22 ({@code PERIOD_FULL_EXAMPLE_G}, ID B)</li>
 *       <li>Exception 2021-03-16 to 2021-04-30 ({@code PERIOD_FULL_EXCEPTIONAL_F}, ID F, OPEN_ALL_DAY)</li>
 *       <li>Exception 2021-07-04 to 2021-09-22 ({@code PERIOD_FULL_EXCEPTIONAL_G}, ID 0, CLOSED)</li>
 *     </ul>
 *   </li>
 *   <li>On service point 1:</li>
 *   <li>
 *     <ul>
 *       <li>Calendar  2021-05-01 to 2021-09-22 ({@code PERIOD_FULL_EXAMPLE_D}, ID D)</li>
 *     </ul>
 *   </li>
 *   <li>On service point 5:</li>
 *   <li>
 *     <ul>
 *       <li>Exception 2021-01-01 to 2021-01-04 ({@code PERIOD_FULL_EXCEPTIONAL_C}, ID C)</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Tag("idempotent")
public abstract class CalculateOpeningAbstractTest extends AbstractExistingCalendarTest {

  public static final String CALCULATE_OPENING_API_ROUTE = "/calendar/periods/%s/calculateopening";

  protected Response sendRequest(UUID servicePointId, LocalDate date) {
    return ra()
      .queryParam("requestedDate", date.toString())
      .get(getRequestUrl(String.format(CALCULATE_OPENING_API_ROUTE, servicePointId)));
  }
}
