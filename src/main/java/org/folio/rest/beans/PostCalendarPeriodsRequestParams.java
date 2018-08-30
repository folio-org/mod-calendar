package org.folio.rest.beans;

import org.folio.rest.jaxrs.model.OpeningPeriod_;

public class PostCalendarPeriodsRequestParams {
  String lang;
  OpeningPeriod_ entity;
  boolean exceptional;
  Openings openingsTable;

  public PostCalendarPeriodsRequestParams(String lang, OpeningPeriod_ entity, boolean exceptional, Openings openingsTable) {
    this.lang = lang;
    this.entity = entity;
    this.exceptional = exceptional;
    this.openingsTable = openingsTable;
  }

  public String getLang() {
    return lang;
  }

  public OpeningPeriod_ getEntity() {
    return entity;
  }

  public boolean isExceptional() {
    return exceptional;
  }

  public Openings getOpeningsTable() {
    return openingsTable;
  }

}
