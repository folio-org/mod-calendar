package org.folio.calendar.utils;

import java.time.temporal.Temporal;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * Utilities that accept dates or times non-discriminately
 */
@UtilityClass
public class TemporalUtils {

  /**
   * Find overlaps within a set of {@link TemporalRange TemporalRanges}, if any exist
   * @param <D> the temporal object to compare with, usually LocalDate or LocalTime
   * @param <T> the type of objects associated with each {@code TemporalRange}
   * @param ranges a set of ranges to evaluate
   * @return an optional list of values that overlap (empty/no value if there were no overlaps).
   * Not all overlaps may be returned, however, if there are overlap(s), then this function will
   * return at least two overlapping openings.
   */
  public static <D extends Temporal & Comparable<? super D>, T> Optional<Set<T>> getOverlaps(
    Iterable<TemporalRange<D, T>> ranges
  ) {
    PriorityQueue<TemporalFromRange<D, T>> queue = new PriorityQueue<>();

    // create a sorted queue of each time, with the first times at the beginning
    for (TemporalRange<D, T> range : ranges) {
      queue.add(new TemporalFromRange<>(range.getStart(), range, true));
      queue.add(new TemporalFromRange<>(range.getEnd(), range, false));
    }

    // track the ranges we are currently inside of
    Deque<TemporalRange<D, T>> stack = new LinkedList<>();

    Set<T> conflicts = new HashSet<>();

    while (!queue.isEmpty()) {
      TemporalFromRange<D, T> time = queue.poll();
      if (time.isStart()) {
        // for new ranges, we just add them to the stack
        stack.push(time.getRangeSource());
      } else {
        // a range ended, but we hit an overlap
        if (stack.size() > 1) {
          conflicts.addAll(
            stack.stream().map(TemporalRange<D, T>::getSource).collect(Collectors.toList())
          );
        }
        stack.pop();
      }
    }

    if (conflicts.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(conflicts);
    }
  }
}
