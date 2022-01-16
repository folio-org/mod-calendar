package org.folio.calendar.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.dto.PeriodCollection;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.repository.PeriodQueryFilter;

/**
 * Utilities for acting on {@link org.folio.calendar.domain.dto.PeriodCollection} objects
 */
@UtilityClass
@Log4j2
public class PeriodCollectionUtils {

  /**
   * Convert a collection of periods to a PeriodCollection encapsulating object
   *
   * @param periods The periods to convert
   * @return a {@link org.folio.calendar.domain.dto.PeriodCollection}
   */
  public static PeriodCollection toCollection(List<Period> periods) {
    return PeriodCollection.builder().openingPeriods(periods).totalRecords(periods.size()).build();
  }

  /**
   * Sift through of periods based on a filter (for exceptional/normal openings), optionally removing opening day information
   *
   * @param calendars the list of calendars to convert and filter
   * @param filter a {@link PeriodQueryFilter PeriodQueryFilter} denoting how to filter these results
   * @param withOpeningDays if {@link OpeningDayRelative OpeningDayRelative} information should be included
   * @return a {@link PeriodCollection PeriodCollection} of matching periods
   */
  public static PeriodCollection getPeriodsFromCalendarList(
    Iterable<Calendar> calendars,
    PeriodQueryFilter filter,
    boolean withOpeningDays
  ) {
    List<Period> periods = new ArrayList<>();

    for (Calendar calendar : calendars) {
      if (!filter.passes(calendar)) {
        continue;
      }

      try {
        Period period = PeriodUtils.toPeriod(calendar);

        if (!withOpeningDays) {
          period.setOpeningDays(new ArrayList<>());
        }

        periods.add(period);
      } catch (IllegalArgumentException e) {
        // this is a best effort legacy conversion
        // modern Calendars are not backwards compatible
        log.info("Discarding period due to IllegalArgumentException");
        log.info(e);
      }
    }

    return toCollection(periods);
  }
}
