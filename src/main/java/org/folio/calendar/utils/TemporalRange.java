package org.folio.calendar.utils;

import java.time.temporal.Temporal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A simple class that holds a start and end date or time, used to simplify algorithms
 * @param <D> The temporal object used for comparisons and endpoints, usually
 * {@code LocalDate} or {@code LocalTime}.
 * @param <T> The source object of this range, used for comparisons and the like.
 * If this is not necessary, a value of {@code Object} can be used and null given
 * to the constructor.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TemporalRange<D extends Temporal & Comparable<? super D>, T> {

  /**
   * Used to retain a link to the original object, for reporting conflicts or similar
   */
  protected T source;

  @NonNull
  protected D start;

  @NonNull
  protected D end;
}
