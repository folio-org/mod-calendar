package org.folio.rest.beans;

public class CalendarOpeningsRequestParameters {
  String servicePointId;
  String startDate;
  String endDate;
  int offset;
  int limit;
  String lang;
  boolean includeClosedDays;
  boolean actualOpenings;

  public CalendarOpeningsRequestParameters(String startDate, String endDate, int offset, int limit, String lang, boolean includeClosedDays, boolean actualOpenings) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.offset = offset;
    this.limit = limit;
    this.lang = lang;
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

  public String getLang() {
    return lang;
  }

  public boolean isIncludeClosedDays() {
    return includeClosedDays;
  }

  public boolean isActualOpenings() {
    return actualOpenings;
  }

}
