package org.folio.calendar.domain.entity;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An overall exception to the normal hours of a calendar.  Multiple {@link ExceptionHour} objects can define specific behavior over this interval.
 */
@Data
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
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  /**
   * The calendar that this is exceptional to
   */
  @Column(name = "calendar_id")
  private UUID calendarId;

  /**
   * The first date to which this exception is effective
   */
  @Column(name = "start_date")
  private LocalDate startDate;

  /**
   * The last date to which this exception is effective
   */
  @Column(name = "end_date")
  private LocalDate endDate;

  /**
   * The corresponding openings/closures which relate to this exception
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "exception_id")
  private Set<ExceptionHour> openings;
}
