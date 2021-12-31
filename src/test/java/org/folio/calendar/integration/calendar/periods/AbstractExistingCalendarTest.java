package org.folio.calendar.integration.calendar.periods;

import static org.hamcrest.Matchers.is;

import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.integration.BaseApiAutoDatabaseTest;
import org.folio.calendar.integration.calendar.periods.servicepointid.period.post.CreateCalendarAbstractTest;
import org.folio.calendar.testconstants.Periods;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Setup the following service points for use in tests which may rely on them (such as GET integration tests).
 * Tags {@code "idempotent"} are supported <b>on classes only</b> to only create the calendars/delete them as needed.
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
@Log4j2
public abstract class AbstractExistingCalendarTest extends BaseApiAutoDatabaseTest {

  /**
   * Create all the calendars for these tests.
   */
  void createAllCalendars() {
    log.info("Creating calendars to test against");
    createCalendar(Periods.PERIOD_FULL_EXAMPLE_D);
    createCalendar(Periods.PERIOD_FULL_EXAMPLE_F);
    createCalendar(Periods.PERIOD_FULL_EXAMPLE_G);
    createCalendar(Periods.PERIOD_FULL_EXCEPTIONAL_C);
    createCalendar(Periods.PERIOD_FULL_EXCEPTIONAL_F);
    createCalendar(Periods.PERIOD_FULL_EXCEPTIONAL_G);
  }

  @BeforeAll
  void beforeAll(TestInfo testInfo) {
    if (testInfo.getTags().contains("idempotent")) {
      createAllCalendars();
    }
  }

  @BeforeEach
  void beforeEach(TestInfo testInfo) {
    if (!testInfo.getTags().contains("idempotent")) {
      createAllCalendars();
    }
  }

  /**
   * Send a Calendar creation request and assert its successful completion
   * @param calendar calendar to create, as a legacy Period
   */
  protected void createCalendar(Period calendar) {
    ra()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(calendar)
      .post(
        getRequestUrl(
          String.format(
            CreateCalendarAbstractTest.CREATE_CALENDAR_API_ROUTE,
            calendar.getServicePointId()
          )
        )
      )
      .then()
      .statusCode(is(HttpStatus.CREATED.value()));
  }
}
