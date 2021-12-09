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

@Data
@Table(name = "calendars")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calendar {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @Column(name = "name")
  private String name;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "calendar_id")
  private Set<ServicePointCalendarRelationship> servicePoints;
}
