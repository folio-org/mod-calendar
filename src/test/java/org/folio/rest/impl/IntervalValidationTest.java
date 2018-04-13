package org.folio.rest.impl;

import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class IntervalValidationTest {

  @Test
  public void testIntervalsNotOverlapping() {
    Interval interval = new Interval(1, 2);
    Interval interval2 = new Interval(2, 3);
    List<Interval> intervals = new ArrayList<>();
    intervals.add(interval);
    intervals.add(interval2);
    try {
       IntervalValidation.validate(intervals);
    } catch (CalendarIntervalException e) {
      Assert.fail(e.getMessage());
    }
  }

  @Test(expected = CalendarIntervalException.class)
  public void testIntervalsOverlapping1() throws CalendarIntervalException {
    Interval interval = new Interval(1, 3);
    Interval interval2 = new Interval(2, 4);
    List<Interval> intervals = new ArrayList<>();
    intervals.add(interval);
    intervals.add(interval2);
    IntervalValidation.validate(intervals);
  }

  @Test(expected = CalendarIntervalException.class)
  public void testIntervalsOverlapping2() throws CalendarIntervalException {
    Interval interval = new Interval(1, 5);
    Interval interval2 = new Interval(2, 4);
    List<Interval> intervals = new ArrayList<>();
    intervals.add(interval);
    intervals.add(interval2);
    IntervalValidation.validate(intervals);
  }

  @Test(expected = CalendarIntervalException.class)
  public void testIntervalsOverlapping3() throws CalendarIntervalException {
    Interval interval = new Interval(1, 3);
    Interval interval2 = new Interval(2, 5);
    List<Interval> intervals = new ArrayList<>();
    intervals.add(interval2);
    intervals.add(interval);
    IntervalValidation.validate(intervals);
  }
}
