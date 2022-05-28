package org.folio.calendar.utils;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A simple class that holds a start and end time, used to simplify algorithms
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TimeRange<T> {

  /**
   * Used to retain a link to the original object, for reporting conflicts or similar
   */
  protected T source;

  @NonNull
  protected LocalTime start;

  @NonNull
  protected LocalTime end;
}
