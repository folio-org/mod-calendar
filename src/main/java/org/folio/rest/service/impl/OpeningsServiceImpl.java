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
import io.vertx.core.Promise;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

import org.folio.rest.exceptions.OverlapIntervalException;
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

    Promise<String> promise = Promise.promise();
    pgClient.save(conn, OPENINGS, openings.getId(), openings, promise);

    return promise.future().map(s -> null);
  }

  @Override
  public Future<List<Openings>> findOpeningsById(AsyncResult<SQLConnection> conn, String openingId) {

    Criteria criteria = new Criteria()
      .addField(ID_FIELD)
      .setOperation("=")
      .setVal(openingId);

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
        .setVal(servicePointId);

      criterion = criterion.addCriterion(criteria);
    }

    Promise<Results<Openings>> promise = Promise.promise();;
    pgClient.get(conn, OPENINGS, Openings.class, criterion, false, false, promise);

    return promise.future().map(Results::getResults);
  }

  @Override
  public Future<Void> updateOpenings(AsyncResult<SQLConnection> conn, Openings openings) {

    Promise<UpdateResult> promise = Promise.promise();;
    String where = String.format("WHERE jsonb->>'id' = '%s'", openings.getId());
    pgClient.update(conn, OPENINGS, openings, "jsonb", where, false, promise);

    return promise.future().map(ur -> null);
  }

  @Override
  public Future<Void> deleteOpeningsById(AsyncResult<SQLConnection> conn, String openingsId) {

    Criteria criteria = new Criteria()
      .addField(ID_FIELD)
      .setOperation("=")
      .setVal(openingsId);

    Promise<UpdateResult> promise = Promise.promise();
    pgClient.delete(conn, OPENINGS, new Criterion(criteria), promise);

    return promise.future().map(UpdateResult::getUpdated)
      .compose(updated -> updated == 0 ?
        failedFuture(new NotFoundException(format("Openings with id '%s' is not found", openingsId))) :
        succeededFuture());
  }

  @Override
  public Future<Void> checkOpeningsForOverlap(AsyncResult<SQLConnection> conn,
                                              Openings openings, boolean isUpdate) {

    Promise<Results<Openings>> promise = Promise.promise();
    Criterion criterion = isUpdate
      ? assembleCriterionToCheckOverlapExcludeOwnId(openings)
      : assembleCriterionForCheckingOverlap(openings);
    pgClient.get(conn, OPENINGS, Openings.class, criterion, false, false, promise);

    return promise.future().compose(get -> get.getResults().isEmpty()
      ? succeededFuture()
      : Future.failedFuture(new OverlapIntervalException(getErrorMessage(openings.getExceptional()))));
  }

  private Criterion assembleCriterionForCheckingOverlap(Openings openings) {

    Criteria critServicePoint = new Criteria()
      .addField(SERVICE_POINT_ID)
      .setOperation("=")
      .setVal(openings.getServicePointId());

    Criteria critExceptional = new Criteria()
      .addField(EXCEPTIONAL)
      .setOperation("=")
      .setVal(String.valueOf(openings.getExceptional()));

    Criteria critStartDate = new Criteria()
      .addField(START_DATE)
      .setOperation("<=")
      .setVal(DATE_FORMATTER.print(new DateTime(CalendarUtils.getDateWithoutHoursAndMinutes(openings.getEndDate()))));

    Criteria critEndDate = new Criteria()
      .addField(END_DATE)
      .setOperation(">=")
      .setVal(DATE_FORMATTER.print(new DateTime(CalendarUtils.getDateWithoutHoursAndMinutes(openings.getStartDate()))));


    return new Criterion()
      .addCriterion(critExceptional, "AND")
      .addCriterion(critServicePoint, "AND")
      .addCriterion(critStartDate, "AND", critEndDate);
  }

  private Criterion assembleCriterionToCheckOverlapExcludeOwnId(Openings openings) {

    Criteria critOpeningId = new Criteria()
      .addField(ID_FIELD)
      .setOperation("!=")
      .setVal(openings.getId());

    return assembleCriterionForCheckingOverlap(openings)
      .addCriterion(critOpeningId, "AND");
  }

  private String getErrorMessage(boolean isExceptional) {
    return isExceptional
      ? "Intervals can not overlap."
      : "The date range entered overlaps with another calendar for this service point. Please correct the date range or enter the hours as exceptions.";
  }
}
