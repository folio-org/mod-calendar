package org.folio.calendar.unit.domain.request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.domain.request.Parameters;
import org.junit.jupiter.api.Test;

class ParametersTest {

  @Test
  void testEnumAssociations() {
    assertThat(Parameters.CALENDAR.toString(), is("calendar"));
    assertThat(Parameters.NAME.toString(), is("name"));
    assertThat(Parameters.START_DATE.toString(), is("startDate"));
    assertThat(Parameters.END_DATE.toString(), is("endDate"));
    assertThat(Parameters.ASSIGNMENTS.toString(), is("assignments"));
    assertThat(Parameters.NORMAL_HOURS.toString(), is("normalHours"));
  }
}
