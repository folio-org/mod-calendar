package org.folio.rest.impl;

import io.vertx.core.*;
import org.folio.rest.beans.Openings;
import org.folio.rest.beans.RegularHours;
import org.folio.rest.jaxrs.model.*;
import org.folio.rest.jaxrs.resource.CalendarResource;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.cql.CQLWrapper;
import org.folio.rest.persist.interfaces.Results;
import org.folio.rest.tools.utils.TenantTool;
import org.folio.rest.utils.CalendarUtils;
import org.joda.time.DateTime;
import org.z3950.zing.cql.cql2pgjson.CQL2PgJSON;
import org.z3950.zing.cql.cql2pgjson.FieldException;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.utils.CalendarConstants.*;


public class CalendarAPI implements CalendarResource {

  public CalendarAPI() {
    //stub
  }

  @Override
  public void postCalendarOpeningsByServicePointIdRegular(String servicePointId, String language, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    Openings openingsTable = new Openings(entity.getId(), entity.getServicePointId(), entity.getName(), entity.getStartDate(), entity.getEndDate());
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
    vertxContext.runOnContext(v ->
      postgresClient.startTx(beginTx ->
        postgresClient.save(beginTx, OPENINGS, openingsTable, replyOfSavingOpenings -> {
          if (replyOfSavingOpenings.succeeded()) {
            RegularHours regularHours = new RegularHours(replyOfSavingOpenings.result(), openingsTable.getId(), entity.getOpeningDays());
            postgresClient.save(REGULAR_HOURS, regularHours, replyOfSavingRegularHours -> {
              if (replyOfSavingRegularHours.succeeded()) {
                saveActualOpeningHours(entity, language, asyncResultHandler, postgresClient, beginTx);
              } else {
                postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PostCalendarOpeningsByServicePointIdRegularResponse.withPlainInternalServerError(entity.toString()))));
              }
            });
          } else {
            postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PostCalendarOpeningsByServicePointIdRegularResponse.withPlainInternalServerError(entity.toString()))));
          }
        })
      )
    );
  }

  private void saveActualOpeningHours(OpeningPeriod_ entity, String language, Handler<AsyncResult<Response>> asyncResultHandler, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Object> actualOpeningHours = CalendarUtils.separateEvents(entity);
    postgresClient.saveBatch(ACTUAL_OPENING_HOURS, actualOpeningHours, replyOfSavingActualOpeningHours -> {
      if (replyOfSavingActualOpeningHours.succeeded()) {
        postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PostCalendarOpeningsByServicePointIdRegularResponse.withJsonCreated(language, entity))));
      } else {
        postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PostCalendarOpeningsByServicePointIdRegularResponse.withPlainInternalServerError(entity.toString()))));
      }
    });
  }

  @Override
  public void deleteCalendarOpeningsByServicePointIdRegularByOpeningId(String openingId, String servicePointId, String language, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);

    Criterion criterionForOpeningHours = new Criterion(new Criteria().addField(OPENING_ID).setJSONB(true).setOperation("=").setValue("'" + openingId + "'"));
    Criterion criterionForOpenings = new Criterion(new Criteria().addField("'id'").setJSONB(true).setOperation("=").setValue("'" + openingId + "'"));

    vertxContext.runOnContext(v ->
      postgresClient.startTx(beginTx ->
        postgresClient.delete(beginTx, ACTUAL_OPENING_HOURS, criterionForOpeningHours, replyOfDeletingActualOpeningHours -> {
          if (replyOfDeletingActualOpeningHours.succeeded()) {
            postgresClient.delete(beginTx, REGULAR_HOURS, criterionForOpeningHours, replyOfDeletingRegularHours -> {
              if (replyOfDeletingRegularHours.succeeded()) {
                deleteOpenings(asyncResultHandler, postgresClient, criterionForOpenings, openingId, beginTx);
              } else {
                postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withPlainInternalServerError(openingId))));
              }
            });
          } else {
            postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withPlainInternalServerError(openingId))));
          }
        })
      )
    );
  }

  private void deleteOpenings(Handler<AsyncResult<Response>> asyncResultHandler, PostgresClient postgresClient, Criterion criterionForOpenings, String openingId, AsyncResult<Object> beginTx) {
    postgresClient.delete(beginTx, OPENINGS, criterionForOpenings, replyOfDeletingOpenings -> {
      if (replyOfDeletingOpenings.succeeded()) {
        postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withNoContent())));
      } else {
        postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withPlainInternalServerError(openingId))));
      }
    });
  }


  @Override
  public void putCalendarOpeningsByServicePointIdRegularByOpeningId(String openingId, String servicePointId, String language, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    Openings openingsTable = new Openings(entity.getId(), entity.getServicePointId(), entity.getName(), entity.getStartDate(), entity.getEndDate());
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);

    Criterion criterionForOpeningHours = new Criterion(new Criteria().addField(OPENING_ID).setJSONB(true).setOperation("=").setValue("'" + openingId + "'"));
    Criterion criterionForOpenings = new Criterion(new Criteria().addField("'id'").setJSONB(true).setOperation("=").setValue("'" + openingId + "'"));

    vertxContext.runOnContext(v ->
      postgresClient.startTx(beginTx ->
        postgresClient.update(OPENINGS, openingsTable, criterionForOpenings, true, replyOfSavingOpenings -> {
          if (replyOfSavingOpenings.succeeded()) {
            RegularHours regularHours = new RegularHours(openingId, openingsTable.getId(), entity.getOpeningDays());
            postgresClient.update(REGULAR_HOURS, regularHours, criterionForOpeningHours, true, replyOfSavingRegularHours -> {
              if (replyOfSavingRegularHours.succeeded()) {
                putActualOpeningHours(entity, criterionForOpeningHours, asyncResultHandler, postgresClient, beginTx);
              } else {
                postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withPlainInternalServerError(entity.toString()))));
              }
            });
          } else {
            postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withPlainInternalServerError(entity.toString()))));
          }
        })
      )
    );
  }

  private void putActualOpeningHours(OpeningPeriod_ entity, Criterion openingId, Handler<AsyncResult<Response>> asyncResultHandler, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Object> actualOpeningHours = CalendarUtils.separateEvents(entity);
    postgresClient.delete(beginTx, ACTUAL_OPENING_HOURS, openingId, replyOfDeletingActualOpeningHours -> {
      if (replyOfDeletingActualOpeningHours.succeeded()) {
        postgresClient.saveBatch(ACTUAL_OPENING_HOURS, actualOpeningHours, replyOfSavingActualOpeningHours -> {
          if (replyOfSavingActualOpeningHours.succeeded()) {
            postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withJsonNoContent(entity))));
          } else {
            postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withPlainInternalServerError(entity.toString()))));
          }
        });
      } else {
        postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withPlainInternalServerError(entity.toString()))));
      }
    });
  }

  @Override
  public void getCalendarOpeningsByServicePointIdRegularByOpeningId(String openingId, String servicePointId, String language, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    OpeningCollection openingCollection = new OpeningCollection();
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    CQLWrapper cql = new CQLWrapper();
    try {
      cql.setField(new CQL2PgJSON(OPENINGS + ".jsonb"));
      cql.setQuery(ID_FIELD + "==" + openingId);
    } catch (FieldException e) {
      asyncResultHandler.handle(Future.succeededFuture(GetCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withPlainInternalServerError(openingCollection.toString())));
    }
    vertxContext.runOnContext(a ->
      postgresClient.startTx(beginTx ->
        postgresClient.get(OPENINGS, Openings.class, cql, true, false, resultOfSelectOpenings -> {
          if (resultOfSelectOpenings.succeeded()) {
            addOpeningPeriodsToCollection(openingCollection, resultOfSelectOpenings);
            getOpeningDaysByOpeningIdFuture(asyncResultHandler, openingCollection, postgresClient, beginTx);
          } else {
            postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withPlainInternalServerError(openingCollection.toString()))));
          }
        })));
  }

  @Override
  public void getCalendarOpeningsByServicePointIdRegular(String servicePointId, Boolean withOpeningDays, Boolean showPast, String language, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    OpeningCollection openingCollection = new OpeningCollection();
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    Criterion criterionForOpeningHours = assembleCriterion(servicePointId, showPast);

    vertxContext.runOnContext(a ->
      postgresClient.startTx(beginTx ->
        postgresClient.get(beginTx, OPENINGS, Openings.class, criterionForOpeningHours, true, false, resultOfSelectOpenings -> {
          if (resultOfSelectOpenings.succeeded()) {
            addOpeningPeriodsToCollection(openingCollection, resultOfSelectOpenings);
            if (withOpeningDays) {
              getOpeningDaysByServicePointIdFuture(asyncResultHandler, openingCollection, postgresClient, beginTx);
            } else {
              postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarOpeningsByServicePointIdRegularResponse.withJsonOK(openingCollection))));
            }
          } else {
            postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarOpeningsByServicePointIdRegularResponse.withPlainInternalServerError(openingCollection.toString()))));
          }
        })));
  }

  private void getOpeningDaysByServicePointIdFuture(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Future> futures = getOpeningDays(asyncResultHandler, openingCollection, postgresClient, beginTx);

    CompositeFuture.all(futures).setHandler(querys -> {
      if (querys.succeeded()) {
        postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarOpeningsByServicePointIdRegularResponse.withJsonOK(openingCollection))));
      } else {
        postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarOpeningsByServicePointIdRegularResponse.withPlainInternalServerError(openingCollection.toString()))));
      }
    });
  }

  private void getOpeningDaysByOpeningIdFuture(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Future> futures = getOpeningDays(asyncResultHandler, openingCollection, postgresClient, beginTx);

    CompositeFuture.all(futures).setHandler(querys -> {
      if (querys.succeeded()) {
        postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withJsonOK(openingCollection.getOpeningPeriods().get(0)))));
      } else {
        postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withPlainNotFound(openingCollection.getOpeningPeriods().get(0).toString()))));
      }
    });
  }

  private Criterion assembleCriterion(String servicePointId, Boolean showPast) {
    Criteria critServicePoint = new Criteria().addField(SERVICE_POINT_ID).setJSONB(true).setOperation("=").setValue("'" + servicePointId + "'");
    Criteria critShowPast = new Criteria();
    critShowPast.addField(END_DATE);
    critShowPast.setOperation(Criteria.OP_GREATER_THAN_EQ);
    critShowPast.setValue(CalendarUtils.DATE_FORMATTER.print(new DateTime()));

    Criterion criterionForOpeningHours = new Criterion();
    if (!showPast) {
      criterionForOpeningHours.addCriterion(critServicePoint, Criteria.OP_AND, critShowPast);
    } else {
      criterionForOpeningHours.addCriterion(critServicePoint, Criteria.OP_AND);
    }
    return criterionForOpeningHours;
  }

  private void addOpeningPeriodsToCollection(OpeningCollection openingCollection, AsyncResult<Results> resultOfSelectOpenings) {
    openingCollection.setTotalRecords(resultOfSelectOpenings.result().getResults().size());
    for (Object object : resultOfSelectOpenings.result().getResults()) {
      if (object instanceof Openings) {
        Openings openings = (Openings) object;
        OpeningPeriod_ openingPeriod = new OpeningPeriod_();
        openingPeriod.setStartDate(openings.getStartDate());
        openingPeriod.setEndDate(openings.getEndDate());
        openingPeriod.setName(openings.getName());
        openingPeriod.setServicePointId(openings.getServicePointId());
        openingPeriod.setId(openings.getId());
        openingCollection.getOpeningPeriods().add(openingPeriod);
      }
    }
  }

  private List<Future> getOpeningDays(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Future> futures = new ArrayList<>();
    for (OpeningPeriod_ openingPeriod : openingCollection.getOpeningPeriods()) {
      Criterion criterionForRegularHours = new Criterion(new Criteria().addField(OPENING_ID).setJSONB(true).setOperation("=").setValue("'" + openingPeriod.getId() + "'"));
      Future<Void> future = Future.future();
      futures.add(future);
      postgresClient.get(REGULAR_HOURS, RegularHours.class, criterionForRegularHours, true, false, resultOfSelectRegularHours -> {
        if (resultOfSelectRegularHours.succeeded()) {
          try {
            List<RegularHours> regularHoursList = (List<RegularHours>) resultOfSelectRegularHours.result().getResults();
            for (RegularHours regularHours : regularHoursList) {
              regularHours.setOpeningDays(regularHours.getOpeningDays());
              Map<String, OpeningPeriod_> openingPeriods = openingCollection.getOpeningPeriods().stream()
                .collect(Collectors.toMap(OpeningPeriod_::getId, Function.identity()));
              openingPeriods.get(regularHours.getOpeningId()).setOpeningDays(regularHours.getOpeningDays());
              future.complete();
            }
          } catch (ClassCastException ex) {
            future.fail(resultOfSelectRegularHours.cause());
            postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarOpeningsByServicePointIdRegularResponse.withPlainInternalServerError(openingCollection.toString()))));
          }
        } else {
          future.fail(resultOfSelectRegularHours.cause());
          postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarOpeningsByServicePointIdRegularResponse.withPlainInternalServerError(openingCollection.toString()))));
        }
      });
    }

    return futures;
  }


  @Override
  public void getCalendarOpenings(String servicePointId, Date startDate, Date endDate, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }


  @Override
  public void getCalendarOpeningsByServicePointIdExceptional(String servicePointId, Boolean withOpeningDays, Boolean showPast, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  @Override
  public void postCalendarOpeningsByServicePointIdExceptional(String servicePointId, OpeningExceptionJson entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  @Override
  public void deleteCalendarOpeningsByServicePointIdExceptional(String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  @Override
  public void getCalendarOpeningsByServicePointIdExceptionalByOpeningId(String openingId, String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  @Override
  public void deleteCalendarOpeningsByServicePointIdExceptionalByOpeningId(String openingId, String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  @Override
  public void putCalendarOpeningsByServicePointIdExceptionalByOpeningId(String openingId, String servicePointId, OpeningExceptionJson entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  @Override
  public void getCalendarOpeningsByServicePointIdCalculateopening(String servicePointId, Date date, Unit unit, Integer amount, Boolean includeClosed, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  @Override
  public void getCalendarOpeningsByServicePointIdCalculateopeningduration(String servicePointId, Date startTime, Date endTime, Unit unit, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  @Override
  public void deleteCalendarOpeningsByServicePointIdRegular(String servicePointId, String language, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  private static PostgresClient getPostgresClient(Map<String, String> okapiHeaders, Context vertxContext) {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    return PostgresClient.getInstance(vertxContext.owner(), tenantId);
  }
}
