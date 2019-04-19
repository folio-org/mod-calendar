package org.folio.rest.beans;

public class CalendarOpeningsRequestParameters {

  private String startDate;
  private String endDate;
  private int offset;
  private int limit;
  private boolean includeClosedDays;
  private boolean actualOpenings;

  public CalendarOpeningsRequestParameters(String startDate, String endDate, int offset, int limit,
                                           boolean includeClosedDays, boolean actualOpenings) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.offset = offset;
    this.limit = limit;
    this.includeClosedDays = includeClosedDays;
    this.actualOpenings = actualOpenings;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }

  public boolean isIncludeClosedDays() {
    return includeClosedDays;
  }

  public boolean isActualOpenings() {
    return actualOpenings;
  }

}
