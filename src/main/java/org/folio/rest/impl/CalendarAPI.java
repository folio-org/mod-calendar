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
import org.folio.rest.persist.Criteria.UpdateSection;
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
        queryBuilder.append("open=").append(true).append(" AND active=").append(true);
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

    vertxContext.runOnContext(v -> {
      try {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("startDate >= ").append(CalendarUtils.getUTCDateformat().print(description.getStartDate().getTime()))
          .append(" AND endDate <= ").append(CalendarUtils.getUTCDateformat().print(description.getEndDate().getTime()))
          .append(" AND eventType = ");
        if (description.getDescriptionType() != null && description.getDescriptionType() == DescriptionType.EXCEPTION) {
          queryBuilder.append(CalendarConstants.EXCEPTION);
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

                              postgresClient.endTx(beginTx, done ->
                                {
                                  Future future;
                                  if (DescriptionType.OPENING_DAY == description.getDescriptionType()) {
                                    future = deactivateEvents(postgresClient, description);
                                  } else {
                                    future = updateEventStatusByException(postgresClient, description, Boolean.FALSE);
                                  }
                                  future.setHandler(subHandler -> {
                                    asyncResultHandler.handle(Future.succeededFuture(PostCalendarEventdescriptionsResponse.withJsonCreated(createdDescription)));
                                  });

                                }
                              );
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
      if (resultHandler.succeeded()) {
        asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse.withNoContent()));
      } else {
        asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse
          .withPlainInternalServerError(future.cause().getMessage())));
      }
    });
  }

  private static Future<Void> updateEventStatusByException(PostgresClient postgresClient, Description eventDescription, Boolean status) {
    Future<Void> future = Future.future();
    try {
      Criterion cr = new Criterion();
      Criteria crit = new Criteria();
      crit.addField("'startDate'");
      crit.setOperation(Criteria.OP_GREATER_THAN_EQ);
      crit.setValue(CalendarUtils.getUTCDateformat().print(eventDescription.getStartDate().getTime()));

      Criteria otherCrit = new Criteria();
      otherCrit.addField("'endDate'");
      otherCrit.setOperation(Criteria.OP_LESS_THAN_EQ);
      otherCrit.setValue(CalendarUtils.getUTCDateformat().print(eventDescription.getEndDate().getTime()));

      cr.addCriterion(crit, Criteria.OP_AND, otherCrit, Criteria.OP_AND);
      Criteria eventTypeCrit = new Criteria();
      eventTypeCrit.addField("'eventType'");
      eventTypeCrit.setOperation(Criteria.OP_EQUAL);
      eventTypeCrit.setValue(CalendarConstants.OPENING_DAY);
      cr.addCriterion(eventTypeCrit);

      try {
        UpdateSection us = new UpdateSection();
        us.addField("active");
        us.setValue(status);

        postgresClient.update(EVENT, us, cr, true, handler -> {
          if (handler.succeeded()) {
            System.out.println("Successfully updated events!");
            future.complete();
          } else {
            future.fail("Failed to update events.");
          }
        });
      } catch (Exception exc) {
        future.fail("Failed to update events.");
      }
    } catch (Exception exc) {
      future.fail("Failed to update events.");
    }
    return future;
  }


  private Future deactivateEvents(PostgresClient postgresClient, Description eventDescription) {
    Future future = Future.future();
    try {
      StringBuilder queryBuilder = new StringBuilder();
      queryBuilder
        .append("startDate <= ").append(CalendarUtils.getUTCDateformat().print(eventDescription.getEndDate().getTime()))
        .append(" AND endDate >= ").append(CalendarUtils.getUTCDateformat().print(eventDescription.getStartDate().getTime()))
        .append(" AND descriptionType = ").append(DescriptionType.EXCEPTION);
      CQL2PgJSON descriptionCql2pgJson = new CQL2PgJSON(EVENT_DESCRIPTION + ".jsonb");
      CQLWrapper descriptionCql = new CQLWrapper(descriptionCql2pgJson, queryBuilder.toString());
      postgresClient.get(EVENT_DESCRIPTION, Description.class, descriptionCql, true,
        replyOfGetEventDescriptionsByDate -> {
          if (replyOfGetEventDescriptionsByDate.succeeded()) {
            if (replyOfGetEventDescriptionsByDate.result().getResults() == null || replyOfGetEventDescriptionsByDate.result().getResults().isEmpty()) {
              System.out.println("No events to update!");
              future.complete();
            } else {
              Criterion cr = new Criterion();
              try {
                for (int i = 0; i < replyOfGetEventDescriptionsByDate.result().getResults().size(); i++) {
                  Description currDescription = (Description) replyOfGetEventDescriptionsByDate.result().getResults().get(i);
                  Criteria crit = new Criteria();
                  crit.addField("'startDate'");
                  crit.setOperation(Criteria.OP_GREATER_THAN_EQ);
                  crit.setValue(CalendarUtils.getUTCDateformat().print(currDescription.getStartDate().getTime()));

                  Criteria otherCrit = new Criteria();
                  otherCrit.addField("'endDate'");
                  otherCrit.setOperation(Criteria.OP_LESS_THAN_EQ);
                  otherCrit.setValue(CalendarUtils.getUTCDateformat().print(currDescription.getEndDate().getTime()));

                  if (i < replyOfGetEventDescriptionsByDate.result().getResults().size() - 1) {
                    cr.addCriterion(crit, Criteria.OP_AND, otherCrit, Criteria.OP_OR);
                  } else {
                    cr.addCriterion(crit, Criteria.OP_AND, otherCrit, Criteria.OP_AND);
                  }
                }
                Criteria eventTypeCrit = new Criteria();
                eventTypeCrit.addField("'eventType'");
                eventTypeCrit.setOperation(Criteria.OP_EQUAL);
                eventTypeCrit.setValue(CalendarConstants.OPENING_DAY);
                cr.addCriterion(eventTypeCrit);

                try {
                  UpdateSection us = new UpdateSection();
                  us.addField("active");
                  us.setValue(Boolean.FALSE);

                  postgresClient.update(EVENT, us, cr, true, handler -> {
                    if (handler.succeeded()) {
                      System.out.println("Successfully updated events!");
                      future.complete();
                    } else {
                      future.fail("Failed to update events.");
                    }
                  });
                } catch (Exception exc) {
                  future.fail("Failed to update events.");
                }
              } catch (Exception exc) {
                future.fail("Failed to update events.");
              }
            }
          } else {
            future.fail("Failed to update events.");
          }
        }
      );
    } catch (Exception exc) {
      future.fail("Failed to update events.");
    }
    return future;
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
        postgresClient.get(EVENT_DESCRIPTION, Description.class, cql, true,
          replyOfGetDescriptionById -> {
            try {
              if (replyOfGetDescriptionById.succeeded()) {
                if (replyOfGetDescriptionById.result().getResults().size() == 0) {
                  future.fail("There is no event description with this id: " + eventDescriptionId);
                } else {
                  Future<Void> deleteEventsFuture = deleteEventsByDescriptionId(postgresClient, eventDescriptionId);
                  deleteEventsFuture.setHandler(handler -> {
                    if (handler.succeeded()
                      && replyOfGetDescriptionById.result() != null
                      && replyOfGetDescriptionById.result().getResults() != null
                      && !replyOfGetDescriptionById.result().getResults().isEmpty()
                      && replyOfGetDescriptionById.result().getResults().get(0) instanceof Description) {
                      Future<Void> updateOpeningEvents = updateEventStatusByException(postgresClient, (Description) replyOfGetDescriptionById.result().getResults().get(0), Boolean.TRUE);
                      updateOpeningEvents.setHandler(subHandler -> {
                        if (subHandler.succeeded()) {
                          deleteEventDescription(postgresClient, eventDescriptionId).setHandler(future.completer());
                        } else {
                          future.fail("Failed to update opening events.");
                        }
                      });
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
        if (resultHandler.succeeded() && future.isComplete()) {
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
                if (description.getDescriptionType() != null && description.getDescriptionType() == DescriptionType.EXCEPTION) {
                  queryBuilder.append(CalendarConstants.EXCEPTION);
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
                        postgresClient.update(EVENT_DESCRIPTION, description, description.getId(), replyDescriptor -> {
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
                                    Future subFuture;
                                    if (DescriptionType.OPENING_DAY == description.getDescriptionType()) {
                                      subFuture = deactivateEvents(postgresClient, description);
                                    } else {
                                      subFuture = updateEventStatusByException(postgresClient, description, Boolean.FALSE);
                                    }
                                    subFuture.setHandler(subHandler -> {
                                      asyncResultHandler.handle(
                                        Future.succeededFuture(PutCalendarEventdescriptionsByEventDescriptionIdResponse.withNoContent()));
                                    });
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

  private static Future<Void> deleteEventsByDescriptionId(PostgresClient postgresClient, String eventDescriptionId) {
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
            for (Object eventObj : replyGetEventId.result().getResults()) {
              if (!(eventObj instanceof Event)) {
                continue;
              }
              Event event = (Event) eventObj;
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
