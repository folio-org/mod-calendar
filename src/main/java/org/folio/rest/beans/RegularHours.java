package org.folio.rest.beans;

import org.folio.rest.jaxrs.model.OpeningDayWeekDay;

import java.util.List;
import java.util.UUID;

public class RegularHours {


  private String id;
  private String openingId;
  private List<OpeningDayWeekDay> openingDays;

  public RegularHours() {
  }

  public RegularHours(String id, String openingId, List<OpeningDayWeekDay> openingDays) {
    this.id = id;
    this.openingId = openingId;
    this.openingDays = openingDays;
  }

  public RegularHours(String openingId, List<OpeningDayWeekDay> openingDays) {
    this(UUID.randomUUID().toString(), openingId, openingDays);
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

  public List<OpeningDayWeekDay> getOpeningDays() {
    return openingDays;
  }

  public void setOpeningDays(List<OpeningDayWeekDay> openingDays) {
    this.openingDays = openingDays;
  }
}
