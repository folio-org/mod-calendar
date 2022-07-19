package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayRelativeWeekdaysDTO;
import org.folio.calendar.domain.types.Weekday;

@UtilityClass
public class OpeningDayRelativeWeekdaysConstants {

  public static final OpeningDayRelativeWeekdaysDTO SUNDAY = new OpeningDayRelativeWeekdaysDTO(
    Weekday.SUNDAY
  );
  public static final OpeningDayRelativeWeekdaysDTO MONDAY = new OpeningDayRelativeWeekdaysDTO(
    Weekday.MONDAY
  );
  public static final OpeningDayRelativeWeekdaysDTO TUESDAY = new OpeningDayRelativeWeekdaysDTO(
    Weekday.TUESDAY
  );
  public static final OpeningDayRelativeWeekdaysDTO WEDNESDAY = new OpeningDayRelativeWeekdaysDTO(
    Weekday.WEDNESDAY
  );
  public static final OpeningDayRelativeWeekdaysDTO THURSDAY = new OpeningDayRelativeWeekdaysDTO(
    Weekday.THURSDAY
  );
  public static final OpeningDayRelativeWeekdaysDTO FRIDAY = new OpeningDayRelativeWeekdaysDTO(
    Weekday.FRIDAY
  );
  public static final OpeningDayRelativeWeekdaysDTO SATURDAY = new OpeningDayRelativeWeekdaysDTO(
    Weekday.SATURDAY
  );
}
