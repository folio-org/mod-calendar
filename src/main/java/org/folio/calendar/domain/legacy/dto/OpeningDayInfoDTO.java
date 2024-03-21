package org.folio.calendar.domain.legacy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.List;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.With;

/**
 * An entire day&#39;s opening time
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OpeningDayInfoDTO {

  /**
   * All start/end time pairs for a day
   */
  @JsonProperty("openingHour")
  @Nullable
  @Valid
  @Singular("openingHourPair")
  private List<OpeningHourRangeDTO> openingHour;

  /**
   * Concrete date that this corresponds to.  For calculateOpening only, use openingDayConcrete for other circumstances
   */
  @JsonProperty("date")
  @Nullable
  private LegacyPeriodDate date;

  /**
   * If this denotes an all-day opening or closing
   */
  @JsonProperty("allDay")
  @Nullable
  @Getter(AccessLevel.NONE)
  private Boolean allDay;

  /**
   * Whether this is to indicate a closure (for exceptions)
   */
  @JsonProperty("open")
  @Getter(AccessLevel.NONE)
  @Builder.Default
  private Boolean open = true;

  /**
   * Denotes if this opening/closure is the result of an exception rather than a normal period/calendar
   */
  @JsonProperty("exceptional")
  @Nullable
  @Getter(AccessLevel.NONE)
  @Builder.Default
  private Boolean exceptional = false;

  /**
   * If this denotes an all-day opening or closing
   * @return allDay
   */
  public Boolean isAllDay() {
    return this.allDay;
  }

  /**
   * Whether this is to indicate a closure (for exceptions)
   * @return open
   */
  public Boolean isOpen() {
    return this.open;
  }
}
