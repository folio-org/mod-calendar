package org.folio.calendar.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.domain.dto.OpeningHourRange;

/**
 * Assorted time-related constants
 */
@UtilityClass
public class TimeConstants {

  /**
   * Formatter for HH:mm style times, e.g. "00:00", "14:30"
   */
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  /**
   * The first minute in the day
   */
  public static final LocalTime TIME_MIN = LocalTime.of(0, 0);
  /**
   * The first minute in the day, as a HH:mm string
   */
  public static final String TIME_MIN_STRING = TIME_MIN.format(TIME_FORMATTER);
  /**
   * The last minute in the day
   */
  public static final LocalTime TIME_MAX = LocalTime.of(23, 59);
  /**
   * The last minute in the day, as a HH:mm string
   */
  public static final String TIME_MAX_STRING = TIME_MAX.format(TIME_FORMATTER);

  /**
   * An OpeningHourRange that lasts all day
   */
  public static final OpeningHourRange ALL_DAY = new OpeningHourRange(
    TIME_MIN_STRING,
    TIME_MAX_STRING
  );

  public static final OpeningDayInfo ALL_DAY_CLOSURE = OpeningDayInfo
    .builder()
    .allDay(true)
    .exceptional(false)
    .openingHour(Arrays.asList(ALL_DAY))
    .build();
}
