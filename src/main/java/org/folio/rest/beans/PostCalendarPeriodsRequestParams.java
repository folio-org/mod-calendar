package org.folio.rest.beans;

import org.folio.rest.jaxrs.model.OpeningPeriod_;

public class PostCalendarPeriodsRequestParams {
  String lang;
  OpeningPeriod_ entity;
  boolean exceptional;
  Openings openingsTable;

  public PostCalendarPeriodsRequestParams() {
  }

  public PostCalendarPeriodsRequestParams(String lang, OpeningPeriod_ entity, boolean exceptional, Openings openingsTable) {
    this.lang = lang;
    this.entity = entity;
    this.exceptional = exceptional;
    this.openingsTable = openingsTable;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public OpeningPeriod_ getEntity() {
    return entity;
  }

  public void setEntity(OpeningPeriod_ entity) {
    this.entity = entity;
  }

  public boolean isExceptional() {
    return exceptional;
  }

  public void setExceptional(boolean exceptional) {
    this.exceptional = exceptional;
  }

  public Openings getOpeningsTable() {
    return openingsTable;
  }

  public void setOpeningsTable(Openings openingsTable) {
    this.openingsTable = openingsTable;
  }

}
