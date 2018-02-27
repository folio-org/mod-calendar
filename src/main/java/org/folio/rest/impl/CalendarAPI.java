package org.folio.rest.impl;

import io.vertx.core.*;
import org.folio.rest.jaxrs.model.CalendarEventCollection;
import org.folio.rest.jaxrs.model.CalendarEventDescriptionCollection;
import org.folio.rest.jaxrs.model.Description;
import org.folio.rest.jaxrs.model.Description.DescriptionType;
import org.folio.rest.jaxrs.model.Event;
import org.folio.rest.jaxrs.resource.CalendarResource;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.cql.CQLWrapper;
import org.folio.rest.tools.utils.TenantTool;
import org.folio.rest.utils.CalendarConstants;
import org.folio.rest.utils.CalendarUtils;
import org.z3950.zing.cql.cql2pgjson.CQL2PgJSON;

import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.folio.rest.tools.ClientGenerator.OKAPI_HEADER_TENANT;
import static org.folio.rest.utils.CalendarConstants.*;

public class CalendarAPI implements CalendarResource {

  @Override
  public void getCalendarEvents(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    vertxContext.runOnContext(v -> {
      try {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("open").append("=").append(true);
        CQL2PgJSON cql2pgJson = new CQL2PgJSON(EVENT + ".jsonb");
        CQLWrapper cql = new CQLWrapper(cql2pgJson, queryBuilder.toString());
        postgresClient.get(EVENT, Event.class, cql, true, true,
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
    });
  }

  @Override
  public void getCalendarEventdescriptions(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);

    vertxContext.runOnContext(v -> {
      try {
        CQLWrapper cql = new CQLWrapper();

        postgresClient.get(EVENT_DESCRIPTION, Description.class, cql, true, true,
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
    });
  }

  @Override
  public void postCalendarEventdescriptions(Description description, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    if (description.getCreationDate() == null) {
      description.setCreationDate(new Date());
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    vertxContext.runOnContext(v -> {
      try {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("startDate >= ").append(format.format(description.getStartDate()))
          .append(" AND endDate <= ").append(format.format(description.getEndDate()))
          .append(" AND eventType = ");
        if (description.getDescriptionType() != null && description.getDescriptionType() == DescriptionType.EXCLUSION) {
          queryBuilder.append(CalendarConstants.EXCLUSION);
        } else {
          queryBuilder.append(CalendarConstants.OPENING_DAY);
        }
        CQL2PgJSON cql2pgJson = new CQL2PgJSON(EVENT + ".jsonb");
        CQLWrapper cql = new CQLWrapper(cql2pgJson, queryBuilder.toString());
        postgresClient.get(EVENT, Event.class, cql, true,
          replyOfGetEventsByDate -> {
            if (replyOfGetEventsByDate.failed()) {
              asyncResultHandler.handle(Future.succeededFuture(
                PostCalendarEventdescriptionsResponse.withPlainInternalServerError(
                  "Error while listing events.")));
            } else if (replyOfGetEventsByDate.result().getResults().isEmpty()) {
              postgresClient.startTx(beginTx -> {
                try {
                  postgresClient.save(beginTx, EVENT_DESCRIPTION, description, replyDescriptor -> {
                    if (replyDescriptor.succeeded()) {
                      List<Object> events = CalendarUtils.separateEvents(description, replyDescriptor.result());
                      if (events.isEmpty()) {
                        postgresClient.rollbackTx(beginTx, rollbackHandler -> {
                          asyncResultHandler.handle(Future.succeededFuture(
                            PostCalendarEventdescriptionsResponse.withPlainBadRequest("No events can be generated in the given interval")));
                        });
                      } else {
                        try {
                          postgresClient.saveBatch(EVENT, events, replyEvent -> {
                            if (!replyEvent.succeeded()) {
                              postgresClient.rollbackTx(beginTx, rollbackHandler -> {
                                asyncResultHandler.handle(Future.succeededFuture(
                                  PostCalendarEventdescriptionsResponse.withPlainInternalServerError(replyEvent.cause().getMessage())));
                              });
                            } else {
                              Description createdDescription = description;
                              createdDescription.setId(replyDescriptor.result());

                              postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(
                                Future.succeededFuture(PostCalendarEventdescriptionsResponse.withJsonCreated(createdDescription))));
                            }
                          });
                        } catch (Exception e) {
                          asyncResultHandler.handle(Future.succeededFuture(
                            PostCalendarEventdescriptionsResponse.withPlainInternalServerError(
                              e.getMessage())));
                        }
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
              });
            } else {
              asyncResultHandler.handle(Future.succeededFuture(
                PostCalendarEventdescriptionsResponse.withPlainConflict(
                  "Intervals can not overlap")));
            }
          });
      } catch (Exception e) {
        asyncResultHandler.handle(Future.succeededFuture(
          PostCalendarEventdescriptionsResponse.withPlainInternalServerError(
            e.getMessage())));
      }
    });
  }

  @Override
  public void deleteCalendarEventdescriptionsByEventDescriptionId(String uiEventDescriptionId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    Future<Void> future = deleteEventDescriptionsAndEvents(uiEventDescriptionId, vertxContext, okapiHeaders);
    future.setHandler(resultHandler -> {
      if (future.succeeded()) {
        asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse.withNoContent()));
      } else {
        asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse
          .withPlainInternalServerError(future.cause().getMessage())));
      }
    });
  }

  // TODO: bad request or not found if the description does not exist anymore
  private static Future<Void> deleteEventDescriptionsAndEvents(String eventDescriptionId, Context vertxContext, Map<String, String> okapiHeaders) {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);

    Future<Void> future = Future.future();
    vertxContext.runOnContext(v -> {
      try {
        CQLWrapper cql = new CQLWrapper();
        cql.setQuery("_" + ID_FIELD + "=" + eventDescriptionId);
        //TODO: check if rollback is possible on failure!
        postgresClient.get(EVENT_DESCRIPTION, Description.class, cql, true,
          replyOfGetDescriptionById -> {
            try {
              if (replyOfGetDescriptionById.succeeded()) {
                if (replyOfGetDescriptionById.result().getResults().size() == 0) {
                  future.fail("There is no event description with this id: " + eventDescriptionId);
                } else {
                  Future deleteEventsFuture = deleteEventsByDescriptionId(postgresClient, eventDescriptionId);
                  deleteEventsFuture.setHandler(handler -> {
                    if (deleteEventsFuture.succeeded()) {
                      deleteEventDescription(postgresClient, eventDescriptionId).setHandler(future.completer());
                    } else {
                      future.fail("Failed to delete events.");
                    }
                  });
                }
              } else {
                future.fail(replyOfGetDescriptionById.cause().getMessage());
              }
            } catch (Exception e) {
              future.fail(e.getMessage());
            }
          });
      } catch (Exception e) {
        future.fail(e.getMessage());
      }
    });
    return future;
  }

  @Override
  public void putCalendarEventdescriptionsByEventDescriptionId(String eventDescriptionId, Description description, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClientForEventDelete = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    vertxContext.runOnContext(v -> {
      Future<Void> future = deleteEventsByDescriptionId(postgresClientForEventDelete, eventDescriptionId);

      future.setHandler(resultHandler -> {
        if (future.succeeded() && future.isComplete()) {
          try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
            vertxContext.runOnContext(vc -> {
              try {
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append("startDate >= ").append(format.format(description.getStartDate()))
                  .append(" AND endDate <= ").append(format.format(description.getEndDate()))
                  .append(" AND descriptionId <> ").append(description.getId())
                  .append(" AND eventType = ");
                if (description.getDescriptionType() != null && description.getDescriptionType() == DescriptionType.EXCLUSION) {
                  queryBuilder.append(CalendarConstants.EXCLUSION);
                } else {
                  queryBuilder.append(CalendarConstants.OPENING_DAY);
                }
                CQL2PgJSON cql2pgJson = new CQL2PgJSON(EVENT + ".jsonb");
                CQLWrapper cql = new CQLWrapper(cql2pgJson, queryBuilder.toString());
                postgresClient.get(EVENT, Event.class, cql, true,
                  replyOfGetEventsByDate -> {
                    if (replyOfGetEventsByDate.failed()) {
                      asyncResultHandler.handle(Future.succeededFuture(
                        PostCalendarEventdescriptionsResponse.withPlainInternalServerError(
                          "Error while listing events.")));
                    } else if (replyOfGetEventsByDate.result().getResults().isEmpty()) {

                      try {
                        Criteria idCrit = new Criteria();
                        idCrit.addField("'id'");
                        idCrit.setOperation("=");
                        idCrit.setValue(description.getId());
                        postgresClient.update(EVENT_DESCRIPTION, description, new Criterion(idCrit), true, replyDescriptor -> {
                          if (replyDescriptor.succeeded()) {
                            List<Object> events = CalendarUtils.separateEvents(description, description.getId());
                            if (events.isEmpty()) {
                              asyncResultHandler.handle(Future.succeededFuture(
                                PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError("Can not add empty event set!")));
                            } else {
                              try {
                                postgresClient.saveBatch(EVENT, events, replyEvent -> {
                                  if (!replyEvent.succeeded()) {
                                    asyncResultHandler.handle(Future.succeededFuture(
                                      PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(replyEvent.cause().getMessage())));
                                  } else {
                                    asyncResultHandler.handle(
                                      Future.succeededFuture(PutCalendarEventdescriptionsByEventDescriptionIdResponse.withNoContent()));
                                  }
                                });
                              } catch (Exception e) {
                                asyncResultHandler.handle(Future.succeededFuture(
                                  PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(
                                    e.getMessage())));
                              }
                            }

                          } else {
                            asyncResultHandler.handle(Future.succeededFuture(
                              PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(
                                replyDescriptor.cause().getMessage())));
                          }
                        });
                      } catch (Exception e) {
                        asyncResultHandler.handle(Future.succeededFuture(
                          PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(
                            e.getMessage())));
                      }
                    } else {
                      asyncResultHandler.handle(Future.succeededFuture(
                        PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(
                          "Intervals can not overlap!")));
                    }
                  });
              } catch (Exception e) {
                asyncResultHandler.handle(Future.succeededFuture(
                  PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(
                    e.getMessage())));
              }
            });
          } catch (Exception e) {
            asyncResultHandler.handle(Future.succeededFuture(PutCalendarEventdescriptionsByEventDescriptionIdResponse
              .withPlainInternalServerError(String.valueOf(future.result()))));
          }
        } else {
          asyncResultHandler.handle(Future.succeededFuture(PutCalendarEventdescriptionsByEventDescriptionIdResponse
            .withPlainInternalServerError(future.cause().getMessage())));
        }
      });
    });

  }

  private static Future deleteEventsByDescriptionId(PostgresClient postgresClient, String eventDescriptionId) {
    Future future = Future.future();
    try {
      StringBuilder queryBuilder = new StringBuilder();
      queryBuilder.append(DESCRIPTION_ID_FIELD).append("=").append(eventDescriptionId);
      CQL2PgJSON cql2pgJson = new CQL2PgJSON(EVENT + ".jsonb");
      CQLWrapper cql = new CQLWrapper(cql2pgJson, queryBuilder.toString());
      postgresClient.get(EVENT, Event.class, cql, true,
        replyGetEventId -> {
          try {
            List<Future> deleteFutureResults = new ArrayList<>();
            for (Event event : (List<Event>) replyGetEventId.result().getResults()) {
              String eventId = event.getId();
              Future deleteFuture = Future.future();
              deleteFutureResults.add(deleteFuture);
              try {
                postgresClient.delete(EVENT, eventId, replyDeleteEvent -> {
                  if (!replyDeleteEvent.succeeded()) {
                    deleteFuture.fail(replyDeleteEvent.cause().getMessage());
                  } else {
                    deleteFuture.complete();
                  }
                });
              } catch (Exception e) {
                deleteFuture.fail(e.getMessage());
              }
            }
            CompositeFuture.all(deleteFutureResults).setHandler(ar -> {
              if (ar.succeeded()) {
                future.complete();
              } else {
                future.fail("Failed to delete all events!");
              }
            });
          } catch (Exception e) {
            future.fail(e.getMessage());
          }
        });
    } catch (Exception e) {
      future.fail(e.getMessage());
    }
    return future;
  }

  private static Future deleteEventDescription(PostgresClient postgresClient, String eventDescriptionId) {
    Future future = Future.future();
    try {
      postgresClient.delete(EVENT_DESCRIPTION, eventDescriptionId, replyDeleteDescription -> {
        if (replyDeleteDescription.failed()) {
          future.fail(replyDeleteDescription.cause().getMessage());
        } else {
          future.complete();
        }
      });
    } catch (Exception e) {
      future.fail(e.getMessage());
    }
    return future;
  }
}
