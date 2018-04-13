package org.folio.rest.impl;

import org.joda.time.Interval;

import java.util.List;

/**
 * @author mtornai
 */
public class IntervalValidation {

  private IntervalValidation() {
  }

  public  static void  validate(List<Interval> intervals) throws CalendarIntervalException {
    intervals.sort(new IntervalComparator());
    for (int i = 0; i < intervals.size() - 1; i++) {
      if (intervals.get(i).overlaps(intervals.get(i + 1))) {
        throw new CalendarIntervalException("Error selected times are overlapping");
      }
    }
  }
}
