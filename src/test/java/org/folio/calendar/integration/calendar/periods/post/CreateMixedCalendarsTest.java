package org.folio.calendar.integration.calendar.periods.post;

import static org.hamcrest.Matchers.is;

import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CreateMixedCalendarsTest extends CreateCalendarAbstractTest {

  @Test
  void testNormalCalendarAndOverlappingExceptionalCreation() {
    sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_A,
      Periods.PERIOD_FULL_EXAMPLE_A.getServicePointId()
    )
      .then()
      .statusCode(is(HttpStatus.CREATED.value()));

    sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_A.withId(UUIDs.UUID_B),
      Periods.PERIOD_FULL_EXCEPTIONAL_A.withId(UUIDs.UUID_B).getServicePointId()
    )
      .then()
      .statusCode(is(HttpStatus.CREATED.value()));
  }

  @Test
  void testOverlappingExceptionalAndNormalCalendarCreation() {
    sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXCEPTIONAL_A,
      Periods.PERIOD_FULL_EXCEPTIONAL_A.getServicePointId()
    )
      .then()
      .statusCode(is(HttpStatus.CREATED.value()));

    sendCalendarCreationRequest(
      Periods.PERIOD_FULL_EXAMPLE_A.withId(UUIDs.UUID_B),
      Periods.PERIOD_FULL_EXAMPLE_A.withId(UUIDs.UUID_B).getServicePointId()
    )
      .then()
      .statusCode(is(HttpStatus.CREATED.value()));
  }
}
