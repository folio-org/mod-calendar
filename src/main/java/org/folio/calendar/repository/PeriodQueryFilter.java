package org.folio.calendar.repository;

import org.folio.calendar.domain.entity.Calendar;

/**
 * Filters for whether exceptions or normal hours should be returned in a period query
 */
public class PeriodQueryFilter {

  /**
   * Internal enum for filter types
   */
  protected enum FilterType {
    NORMAL_HOURS,
    EXCEPTIONS,
  }

  public static final PeriodQueryFilter NORMAL_HOURS = new PeriodQueryFilter(
    FilterType.NORMAL_HOURS
  );
  public static final PeriodQueryFilter EXCEPTIONS = new PeriodQueryFilter(FilterType.EXCEPTIONS);

  protected FilterType type;

  PeriodQueryFilter(FilterType type) {
    this.type = type;
  }

  public boolean passes(Calendar calendar) {
    if (this.type == FilterType.NORMAL_HOURS) {
      return !calendar.getNormalHours().isEmpty();
    } else if (this.type == FilterType.EXCEPTIONS) {
      return !calendar.getExceptions().isEmpty();
    }
    throw new IllegalArgumentException("An invalid filter was applied");
  }
}
