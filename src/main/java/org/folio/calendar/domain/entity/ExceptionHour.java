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
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import javax.annotation.CheckForNull;
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
public class ExceptionHour implements Serializable {

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
  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exception_id")
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

  /**
   * Clears all IDs from this object, to prepare it for insertion (as new IDs
   * will then be generated).
   */
  public void clearIds() {
    this.setId(null);
  }
}
