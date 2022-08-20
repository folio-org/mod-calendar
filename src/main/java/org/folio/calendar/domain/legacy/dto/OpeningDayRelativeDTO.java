package org.folio.calendar.domain.legacy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 * Opening information for a weekday
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OpeningDayRelativeDTO {

  @JsonProperty("weekdays")
  @Nullable
  private OpeningDayRelativeWeekdaysDTO weekdays;

  @JsonProperty("openingDay")
  private OpeningDayInfoDTO openingDay;
}
