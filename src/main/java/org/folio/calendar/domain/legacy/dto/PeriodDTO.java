package org.folio.calendar.domain.legacy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.With;

/**
 * A single period for a service point
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PeriodDTO {

  /**
   * Internal calendar UUID
   */
  @JsonProperty("id")
  private UUID id;

  /**
   * The UUID for the relevant service point
   */
  @JsonProperty("servicePointId")
  private UUID servicePointId;

  /**
   * The calendar&#39;s name
   */
  @JsonProperty("name")
  private String name;

  /**
   * The first effective date (inclusive) of this calendar
   */
  @JsonProperty("startDate")
  private LegacyPeriodDate startDate;

  /**
   * The last effective date (inclusive) of this calendar
   */
  @JsonProperty("endDate")
  private LegacyPeriodDate endDate;

  /**
   * All opening information for each weekday in this period.
   */
  @JsonProperty("openingDays")
  @Valid
  @Singular
  private List<OpeningDayRelativeDTO> openingDays;
}
