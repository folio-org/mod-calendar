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

@Data
@Table(name = "calendars")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calendar {

  @Id
  @NotNull
  @Column(name = "id")
  private UUID id;

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "start_date")
  private LocalDate startDate;

  @NotNull
  @Column(name = "end_date")
  private LocalDate endDate;

  @Singular
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "calendar_id")
  private Set<ServicePointCalendarAssignment> servicePoints;

  @Singular
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "calendar_id")
  private Set<NormalOpening> normalHours;

  @Singular
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "calendar_id")
  private Set<ExceptionRange> exceptions;
}
