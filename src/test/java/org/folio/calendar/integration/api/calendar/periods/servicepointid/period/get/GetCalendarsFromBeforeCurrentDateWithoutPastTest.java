package org.folio.calendar.integration.api.calendar.periods.servicepointid.period.get;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.PeriodCollection;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.UUIDs;
import org.folio.calendar.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * Attempt getting periods from before the current date without the past included
 */
class GetCalendarsFromBeforeCurrentDateWithoutPastTest extends GetCalendarAbstractTest {

  @Test
  void testNormalHours() {
    DateUtils.setCurrentDateOverride(Dates.DATE_2021_12_31);
    Response response = sendCalendarGetRequest(UUIDs.UUID_0, false, false, false);
    response.then().statusCode(is(HttpStatus.OK.value()));
    PeriodCollection collection = response.getBody().as(PeriodCollection.class);
    assertThat(
      "The returned collection has no elements",
      collection.getOpeningPeriods(),
      hasSize(0)
    );
    assertThat(
      "The returned collection has no records",
      collection.getTotalRecords(),
      is(equalTo(0))
    );
    assertThat(
      "The returned collection contains no periods",
      collection.getOpeningPeriods(),
      is(empty())
    );
  }

  @Test
  void testExceptions() {
    DateUtils.setCurrentDateOverride(Dates.DATE_2021_12_31);
    Response response = sendCalendarGetRequest(UUIDs.UUID_0, false, false, true);
    response.then().statusCode(is(HttpStatus.OK.value()));
    PeriodCollection collection = response.getBody().as(PeriodCollection.class);
    assertThat(
      "The returned collection has no elements",
      collection.getOpeningPeriods(),
      hasSize(0)
    );
    assertThat(
      "The returned collection has no records",
      collection.getTotalRecords(),
      is(equalTo(0))
    );
    assertThat(
      "The returned collection contains no periods",
      collection.getOpeningPeriods(),
      is(empty())
    );
  }
}
