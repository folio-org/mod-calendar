package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import org.folio.calendar.domain.legacy.dto.PeriodDTO;
import org.folio.calendar.testconstants.OpeningDayRelativeConstants;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

class PeriodUtilsExceptionalTest {

  @Test
  void testRegularClosureIsNotExceptional() {
    assertThat(
      "A non-exceptional opening (for closure) is not exceptional",
      PeriodUtils.isExceptional(
        PeriodDTO.builder().openingDay(OpeningDayRelativeConstants.MONDAY_CLOSED).build()
      ),
      is(false)
    );
  }

  @Test
  void testRegularOpeningIsNotExceptional() {
    assertThat(
      "A non-exceptional opening (for opening hours) is not exceptional",
      PeriodUtils.isExceptional(
        PeriodDTO
          .builder()
          .openingDay(OpeningDayRelativeConstants.MONDAY_OPEN_04_00_TO_14_59)
          .build()
      ),
      is(false)
    );
  }

  @Test
  void testExceptionalClosureIsExceptional() {
    assertThat(
      "An exceptional opening (for closure) is exceptional",
      PeriodUtils.isExceptional(
        PeriodDTO.builder().openingDay(OpeningDayRelativeConstants.EXCEPTIONAL_CLOSED).build()
      ),
      is(true)
    );
  }

  @Test
  void testExceptionalOpeningIsExceptional() {
    assertThat(
      "An exceptional opening (for different opening hours) is exceptional",
      PeriodUtils.isExceptional(
        PeriodDTO
          .builder()
          .openingDay(OpeningDayRelativeConstants.EXCEPTIONAL_OPEN_04_00_TO_14_59)
          .build()
      ),
      is(true)
    );
  }

  @Test
  void testConversionOfTooManyOpenings() {
    assertThrows(
      "A period must only have one openingDays",
      IllegalArgumentException.class,
      () -> PeriodUtils.convertExceptionalPeriodToExceptionRanges(Periods.PERIOD_FULL_EXAMPLE_B)
    );
  }

  @Test
  void testConversionOfNullOpeningList() {
    assertThrows(
      "A period must only have one openingDays",
      IllegalArgumentException.class,
      () ->
        PeriodUtils.convertExceptionalPeriodToExceptionRanges(
          Periods.PERIOD_EXCEPTIONAL_INVALID_NULL_OPENING
        )
    );
  }

  @Test
  void testConversionOfMultiOpeningInOneList() {
    assertThrows(
      "A period must only have one openingDays",
      IllegalArgumentException.class,
      () ->
        PeriodUtils.convertExceptionalPeriodToExceptionRanges(
          Periods.PERIOD_EXCEPTIONAL_INVALID_MULTIPLE_OPENINGS
        )
    );
  }
}
