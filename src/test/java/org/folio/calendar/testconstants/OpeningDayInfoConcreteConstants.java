package org.folio.calendar.testconstants;

import java.util.ArrayList;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayInfo;

@UtilityClass
public class OpeningDayInfoConcreteConstants {

  public static final OpeningDayInfo NO_OPENING_ADJACENT = OpeningDayInfo
    .builder()
    .openingHour(new ArrayList<>())
    .open(false)
    .allDay(false)
    .exceptional(false)
    .build();
  public static final OpeningDayInfo NO_OPENING_ON_REQUESTED_DAY = OpeningDayInfo
    .builder()
    .openingHour(new ArrayList<>())
    .open(false)
    .allDay(true)
    .exceptional(false)
    .build();
  public static final OpeningDayInfo EXCEPTIONALLY_CLOSED_ON_REQUESTED_DAY = OpeningDayInfo
    .builder()
    .openingHour(Arrays.asList(OpeningHourRanges.ALL_DAY))
    .open(false)
    .allDay(true)
    .exceptional(true)
    .build();
}
