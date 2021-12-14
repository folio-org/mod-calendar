package org.folio.calendar.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.domain.dto.OpeningDayRelativeWeekdays;
import org.folio.calendar.domain.dto.OpeningHourRange;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.dto.Weekday;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.entity.ServicePointCalendarAssignment;

@UtilityClass
public class PeriodUtils {

  /**
   * Convert period openings to normalized openings, consolidating as necessary
   * @param openings
   * @param calendarId
   * @return
   */
  @SuppressWarnings("java:S135")
  public static List<NormalOpening> convertOpeningDayRelativeToNormalOpening(
    Iterable<OpeningDayRelative> openings,
    UUID calendarId
  ) {
    List<NormalOpening> normalizedOpenings = new ArrayList<>();

    NormalOpening.NormalOpeningBuilder builder = NormalOpening.builder().calendarId(calendarId);

    for (OpeningDayRelative opening : openings) {
      OpeningDayInfo openingInfo = opening.getOpeningDay();

      builder =
        builder.startDay(opening.getWeekdays().getDay()).endDay(opening.getWeekdays().getDay());

      // we do not create NormalOpenings for closures
      if (!openingInfo.isOpen()) {
        continue;
      }

      if (openingInfo.isAllDay()) {
        normalizedOpenings.add(
          builder.startTime(TimeConstants.TIME_MIN).endTime(TimeConstants.TIME_MAX).build()
        );
        continue;
      }

      for (OpeningHourRange hourRange : openingInfo.getOpeningHour()) {
        // legacy version preserves error states; we will discard them silently
        // hourRange.getStartTime() is after hourRange.getEndTime()
        if (hourRange.getStartTime().compareTo(hourRange.getEndTime()) > 0) {
          continue;
        }

        normalizedOpenings.add(
          builder
            .startTime(DateUtils.fromTimeString(hourRange.getStartTime()))
            .endTime(DateUtils.fromTimeString(hourRange.getEndTime()))
            .build()
        );
      }
    }

    return consolidateNormalOpenings(normalizedOpenings);
  }

  /**
   * Consolidate adjacent normal openings into one cohesive list.
   * The list MUST BE sorted.  Small overlaps will be handled, however, they must not span multiple weekdays.
   * @param normalOpenings list of sorted and canonicalized openings to consolidate
   * @return consolidated openings
   */
  private List<NormalOpening> consolidateNormalOpenings(List<NormalOpening> normalOpenings) {
    // consolidation
    for (int i = normalOpenings.size() - 1; i >= 0; i--) {
      // no more to consolidate
      if (i == 0 && normalOpenings.size() == 1) {
        break;
      }

      NormalOpening former;
      NormalOpening latter;

      if (i == 0) {
        former = normalOpenings.get(normalOpenings.size() - 1);
        latter = normalOpenings.get(i);
      } else {
        former = normalOpenings.get(i - 1);
        latter = normalOpenings.get(i);
      }

      // merge if touching each other, including across days
      if (NormalOpening.adjacent(former, latter)) {
        if (i == 0) {
          normalOpenings.set(normalOpenings.size() - 1, NormalOpening.merge(former, latter));
          normalOpenings.remove(0);
        } else {
          normalOpenings.set(i - 1, NormalOpening.merge(former, latter));
          normalOpenings.remove(i);
        }
      }
    }

    return normalOpenings;
  }

  /**
   * Convert a modern calendar to a legacy period object
   * @param calendar
   * @return
   */
  public Period toPeriod(Calendar calendar) {
    Period.PeriodBuilder builder = Period
      .builder()
      .id(calendar.getId())
      .name(calendar.getName())
      .startDate(calendar.getStartDate())
      .endDate(calendar.getEndDate());

    // passing ServicePointCalendarAssignment[] to toArray causes cast
    ServicePointCalendarAssignment[] servicePoints = calendar
      .getServicePoints()
      .toArray(new ServicePointCalendarAssignment[0]);

    if (servicePoints.length < 1) {
      throw new IllegalArgumentException(
        String.format(
          "Calendar %s must have at least one service point to be converted to a Period!",
          calendar.getId()
        )
      );
    } else if (servicePoints.length > 1) {
      throw new IllegalArgumentException(
        String.format(
          "Calendar %s must have only one service point to be converted to a Period!",
          calendar.getId()
        )
      );
    }
    builder = builder.servicePointId(servicePoints[0].getServicePointId());

    // convert contiguous NormalOpenings into single-weekday groups
    Map<Weekday, List<OpeningHourRange>> openings = new EnumMap<>(Weekday.class);

    for (NormalOpening opening : calendar.getNormalHours()) {
      Map<Weekday, OpeningHourRange> openingRanges = opening.splitIntoWeekdays();

      for (Map.Entry<Weekday, OpeningHourRange> range : openingRanges.entrySet()) {
        if (!openings.containsKey(range.getKey())) {
          openings.put(range.getKey(), new ArrayList<>());
        }
        openings.get(range.getKey()).add(range.getValue());
      }
    }

    // build NormalOpenings into OpeningDayRelative
    List<OpeningDayRelative> openingDays = new ArrayList<>();

    for (Map.Entry<Weekday, List<OpeningHourRange>> entry : openings.entrySet()) {
      OpeningDayInfo.OpeningDayInfoBuilder openingDayInfoBuilder = OpeningDayInfo
        .builder()
        .open(true)
        .allDay(false);

      if (entry.getValue().size() == 1 && entry.getValue().get(0).equals(TimeConstants.ALL_DAY)) {
        openingDayInfoBuilder = openingDayInfoBuilder.allDay(true);
      } else {
        openingDayInfoBuilder = openingDayInfoBuilder.openingHour(entry.getValue());
      }

      openingDays.add(
        OpeningDayRelative
          .builder()
          .weekdays(new OpeningDayRelativeWeekdays(entry.getKey()))
          .openingDay(openingDayInfoBuilder.build())
          .build()
      );
    }

    builder = builder.openingDays(openingDays);

    return builder.build();
  }
}
