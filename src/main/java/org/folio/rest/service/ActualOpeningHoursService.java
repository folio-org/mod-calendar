package org.folio.rest.service;

import io.vertx.core.Future;
import org.folio.rest.beans.ActualOpeningHours;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Order;

import java.util.List;

public interface ActualOpeningHoursService {

  Future<List<ActualOpeningHours>> findActualOpeningHoursForGivenDay(String tenantId,
                                                                     String servicePointId,
                                                                     String date);

  Future<List<ActualOpeningHours>> findActualOpeningHoursForClosestOpenDay(String tenantId,
                                                                           String servicePointId,
                                                                           String date,
                                                                           SearchDirection searchDirection);

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
