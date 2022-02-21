package org.folio.calendar.unit.domain.entity;

import static org.junit.Assert.assertThrows;

import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.NormalOpenings;
import org.junit.jupiter.api.Test;

class NormalOpeningMergeTest {

  @Test
  void testInabilityToMergeDifferentCalendarOpenings() {
    NormalOpening a = NormalOpenings.MONDAY_ALL_DAY.withCalendar(
      Calendars.CALENDAR_2021_01_01_TO_2021_01_01
    );
    NormalOpening b = NormalOpenings.TUESDAY_ALL_DAY.withCalendar(
      Calendars.CALENDAR_2021_01_01_TO_2021_01_04
    );
    assertThrows(
      "Openings from different calendars should not be mergeable",
      IllegalArgumentException.class,
      () -> NormalOpening.merge(a, b)
    );
  }
}
