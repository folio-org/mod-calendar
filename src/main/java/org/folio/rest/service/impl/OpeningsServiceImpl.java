package org.folio.rest.service.impl;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.lang.String.format;

import static org.folio.rest.utils.CalendarConstants.END_DATE;
import static org.folio.rest.utils.CalendarConstants.EXCEPTIONAL;
import static org.folio.rest.utils.CalendarConstants.ID_FIELD;
import static org.folio.rest.utils.CalendarConstants.OPENINGS;
import static org.folio.rest.utils.CalendarConstants.SERVICE_POINT_ID;
import static org.folio.rest.utils.CalendarConstants.START_DATE;
import static org.folio.rest.utils.CalendarUtils.DATE_FORMATTER;

import java.util.List;

import javax.ws.rs.NotFoundException;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

import org.joda.time.DateTime;

import org.folio.rest.beans.Openings;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.interfaces.Results;
import org.folio.rest.service.OpeningsService;
import org.folio.rest.utils.CalendarUtils;

public class OpeningsServiceImpl implements OpeningsService {

  private PostgresClient pgClient;

  public OpeningsServiceImpl(PostgresClient pgClient) {
    this.pgClient = pgClient;
  }

  @Override
  public Future<Void> saveOpenings(AsyncResult<SQLConnection> conn, Openings openings) {

    Future<String> future = Future.future();
    pgClient.save(conn, OPENINGS, openings, future.completer());

    return future.map(s -> null);
  }

  @Override
  public Future<List<Openings>> findOpeningsById(AsyncResult<SQLConnection> conn, String openingId) {

    Criteria criteria = new Criteria()
      .addField(ID_FIELD)
      .setOperation("=")
      .setValue("'" + openingId + "'");

    Future<Results<Openings>> future = Future.future();

    pgClient.get(conn, OPENINGS, Openings.class, new Criterion(criteria), false, false, future.completer());

    return future.map(Results::getResults);
  }

  @Override
  public Future<List<Openings>> findOpeningsByServicePointId(AsyncResult<SQLConnection> conn, String servicePointId) {

    Criterion criterion = new Criterion();
    if (servicePointId != null) {
      Criteria criteria = new Criteria()
        .addField(SERVICE_POINT_ID)
        .setOperation("=")
        .setValue("'" + servicePointId + "'");

      criterion = criterion.addCriterion(criteria);
    }

    Future<Results<Openings>> future = Future.future();
    pgClient.get(conn, OPENINGS, Openings.class, criterion, false, false, future.completer());

    return future.map(Results::getResults);
  }

  @Override
  public Future<Void> updateOpenings(AsyncResult<SQLConnection> conn, Openings openings) {

    Future<UpdateResult> future = Future.future();
    String where = String.format("WHERE jsonb->>'id' = '%s'", openings.getId());
    pgClient.update(conn, OPENINGS, openings, "jsonb", where, false, future.completer());

    return future.map(ur -> null);
  }

  @Override
  public Future<Void> deleteOpeningsById(AsyncResult<SQLConnection> conn, String openingsId) {

    Criteria criteria = new Criteria()
      .addField(ID_FIELD)
      .setOperation("=")
      .setValue("'" + openingsId + "'");

    Future<UpdateResult> future = Future.future();
    pgClient.delete(conn, OPENINGS, new Criterion(criteria), future.completer());

    return future.map(UpdateResult::getUpdated)
      .compose(updated -> updated == 0 ?
        failedFuture(new NotFoundException(format("Openings with id '%s' is not found", openingsId))) :
        succeededFuture());
  }

  @Override
  public Future<Void> checkOpeningsForOverlap(AsyncResult<SQLConnection> conn, Openings openings) {

    Future<Results<Openings>> future = Future.future();
    Criterion crit = assembleCriterionForCheckingOverlap(openings);
    pgClient.get(conn, OPENINGS, Openings.class, crit, false, false, future.completer());

    return future.compose(get ->
      get.getResults().isEmpty() ? succeededFuture() : failedFuture("Intervals can not overlap."));
  }

  private Criterion assembleCriterionForCheckingOverlap(Openings openings) {
    Criteria critOpeningId = new Criteria()
      .addField(ID_FIELD)
      .setOperation("=")
      .setValue("'" + openings.getId() + "'");

    Criteria critServicePoint = new Criteria()
      .addField(SERVICE_POINT_ID)
      .setOperation("=")
      .setValue("'" + openings.getServicePointId() + "'");

    Criteria critExceptional = new Criteria()
      .addField(EXCEPTIONAL)
      .setOperation("=")
      .setValue("'" + openings.getExceptional() + "'");

    Criteria critStartDate = new Criteria()
      .addField(START_DATE)
      .setOperation("<=")
      .setValue(DATE_FORMATTER.print(new DateTime(CalendarUtils.getDateWithoutHoursAndMinutes(openings.getStartDate()))));

    Criteria critEndDate = new Criteria()
      .addField(END_DATE)
      .setOperation(">=")
      .setValue(DATE_FORMATTER.print(new DateTime(CalendarUtils.getDateWithoutHoursAndMinutes(openings.getEndDate()))));


    return new Criterion()
      .addCriterion(critExceptional, "AND")
      .addCriterion(critServicePoint, "AND")
      .addCriterion(critStartDate, "AND", critEndDate)
      .addCriterion(critOpeningId, "AND");
  }
}
