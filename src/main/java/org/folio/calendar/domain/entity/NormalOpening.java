package org.folio.calendar.domain.entity;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.folio.calendar.domain.dto.OpeningHourRange;
import org.folio.calendar.domain.dto.Weekday;
import org.folio.calendar.utils.DateUtils;
import org.folio.calendar.utils.TimeConstants;
import org.folio.calendar.utils.WeekdayUtils;

/**
 * A normal opening for a service point
 */
@Data
@With
@Table(name = "normal_hours")
@Entity
@Builder
@NoArgsConstructor
public class NormalOpening {

  /**
   * The opening's internal ID
   */
  @Id
  @GeneratedValue
  @NotNull
  @Column(name = "id")
  private UUID id;

  /**
   * The calendar that this opening is a part of
   */
  @NotNull
  @Column(name = "calendar_id")
  private UUID calendarId;

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
   * @param id
   * @param calendarId
   * @param startDay
   * @param startTime
   * @param endDay
   * @param endTime
   */
  public NormalOpening(
    final UUID id,
    final UUID calendarId,
    final Weekday startDay,
    final LocalTime startTime,
    final Weekday endDay,
    final LocalTime endTime
  ) {
    this.id = id;
    this.calendarId = calendarId;
    this.startDay = startDay;
    this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
    this.endDay = endDay;
    this.endTime = endTime.truncatedTo(ChronoUnit.MINUTES);
  }

  /**
   * Determine if two NormalOpenings are adjacent to each other.  Small overlaps less than one weekday will be considered adjacent.
   * @param opening1 The first opening to compare
   * @param opening2 The second opening to compare
   * @return if they are adjacent
   */
  public static boolean adjacent(final NormalOpening opening1, final NormalOpening opening2) {
    if (opening1.equals(opening2)) {
      return false;
    }

    NormalOpening former = opening1;
    NormalOpening latter = opening2;

    // account for small variations in overlap, < 1 weekday
    if (
      former.getEndDay() == latter.getStartDay() &&
      !latter.getStartTime().isAfter(former.getEndTime())
    ) {
      latter = latter.withStartTime(former.getEndTime().plusMinutes(1));
    }

    // former ends at midnight the day before latter
    if (
      former.getEndTime().equals(TimeConstants.TIME_MAX) &&
      latter.getStartTime().equals(TimeConstants.TIME_MIN) &&
      WeekdayUtils.next(former.getEndDay()).equals(latter.getStartDay())
    ) {
      return true;
    }

    // former ends one minute before latter, on the same day
    return (
      former.getEndDay() == latter.getStartDay() &&
      former.getEndTime().plusMinutes(1).equals(latter.getStartTime())
    );
  }

  /**
   * Merge two NormalOpenings into one range over both.  You most likely want to make sure they are {@link NormalOpening#adjacent} first.
   * @param opening1
   * @param opening2
   * @return a NormalOpening which surrounds opening1 and opening2
   */
  public static NormalOpening merge(final NormalOpening opening1, final NormalOpening opening2) {
    if (!opening1.getCalendarId().equals(opening2.getCalendarId())) {
      throw new IllegalArgumentException(
        "Cannot merge two NormalOpenings from different calendars!"
      );
    }

    return opening1.withEndDay(opening2.getEndDay()).withEndTime(opening2.getEndTime());
  }

  public Map<Weekday, OpeningHourRange> splitIntoWeekdays() {
    List<Weekday> weekdays = WeekdayUtils.getRange(this.getStartDay(), this.getEndDay());

    Map<Weekday, OpeningHourRange> map = new EnumMap<>(Weekday.class);

    for (Weekday day : weekdays) {
      OpeningHourRange.OpeningHourRangeBuilder builder = OpeningHourRange
        .builder()
        .startTime(TimeConstants.TIME_MIN_STRING)
        .endTime(TimeConstants.TIME_MAX_STRING);
      if (day == this.getStartDay()) {
        builder = builder.startTime(DateUtils.toTimeString(this.getStartTime()));
      }
      if (day == this.getEndDay()) {
        builder = builder.endTime(DateUtils.toTimeString(this.getEndTime()));
      }
      map.put(day, builder.build());
    }

    return map;
  }

  /**
   * The time (with startDay) that this opening begins
   */
  public LocalTime getStartTime() {
    return this.startTime.truncatedTo(ChronoUnit.MINUTES);
  }

  /**
   * The time (with endDay) that this opening ends
   */
  public LocalTime getEndTime() {
    return this.endTime.truncatedTo(ChronoUnit.MINUTES);
  }

  /**
   * The time (with startDay) that this opening begins
   */
  public void setStartTime(final LocalTime startTime) {
    this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
  }

  /**
   * The time (with endDay) that this opening ends
   */
  public void setEndTime(final LocalTime endTime) {
    this.endTime = endTime.truncatedTo(ChronoUnit.MINUTES);
  }

  public static class NormalOpeningBuilder {

    /**
     * The time (with startDay) that this opening begins
     * @return {@code this}.
     */
    public NormalOpening.NormalOpeningBuilder startTime(final LocalTime startTime) {
      this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
      return this;
    }

    /**
     * The time (with endDay) that this opening ends
     * @return {@code this}.
     */
    public NormalOpening.NormalOpeningBuilder endTime(final LocalTime endTime) {
      this.endTime = endTime.truncatedTo(ChronoUnit.MINUTES);
      return this;
    }
  }
}
