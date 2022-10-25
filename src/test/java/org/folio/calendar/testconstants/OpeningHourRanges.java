package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.legacy.dto.OpeningHourRangeDTO;

@UtilityClass
public class OpeningHourRanges {

  public static final OpeningHourRangeDTO ALL_DAY = new OpeningHourRangeDTO(
    Times.TIME_00_00_STRING_HH_mm,
    Times.TIME_23_59_STRING_HH_mm
  );
  public static final OpeningHourRangeDTO RANGE_00_00_TO_12_30 = new OpeningHourRangeDTO(
    Times.TIME_00_00_STRING_HH_mm,
    Times.TIME_12_30_STRING_HH_mm
  );
  public static final OpeningHourRangeDTO RANGE_00_00_TO_14_59 = new OpeningHourRangeDTO(
    Times.TIME_00_00_STRING_HH_mm,
    Times.TIME_14_59_STRING_HH_mm
  );
  public static final OpeningHourRangeDTO RANGE_04_00_TO_14_59 = new OpeningHourRangeDTO(
    Times.TIME_04_00_STRING_HH_mm,
    Times.TIME_14_59_STRING_HH_mm
  );
  public static final OpeningHourRangeDTO RANGE_15_00_TO_23_59 = new OpeningHourRangeDTO(
    Times.TIME_15_00_STRING_HH_mm,
    Times.TIME_23_59_STRING_HH_mm
  );
  public static final OpeningHourRangeDTO RANGE_23_00_TO_23_59 = new OpeningHourRangeDTO(
    Times.TIME_23_00_STRING_HH_mm,
    Times.TIME_23_59_STRING_HH_mm
  );

  public static final OpeningHourRangeDTO INVALID_RANGE = new OpeningHourRangeDTO(
    Times.TIME_14_59_STRING_HH_mm,
    Times.TIME_04_00_STRING_HH_mm
  );
}
