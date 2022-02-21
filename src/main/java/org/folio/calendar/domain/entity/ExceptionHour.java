package org.folio.calendar.domain.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import javax.annotation.CheckForNull;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

/**
 * Models opening hours for an exception to the normal hours.
 *
 * @see ExceptionRange
 */
@Data
@With
@Table(name = "exception_hours")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionHour {

  /**
   * The exception hour's internal ID
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
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exception_id")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private ExceptionRange exception;

  /**
   * The start (absolute, inclusive) date of the range which this opening corresponds to
   */
  @NotNull
  @Column(name = "start_date")
  private LocalDate startDate;

  /**
   * The end (absolute, inclusive) date of the range which this opening corresponds to
   */
  @NotNull
  @Column(name = "end_date")
  private LocalDate endDate;

  /**
   * If not null, the time which (with startDate) the service point opens exceptionally.
   * If null, the library is closed for the range of startDate - endDate.
   */
  @Column(name = "open_start_time")
  @CheckForNull
  private LocalTime startTime;

  /**
   * If not null, the time which (with startDate) the service point closes exceptionally.
   * If null, the library is closed for the range of startDate - endDate.
   */
  @Column(name = "open_end_time")
  @CheckForNull
  private LocalTime endTime;
}
