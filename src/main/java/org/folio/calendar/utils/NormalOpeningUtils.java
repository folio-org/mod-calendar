package org.folio.calendar.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.types.Weekday;

/**
 * Utilities for normal openings
 */
@UtilityClass
public class NormalOpeningUtils {

  /**
   * Find overlaps between two normal openings, if any exist
   * @param ranges a set of ranges to evaluate
   * @return an optional list of openings that overlap (empty/no value if there were no overlaps).
   * Not all overlaps may be returned, however, if there are overlap(s), then this function will
   * return at least two overlapping openings.
   */
  public static Set<NormalOpening> getOverlaps(Iterable<NormalOpening> openings) {
    // initialize weekday map
    EnumMap<Weekday, List<TimeRange>> weekdays = new EnumMap<>(Weekday.class);
    Weekday.getAll().forEach(weekday -> weekdays.put(weekday, new ArrayList<>()));

    // split openings into weekdays
    openings.forEach(opening -> fillWeekdayMapWithTimeTuples(weekdays, opening));

    Set<NormalOpening> conflicts = new HashSet<>();

    for (Entry<Weekday, List<TimeRange>> entry : weekdays.entrySet()) {
      Optional<Set<NormalOpening>> weekdayConflicts = TimeUtils.getOverlaps(entry.getValue());
      if (weekdayConflicts.isPresent()) {
        conflicts.addAll(weekdayConflicts.get());
      }
    }

    return conflicts;
  }

  /**
   * Split a {@link NormalOpening NormalOpening} up into separate {@link TimeRange TimeRanges}
   * for each weekday, inserting these range(s) into an {@link EnumMap EnumMap}
   * @param weekdays the map to insert ranges into
   * @param opening the opening to split into weekdays
   */
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
