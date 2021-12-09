package org.folio.calendar.domain.entity;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
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
  @Column(name = "service_point_id")
  @GeneratedValue
  private UUID servicePointId;

  /**
   * The ID of the calendar to be applied
   */
  @Column(name = "calendar_id")
  @GeneratedValue
  private UUID calendarId;
}
