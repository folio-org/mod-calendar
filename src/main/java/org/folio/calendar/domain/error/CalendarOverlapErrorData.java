package org.folio.calendar.domain.error;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.With;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CalendarOverlapErrorData implements ErrorData {

  /**
   * A list of all found conflicts
   */
  @Singular
  private List<CalendarOverlapErrorDataConflict> conflicts;
}
