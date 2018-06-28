package org.folio.rest.beans;

public class CalendarOpeningsRequestParameters {
  String servicePointId;
  String startDate;
  String endDate;
  int offset;
  int limit;
  String lang;

  public CalendarOpeningsRequestParameters() {
  }

  public CalendarOpeningsRequestParameters(String servicePointId, String startDate, String endDate, int offset, int limit, String lang) {
    this.servicePointId = servicePointId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.offset = offset;
    this.limit = limit;
    this.lang = lang;
  }

  public String getServicePointId() {
    return servicePointId;
  }

  public void setServicePointId(String servicePointId) {
    this.servicePointId = servicePointId;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }
}
