package org.folio.calendar.domain.legacy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 * An opening range
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OpeningHourRangeDTO {

  /**
   * When the opening starts, as a 24 hour time
   */
  @JsonProperty("startTime")
  private String startTime;

  /**
   * When the opening ends, as a 24 hour time
   */
  @JsonProperty("endTime")
  private String endTime;
}
