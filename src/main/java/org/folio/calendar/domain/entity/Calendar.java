package org.folio.calendar.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import javax.annotation.CheckForNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.With;

/**
 * Calendar entity
 */
@Data
@With
@Table(name = "calendars")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calendar implements Serializable {

  /**
   * The UUID of the calendar
   */
  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  /**
   * A human-readable name for the calendar
   */
  @NotNull
  @Column(name = "name")
  private String name;

  /**
   * The first effective day of the calendar
   */
  @NotNull
  @Column(name = "start_date")
  private LocalDate startDate;

  /**
   * The last effective day of the calendar
   */
  @NotNull
  @Column(name = "end_date")
  private LocalDate endDate;

  /**
   * All assigned service points
   */
  @Singular
  @OneToMany(
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    orphanRemoval = true,
    mappedBy = "calendar"
  )
  private Set<ServicePointCalendarAssignment> servicePoints;

  /**
   * Normal openings for the calendar
   */
  @Singular
  @OneToMany(
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    orphanRemoval = true,
    mappedBy = "calendar"
  )
  private Set<NormalOpening> normalHours;

  /**
   * Exceptional openings and closures
   */
  @Singular
  @OneToMany(
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    orphanRemoval = true,
    mappedBy = "calendar"
  )
  private Set<ExceptionRange> exceptions;

  /**
   * When the calendar was created, if available
   */
  @CheckForNull
  @Column(name = "created_date")
  private Instant createdDate;

  /**
   * Who created the calendar, if available
   */
  @CheckForNull
  @Column(name = "created_by_user_id")
  private UUID createdByUserId;

  /**
   * When the calendar was last edited, if available
   */
  @CheckForNull
  @Column(name = "updated_date")
  private Instant updatedDate;

  /**
   * Who last edited the calendar, if available
   */
  @CheckForNull
  @Column(name = "updated_by_user_id")
  private UUID updatedByUserId;

  @PreUpdate
  @PrePersist
  public void propagate() {
    if (this.getServicePoints() != null) {
      this.getServicePoints().forEach(assignment -> assignment.setCalendar(this));
    }
    if (this.getNormalHours() != null) {
      this.getNormalHours().forEach(opening -> opening.setCalendar(this));
    }
    if (this.getExceptions() != null) {
      this.getExceptions().forEach(exception -> exception.setCalendar(this));
    }
  }

  /**
   * Clears all IDs from this object, to prepare it for insertion (as new IDs
   * will then be generated).
   */
  public void clearIds() {
    this.setId(null);

    if (this.getServicePoints() != null) {
      this.getServicePoints().forEach(ServicePointCalendarAssignment::clearIds);
    }
    if (this.getNormalHours() != null) {
      this.getNormalHours().forEach(NormalOpening::clearIds);
    }
    if (this.getExceptions() != null) {
      this.getExceptions().forEach(ExceptionRange::clearIds);
    }
  }

  /**
   * Clears all metadata from the object, to use for comparison.
   */
  public Calendar withoutMetadata() {
    return this.withCreatedDate(null)
      .withCreatedByUserId(null)
      .withUpdatedDate(null)
      .withUpdatedByUserId(null);
  }
}
