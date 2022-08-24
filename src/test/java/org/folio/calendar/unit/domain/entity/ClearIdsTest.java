package org.folio.calendar.unit.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Set;
import java.util.stream.Collectors;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.testconstants.Calendars;
import org.junit.jupiter.api.Test;

class ClearIdsTest {

  @Test
  void testClearIdsNull() {
    Calendar cal = Calendar
      .builder()
      .id(null)
      .build()
      .withServicePoints(null)
      .withNormalHours(null)
      .withExceptions(null);
    cal.clearIds();
    assertThat(cal.getId(), is(nullValue()));
  }

  @Test
  void testClearIdsNullRange() {
    Calendar cal = Calendar
      .builder()
      .exception(ExceptionRange.builder().build().withOpenings(null))
      .build();
    cal.clearIds();
    assertThat(cal.getId(), is(nullValue()));
  }

  @Test
  void testClearIdsFull() {
    Calendar cal = Calendars.CALENDAR_COMBINED_EXAMPLE_A;
    cal.clearIds();
    assertThat(cal.getId(), is(nullValue()));
    assertThat(
      cal.getNormalHours().stream().map(NormalOpening::getId).collect(Collectors.toList()),
      everyItem(is(nullValue()))
    );
    assertThat(
      cal.getExceptions().stream().map(ExceptionRange::getId).collect(Collectors.toList()),
      everyItem(is(nullValue()))
    );
    assertThat(
      cal
        .getExceptions()
        .stream()
        .map(ExceptionRange::getOpenings)
        .flatMap(Set::stream)
        .map(ExceptionHour::getId)
        .collect(Collectors.toList()),
      everyItem(is(nullValue()))
    );
  }
}
