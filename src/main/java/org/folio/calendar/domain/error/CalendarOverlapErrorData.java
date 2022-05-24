package org.folio.calendar.domain.error;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.With;

@Data
@With
@AllArgsConstructor
@Builder(toBuilder = true)
public class CalendarOverlapErrorData implements ErrorData {

  /**
   * A list of all service points with conflicts
   */
  @Singular
  private List<UUID> conflictingServicePointIds;
}
