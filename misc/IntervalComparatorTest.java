package org.folio.rest.impl;

import org.joda.time.Interval;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntervalComparatorTest {
  private static final IntervalComparator intervalComparator = new IntervalComparator();

  @Test
  public void testEqual() {
    Interval interval = new Interval(1000, 2000);
    Interval interval2 = new Interval(1000, 2000);
    int result = intervalComparator.compare(interval, interval2);
    assertEquals("expected to be equal", 0, result);
  }

  @Test
  public void testGreaterThan() {
    Interval interval = new Interval(1200, 2000);
    Interval interval2 = new Interval(1000, 2000);
    int result = intervalComparator.compare(interval, interval2);
    assertTrue("expected to greater than", result >= 1);
  }

  @Test
  public void testLessThan() {
    Interval interval = new Interval(1000, 2000);
    Interval interval2 = new Interval(1120, 2000);
    int result = intervalComparator.compare(interval, interval2);
    assertTrue("expected to be less than", result <= -1);
  }
}
