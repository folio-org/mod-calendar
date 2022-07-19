package org.folio.calendar.testconstants;

import java.util.ArrayList;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayInfoDTO;

@UtilityClass
public class OpeningDayInfoConcreteConstants {

  public static final OpeningDayInfoDTO NO_OPENING_ADJACENT = OpeningDayInfoDTO
    .builder()
    .openingHour(new ArrayList<>())
    .open(false)
    .allDay(false)
    .exceptional(false)
    .build();
  public static final OpeningDayInfoDTO NO_OPENING_ON_REQUESTED_DAY = OpeningDayInfoDTO
    .builder()
    .openingHour(new ArrayList<>())
    .open(false)
    .allDay(true)
    .exceptional(false)
    .build();
  public static final OpeningDayInfoDTO EXCEPTIONALLY_CLOSED_ON_REQUESTED_DAY = OpeningDayInfoDTO
    .builder()
    .openingHour(Arrays.asList(OpeningHourRanges.ALL_DAY))
    .open(false)
    .allDay(true)
    .exceptional(true)
    .build();
}
