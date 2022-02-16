package org.folio.calendar.integration.calendar.periods.servicepointid.period.get;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import org.folio.calendar.domain.dto.PeriodCollection;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.folio.calendar.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GetCalendarNormalHoursTest extends GetCalendarAbstractTest {

  @Test
  void testPastWithOpeningDays() {
    DateUtils.setCurrentDateOverride(Dates.DATE_2021_07_04);
    Response response = sendCalendarGetRequest(UUIDs.UUID_0, true, true, false);
    response.then().statusCode(is(HttpStatus.OK.value()));
    PeriodCollection collection = response.getBody().as(PeriodCollection.class);
    assertThat(
      "The returned collection has both elements",
      collection.getOpeningPeriods(),
      hasSize(2)
    );
    assertThat(
      "The returned collection has both records",
      collection.getTotalRecords(),
      is(equalTo(2))
    );
    assertThat(
      "The returned collection contains both periods",
      collection.getOpeningPeriods(),
      is(Arrays.asList(Periods.PERIOD_FULL_EXAMPLE_F, Periods.PERIOD_FULL_EXAMPLE_G))
    );
  }

  @Test
  void testPastWithoutOpeningDays() {
    DateUtils.setCurrentDateOverride(Dates.DATE_2021_07_04);
    Response response = sendCalendarGetRequest(UUIDs.UUID_0, false, true, false);
    response.then().statusCode(is(HttpStatus.OK.value()));
    PeriodCollection collection = response.getBody().as(PeriodCollection.class);
    assertThat(
      "The returned collection has both elements",
      collection.getOpeningPeriods(),
      hasSize(2)
    );
    assertThat(
      "The returned collection has both records",
      collection.getTotalRecords(),
      is(equalTo(2))
    );
    assertThat(
      "The returned collection contains both periods without opening days",
      collection.getOpeningPeriods(),
      is(
        Arrays.asList(
          Periods.PERIOD_FULL_EXAMPLE_F.withOpeningDays(new ArrayList<>()),
          Periods.PERIOD_FULL_EXAMPLE_G.withOpeningDays(new ArrayList<>())
        )
      )
    );
  }

  @Test
  void testPresentWithOpeningDays() {
    DateUtils.setCurrentDateOverride(Dates.DATE_2021_07_04);
    Response response = sendCalendarGetRequest(UUIDs.UUID_0, true, false, false);
    response.then().statusCode(is(HttpStatus.OK.value()));
    PeriodCollection collection = response.getBody().as(PeriodCollection.class);
    assertThat(
      "The returned collection has the one current element",
      collection.getOpeningPeriods(),
      hasSize(1)
    );
    assertThat(
      "The returned collection has the one current record",
      collection.getTotalRecords(),
      is(equalTo(1))
    );
    assertThat(
      "The returned collection contains the single current periods",
      collection.getOpeningPeriods(),
      is(Arrays.asList(Periods.PERIOD_FULL_EXAMPLE_G))
    );
  }

  @Test
  void testPresentWithoutOpeningDays() {
    DateUtils.setCurrentDateOverride(Dates.DATE_2021_07_04);
    Response response = sendCalendarGetRequest(UUIDs.UUID_0, false, false, false);
    response.then().statusCode(is(HttpStatus.OK.value()));
    PeriodCollection collection = response.getBody().as(PeriodCollection.class);
    assertThat(
      "The returned collection has the current element",
      collection.getOpeningPeriods(),
      hasSize(1)
    );
    assertThat(
      "The returned collection has the current record",
      collection.getTotalRecords(),
      is(equalTo(1))
    );
    assertThat(
      "The returned collection contains the current period without opening days",
      collection.getOpeningPeriods(),
      is(Arrays.asList(Periods.PERIOD_FULL_EXAMPLE_G.withOpeningDays(new ArrayList<>())))
    );
  }
}
