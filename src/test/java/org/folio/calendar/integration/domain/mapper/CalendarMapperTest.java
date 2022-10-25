package org.folio.calendar.integration.domain.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.testconstants.Calendars;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CalendarMapperTest extends BaseApiTest {

  @Autowired
  CalendarMapper mapper;

  @Test
  void testMapper() {
    assertThat(
      mapper.fromDto(mapper.toDto(Calendars.CALENDAR_COMBINED_EXAMPLE_A)),
      is(equalTo(Calendars.CALENDAR_COMBINED_EXAMPLE_A))
    );
    assertThat(
      mapper.fromDto(mapper.toDto(Calendars.CALENDAR_COMBINED_EXAMPLE_A).withMetadata(null)),
      is(equalTo(Calendars.CALENDAR_COMBINED_EXAMPLE_A))
    );
  }

  @Test
  void testNull() {
    assertThat(mapper.fromDto(mapper.toDto(null)), is(nullValue()));
  }

  @Test
  void testEmpty() {
    Calendar empty = Calendar.builder().build();
    assertThat(mapper.fromDto(mapper.toDto(empty)), is(equalTo(empty)));
  }
}
