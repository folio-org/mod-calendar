package org.folio.calendar.domain.error;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.folio.calendar.domain.entity.NormalOpening;

@Data
@With
@AllArgsConstructor
@Builder(toBuilder = true)
public class NormalOpeningOverlapErrorData implements ErrorData {

  /**
   * A list of normal openings with conflicts
   */
  private Set<NormalOpening> conflictingServicePointIds;
}
