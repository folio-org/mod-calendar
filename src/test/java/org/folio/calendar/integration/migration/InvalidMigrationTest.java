package org.folio.calendar.integration.migration;

import static org.folio.calendar.integration.calendar.periods.servicepointid.period.periodid.get.GetSpecificCalendarAbstractTest.GET_CALENDAR_API_ROUTE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class InvalidMigrationTest extends AbstractMigrationTest {

  @BeforeAll
  void migrate() {
    loadMigrationSql("database-migrate-invalid.sql");
    runMigration();
  }

  // tests the valid calendar in the bad data
  @Test
  void testValidCalendar() {
    Response response = ra()
      .get(getRequestUrl(String.format(GET_CALENDAR_API_ROUTE, UUIDs.UUID_5, UUIDs.UUID_C)));
    response.then().statusCode(is(HttpStatus.OK.value()));

    Period period = response.getBody().as(Period.class);
    assertThat(
      "The returned period is the expected period",
      period,
      is(Periods.PERIOD_FULL_EXCEPTIONAL_C)
    );
  }

  // tests the totally invalid calendars in the bad data
  @Test
  void testInvalidCalendars() {
    ra()
      .get(getRequestUrl(String.format(GET_CALENDAR_API_ROUTE, UUIDs.UUID_0, UUIDs.UUID_0)))
      .then()
      .statusCode(is(HttpStatus.NOT_FOUND.value()));

    ra()
      .get(getRequestUrl(String.format(GET_CALENDAR_API_ROUTE, UUIDs.UUID_0, UUIDs.UUID_F)))
      .then()
      .statusCode(is(HttpStatus.NOT_FOUND.value()));
  }

  // tests the totally improper normal hours calendar in the bad data
  @Test
  void testCalendarWithInvalidHours() {
    Response response = ra()
      .get(getRequestUrl(String.format(GET_CALENDAR_API_ROUTE, UUIDs.UUID_0, UUIDs.UUID_B)));
    response.then().statusCode(is(HttpStatus.OK.value()));

    Period period = response.getBody().as(Period.class);
    assertThat(
      "The returned period is the expected period with invalid hours omitted",
      period.getOpeningDays(),
      hasSize(0)
    );
  }
}
