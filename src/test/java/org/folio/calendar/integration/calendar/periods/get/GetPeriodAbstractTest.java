package org.folio.calendar.integration.calendar.periods.get;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.util.UUID;
import org.folio.calendar.integration.calendar.periods.AbstractExistingCalendarTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Setup an environment for testing GET /calendar/periods.
 *
 * <ul>
 *   <li>On service point 0:</li>
 *   <li>
 *     <ul>
 *       <li>Calendar  2021-01-01 to 2021-04-30 ({@code PERIOD_FULL_EXAMPLE_F}, ID A)</li>
 *       <li>Calendar  2021-05-01 to 2021-09-22 ({@code PERIOD_FULL_EXAMPLE_G}, ID B)</li>
 *       <li>Exception 2021-03-16 to 2021-04-30 ({@code PERIOD_FULL_EXCEPTIONAL_F}, ID F)</li>
 *       <li>Exception 2021-07-04 to 2021-09-22 ({@code PERIOD_FULL_EXCEPTIONAL_G}, ID 0)</li>
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
@ActiveProfiles("test")
public abstract class GetPeriodAbstractTest extends AbstractExistingCalendarTest {

  public static final String GET_PERIOD_API_ROUTE = "/calendar/periods";

  protected Response sendRequest(
    UUID servicePointId,
    LocalDate startDate,
    LocalDate endDate,
    Boolean includeClosedDays,
    Boolean actualOpening,
    Integer offset,
    Integer limit
  ) {
    RequestSpecification ra = ra();
    if (servicePointId != null) {
      ra = ra.queryParam("servicePointId", servicePointId);
    }
    if (startDate != null) {
      ra = ra.queryParam("startDate", startDate.toString());
    }
    if (endDate != null) {
      ra = ra.queryParam("endDate", endDate.toString());
    }
    if (includeClosedDays != null) {
      ra = ra.queryParam("includeClosedDays", includeClosedDays);
    }
    if (actualOpening != null) {
      ra = ra.queryParam("actualOpening", actualOpening);
    }
    if (offset != null) {
      ra = ra.queryParam("offset", offset);
    }
    if (limit != null) {
      ra = ra.queryParam("limit", limit);
    }
    return ra.get(getRequestUrl(String.format(GET_PERIOD_API_ROUTE, servicePointId)));
  }
}
