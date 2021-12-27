package org.folio.calendar.domain.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 * The relationship between a service point and a calendar
 */
@Data
@With
@Table(name = "service_point_calendars")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicePointCalendarAssignment {

  /**
   * The UUID for this row
   */
  @Id
  @NotNull
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  /**
   * The service point ID to which the calendar applies
   */
  @NotNull
  @Column(name = "service_point_id")
  private UUID servicePointId;

  /**
   * The calendar being applied
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "calendar_id")
  private Calendar calendar;
}
