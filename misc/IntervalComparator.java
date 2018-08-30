package org.folio.rest.impl;

import org.joda.time.Interval;

import java.util.Comparator;

/**
 * @author mtornai
 */
public class IntervalComparator implements Comparator<Interval> {
  @Override
  public int compare(Interval x, Interval y) {
    return x.getStart().compareTo(y.getStart());
  }
}

