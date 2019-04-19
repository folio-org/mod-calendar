package org.folio.rest.service.impl;

import static org.folio.rest.persist.Criteria.Criteria.OP_EQUAL;
import static org.folio.rest.utils.CalendarConstants.ID_FIELD;
import static org.folio.rest.utils.CalendarConstants.OPENING_ID;
import static org.folio.rest.utils.CalendarConstants.REGULAR_HOURS;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

import org.folio.rest.beans.RegularHours;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.interfaces.Results;
import org.folio.rest.service.RegularHoursService;

public class RegularHoursServiceImpl implements RegularHoursService {

  private PostgresClient pgClient;

  public RegularHoursServiceImpl(PostgresClient pgClient) {
    this.pgClient = pgClient;
  }

  @Override
  public Future<Void> saveRegularHours(AsyncResult<SQLConnection> conn, RegularHours entity) {

    Future<String> future = Future.future();
    pgClient.save(conn, REGULAR_HOURS, entity, future.completer());

    return future.map(s -> null);
  }

  @Override
  public Future<Void> updateRegularHours(AsyncResult<SQLConnection> conn, RegularHours regularHours) {


    Future<UpdateResult> future = Future.future();
    String where = String.format("WHERE jsonb->>'openingId' = '%s'", regularHours.getOpeningId());
    pgClient.update(conn, REGULAR_HOURS, regularHours, "jsonb", where, false, future.completer());

    return future.map(ur -> null);
  }

  @Override
  public Future<List<RegularHours>> findRegularHoursByOpeningId(AsyncResult<SQLConnection> conn, String openingId) {

    Criteria criteria = new Criteria()
      .addField(OPENING_ID)
      .setOperation(OP_EQUAL)
      .setValue("'" + openingId + "'");

    Future<Results<RegularHours>> future = Future.future();
    pgClient.get(conn, REGULAR_HOURS, RegularHours.class, new Criterion(criteria), false, false, future.completer());

    return future.map(Results::getResults);
  }

  @Override
  public Future<Void> deleteRegularHoursByOpeningsId(AsyncResult<SQLConnection> conn, String openingsId) {

    Criteria criteria = new Criteria()
      .addField(ID_FIELD)
      .setOperation(OP_EQUAL)
      .setValue("'" + openingsId + "'");

    Future<UpdateResult> future = Future.future();
    pgClient.delete(conn, REGULAR_HOURS, new Criterion(criteria), future.completer());

    return future.map(ur -> null);
  }
}
