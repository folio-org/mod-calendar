package org.folio.calendar.unit.utils;

import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;
import org.folio.calendar.domain.legacy.dto.OpeningDayRelativeDTO;
import org.folio.calendar.testconstants.OpeningDayRelativeConstants;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

class PeriodUtilsNullConversionTest {

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
