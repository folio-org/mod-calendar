package org.folio.calendar.utils;

import java.time.temporal.Temporal;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Helper class used to facilitate set overlap search algorithm in {@link TimeUtils TimeUtils}
 */
@Data
@AllArgsConstructor
public class TemporalFromRange<D extends Temporal & Comparable<? super D>, T>
  implements Comparable<TemporalFromRange<D, ?>> {

  private D time;
  private TemporalRange<D, T> rangeSource;

  private boolean isStart;

  public int compareTo(TemporalFromRange<D, ?> other) {
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
