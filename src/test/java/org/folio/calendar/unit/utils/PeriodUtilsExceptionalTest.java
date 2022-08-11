package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.legacy.dto.OpeningDayRelativeDTO;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.ExceptionRanges;
import org.folio.calendar.testconstants.OpeningDayRelativeConstants;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

class PeriodUtilsExceptionalTest {

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
    List<OpeningDayRelativeDTO> list = new ArrayList<>();
    assertThrows(
      "A list of no openings cannot be converted to exception range(s)",
      IllegalArgumentException.class,
      () ->
        PeriodUtils.convertOpeningDayRelativeDTOToExceptionRanges(
          Dates.DATE_2021_01_01,
          Dates.DATE_2021_12_31,
          list
        )
    );
  }

  @Test
  void testConversionOfMultipleExceptionsToExceptionRanges() {
    List<OpeningDayRelativeDTO> list = Arrays.asList(
      OpeningDayRelativeConstants.EXCEPTIONAL_CLOSED,
      OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_04_00_TO_14_59
    );
    assertThrows(
      "A list of multiple openings cannot be converted to exception range(s)",
      IllegalArgumentException.class,
      () ->
        PeriodUtils.convertOpeningDayRelativeDTOToExceptionRanges(
          Dates.DATE_2021_01_01,
          Dates.DATE_2021_12_31,
          list
        )
    );
  }

  @Test
  void testConversionOfMultipleHourPairsToExceptionRanges() {
    List<OpeningDayRelativeDTO> list = Arrays.asList(
      OpeningDayRelativeConstants.EXCEPTIONAL_INVALID_MULTIPLE_OPENINGS
    );
    assertThrows(
      "An opening with multiple hour ranges cannot be converted to exception range(s)",
      IllegalArgumentException.class,
      () ->
        PeriodUtils.convertOpeningDayRelativeDTOToExceptionRanges(
          Dates.DATE_2021_01_01,
          Dates.DATE_2021_12_31,
          list
        )
    );
  }

  @Test
  void testConversionOfClosureToExceptionRanges() {
    List<ExceptionRange> result = PeriodUtils.convertOpeningDayRelativeDTOToExceptionRanges(
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_12_31,
      Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_CLOSED)
    );
    assertThat(
      "An exceptional closure produces exactly one equivalent ExceptionRange",
      result,
      hasSize(1)
    );

    // override default ID
    result.get(0).setId(null);
    assertThat(
      "An exceptional closure produces exactly one equivalent ExceptionRange with no openings",
      result.get(0).getOpenings(),
      hasSize(0)
    );

    assertThat(
      "An exceptional closure can be equivalently represented as an ExceptionRange",
      result,
      hasItem(
        ExceptionRanges
          .withExceptionId(ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31, result.get(0).getId())
          .withName("Untitled exception")
      )
    );
  }

  @Test
  void testConversionOfAllDayOpeningToExceptionRanges() {
    List<ExceptionRange> result = PeriodUtils.convertOpeningDayRelativeDTOToExceptionRanges(
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_04,
      Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_ALL_DAY)
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

    assertThat(
      "An exceptional all-day opening can be equivalently represented as an ExceptionRange",
      result,
      hasItem(
        ExceptionRanges.withExceptionId(
          ExceptionRanges.OPEN_ALL_DAY_JAN_1_THRU_JAN_4,
          result.get(0).getId()
        )
      )
    );
  }

  @Test
  void testConversionOfPartialDayOpeningToExceptionRanges() {
    List<ExceptionRange> result = PeriodUtils.convertOpeningDayRelativeDTOToExceptionRanges(
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_04,
      Arrays.asList(OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_04_00_TO_14_59)
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
          ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4,
          result.get(0).getId()
        )
      )
    );
  }
}
