package org.folio.calendar.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    Map<Weekday, List<TimeRange<NormalOpening>>> weekdays = initializeWeekdayMapOfTimeRanges();

    // split openings into weekdays
    openings.forEach(opening -> fillWeekdayMapWithTimeTuples(weekdays, opening));

    Set<NormalOpening> conflicts = new HashSet<>();

    for (Entry<Weekday, List<TimeRange<NormalOpening>>> entry : weekdays.entrySet()) {
      Optional<Set<NormalOpening>> weekdayConflicts = TimeUtils.getOverlaps(entry.getValue());
      if (weekdayConflicts.isPresent()) {
        conflicts.addAll(weekdayConflicts.get());
      }
    }

    return conflicts;
  }

  /**
   * Create a weekday map with empty lists for each weekday
   * @param <T> the type sourcing each {@code TimeRange}
   * @return a map with each weekday mapped to an empty list
   */
  public static <T> Map<Weekday, List<TimeRange<T>>> initializeWeekdayMapOfTimeRanges() {
    EnumMap<Weekday, List<TimeRange<T>>> map = new EnumMap<>(Weekday.class);
    Weekday.getAll().forEach(weekday -> map.put(weekday, new ArrayList<>()));
    return map;
  }

  /**
   * Split a {@link NormalOpening NormalOpening} up into separate {@link TimeRange TimeRanges}
   * for each weekday, inserting these range(s) into an {@link EnumMap EnumMap}
   * @param weekdays the map to insert ranges into
   * @param opening the opening to split into weekdays
   */
  public static void fillWeekdayMapWithTimeTuples(
    Map<Weekday, List<TimeRange<NormalOpening>>> weekdays,
    NormalOpening opening
  ) {
    for (Weekday weekday : Weekday.getRange(opening.getStartDay(), opening.getEndDay())) {
      TimeRange<NormalOpening> tuple = new TimeRange<>(
        opening,
        TimeConstants.TIME_MIN,
        TimeConstants.TIME_MAX
      );
      if (weekday == opening.getStartDay()) {
        tuple.setStart(opening.getStartTime());
      }
      if (weekday == opening.getEndDay()) {
        tuple.setEnd(opening.getEndTime());
      }
      weekdays.get(weekday).add(tuple);
    }
  }
}
