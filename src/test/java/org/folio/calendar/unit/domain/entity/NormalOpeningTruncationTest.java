package org.folio.calendar.unit.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.testconstants.Times;
import org.junit.jupiter.api.Test;

class NormalOpeningTruncationTest {

  @Test
  void testSetSecondPrecision() {
    NormalOpening changed = NormalOpening
      .builder()
      .startTime(Times.TIME_18_12_20)
      .endTime(Times.TIME_18_12_20)
      .build();

    assertThat(
      "start time does not consider seconds",
      changed.getStartTime(),
      is(equalTo(Times.TIME_18_12))
    );
    assertThat(
      "end time does not consider seconds",
      changed.getEndTime(),
      is(equalTo(Times.TIME_18_12))
    );

    changed.setStartTime(Times.TIME_18_12_20);
    changed.setEndTime(Times.TIME_18_12_20);

    assertThat(
      "start time does not consider seconds",
      changed.getStartTime(),
      is(equalTo(Times.TIME_18_12))
    );
    assertThat(
      "end time does not consider seconds",
      changed.getEndTime(),
      is(equalTo(Times.TIME_18_12))
    );
  }
}
