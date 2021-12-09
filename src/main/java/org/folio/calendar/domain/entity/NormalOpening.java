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

@Data
@Table(name = "normal_hours")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalOpening {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @Column(name = "calendar_id")
  private UUID calendarId;

  @Column(name = "start_day")
  @Enumerated(EnumType.STRING)
  private Weekday startDay;

  @Column(name = "start_time")
  private LocalTime startTime;

  @Column(name = "end_day")
  @Enumerated(EnumType.STRING)
  private Weekday endDay;

  @Column(name = "end_time")
  private LocalTime endTime;
}
