package org.folio.rest.service.impl;

import static org.folio.rest.utils.CalendarConstants.ACTUAL_DAY;
import static org.folio.rest.utils.CalendarConstants.ACTUAL_OPENING_HOURS;
import static org.folio.rest.utils.CalendarConstants.OPENINGS;
import static org.folio.rest.utils.CalendarConstants.OPENING_ID;
import static org.folio.rest.utils.CalendarUtils.DATE_FORMATTER_SHORT;
import static org.folio.rest.utils.CalendarUtils.DATE_PATTERN;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.folio.rest.beans.ActualOpeningHours;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.SQLConnection;
import org.folio.rest.persist.interfaces.Results;
import org.folio.rest.service.ActualOpeningHoursService;
import org.joda.time.DateTime;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;

public class ActualOpeningHoursServiceImpl implements ActualOpeningHoursService {

  private PostgresClient pgClient;

  public ActualOpeningHoursServiceImpl(PostgresClient pgClient) {
    this.pgClient = pgClient;
  }

  @Override
  public Future<List<ActualOpeningHours>> findActualOpeningHoursForGivenDay(String servicePointId,
                                                                            Date requestedDate,
                                                                            String tenantId) {

    SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
    df.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

    String query = String.format(
      "SELECT aoh.jsonb FROM %1$s.%2$s aoh " +
      "JOIN %1$s.%3$s o ON aoh.jsonb->>'openingId' = o.jsonb->>'id' " +
      "WHERE o.jsonb->>'servicePointId' = '%4$s' " +
      "AND aoh.jsonb->>'actualDay' = '%5$s'",

      PostgresClient.convertToPsqlStandard(tenantId), ACTUAL_OPENING_HOURS, OPENINGS, servicePointId, df.format(requestedDate));

    Promise<RowSet<Row>> promise = Promise.promise();
    pgClient.select(query, promise);

    return promise.future().map(this::getActualOpeningHours);
  }

  @Override
  public Future<List<ActualOpeningHours>> findActualOpeningHoursForClosestOpenDay(String servicePointId,
                                                                                  Date requestedDate,
                                                                                  SearchDirection searchDirection,
                                                                                  String tenantId) {

    SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
    df.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

    //Following query extracts all ActualOpenHours for the closest open day (next or previous).
    //The day is considered as open when it contains at least one ActualOpenHours record and also
    //does not contain any ActualOpenHours with properties 'exceptional' = true and 'closed' = true
    //which means that service point is closed for the given day.
    String query = String.format(
      "WITH " +
      "openings_ids as (" +
        "SELECT jsonb->>'id' opening_id FROM %1$s.%2$s " +
        "WHERE jsonb->>'servicePointId' = '%4$s'" +
      ")," +
      "exceptional_closed_days as (" +
        "SELECT aoh.jsonb ->> 'actualDay' FROM %1$s.%3$s aoh " +
        "WHERE aoh.jsonb ->> 'openingId' IN (SELECT opening_id FROM openings_ids) " +
        "AND aoh.jsonb ->> 'exceptional' = 'true' " +
        "AND aoh.jsonb ->> 'open' = 'false'" +
      "), " +
      "closest_open_day as (" +
        "SELECT aoh1.jsonb->>'actualDay' actual_day FROM %1$s.%3$s aoh1 " +
        "WHERE aoh1.jsonb->>'openingId' IN (SELECT opening_id FROM openings_ids) " +
        "AND aoh1.jsonb->>'actualDay' %7$s '%5$s' " +
        "AND aoh1.jsonb->>'open' = 'true' " +
        "AND aoh1.jsonb ->> 'actualDay' NOT IN (SELECT * FROM exceptional_closed_days)" +
        "ORDER BY aoh1.jsonb->>'actualDay' %6$s LIMIT 1" +
      ")" +
      "SELECT jsonb FROM %1$s.%3$s " +
      "WHERE jsonb->>'openingId' IN (SELECT opening_id FROM openings_ids) " +
      "AND jsonb->>'actualDay' = (SELECT actual_day FROM closest_open_day)",

      PostgresClient.convertToPsqlStandard(tenantId), OPENINGS, ACTUAL_OPENING_HOURS, servicePointId,
      df.format(requestedDate), searchDirection.getOrder(), searchDirection.getOperator());

    Promise<RowSet<Row>> promise = Promise.promise();
    pgClient.select(query, promise);

    return promise.future().map(this::getActualOpeningHours);
  }

  @Override
  public Future<List<ActualOpeningHours>> findActualOpeningHoursByOpeningIdAndRange(AsyncResult<SQLConnection> conn,
                                                                                    String openingId,
                                                                                    String startDate,
                                                                                    String endDate) {

    Promise<Results<ActualOpeningHours>> promise = Promise.promise();
    Criterion criterion = assembleCriterionByRange(openingId, startDate, endDate);
    pgClient.get(conn, ACTUAL_OPENING_HOURS, ActualOpeningHours.class, criterion, false, false, promise);

    return promise.future().map(Results::getResults);
  }

  @Override
  public Future<Void> saveActualOpeningHours(AsyncResult<SQLConnection> conn, List<Object> actualOpeningHours) {

    Promise<RowSet<Row>> promise = Promise.promise();
    pgClient.saveBatch(conn, ACTUAL_OPENING_HOURS, actualOpeningHours, promise);

    return promise.future().map(rs -> null);
  }

  @Override
  public Future<Void> deleteActualOpeningHoursByOpeningsId(AsyncResult<SQLConnection> conn, String openingsId) {

    Criteria criteria = new Criteria()
      .addField(OPENING_ID)
      .setOperation("=")
      .setVal(openingsId);

    Promise<RowSet<Row>> promise = Promise.promise();
    pgClient.delete(conn, ACTUAL_OPENING_HOURS, new Criterion(criteria), promise);

    return promise.future().map(ur -> null);
  }

  private Criterion assembleCriterionByRange(String openingId, String startDate, String endDate) {
    Criteria critOpeningId = new Criteria()
      .addField(OPENING_ID)
      .setOperation("=")
      .setVal(openingId);

    Criteria critStartDate = new Criteria()
      .addField(ACTUAL_DAY)
      .setOperation(">=")
      .setVal(DATE_FORMATTER_SHORT.print(new DateTime(startDate)));

    Criteria critEndDate = new Criteria()
      .addField(ACTUAL_DAY)
      .setOperation("<=")
      .setVal(DATE_FORMATTER_SHORT.print(new DateTime(endDate)));

    Criterion criterionForOpeningHours = new Criterion()
      .addCriterion(critOpeningId, "AND");

    if (startDate != null) {
      criterionForOpeningHours.addCriterion(critStartDate, "AND");
    }
    if (endDate != null) {
      criterionForOpeningHours.addCriterion(critEndDate, "AND");
    }

    return criterionForOpeningHours;
  }

  private List<ActualOpeningHours> getActualOpeningHours(RowSet<Row> rowSet) {
    RowIterator<Row> iterator = rowSet.iterator();
    List<ActualOpeningHours> actualOpeningHours = new ArrayList<>();
    iterator.forEachRemaining(row -> actualOpeningHours.add(
      row.get(JsonObject.class, 0).mapTo(ActualOpeningHours.class)));

    return actualOpeningHours;
  }
}
