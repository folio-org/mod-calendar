package org.folio.calendar.unit.utils;

import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;
import org.folio.calendar.domain.dto.OpeningDayRelativeDTO;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.OpeningDayRelativeConstants;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

class PeriodUtilsNullConversionTest {

  @Test
  void testNullOpeningDayRelativeExceptional() {
    List<OpeningDayRelativeDTO> invalidOpeningList = Arrays.asList(
      OpeningDayRelativeConstants.EXCEPTIONAL_INVALID_NULL_OPENING
    );
    assertThrows(
      "A null OpeningHourRange in a OpeningDayRelative cannot be converted to an exception range",
      IllegalArgumentException.class,
      () ->
        PeriodUtils.convertOpeningDayRelativeDTOToExceptionRanges(
          Dates.DATE_2021_01_01,
          Dates.DATE_2021_01_02,
          invalidOpeningList
        )
    );
  }

  @Test
  void testNullOpeningDayRelativeNormal() {
    List<OpeningDayRelativeDTO> invalidOpeningList = Arrays.asList(
      OpeningDayRelativeConstants.EXCEPTIONAL_CLOSED
    );
    assertThrows(
      "A null weekday in a OpeningDayRelative cannot be converted to a normal opening",
      IllegalArgumentException.class,
      () -> PeriodUtils.convertOpeningDayRelativeDTOToNormalOpening(invalidOpeningList)
    );
  }
}
