package org.folio.calendar.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.domain.dto.OpeningHourRange;
import org.folio.calendar.domain.entity.NormalOpening;

@UtilityClass
public class PeriodUtils {

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
          builder.startTime(TimeConstants.DAY_MIN).endTime(TimeConstants.DAY_MAX).build()
        );
        continue;
      }

      for (OpeningHourRange hourRange : openingInfo.getOpeningHour()) {
        // legacy version preserves error states; we will discard them silently
        if (hourRange.getStartTime().isAfter(hourRange.getEndTime())) {
          continue;
        }

        normalizedOpenings.add(
          builder.startTime(hourRange.getStartTime()).endTime(hourRange.getEndTime()).build()
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
}
