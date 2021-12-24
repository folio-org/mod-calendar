package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.domain.entity.ExceptionHour;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.ExceptionRanges;
import org.folio.calendar.testconstants.OpeningDayRelativeConstants;
import org.folio.calendar.testconstants.UUIDs;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

public class PeriodUtilsExceptionalTest {

  @Test
  void testRegularClosureIsNotExceptional() {
    assertThat(
      "A non-exceptional opening (for closure) is not exceptional",
      PeriodUtils.areOpeningsExceptional(Arrays.asList(OpeningDayRelativeConstants.MONDAY_CLOSED)),
      is(false)
    );
  }

  @Test
  void testRegularOpeningIsNotExceptional() {
    assertThat(
      "A non-exceptional opening (for opening hours) is not exceptional",
      PeriodUtils.areOpeningsExceptional(
        Arrays.asList(OpeningDayRelativeConstants.MONDAY_OPEN_04_00_TO_14_59)
      ),
      is(false)
    );
  }

  @Test
  void testExceptionalClosureIsExceptional() {
    assertThat(
      "An exceptional opening (for closure) is exceptional",
      PeriodUtils.areOpeningsExceptional(
        Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_CLOSED)
      ),
      is(true)
    );
  }

  @Test
  void testExceptionalOpeningIsExceptional() {
    assertThat(
      "An exceptional opening (for different opening hours) is exceptional",
      PeriodUtils.areOpeningsExceptional(
        Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_04_00_TO_14_59)
      ),
      is(true)
    );
  }

  @Test
  void testConversionOfNoExceptionsToExceptionRanges() {
    List<OpeningDayRelative> list = new ArrayList<>();
    assertThrows(
      "A list of no openings cannot be converted to exception range(s)",
      IllegalArgumentException.class,
      () ->
        PeriodUtils.convertOpeningDayRelativeToExceptionRanges(
          Dates.DATE_2021_01_01,
          Dates.DATE_2021_12_31,
          list,
          UUIDs.UUID_0
        )
    );
  }

  @Test
  void testConversionOfMultipleExceptionsToExceptionRanges() {
    List<OpeningDayRelative> list = Arrays.asList(
      OpeningDayRelativeConstants.EXCEPTIONAL_CLOSED,
      OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_04_00_TO_14_59
    );
    assertThrows(
      "A list of multiple openings cannot be converted to exception range(s)",
      IllegalArgumentException.class,
      () ->
        PeriodUtils.convertOpeningDayRelativeToExceptionRanges(
          Dates.DATE_2021_01_01,
          Dates.DATE_2021_12_31,
          list,
          UUIDs.UUID_0
        )
    );
  }

  @Test
  void testConversionOfMultipleHourPairsToExceptionRanges() {
    List<OpeningDayRelative> list = Arrays.asList(
      OpeningDayRelativeConstants.EXCEPTIONAL_INVALID_MULTIPLE_OPENINGS
    );
    assertThrows(
      "An opening with multiple hour ranges cannot be converted to exception range(s)",
      IllegalArgumentException.class,
      () ->
        PeriodUtils.convertOpeningDayRelativeToExceptionRanges(
          Dates.DATE_2021_01_01,
          Dates.DATE_2021_12_31,
          list,
          UUIDs.UUID_0
        )
    );
  }

  @Test
  void testConversionOfClosureToExceptionRanges() {
    List<ExceptionRange> result = PeriodUtils.convertOpeningDayRelativeToExceptionRanges(
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_12_31,
      Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_CLOSED),
      UUIDs.UUID_0
    );
    assertThat(
      "An exceptional closure produces exactly one equivalent ExceptionRange",
      result,
      hasSize(1)
    );

    // override default ID
    result.get(0).setId(null);
    assertThat(
      "An exceptional closure produces exactly one equivalent ExceptionRange",
      result.get(0).getOpenings(),
      hasSize(1)
    );
    for (ExceptionHour hours : result.get(0).getOpenings()) {
      hours.setExceptionId(null);
    }

    assertThat(
      "An exceptional closure can be equivalently represented as an ExceptionRange",
      result,
      hasItem(
        ExceptionRanges.withExceptionId(
          ExceptionRanges.CLOSED_ALL_YEAR_CALENDAR_0,
          result.get(0).getId()
        )
      )
    );
  }

  @Test
  void testConversionOfAllDayOpeningToExceptionRanges() {
    List<ExceptionRange> result = PeriodUtils.convertOpeningDayRelativeToExceptionRanges(
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_04,
      Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_ALL_DAY),
      UUIDs.UUID_A
    );
    assertThat(
      "An exceptional all-day opening produces exactly one equivalent ExceptionRange",
      result,
      hasSize(1)
    );

    // override default ID
    result.get(0).setId(null);
    assertThat(
      "An exceptional all-day opening produces exactly one equivalent ExceptionRange",
      result.get(0).getOpenings(),
      hasSize(1)
    );
    for (ExceptionHour hours : result.get(0).getOpenings()) {
      hours.setExceptionId(null);
    }

    assertThat(
      "An exceptional all-day opening can be equivalently represented as an ExceptionRange",
      result,
      hasItem(
        ExceptionRanges.withExceptionId(
          ExceptionRanges.OPEN_ALL_DAY_JAN_1_THRU_JAN_4_CALENDAR_A,
          result.get(0).getId()
        )
      )
    );
  }

  @Test
  void testConversionOfPartialDayOpeningToExceptionRanges() {
    List<ExceptionRange> result = PeriodUtils.convertOpeningDayRelativeToExceptionRanges(
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_04,
      Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_04_00_TO_14_59),
      UUIDs.UUID_B
    );

    assertThat(
      "An exceptional partial-day opening produces exactly one equivalent ExceptionRange",
      result,
      hasSize(1)
    );
    assertThat(
      "An exceptional partial-day opening across four days produces exactly four equivalent ExceptionRanges",
      result.get(0).getOpenings(),
      hasSize(4)
    );

    assertThat(
      "An exceptional partial-day opening can be equivalently represented as an ExceptionRange",
      result,
      hasItem(
        ExceptionRanges.withExceptionId(
          ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4_CALENDAR_B,
          result.get(0).getId()
        )
      )
    );
  }
}
