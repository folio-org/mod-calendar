package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningHourRange;

@UtilityClass
public class OpeningHourRanges {

  public static final OpeningHourRange ALL_DAY = new OpeningHourRange(
    Times.TIME_00_00_STRING_HH_mm,
    Times.TIME_23_59_STRING_HH_mm
  );
  public static final OpeningHourRange RANGE_00_00_TO_12_30 = new OpeningHourRange(
    Times.TIME_00_00_STRING_HH_mm,
    Times.TIME_12_30_STRING_HH_mm
  );
  public static final OpeningHourRange RANGE_04_00_TO_14_59 = new OpeningHourRange(
    Times.TIME_04_00_STRING_HH_mm,
    Times.TIME_14_59_STRING_HH_mm
  );
  public static final OpeningHourRange RANGE_15_00_TO_23_59 = new OpeningHourRange(
    Times.TIME_15_00_STRING_HH_mm,
    Times.TIME_23_59_STRING_HH_mm
  );
  public static final OpeningHourRange RANGE_23_00_TO_23_59 = new OpeningHourRange(
    Times.TIME_23_00_STRING_HH_mm,
    Times.TIME_23_59_STRING_HH_mm
  );

  public static final OpeningHourRange INVALID_RANGE = new OpeningHourRange(
    Times.TIME_14_59_STRING_HH_mm,
    Times.TIME_04_00_STRING_HH_mm
  );
}
