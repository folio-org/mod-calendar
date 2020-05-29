package org.folio.rest.service.impl;

import static org.folio.rest.utils.CalendarConstants.ID_FIELD;
import static org.folio.rest.utils.CalendarConstants.OPENING_ID;
import static org.folio.rest.utils.CalendarConstants.REGULAR_HOURS;

import java.util.List;

import org.folio.rest.beans.RegularHours;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.SQLConnection;
import org.folio.rest.persist.interfaces.Results;
import org.folio.rest.service.RegularHoursService;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class RegularHoursServiceImpl implements RegularHoursService {

  private PostgresClient pgClient;

  public RegularHoursServiceImpl(PostgresClient pgClient) {
    this.pgClient = pgClient;
  }

  @Override
  public Future<Void> saveRegularHours(AsyncResult<SQLConnection> conn, RegularHours entity) {

    Promise<String> promise = Promise.promise();
    pgClient.save(conn, REGULAR_HOURS, entity.getId(), entity, promise);

    return promise.future().map(s -> null);
  }

  @Override
  public Future<Void> updateRegularHours(AsyncResult<SQLConnection> conn, RegularHours regularHours) {


    Promise<RowSet<Row>> promise = Promise.promise();
    String where = String.format("WHERE jsonb->>'openingId' = '%s'", regularHours.getOpeningId());
    pgClient.update(conn, REGULAR_HOURS, regularHours, "jsonb", where, false, promise);

    return promise.future().map(ur -> null);
  }

  @Override
  public Future<List<RegularHours>> findRegularHoursByOpeningId(AsyncResult<SQLConnection> conn, String openingId) {

    Criteria criteria = new Criteria()
      .addField(OPENING_ID)
      .setOperation("=")
      .setVal(openingId);

    Promise<Results<RegularHours>> promise = Promise.promise();
    pgClient.get(conn, REGULAR_HOURS, RegularHours.class, new Criterion(criteria), false, false, promise);

    return promise.future().map(Results::getResults);
  }

  @Override
  public Future<Void> deleteRegularHoursByOpeningsId(AsyncResult<SQLConnection> conn, String openingsId) {

    Criteria criteria = new Criteria()
      .addField(ID_FIELD)
      .setOperation("=")
      .setVal(openingsId);

    Promise<RowSet<Row>> promise = Promise.promise();
    pgClient.delete(conn, REGULAR_HOURS, new Criterion(criteria), promise);

    return promise.future().map(ur -> null);
  }
}
