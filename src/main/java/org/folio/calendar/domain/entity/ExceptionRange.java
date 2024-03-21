package org.folio.calendar.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import lombok.With;

/**
 * An overall exception to the normal hours of a calendar.  Multiple {@link org.folio.calendar.domain.entity.ExceptionHour} objects can define specific behavior over this interval.
 */
@Data
@With
@Table(name = "exceptions")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionRange implements Serializable {

  /**
   * The exception's internal ID
   */
  @Id
  @NotNull
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  /**
   * The calendar that this is exceptional to
   */
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JoinColumn(name = "calendar_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Calendar calendar;

  @NotNull
  @Builder.Default
  @Column(name = "name")
  private String name = "Untitled exception";

  /**
   * The first date to which this exception is effective
   */
  @NotNull
  @Column(name = "start_date")
  private LocalDate startDate;

  /**
   * The last date to which this exception is effective
   */
  @NotNull
  @Column(name = "end_date")
  private LocalDate endDate;

  /**
   * The corresponding openings which relate to this exception
   */
  @Singular
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "exception")
  private Set<ExceptionHour> openings;

  @PreUpdate
  @PrePersist
  public void propagate() {
    if (this.getOpenings() != null) {
      this.getOpenings().forEach(opening -> opening.setException(this));
    }
  }

  /**
   * Clears all IDs from this object, to prepare it for insertion (as new IDs
   * will then be generated).
   */
  public void clearIds() {
    this.setId(null);
    if (this.getOpenings() != null) {
      this.getOpenings().forEach(ExceptionHour::clearIds);
    }
  }
}
