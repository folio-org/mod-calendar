package org.folio.calendar.domain.legacy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.folio.calendar.domain.types.Weekday;

/**
 * Weekday that this opening information applies to.  Despite the plurality,
 * exactly one weekday will be given here -- the name is preserved for legacy
 * reasons.
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OpeningDayRelativeWeekdaysDTO {

  /**
   * A day of the week.  Either SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, or SATURDAY
   */
  @JsonProperty("day")
  private Weekday day;
}
