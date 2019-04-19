package org.folio.rest.service.impl;

import static org.folio.rest.utils.CalendarConstants.ACTUAL_OPENING_HOURS;
import static org.folio.rest.utils.CalendarConstants.OPENINGS;
import static org.folio.rest.utils.CalendarUtils.DATE_PATTERN;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import org.folio.rest.beans.ActualOpeningHours;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.service.ActualOpeningHoursService;

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

    Future<ResultSet> future = Future.future();
    pgClient.select(query, future.completer());

    return future.map(rs -> rs.getResults()
      .stream()
      .map(objects -> new JsonObject(objects.getString(0)).mapTo(ActualOpeningHours.class))
      .collect(Collectors.toList()));
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
      "closest_open_day as (" +
        "SELECT aoh1.jsonb->>'actualDay' actual_day FROM %1$s.%3$s aoh1 " +
        "WHERE aoh1.jsonb->>'openingId' IN (SELECT opening_id FROM openings_ids) " +
        "AND aoh1.jsonb->>'actualDay' %7$s '%5$s' " +
        "AND aoh1.jsonb->>'open' = 'true' " +
        "AND (" +
          "SELECT count(_id) FROM %1$s.%3$s aoh2 " +
          "WHERE aoh2.jsonb->>'openingId' IN (SELECT opening_id FROM openings_ids) " +
          "AND aoh2.jsonb->>'actualDay' = aoh1.jsonb->>'actualDay' " +
          "AND aoh2.jsonb->>'exceptional' = 'true' " +
          "AND aoh2.jsonb->>'open' = 'false'" +
        ") = 0 " +
        "ORDER BY aoh1.jsonb->>'actualDay' %6$s LIMIT 1" +
      ")" +
      "SELECT jsonb FROM %1$s.%3$s " +
      "WHERE jsonb->>'openingId' IN (SELECT opening_id FROM openings_ids) " +
      "AND jsonb->>'actualDay' = (SELECT actual_day FROM closest_open_day)",

      PostgresClient.convertToPsqlStandard(tenantId), OPENINGS, ACTUAL_OPENING_HOURS, servicePointId,
      df.format(requestedDate), searchDirection.getOrder(), searchDirection.getOperator());

    Future<ResultSet> future = Future.future();
    pgClient.select(query, future.completer());

    return future.map(rs -> rs.getResults()
      .stream()
      .map(objects -> new JsonObject(objects.getString(0)).mapTo(ActualOpeningHours.class))
      .collect(Collectors.toList()));
  }
}
