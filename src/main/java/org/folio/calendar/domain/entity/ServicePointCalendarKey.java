package org.folio.calendar.domain.entity;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite key for service_point_calendars table
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePointCalendarKey implements Serializable {

  /**
   * The service point ID to which the calendar applies
   */
  @NotNull
  @Column(name = "service_point_id")
  private UUID servicePointId;

  /**
   * The ID of the calendar to be applied
   */
  @NotNull
  @Column(name = "calendar_id")
  private UUID calendarId;
}
