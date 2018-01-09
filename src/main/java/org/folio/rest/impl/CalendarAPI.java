package org.folio.rest.impl;

import static org.folio.rest.tools.ClientGenerator.*;
import static org.folio.rest.utils.CalendarConstants.*;
import static org.folio.rest.utils.CalendarUtils.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.jaxrs.model.CalendarEventCollection;
import org.folio.rest.jaxrs.model.CalendarEventDescriptionCollection;
import org.folio.rest.jaxrs.model.CalendarEventExclusionDescriptionCollection;
import org.folio.rest.jaxrs.model.Description;
import org.folio.rest.jaxrs.model.Event;
import org.folio.rest.jaxrs.model.Exclusion;
import org.folio.rest.jaxrs.resource.CalendarResource;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.interfaces.Results;
import org.folio.rest.tools.utils.TenantTool;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.sql.UpdateResult;

public class CalendarAPI implements CalendarResource {

  @Override
  public void getCalendarEvents(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    Criteria idCrit = new Criteria().setJSONB(true);
    Criterion criterion = new Criterion(idCrit);
    vertxContext.runOnContext(v -> postgresClient.startTx(beginTx -> {
      try {
        postgresClient.get(EVENT, Event.class, criterion, true, true,
          resultOfSelect -> {
            if (resultOfSelect.succeeded()) {
              CalendarEventCollection calendarEventCollection = new CalendarEventCollection();
              calendarEventCollection.setEvents((List<Event>) resultOfSelect.result().getResults());
              calendarEventCollection.setTotalRecords(resultOfSelect.result().getResultInfo().getTotalRecords());
              asyncResultHandler
                .handle(Future.succeededFuture(GetCalendarEventsResponse.withJsonOK(calendarEventCollection)));
            } else {
              asyncResultHandler.handle(Future.succeededFuture(
                GetCalendarEventsResponse.withPlainInternalServerError(
                  resultOfSelect.cause().getMessage())));
            }
          });
      } catch (Exception e) {
        asyncResultHandler.handle(Future.succeededFuture(
          GetCalendarEventsResponse.withPlainInternalServerError(
            e.getMessage())));
      }
    }));
  }

  @Override
  public void getCalendarEventdescriptions(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    Criteria idCrit = new Criteria().setJSONB(true);
    Criterion criterion = new Criterion(idCrit);
    vertxContext.runOnContext(v -> postgresClient.startTx(beginTx -> {
      try {
        postgresClient.get(EVENT_DESCRIPTION, Description.class, criterion, true, true,
          resultOfSelect -> {

            if (resultOfSelect.succeeded()) {
              CalendarEventDescriptionCollection calendarEventCollection = new CalendarEventDescriptionCollection();
              calendarEventCollection.setDescriptions((List<Description>) resultOfSelect.result().getResults());
              calendarEventCollection.setTotalRecords(resultOfSelect.result().getResultInfo().getTotalRecords());
              asyncResultHandler
                .handle(Future.succeededFuture(GetCalendarEventdescriptionsResponse.withJsonOK(calendarEventCollection)));
            } else {
              asyncResultHandler.handle(Future.succeededFuture(
                GetCalendarEventdescriptionsResponse.withPlainInternalServerError(
                  resultOfSelect.cause().getMessage())));
            }
          });
      } catch (Exception e) {
        asyncResultHandler.handle(Future.succeededFuture(
          GetCalendarEventdescriptionsResponse.withPlainInternalServerError(
            e.getMessage())));
      }
    }));
  }

