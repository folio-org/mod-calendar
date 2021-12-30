package org.folio.calendar.domain.entity;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.With;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.domain.dto.Weekday;
import org.folio.calendar.utils.DateUtils;
import org.folio.calendar.utils.PeriodUtils;
import org.folio.calendar.utils.WeekdayUtils;

/**
 * Calendar entity
 */
@Data
@With
@Table(name = "calendars")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calendar {

  /**
   * The UUID of the calendar
   */
  @Id
  @NotNull
  @Column(name = "id")
  private UUID id;

  /**
   * A human-readable name for the calendar
   */
  @NotNull
  @Column(name = "name")
  private String name;

  /**
   * The first effective day of the calendar
   */
  @NotNull
  @Column(name = "start_date")
  private LocalDate startDate;

  /**
   * The last effective day of the calendar
   */
  @NotNull
  @Column(name = "end_date")
  private LocalDate endDate;

  /**
   * All assigned service points
   */
  @Singular
  @OneToMany(
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    orphanRemoval = true,
    mappedBy = "calendar"
  )
  private Set<ServicePointCalendarAssignment> servicePoints;

  /**
   * Normal openings for the calendar
   */
  @Singular
  @OneToMany(
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    orphanRemoval = true,
    mappedBy = "calendar"
  )
  private Set<NormalOpening> normalHours;

  /**
   * Exceptional openings and closures
   */
  @Singular
  @OneToMany(
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    orphanRemoval = true,
    mappedBy = "calendar"
  )
  private Set<ExceptionRange> exceptions;

  @PrePersist
  @PreUpdate
  private void prePersist() {
    if (this.getServicePoints() != null) {
      this.getServicePoints().forEach(assignment -> assignment.setCalendar(this));
    }
    if (this.getNormalHours() != null) {
      this.getNormalHours().forEach(opening -> opening.setCalendar(this));
    }
    if (this.getExceptions() != null) {
      this.getExceptions().forEach(exception -> exception.setCalendar(this));
    }
  }

  /**
   * Get all of the dates spanned by this calendar as OpeningDayInfo objects representing normal openings
   * @param firstDate the first date to include, optional
   * @param lastDate the last date to include, optional
   * @return a map of date to OpeningDayInfo
   */
  public Map<LocalDate, OpeningDayInfo> getDailyNormalOpenings(
    LocalDate firstDate,
    LocalDate lastDate
  ) {
    Map<LocalDate, OpeningDayInfo> dateMap = new HashMap<>();

    List<OpeningDayRelative> openings = PeriodUtils.getOpeningDayRelativeFromNormalOpenings(
      this.getNormalHours()
    );
    Map<Weekday, OpeningDayInfo> openingsByWeekday = new EnumMap<>(Weekday.class);
    for (OpeningDayRelative opening : openings) {
      openingsByWeekday.put(opening.getWeekdays().getDay(), opening.getOpeningDay());
    }

    List<LocalDate> dates = DateUtils.getDateRange(
      DateUtils.max(this.getStartDate(), firstDate),
      DateUtils.min(this.getEndDate(), lastDate)
    );

    for (LocalDate date : dates) {
      OpeningDayInfo opening = openingsByWeekday.get(WeekdayUtils.toWeekday(date));
      if (opening != null) {
        dateMap.put(date, opening);
      }
    }

    return dateMap;
  }

  /**
   * Get all of the dates spanned by the exception (singular, must be legacy calendar) as OpeningDayInfo objects
   * @param firstDate the first date to include, optional
   * @param lastDate the last date to include, optional
   * @return a map of date to OpeningDayInfo
   */
  public Map<LocalDate, OpeningDayInfo> getDailyExceptionalOpenings(
    LocalDate firstDate,
    LocalDate lastDate
  ) {
    Map<LocalDate, OpeningDayInfo> dateMap = new HashMap<>();

    if (this.getExceptions().isEmpty()) {
      return dateMap;
    }

    OpeningDayInfo exception = PeriodUtils
      .getOpeningDayRelativeFromExceptionRanges(this.getExceptions())
      .getOpeningDay();

    List<LocalDate> dates = DateUtils.getDateRange(
      DateUtils.max(this.getStartDate(), firstDate),
      DateUtils.min(this.getEndDate(), lastDate)
    );

    for (LocalDate date : dates) {
      dateMap.put(date, exception);
    }

    return dateMap;
  }
}
