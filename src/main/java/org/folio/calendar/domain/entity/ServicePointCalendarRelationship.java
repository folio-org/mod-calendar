package org.folio.calendar.domain.entity;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "service_point_calendars")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ServicePointCalendarKey.class)
public class ServicePointCalendarRelationship {

  /**
   * The service point ID to which the calendar applies
   */
  @Id
  private UUID servicePointId;

  /**
   * The ID of the calendar to be applied
   */
  @Id
  private UUID calendarId;
}
