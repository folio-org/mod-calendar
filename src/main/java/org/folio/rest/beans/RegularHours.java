package org.folio.rest.beans;

import org.folio.rest.jaxrs.model.OpeningDay;

import java.util.List;

public class RegularHours {


  private String id;
  private String openingId;
  private List<OpeningDay> openingDays;

  public RegularHours() {
  }

  public RegularHours(String id, String openingId, List<OpeningDay> openingDays) {
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

  public List<OpeningDay> getOpeningDays() {
    return openingDays;
  }

  public void setOpeningDays(List<OpeningDay> openingDays) {
    this.openingDays = openingDays;
  }
}
