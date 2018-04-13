package org.folio.rest.impl;

import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author mtornai
 */
public class IntervalValidation {

  private static final Logger log = LoggerFactory.getLogger(IntervalValidation.class);

  public  static void  validate(List<Interval> intervals) throws CalendarIntervalException {
    intervals.sort(new IntervalComparator());
    for (int i = 0; i < intervals.size() - 1; i++) {
      if (intervals.get(i).overlaps(intervals.get(i + 1))) {
        throw new CalendarIntervalException("Error selected times are overlapping");
      }
    }
  }
}
