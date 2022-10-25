package org.folio.calendar.utils;

import java.time.LocalTime;
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
    Map<Weekday, List<TemporalRange<LocalTime, NormalOpening>>> weekdays = initializeWeekdayMapOfRanges();

    // split openings into weekdays
    openings.forEach(opening -> fillWeekdayMapWithTimeTuples(weekdays, opening));

    Set<NormalOpening> conflicts = new HashSet<>();

    for (Entry<Weekday, List<TemporalRange<LocalTime, NormalOpening>>> entry : weekdays.entrySet()) {
      Optional<Set<NormalOpening>> weekdayConflicts = TemporalUtils.getOverlaps(entry.getValue());
      if (weekdayConflicts.isPresent()) {
        conflicts.addAll(weekdayConflicts.get());
      }
    }

    return conflicts;
  }

  /**
   * Create a weekday map with empty lists for each weekday
   * @param <T> the type sourcing each {@code TemporalRange}
   * @return a map with each weekday mapped to an empty list
   */
  public static <T> Map<Weekday, List<TemporalRange<LocalTime, T>>> initializeWeekdayMapOfRanges() {
    EnumMap<Weekday, List<TemporalRange<LocalTime, T>>> map = new EnumMap<>(Weekday.class);
    Weekday.getAll().forEach(weekday -> map.put(weekday, new ArrayList<>()));
    return map;
  }

  /**
   * Split a {@link NormalOpening NormalOpening} up into separate time-based
   * {@link TemporalRange TemporalRanges} for each weekday, inserting these
   * range(s) into an {@link EnumMap EnumMap}
   * @param weekdays the map to insert ranges into
   * @param opening the opening to split into weekdays
   */
  public static void fillWeekdayMapWithTimeTuples(
    Map<Weekday, List<TemporalRange<LocalTime, NormalOpening>>> weekdays,
    NormalOpening opening
  ) {
    // if the opening wraps around into the same weekday, split it into two
    // portions to enclose it within a single day
    if (
      opening.getStartDay() == opening.getEndDay() &&
      opening.getStartTime().isAfter(opening.getEndTime())
    ) {
      Weekday
        .getAll()
        .forEach((Weekday weekday) -> {
          if (weekday == opening.getStartDay()) {
            weekdays
              .get(weekday)
              .add(new TemporalRange<>(opening, TimeConstants.TIME_MIN, opening.getEndTime()));
            weekdays
              .get(weekday)
              .add(new TemporalRange<>(opening, opening.getStartTime(), TimeConstants.TIME_MAX));
          } else {
            weekdays
              .get(weekday)
              .add(new TemporalRange<>(opening, TimeConstants.TIME_MIN, TimeConstants.TIME_MAX));
          }
        });
      return;
    }
    for (Weekday weekday : Weekday.getRange(opening.getStartDay(), opening.getEndDay())) {
      TemporalRange<LocalTime, NormalOpening> tuple = new TemporalRange<>(
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

  /**
   * Determine if two NormalOpenings are adjacent to each other.  Small
   * overlaps less than one weekday will be considered adjacent.
   *
   * @param opening1 The first opening to compare
   * @param opening2 The second opening to compare
   * @return if they are adjacent
   */
  public static boolean adjacent(final NormalOpening opening1, final NormalOpening opening2) {
    if (opening1.equals(opening2)) {
      return false;
    }

    NormalOpening former = opening1;
    NormalOpening latter = opening2;

    // account for small variations in overlap, < 1 weekday
    if (
      former.getEndDay() == latter.getStartDay() &&
      !latter.getStartTime().isAfter(former.getEndTime())
    ) {
      latter = latter.withStartTime(former.getEndTime().plusMinutes(1));
    }

    // former ends at midnight the day before latter
    if (
      former.getEndTime().equals(TimeConstants.TIME_MAX) &&
      latter.getStartTime().equals(TimeConstants.TIME_MIN) &&
      former.getEndDay().next() == latter.getStartDay()
    ) {
      return true;
    }

    // former ends one minute before latter, on the same day
    return (
      former.getEndDay() == latter.getStartDay() &&
      former.getEndTime().plusMinutes(1).equals(latter.getStartTime())
    );
  }

  /**
   * Merge two NormalOpenings into one range over both.  You most likely want
   * to make sure they are
   * {@link org.folio.calendar.domain.entity.NormalOpening#adjacent} first.
   *
   * @param opening1 a {@link org.folio.calendar.domain.entity.NormalOpening NormalOpening}
   * @param opening2 a {@link org.folio.calendar.domain.entity.NormalOpening NormalOpening}
   * @return a NormalOpening which surrounds opening1 and opening2
   */
  // comparison of Calendars with ==/!= was intentional as we only care about references
  // plus, we want null to work as expected
  @SuppressWarnings("java:S1698")
  public static NormalOpening merge(final NormalOpening opening1, final NormalOpening opening2) {
    if (opening1.getCalendar() != opening2.getCalendar()) {
      throw new IllegalArgumentException(
        "Cannot merge two NormalOpenings from different calendars!"
      );
    }

    return opening1.withEndDay(opening2.getEndDay()).withEndTime(opening2.getEndTime());
  }
}
