package org.folio.calendar.unit.utils;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.utils.TemporalRange;
import org.folio.calendar.utils.TemporalUtils;
import org.folio.calendar.utils.TimeConstants;
import org.junit.jupiter.api.Test;

class TemporalUtilsDateTimeSliceTest {

  TemporalRange<LocalDateTime, Object> RANGE_2021_01_01_TO_2021_01_01 = new TemporalRange<>(
    LocalDateTime.of(Dates.DATE_2021_01_01, Times.TIME_04_00),
    LocalDateTime.of(Dates.DATE_2021_01_01, Times.TIME_23_00)
  );
  TemporalRange<LocalDateTime, Object> RANGE_2021_01_01_TO_2021_01_03 = new TemporalRange<>(
    LocalDateTime.of(Dates.DATE_2021_01_01, Times.TIME_04_00),
    LocalDateTime.of(Dates.DATE_2021_01_03, Times.TIME_23_00)
  );

  @Test
  void testSingleDayRange() {
    assertThat(
      TemporalUtils.getLocalTimeSliceOfDateTimeRange(
        RANGE_2021_01_01_TO_2021_01_01,
        Dates.DATE_2021_01_01
      ),
      isPresentAnd(is(new TemporalRange<LocalTime, Object>(Times.TIME_04_00, Times.TIME_23_00)))
    );
    assertThat(
      TemporalUtils.getLocalTimeSliceOfDateTimeRange(
        RANGE_2021_01_01_TO_2021_01_01,
        Dates.DATE_2021_01_02
      ),
      isEmpty()
    );
  }

  @Test
  void testEdges() {
    assertThat(
      TemporalUtils.getLocalTimeSliceOfDateTimeRange(
        RANGE_2021_01_01_TO_2021_01_03,
        Dates.DATE_2021_01_01
      ),
      isPresentAnd(
        is(new TemporalRange<LocalTime, Object>(Times.TIME_04_00, TimeConstants.TIME_MAX))
      )
    );
    assertThat(
      TemporalUtils.getLocalTimeSliceOfDateTimeRange(
        RANGE_2021_01_01_TO_2021_01_03,
        Dates.DATE_2021_01_02
      ),
      isPresentAnd(
        is(new TemporalRange<LocalTime, Object>(TimeConstants.TIME_MIN, TimeConstants.TIME_MAX))
      )
    );
    assertThat(
      TemporalUtils.getLocalTimeSliceOfDateTimeRange(
        RANGE_2021_01_01_TO_2021_01_03,
        Dates.DATE_2021_01_03
      ),
      isPresentAnd(
        is(new TemporalRange<LocalTime, Object>(TimeConstants.TIME_MIN, Times.TIME_23_00))
      )
    );
    assertThat(
      TemporalUtils.getLocalTimeSliceOfDateTimeRange(
        RANGE_2021_01_01_TO_2021_01_03,
        Dates.DATE_2021_01_04
      ),
      isEmpty()
    );
  }
}
