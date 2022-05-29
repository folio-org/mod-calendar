package org.folio.calendar.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Parameters {
  CALENDAR("calendar"),
  NAME("name"),
  START_DATE("startDate"),
  END_DATE("endDate"),
  ASSIGNMENTS("assignments"),
  NORMAL_HOURS("normalHours"),
  EXCEPTIONS("exceptions");

  private String value;

  @Override
  public String toString() {
    return this.getValue();
  }
}
