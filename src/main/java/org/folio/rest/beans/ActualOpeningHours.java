package org.folio.rest.beans;

import java.io.Serializable;
import java.util.Date;

public class ActualOpeningHours implements Serializable {

  private String id;
  private String openingId;
  private Date actualDay;
  private String startTime;
  private String endTime;
  private Boolean allDay;
  private Boolean open;

  public ActualOpeningHours() {
  }

  public ActualOpeningHours(String id, String openingId, Date actualDay, String startTime, String endTime, Boolean allDay, Boolean open) {
    this.id = id;
    this.openingId = openingId;
    this.actualDay = actualDay;
    this.startTime = startTime;
    this.endTime = endTime;
    this.allDay = allDay;
    this.open = open;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOpeningId() {
    return openingId;
  }

  public void setOpeningId(String openingId) {
    this.openingId = openingId;
  }

  public Date getActualDay() {
    return actualDay;
  }

  public void setActualDay(Date actualDay) {
    this.actualDay = actualDay;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public Boolean getAllDay() {
    return allDay;
  }

  public void setAllDay(Boolean allDay) {
    this.allDay = allDay;
  }

  public Boolean getOpen() {
    return open;
  }

  public void setOpen(Boolean open) {
    this.open = open;
  }
}
