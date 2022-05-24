package org.folio.calendar.utils;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.folio.calendar.domain.entity.NormalOpening;

/**
 * A simple class that holds a start and end time, used to simplify algorithms
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TimeRange {

  /**
   * Used to retain a link to the original open, for reporting conflicts
   */
  protected NormalOpening source;

  @NonNull
  protected LocalTime start;

  @NonNull
  protected LocalTime end;
}
