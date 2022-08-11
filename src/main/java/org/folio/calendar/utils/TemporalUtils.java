package org.folio.calendar.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
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
          conflicts.addAll(stack.stream().map(TemporalRange<D, T>::getSource).toList());
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

  /**
   * Get the time range of a {@link TemporalRange TemporalRange} of
   * {@link LocalDateTime LocalDateTime} that falls on a given {@link LocalDate LocalDate}
   * @param <T> the type contained within the range
   * @param range the range to query
   * @param query the date to extract times from
   * @return a temporal range of times, if available
   */
  public static <T> Optional<TemporalRange<LocalTime, T>> getLocalTimeSliceOfDateTimeRange(
    TemporalRange<LocalDateTime, T> range,
    LocalDate query
  ) {
    LocalDate startDate = range.getStart().toLocalDate();
    LocalDate endDate = range.getEnd().toLocalDate();

    if (DateUtils.contains(query, startDate, endDate)) {
      LocalTime startTime = TimeConstants.TIME_MIN;
      LocalTime endTime = TimeConstants.TIME_MAX;

      if (startDate.equals(query)) {
        startTime = range.getStart().toLocalTime();
      }
      if (endDate.equals(query)) {
        endTime = range.getEnd().toLocalTime();
      }

      return Optional.of(new TemporalRange<>(range.getSource(), startTime, endTime));
    }

    return Optional.empty();
  }
}
