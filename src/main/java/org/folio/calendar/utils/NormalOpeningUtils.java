package org.folio.calendar.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.types.Weekday;

/**
 * Utilities for normal openings
 */
@UtilityClass
public class NormalOpeningUtils {

  public static boolean overlaps(NormalOpening a, NormalOpening b) {
    EnumMap<Weekday, List<TimeRange>> weekdays = new EnumMap<>(Weekday.class);

    for (Weekday weekday : Weekday.getAll()) {
      weekdays.put(weekday, new ArrayList<>());
    }

    fillWeekdayMapWithTimeTuples(weekdays, a);
    fillWeekdayMapWithTimeTuples(weekdays, b);

    for (Entry<Weekday, List<TimeRange>> entry : weekdays.entrySet()) {}

    return false;
  }

  protected static void fillWeekdayMapWithTimeTuples(
    EnumMap<Weekday, List<TimeRange>> weekdays,
    NormalOpening opening
  ) {
    for (Weekday weekday : Weekday.getRange(opening.getStartDay(), opening.getEndDay())) {
      TimeRange tuple = new TimeRange(opening, TimeConstants.TIME_MIN, TimeConstants.TIME_MAX);
      if (weekday == opening.getStartDay()) {
        tuple.setStart(opening.getStartTime());
      }
      if (weekday == opening.getEndDay()) {
        tuple.setStart(opening.getEndTime());
      }
      weekdays.get(weekday).add(tuple);
    }
  }
}
