package org.folio.calendar.unit.domain.entity;

import static org.junit.Assert.assertThrows;

import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.testconstants.NormalOpenings;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;

public class NormalOpeningMergeTest {

  @Test
  void testInabilityToMergeDifferentCalendarOpenings() {
    NormalOpening a = NormalOpenings.MONDAY_ALL_DAY.withCalendarId(UUIDs.UUID_0);
    NormalOpening b = NormalOpenings.TUESDAY_ALL_DAY.withCalendarId(UUIDs.UUID_A);
    assertThrows(
      "Openings from different calendars should not be mergeable",
      IllegalArgumentException.class,
      () -> NormalOpening.merge(a, b)
    );
  }
}
