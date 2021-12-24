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

  /**
   * A filter which will return {@link org.folio.calendar.domain.dto.Period Period}s with normal hours
   */
  public static final PeriodQueryFilter NORMAL_HOURS = new PeriodQueryFilter(
    FilterType.NORMAL_HOURS
  );
  /**
   * A filter which will return {@link org.folio.calendar.domain.dto.Period Period}s with exceptions
   */
  public static final PeriodQueryFilter EXCEPTIONS = new PeriodQueryFilter(FilterType.EXCEPTIONS);

  protected FilterType type;

  PeriodQueryFilter(FilterType type) {
    this.type = type;
  }

  /**
   * Check that a {@link org.folio.calendar.domain.entity.Calendar} passes this filter (to be later converted to a Period)
   *
   * @param calendar the Calendar to test
   * @return if it passes the filter
   */
  public boolean passes(Calendar calendar) {
    if (this.type == FilterType.NORMAL_HOURS) {
      return !calendar.getNormalHours().isEmpty();
    } else if (this.type == FilterType.EXCEPTIONS) {
      return !calendar.getExceptions().isEmpty();
    }
    throw new IllegalArgumentException("An invalid filter was applied");
  }
}
