package org.folio.rest.beans;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
  @JsonProperty("exceptional")
  private Boolean exceptional;

  public Openings() {
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

  public Boolean getExceptional() {
    return exceptional;
  }

  public void setExceptional(Boolean exceptional) {
    this.exceptional = exceptional;
  }

}