  @Override
  public void postCalendarEventdescriptions(Description description, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    if (description.getCreationDate() == null) {
      description.setCreationDate(new Date());
    }
    vertxContext.runOnContext(v -> postgresClient.startTx(beginTx -> {
      try {
        postgresClient.save(beginTx, EVENT_DESCRIPTION, description, replyDescriptor -> {
          if (replyDescriptor.succeeded()) {
            List<Object> events = separateEvents(description, replyDescriptor.result());
            try {
              postgresClient.saveBatch(EVENT, events, replyEvent -> {
                if (!replyEvent.succeeded()) {
                  asyncResultHandler.handle(Future.succeededFuture(
                    PostCalendarEventdescriptionsResponse.withPlainInternalServerError(replyEvent.cause().getMessage())));
                } else {
                  postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(
                    Future.succeededFuture(PostCalendarEventdescriptionsResponse.withJsonCreated(description))));
                }
              });
            } catch (Exception e) {
              asyncResultHandler.handle(Future.succeededFuture(
                PostCalendarEventdescriptionsResponse.withPlainInternalServerError(
                  e.getMessage())));
            }

          } else {
            asyncResultHandler.handle(Future.succeededFuture(
              PostCalendarEventdescriptionsResponse.withPlainInternalServerError(
                replyDescriptor.cause().getMessage())));
          }
        });
      } catch (Exception e) {
        asyncResultHandler.handle(Future.succeededFuture(
          PostCalendarEventdescriptionsResponse.withPlainInternalServerError(
            e.getMessage())));
      }
    }));
  }

  @Override
  public void deleteCalendarEventdescriptionsByEventDescriptionId(String uiEventDescriptionId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    Future<DeleteCalendarEventdescriptionsByEventDescriptionIdResponse> future = deleteEventDescriptionsAndEvents(uiEventDescriptionId, vertxContext, okapiHeaders);
    future.setHandler(resultHandler -> {
      if (future.succeeded()) {
        asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse.withNoContent()));
      } else {
        asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse
          .withPlainInternalServerError(future.cause().getMessage())));
      }
    });
  }

  private static Future<DeleteCalendarEventdescriptionsByEventDescriptionIdResponse> deleteEventDescriptionsAndEvents(String eventDescriptionId, Context vertxContext, Map<String, String> okapiHeaders) {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    Criterion criteriaOfDescriptor = new Criterion(
      new Criteria().addField("_" + ID_FIELD).setJSONB(false).setOperation("=").setValue(("'" + eventDescriptionId + "'")));
    Future<DeleteCalendarEventdescriptionsByEventDescriptionIdResponse> future = Future.future();
    vertxContext.runOnContext(v -> postgresClient.startTx(beginTx -> {
      try {
        postgresClient.get(EVENT_DESCRIPTION, Description.class, criteriaOfDescriptor, true,
          replyOfGetDescriptionById -> {
            try {
              if (replyOfGetDescriptionById.succeeded() && replyOfGetDescriptionById.result().getResults().size() > 0) {
                postgresClient.delete(EVENT_DESCRIPTION, eventDescriptionId, replyDeleteDescription -> {
                  if (!replyDeleteDescription.succeeded()) {
                    future.fail(replyDeleteDescription.cause().getMessage());
                  } else {
                    deleteEventsByDescriptionId(postgresClient, replyOfGetDescriptionById, eventDescriptionId, future, vertxContext);
                  }
                });
              } else {
                future.fail("There is no event with this id");
              }
            } catch (Exception e) {
              future.fail(e.getMessage());
            }
          });
      } catch (Exception e) {
        future.fail(e.getMessage());
      }
    }));
    return future;
  }

  @Override
  public void putCalendarEventdescriptionsByEventDescriptionId(String eventDescriptionId, Description eventDescription, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    Future<DeleteCalendarEventdescriptionsByEventDescriptionIdResponse> future = deleteEventDescriptionsAndEvents(eventDescriptionId, vertxContext, okapiHeaders);
    future.setHandler(resultHandler -> {
      if (future.succeeded() && future.isComplete()) {
        try {
          postCalendarEventdescriptions(eventDescription, okapiHeaders, asyncResultHandler, vertxContext);
          asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse.withNoContent()));
        } catch (Exception e) {
          asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse
            .withPlainInternalServerError(String.valueOf(future.result()))));
        }
      } else {
        asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse
          .withPlainInternalServerError(future.cause().getMessage())));
      }
    });

  }

  @Override
  public void getCalendarExclusions(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    asyncResultHandler
      .handle(Future.succeededFuture(GetCalendarExclusionsResponse.withJsonOK(new CalendarEventExclusionDescriptionCollection())));
  }

  @Override
  public void postCalendarExclusions(Exclusion exclusion, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void deleteCalendarExclusionsByExclusionId(String exclusionId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void putCalendarExclusionsByExclusionId(String exclusionId, Exclusion entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  private static void deleteEventsByDescriptionId(PostgresClient postgresClient, AsyncResult<Results> replyGetDescriptionId, String eventDescriptionId, Future<DeleteCalendarEventdescriptionsByEventDescriptionIdResponse> future, Context vertxContext) {
    try {
      Criterion criteriaOfEvent = new Criterion(
        new Criteria().addField("'" + ID_FIELD + "'").setJSONB(true).setOperation("->>").setOperation("=").setValue(("'" + eventDescriptionId + "'")));
      postgresClient.get(EVENT, Event.class, criteriaOfEvent, true,
        replyGetEventId -> {
          try {
            if (replyGetDescriptionId.succeeded()) {
              List<AsyncResult<UpdateResult>> finishedDelete = new ArrayList<>();
              for (Event event : (List<Event>) replyGetEventId.result().getResults()) {
                String eventId = event.getId();
                try {
                  postgresClient.delete(EVENT, eventId, replyDeleteEvent -> {
                    if (!replyDeleteEvent.succeeded()) {
                      future.fail(replyDeleteEvent.cause().getMessage());
                    } else {
                      finishedDelete.add(replyDeleteEvent);
                      if (finishedDelete.size() == replyGetEventId.result().getResults().size()) {
                        future.complete();
                      }
                    }
                  });
                } catch (Exception e) {
                  future.fail(e.getMessage());
                }
              }
            } else {
              future.fail(replyGetDescriptionId.cause().getMessage());
            }
          } catch (Exception e) {
            future.fail(e.getMessage());
          }
        });
    } catch (Exception e) {
      future.fail(e.getMessage());
    }
  }
}
