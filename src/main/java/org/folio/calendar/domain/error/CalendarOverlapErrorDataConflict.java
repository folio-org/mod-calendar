package org.folio.calendar.domain.error;

import java.io.Serializable;
import java.time.LocalDate;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CalendarOverlapErrorDataConflict implements Serializable {

  /**
   * The conflicting calendar's name
   */
  private String name;
  /**
   * The conflicting calendar's start date
   */
  private LocalDate startDate;
  /**
   * The conflicting calendar's end date
   */
  private LocalDate endDate;

  /**
   * A list of all the conflicting service point UUIDs
   */
  @Singular
  private List<UUID> servicePointIds;
}
