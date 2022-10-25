package org.folio.calendar.utils;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.SingleDayOpeningRangeDTO;

/**
 * Utilities for times
 */
@UtilityClass
public class TimeUtils {

  /**
   * Check that two inclusive local time pairs overlap (within the same day)
   *
   * @param start1 Start of time range 1
   * @param end1 End of time range 1
   * @param start2 Start of time range 2
   * @param end2 End of time range 2
   * @return if they overlap
   */
  public static boolean overlaps(
    LocalTime start1,
    LocalTime end1,
    LocalTime start2,
    LocalTime end2
  ) {
    // False if: 2 starts after 1 OR 2 ends before 1 starts
    return !(start2.isAfter(end1) || end2.isBefore(start1));
  }

  /**
   * Check that two inclusive time ranges overlap (within the same day)
   *
   * @param a time range 1
   * @param b time range 2
   * @return if they overlap
   */
  public static boolean overlaps(TemporalRange<LocalTime, ?> a, TemporalRange<LocalTime, ?> b) {
    return overlaps(a.getStart(), a.getEnd(), b.getStart(), b.getEnd());
  }

  /**
   * Check that a start and end time cover an entire day
   *
   * @param startTime the start of the range
   * @param endTime the end of the range
   * @return if they cover an entire day (00:00 - 23:59), to minute accuracy
   */
  public static boolean isAllDay(LocalTime startTime, LocalTime endTime) {
    return (
      startTime.truncatedTo(ChronoUnit.MINUTES).equals(TimeConstants.TIME_MIN) &&
      endTime.truncatedTo(ChronoUnit.MINUTES).equals(TimeConstants.TIME_MAX)
    );
  }

  /**
   * Check that a time range covers an entire day
   *
   * @param openings the range to consider
   * @return if the range covers an entire day (00:00 - 23:59), to minute accuracy
   */
  public static boolean isAllDay(List<? extends TemporalRange<LocalTime, ?>> openings) {
    if (openings.isEmpty()) {
      return false;
    }

    if (openings.size() == 1) {
      return isAllDay(openings.get(0).getStart(), openings.get(0).getEnd());
    }

    List<TemporalRange<LocalTime, ?>> openingsCopy = new ArrayList<>(openings);
    openingsCopy.sort((a, b) -> a.getStart().compareTo(b.getStart()));

    // range must cover the full day
    if (
      !isAllDay(openingsCopy.get(0).getStart(), openingsCopy.get(openingsCopy.size() - 1).getEnd())
    ) {
      return false;
    }

    for (int i = 1; i < openingsCopy.size(); i++) {
      // ensure each range butts up against the previous
      if (
        !openingsCopy
          .get(i - 1)
          .getEnd()
          .plusMinutes(1)
          .truncatedTo(ChronoUnit.MINUTES)
          .equals(openingsCopy.get(i).getStart().truncatedTo(ChronoUnit.MINUTES))
      ) {
        return false;
      }
    }

    return true;
  }

  /**
   * Check that an opening range covers an entire day
   *
   * @param opening the opening to consider
   * @return if the opening covers an entire day (00:00 - 23:59), to minute accuracy
   */
  public static boolean isAllDay(SingleDayOpeningRangeDTO opening) {
    return isAllDay(opening.getStartTime(), opening.getEndTime());
  }

  /**
   * Convert a LocalTime to a string of the format HH:mm
   *
   * @param time the {@link java.time.LocalTime LocalTime} to convert
   * @return the formatted String (HH:mm)
   */
  public static String toTimeString(LocalTime time) {
    return time.format(TimeConstants.TIME_FORMATTER);
  }

  /**
   * Convert a string of the format HH:mm to a LocalTime
   *
   * @param time the formatted String (HH:mm)
   * @return a parsed {@link java.time.LocalTime LocalTime}
   */
  public static LocalTime fromTimeString(String time) {
    return LocalTime.parse(time, TimeConstants.TIME_FORMATTER);
  }
}
