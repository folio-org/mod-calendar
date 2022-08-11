package org.folio.calendar.domain.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.folio.calendar.domain.entity.ExceptionRange;

@Data
@With
@AllArgsConstructor
@Builder(toBuilder = true)
public class ExceptionRangeSingleErrorData implements ErrorData {

  /**
   * A list of exception ranges with conflicts
   */
  private ExceptionRange range;
}
