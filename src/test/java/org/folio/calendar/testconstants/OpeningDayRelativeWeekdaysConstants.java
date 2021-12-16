package org.folio.calendar.testconstants;

import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayRelativeWeekdays;
import org.folio.calendar.domain.dto.Weekday;

@UtilityClass
public class OpeningDayRelativeWeekdaysConstants {

  public static final OpeningDayRelativeWeekdays SUNDAY = new OpeningDayRelativeWeekdays(
    Weekday.SUNDAY
  );
  public static final OpeningDayRelativeWeekdays MONDAY = new OpeningDayRelativeWeekdays(
    Weekday.MONDAY
  );
  public static final OpeningDayRelativeWeekdays TUESDAY = new OpeningDayRelativeWeekdays(
    Weekday.TUESDAY
  );
  public static final OpeningDayRelativeWeekdays WEDNESDAY = new OpeningDayRelativeWeekdays(
    Weekday.WEDNESDAY
  );
  public static final OpeningDayRelativeWeekdays THURSDAY = new OpeningDayRelativeWeekdays(
    Weekday.THURSDAY
  );
  public static final OpeningDayRelativeWeekdays FRIDAY = new OpeningDayRelativeWeekdays(
    Weekday.FRIDAY
  );
  public static final OpeningDayRelativeWeekdays SATURDAY = new OpeningDayRelativeWeekdays(
    Weekday.SATURDAY
  );
}
