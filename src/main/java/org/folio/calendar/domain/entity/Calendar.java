package org.folio.calendar.domain.entity;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calendar {

  /**
   * The UUID of the calendar
   */
  @Id
  @NotNull
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

  @PrePersist
  @PreUpdate
  private void prePersist() {
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
}
