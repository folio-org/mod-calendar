package org.folio.calendar.domain.error;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@With
@AllArgsConstructor
@Builder(toBuilder = true)
public class CalendarNotFoundErrorData implements ErrorData {

  /**
   * A list of all IDs that could not be found
   */
  private List<UUID> notFound;
}
