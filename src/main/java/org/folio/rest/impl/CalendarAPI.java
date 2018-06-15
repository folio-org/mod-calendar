package org.folio.rest.impl;

import io.vertx.core.*;
import org.folio.rest.beans.Openings;
import org.folio.rest.beans.RegularHoursTable;
import org.folio.rest.jaxrs.model.*;
import org.folio.rest.jaxrs.resource.CalendarResource;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.utils.TenantTool;
import org.folio.rest.utils.CalendarUtils;

import javax.ws.rs.core.Response;
import java.util.*;

import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.utils.CalendarConstants.*;


public class CalendarAPI implements CalendarResource {

  public CalendarAPI() {
    //stub
  }

  @Override
  public void postCalendarOpeningsByServicePointIdRegular(String servicePointId, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    Openings openingsTable = new Openings(entity.getId(), entity.getServicePointId(), entity.getName(), entity.getStartDate(), entity.getEndDate());
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
    vertxContext.runOnContext(v ->
      postgresClient.startTx(beginTx ->
        postgresClient.save(beginTx, OPENINGS, openingsTable, replyOfSavingOpenings -> {
          if (replyOfSavingOpenings.succeeded()) {
            RegularHoursTable regularHoursTable = new RegularHoursTable(replyOfSavingOpenings.result(), openingsTable.getId(), entity.getOpeningDays());
            postgresClient.save(REGULAR_HOURS, regularHoursTable, replyOfSavingRegularHours -> {
              if (replyOfSavingRegularHours.succeeded()) {
                saveActualOpeningHours(entity, asyncResultHandler, postgresClient, beginTx);
              } else {
                postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PostCalendarOpeningsByServicePointIdRegularResponse.withInternalServerError())));
              }
            });
          } else {
            postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PostCalendarOpeningsByServicePointIdRegularResponse.withInternalServerError())));
          }
        })
      )
    );
  }

  private void saveActualOpeningHours(OpeningPeriod_ entity, Handler<AsyncResult<Response>> asyncResultHandler, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Object> actualOpeningHours = CalendarUtils.separateEvents(entity);
    postgresClient.saveBatch(ACTUAL_OPENING_HOURS, actualOpeningHours, replyOfSavingActualOpeningHours -> {
      if (replyOfSavingActualOpeningHours.succeeded()) {
        postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PostCalendarOpeningsByServicePointIdRegularResponse.withJsonCreated(entity))));
      } else {
        postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PostCalendarOpeningsByServicePointIdRegularResponse.withInternalServerError())));
      }
    });
  }

  @Override
  public void deleteCalendarOpeningsByServicePointIdRegularByOpeningId(String openingId, String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);

    Criterion criterionForOpeningHours = new Criterion(new Criteria().addField("'openingId'").setJSONB(true).setOperation("=").setValue("'" + openingId + "'"));
    Criterion criterionForOpenings = new Criterion(new Criteria().addField("'id'").setJSONB(true).setOperation("=").setValue("'" + openingId + "'"));

    vertxContext.runOnContext(v ->
      postgresClient.startTx(beginTx ->
        postgresClient.delete(beginTx, ACTUAL_OPENING_HOURS, criterionForOpeningHours, replyOfDeletingActualOpeningHours -> {
          if (replyOfDeletingActualOpeningHours.succeeded()) {
            postgresClient.delete(beginTx, REGULAR_HOURS, criterionForOpeningHours, replyOfDeletingRegularHours -> {
              if (replyOfDeletingRegularHours.succeeded()) {
                deleteOpenings(asyncResultHandler, postgresClient, criterionForOpenings, beginTx);
              } else {
                postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withInternalServerError())));
              }
            });
          } else {
            postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withInternalServerError())));
          }
        })
      )
    );
  }

  private void deleteOpenings(Handler<AsyncResult<Response>> asyncResultHandler, PostgresClient postgresClient, Criterion criterionForOpenings, AsyncResult<Object> beginTx) {
    postgresClient.delete(beginTx, OPENINGS, criterionForOpenings, replyOfDeletingOpenings -> {
      if (replyOfDeletingOpenings.succeeded()) {
        postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withNoContent())));
      } else {
        postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withInternalServerError())));
      }
    });
  }


  @Override
  public void putCalendarOpeningsByServicePointIdRegularByOpeningId(String openingId, String servicePointId, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    Openings openingsTable = new Openings(entity.getId(), entity.getServicePointId(), entity.getName(), entity.getStartDate(), entity.getEndDate());
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);

    Criterion criterionForOpeningHours = new Criterion(new Criteria().addField("'openingId'").setJSONB(true).setOperation("=").setValue("'" + openingId + "'"));
    Criterion criterionForOpenings = new Criterion(new Criteria().addField("'id'").setJSONB(true).setOperation("=").setValue("'" + openingId + "'"));

    vertxContext.runOnContext(v ->
      postgresClient.startTx(beginTx ->
        postgresClient.update(OPENINGS, openingsTable, criterionForOpenings, true, replyOfSavingOpenings -> {
          if (replyOfSavingOpenings.succeeded()) {
            RegularHoursTable regularHoursTable = new RegularHoursTable(openingId, openingsTable.getId(), entity.getOpeningDays());
            postgresClient.update(REGULAR_HOURS, regularHoursTable, criterionForOpeningHours, true, replyOfSavingRegularHours -> {
              if (replyOfSavingRegularHours.succeeded()) {
                putActualOpeningHours(entity, criterionForOpeningHours, asyncResultHandler, postgresClient, beginTx);
              } else {
                postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withInternalServerError())));
              }
            });
          } else {
            postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withInternalServerError())));
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
            postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withInternalServerError())));
          }
        });
      } else {
        postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarOpeningsByServicePointIdRegularByOpeningIdResponse.withInternalServerError())));
      }
    });
  }

  @Override
  public void getCalendarOpeningsByServicePointIdRegular(String servicePointId, Boolean withOpeningHours, Boolean withExceptions, Boolean showPast, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }


  @Override
  public void getCalendarOpenings(String servicePointId, Date startDate, Date endDate, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }


  @Override
  public void getCalendarOpeningsByServicePointIdRegularByOpeningId(String openingId, String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }


  @Override
  public void getCalendarOpeningsByServicePointIdExceptional(String servicePointId, Boolean withOpeningHours, Boolean withExceptions, Boolean showPast, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  @Override
  public void postCalendarOpeningsByServicePointIdExceptional(String servicePointId, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
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
  public void putCalendarOpeningsByServicePointIdExceptionalByOpeningId(String openingId, String servicePointId, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
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
  public void deleteCalendarOpeningsByServicePointIdRegular(String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    //not yet implemented
  }

  private static PostgresClient getPostgresClient(Map<String, String> okapiHeaders, Context vertxContext) {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    return PostgresClient.getInstance(vertxContext.owner(), tenantId);
  }
}
