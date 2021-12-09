package org.folio.calendar.domain.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "exception_hours")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionHour {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @Column(name = "exception_id")
  private UUID exceptionId;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(name = "open_start_time")
  @Nullable
  private LocalTime startTime;

  @Column(name = "open_end_time")
  @Nullable
  private LocalTime endTime;
}
