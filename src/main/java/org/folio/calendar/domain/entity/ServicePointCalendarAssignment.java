package org.folio.calendar.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class ServicePointCalendarAssignment implements Serializable {

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
  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JoinColumn(name = "calendar_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Calendar calendar;

  /**
   * Clears all IDs from this object, to prepare it for insertion (as new IDs
   * will then be generated).
   */
  public void clearIds() {
    this.setId(null);
  }
}
