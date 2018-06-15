package org.folio.rest.beans;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "servicePointId", "name", "startDate", "endDate" })
public class Openings {

  @JsonProperty("id")
  private String id;
  @JsonProperty("servicePointId")
  private String servicePointId;
  @JsonProperty("name")
  private String name;
  @JsonProperty("startDate")
  private Date startDate;
  @JsonProperty("endDate")
  private Date endDate;

  public Openings() {
  }

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
