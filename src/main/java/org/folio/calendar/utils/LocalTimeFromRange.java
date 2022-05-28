package org.folio.calendar.utils;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Helper class used to facilitate set overlap search algorithm in {@link TimeUtils TimeUtils}
 */
@Data
@AllArgsConstructor
public class LocalTimeFromRange<T> implements Comparable<LocalTimeFromRange<?>> {

  private LocalTime time;
  private TimeRange<T> rangeSource;

  private boolean isStart;

  public int compareTo(LocalTimeFromRange<?> other) {
    if (time.compareTo(other.time) != 0) {
      return time.compareTo(other.time);
    }

    // prioritize starting over ending ranges
    if (this.isStart) {
      return -1;
    } else {
      return 1;
    }
  }
}
