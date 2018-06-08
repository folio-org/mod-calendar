package org.folio.rest.beans;

import org.folio.rest.jaxrs.model.OpeningDay_;

import java.util.List;

public class RegularHoursTable {


  private String id;
  private String openingId;
  private List<OpeningDay_> openingDays;

  public RegularHoursTable() {
  }

  public RegularHoursTable(String id, String openingId, List<OpeningDay_> openingDays) {
    this.id = id;
    this.openingId = openingId;
    this.openingDays = openingDays;
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

  public List<OpeningDay_> getOpeningDays() {
    return openingDays;
  }

  public void setOpeningDays(List<OpeningDay_> openingDays) {
    this.openingDays = openingDays;
  }
}
