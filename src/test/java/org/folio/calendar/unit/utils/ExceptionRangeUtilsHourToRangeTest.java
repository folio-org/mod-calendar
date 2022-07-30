package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.ExceptionHours;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.ExceptionRangeUtils;
import org.folio.calendar.utils.TemporalRange;
import org.junit.jupiter.api.Test;

class ExceptionRangeUtilsHourToRangeTest {

  @Test
  void testNoOverlaps() {
    assertThat(
      ExceptionRangeUtils.toTemporalRanges(new ArrayList<>()).toList(),
      containsInAnyOrder()
    );
    assertThat(
      ExceptionRangeUtils
        .toTemporalRanges(Arrays.asList(ExceptionHours.OPEN_ALL_DAY_JAN_1_THRU_JAN_4))
        .toList(),
      containsInAnyOrder(
        new TemporalRange<>(
          ExceptionHours.OPEN_ALL_DAY_JAN_1_THRU_JAN_4,
          LocalDateTime.of(Dates.DATE_2021_01_01, Times.TIME_00_00),
          LocalDateTime.of(Dates.DATE_2021_01_04, Times.TIME_23_59)
        )
      )
    );
    assertThat(
      ExceptionRangeUtils
        .toTemporalRanges(
          Arrays.asList(
            ExceptionHours.OPEN_00_00_TO_14_59_JAN_1,
            ExceptionHours.OPEN_00_00_TO_14_59_JAN_2_THRU_JAN_3,
            ExceptionHours.OPEN_04_00_TO_14_59_JAN_4,
            ExceptionHours.OPEN_15_00_TO_23_00_JAN_4
          )
        )
        .toList(),
      containsInAnyOrder(
        new TemporalRange<>(
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_1,
          LocalDateTime.of(Dates.DATE_2021_01_01, Times.TIME_00_00),
          LocalDateTime.of(Dates.DATE_2021_01_01, Times.TIME_14_59)
        ),
        new TemporalRange<>(
          ExceptionHours.OPEN_00_00_TO_14_59_JAN_2_THRU_JAN_3,
          LocalDateTime.of(Dates.DATE_2021_01_02, Times.TIME_00_00),
          LocalDateTime.of(Dates.DATE_2021_01_03, Times.TIME_14_59)
        ),
        new TemporalRange<>(
          ExceptionHours.OPEN_04_00_TO_14_59_JAN_4,
          LocalDateTime.of(Dates.DATE_2021_01_04, Times.TIME_04_00),
          LocalDateTime.of(Dates.DATE_2021_01_04, Times.TIME_14_59)
        ),
        new TemporalRange<>(
          ExceptionHours.OPEN_15_00_TO_23_00_JAN_4,
          LocalDateTime.of(Dates.DATE_2021_01_04, Times.TIME_15_00),
          LocalDateTime.of(Dates.DATE_2021_01_04, Times.TIME_23_00)
        )
      )
    );
  }
}
