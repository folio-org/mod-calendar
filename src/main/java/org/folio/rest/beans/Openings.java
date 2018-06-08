package org.folio.rest.beans;

import java.util.Date;

public class Openings {
  private String id;
  private String servicePointId;
  private String name;
  private Date startDate;
  private Date endDate;

  public Openings(String id, String servicePointId, String name, Date startDate, Date endDate) {
    this.id = id;
    this.servicePointId = servicePointId;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getServicePointId() {
    return servicePointId;
  }

  public void setServicePointId(String servicePointId) {
    this.servicePointId = servicePointId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
}
