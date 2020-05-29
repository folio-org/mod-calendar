package org.folio.rest.service;

import java.util.Date;
import java.util.List;

import org.folio.rest.beans.ActualOpeningHours;
import org.folio.rest.persist.Criteria.Order;
import org.folio.rest.persist.SQLConnection;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;

public interface ActualOpeningHoursService {

  Future<List<ActualOpeningHours>> findActualOpeningHoursForGivenDay(String servicePointId,
                                                                     Date requestedDate,
                                                                     String tenantId);

  Future<List<ActualOpeningHours>> findActualOpeningHoursForClosestOpenDay(String servicePointId,
                                                                           Date requestedDate,
                                                                           SearchDirection searchDirection,
                                                                           String tenantId);

  Future<List<ActualOpeningHours>> findActualOpeningHoursByOpeningIdAndRange(AsyncResult<SQLConnection> conn,
                                                                             String openingId,
                                                                             String startDate,
                                                                             String endDate);

  Future<Void> saveActualOpeningHours(AsyncResult<SQLConnection> conn, List<Object> actualOpeningHours);

  Future<Void> deleteActualOpeningHoursByOpeningsId(AsyncResult<SQLConnection> conn, String openingsId);

  enum SearchDirection {

    PREVIOUS_DAY(Order.DESC, "<"),
    NEXT_DAY(Order.ASC, ">");

    private final String order;
    private final String operator;

    SearchDirection(String order, String operator) {
      this.order = order;
      this.operator = operator;
    }

    public String getOrder() {
      return order;
    }

    public String getOperator() {
      return operator;
    }
  }
}
