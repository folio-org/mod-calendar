package org.folio.calendar.domain.entity;

import java.time.LocalTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A normal opening for a service point
 */
@Data
@Table(name = "normal_hours")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalOpening {

  /**
   * The opening's internal ID
   */
  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  /**
   * The calendar that this opening is a part of
   */
  @Column(name = "calendar_id")
  private UUID calendarId;

  /**
   * The (relative) weekday upon which this opening starts
   */
  @Column(name = "start_day")
  @Enumerated(EnumType.STRING)
  private Weekday startDay;

  /**
   * The time (with startDay) that this opening begins
   */
  @Column(name = "start_time")
  private LocalTime startTime;

  /**
   * The (relative) weekday upon which this opening ends
   */
  @Column(name = "end_day")
  @Enumerated(EnumType.STRING)
  private Weekday endDay;

  /**
   * The time (with endDay) that this opening ends
   */
  @Column(name = "end_time")
  private LocalTime endTime;
}
