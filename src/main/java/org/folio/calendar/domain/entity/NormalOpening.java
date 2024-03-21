package org.folio.calendar.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.folio.calendar.domain.types.Weekday;

/**
 * A normal opening for a service point
 */
@Data
@With
@Table(name = "normal_hours")
@Entity
@Builder
@NoArgsConstructor
public class NormalOpening implements Serializable {

  /**
   * The opening's internal ID
   */
  @Id
  @NotNull
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  /**
   * The calendar that this opening is a part of
   */
  @NotNull
  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JoinColumn(name = "calendar_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Calendar calendar;

  /**
   * The (relative) weekday upon which this opening starts
   */
  @NotNull
  @Column(name = "start_day")
  @Enumerated(EnumType.STRING)
  private Weekday startDay;

  /**
   * The time (with startDay) that this opening begins
   */
  @NotNull
  @Column(name = "start_time")
  private LocalTime startTime;

  /**
   * The (relative) weekday upon which this opening ends
   */
  @NotNull
  @Column(name = "end_day")
  @Enumerated(EnumType.STRING)
  private Weekday endDay;

  /**
   * The time (with endDay) that this opening ends
   */
  @NotNull
  @Column(name = "end_time")
  private LocalTime endTime;

  /**
   * Create a NormalOpening object
   *
   * @param id a {@link java.util.UUID UUID}
   * @param calendar a {@link org.folio.calendar.domain.entity.Calendar Calendar}
   * @param startDay a {@link org.folio.calendar.domain.dto.Weekday Weekday}
   * @param startTime a {@link java.time.LocalTime LocalTime}
   * @param endDay a {@link org.folio.calendar.domain.dto.Weekday Weekday}
   * @param endTime a {@link java.time.LocalTime LocalTime}
   */
  public NormalOpening(
    final UUID id,
    final Calendar calendar,
    final Weekday startDay,
    final LocalTime startTime,
    final Weekday endDay,
    final LocalTime endTime
  ) {
    this.id = id;
    this.calendar = calendar;
    this.startDay = startDay;
    this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
    this.endDay = endDay;
    this.endTime = endTime.truncatedTo(ChronoUnit.MINUTES);
  }

  /**
   * Clears all IDs from this object, to prepare it for insertion (as new IDs
   * will then be generated).
   */
  public void clearIds() {
    this.setId(null);
  }

  /**
   * Get the time (with startDay) that this opening begins
   *
   * @return a {@link java.time.LocalTime}, truncated to the minutes
   */
  public LocalTime getStartTime() {
    return this.startTime.truncatedTo(ChronoUnit.MINUTES);
  }

  /**
   * Get the time (with endDay) that this opening ends
   *
   * @return a {@link java.time.LocalTime}, truncated to the minutes
   */
  public LocalTime getEndTime() {
    return this.endTime.truncatedTo(ChronoUnit.MINUTES);
  }

  /**
   * Set the time (with startDay) that this opening begins
   *
   * @param startTime a {@link java.time.LocalTime} (will be truncated to the minutes)
   */
  public void setStartTime(final LocalTime startTime) {
    this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
  }

  /**
   * Set the time (with endDay) that this opening ends
   *
   * @param endTime a {@link java.time.LocalTime} (will be truncated to the minutes)
   */
  public void setEndTime(final LocalTime endTime) {
    this.endTime = endTime.truncatedTo(ChronoUnit.MINUTES);
  }

  public static class NormalOpeningBuilder {

    /**
     * The time (with startDay) that this opening begins
     *
     * @param startTime a {@link LocalTime} (will be truncated to the minutes)
     * @return {@code this}, for chaining
     */
    public NormalOpeningBuilder startTime(final LocalTime startTime) {
      this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
      return this;
    }

    /**
     * The time (with endDay) that this opening ends
     *
     * @param endTime a {@link LocalTime} (will be truncated to the minutes)
     * @return {@code this}, for chaining
     */
    public NormalOpeningBuilder endTime(final LocalTime endTime) {
      this.endTime = endTime.truncatedTo(ChronoUnit.MINUTES);
      return this;
    }
  }
}
