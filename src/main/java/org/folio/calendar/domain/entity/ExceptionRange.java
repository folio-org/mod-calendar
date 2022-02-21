package org.folio.calendar.domain.entity;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.With;

/**
 * An overall exception to the normal hours of a calendar.  Multiple {@link ExceptionHour} objects can define specific behavior over this interval.
 */
@Data
@With
@Table(name = "exceptions")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionRange {

  /**
   * The exception's internal ID
   */
  @Id
  @NotNull
  @Column(name = "id")
  private UUID id;

  /**
   * The calendar that this is exceptional to
   */
  @NotNull
  @Column(name = "calendar_id")
  private UUID calendarId;

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
   * The corresponding openings/closures which relate to this exception
   */
  @Singular
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "exception_id")
  private Set<ExceptionHour> openings;
}
