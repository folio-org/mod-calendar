package org.folio.rest.service;

import java.util.Date;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLConnection;

import org.folio.rest.beans.ActualOpeningHours;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Order;

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

  Future<Void> deleteActualOpeningHoursByOpeningsId(AsyncResult<SQLConnection> conn, String openingsId);

  enum SearchDirection {

    PREVIOUS_DAY(Order.DESC, Criteria.OP_LESS_THAN),
    NEXT_DAY(Order.ASC, Criteria.OP_GREATER_THAN);

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
