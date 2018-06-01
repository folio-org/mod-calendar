package org.folio.rest.impl;

import io.vertx.core.*;
import org.folio.rest.jaxrs.model.OpeningPeriod;
import org.folio.rest.jaxrs.model.OpeningPeriod_;
import org.folio.rest.jaxrs.resource.CalendarResource;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.utils.TenantTool;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Map;

import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.utils.CalendarConstants.CALENDAR;


public class CalendarAPI implements CalendarResource {
  private CalendarAPI() {
  }


  private static final Logger log = LoggerFactory.getLogger(CalendarAPI.class);

  private static final String FAILED_TO_UPDATE_EVENTS = "Failed to update events.";
  private static final String JSONB_POSTFIX = ".jsonb";
  private static final String TIME_PATTERN = "HH:mm:ss.SSS'Z'";
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern(TIME_PATTERN);

  @Override
  public void getCalendarOpenings(String servicePointId, Date startDate, Date endDate, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void getCalendarOpeningsByServicePointIdRegular(String servicePointId, Boolean withOpeningHours, Boolean withExceptions, Boolean showPast, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void postCalendarOpeningsByServicePointIdRegular(String servicePointId, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
//    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
//    entity.set
//    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
//    vertxContext.runOnContext(v ->
//      postgresClient.startTx(beginTx -> postgresClient.save(beginTx, CALENDAR, entity, reply -> {
//        if (reply.succeeded()) {
//          postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PostCalendarOpeningsByServicePointIdResponse.withJsonCreated(entity))));
//        } else {
//          postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PostCalendarOpeningsByServicePointIdResponse.withInternalServerError(/*reply.cause().getMessage()*/))));
//        }
//      }))
//    );
  }

  @Override
  public void deleteCalendarOpeningsByServicePointIdRegular(String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void putCalendarOpeningsByServicePointIdRegular(String servicePointId, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void getCalendarOpeningsByServicePointIdRegularByOpeningId(String openingId, String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void deleteCalendarOpeningsByServicePointIdRegularByOpeningId(String openingId, String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void putCalendarOpeningsByServicePointIdRegularByOpeningId(String openingId, String servicePointId, OpeningPeriod entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void getCalendarOpeningsByServicePointIdExceptional(String servicePointId, Boolean withOpeningHours, Boolean withExceptions, Boolean showPast, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void postCalendarOpeningsByServicePointIdExceptional(String servicePointId, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void deleteCalendarOpeningsByServicePointIdExceptional(String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void putCalendarOpeningsByServicePointIdExceptional(String servicePointId, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void getCalendarOpeningsByServicePointIdExceptionalByOpeningId(String openingId, String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void deleteCalendarOpeningsByServicePointIdExceptionalByOpeningId(String openingId, String servicePointId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void putCalendarOpeningsByServicePointIdExceptionalByOpeningId(String openingId, String servicePointId, OpeningPeriod entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void getCalendarOpeningsByServicePointIdCalculateopening(String servicePointId, Date date, Unit unit, Integer amount, Boolean includeClosed, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }

  @Override
  public void getCalendarOpeningsByServicePointIdCalculateopeningduration(String servicePointId, Date startTime, Date endTime, Unit unit, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }
/*
  @Override
  public void getCalendarCalendars(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    Criterion criterion = new Criterion(new Criteria().setJSONB(true));
    vertxContext.runOnContext(a ->
      postgresClient.startTx(beginTx ->
        postgresClient.get(CALENDAR, ModCalendarJson.class, criterion, true, false, resultOfSelect -> {
          if (resultOfSelect.succeeded()) {
            ModCalendarCollection modCalendarCollection = new ModCalendarCollection();
            List<ModCalendarJson> modCalendarJsons = new ArrayList<>();
            for (Object object : resultOfSelect.result().getResults()) {
              if (object instanceof ModCalendarJson) {
                modCalendarJsons.add((ModCalendarJson) object);
              }
            }
            modCalendarCollection.setCalendars(modCalendarJsons);
            postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarCalendarsResponse.withJsonOK(modCalendarCollection))));
          } else {
            postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarCalendarsResponse.withPlainInternalServerError(resultOfSelect.cause().getMessage()))));
          }
        })));
  }

  @Override
  public void deleteCalendarCalendarsByModCalendarId(String modCalendarId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    Criterion criterionForCalendar = new Criterion(new Criteria().addField("'id'").setJSONB(true).setOperation("=").setValue("'" + modCalendarId + "'"));
    Criterion criterionForEventDescriptions = new Criterion(new Criteria().addField("'calendarId'").setJSONB(true).setOperation("=").setValue("'" + modCalendarId + "'"));
    vertxContext.runOnContext(a -> postgresClient.startTx(beginTx -> postgresClient.delete(EVENT, criterionForEventDescriptions, eventDeleteResult -> {
      if (eventDeleteResult.succeeded()) {
        postgresClient.delete(EVENT_DESCRIPTION, criterionForEventDescriptions, descriptionDeleteResult -> {
          if (descriptionDeleteResult.succeeded()) {
            postgresClient.delete(CALENDAR, criterionForCalendar, calendarDeleteResult -> {
              if (calendarDeleteResult.succeeded()) {
                postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarCalendarsByModCalendarIdEventdescriptionsByEventDescriptionIdResponse.withNoContent()))
                );
              } else {
                postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarCalendarsByModCalendarIdEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(descriptionDeleteResult.cause().getMessage()))));
              }
            });
          } else {
            postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarCalendarsByModCalendarIdEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(descriptionDeleteResult.cause().getMessage()))));
          }
        });
      } else {
        postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarCalendarsByModCalendarIdEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(eventDeleteResult.cause().getMessage()))));
      }
    })));
  }

  @Override
  public void putCalendarCalendarsByModCalendarId(String modCalendarId, ModCalendarJson entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    Criterion updateCalendarCriterion = new Criterion(new Criteria().addField("'id'").setJSONB(true).setOperation("->>").setOperation("=").setValue("'" + modCalendarId + "'"));
    vertxContext.runOnContext(v -> postgresClient.startTx(beginTx -> postgresClient.update(CALENDAR, entity, updateCalendarCriterion, true, replyHandler -> {
      if (replyHandler.succeeded()) {
        postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarCalendarsByModCalendarIdResponse.withJsonNoContent(entity))));
      } else {
        postgresClient.rollbackTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(PutCalendarCalendarsByModCalendarIdEventdescriptionsByEventDescriptionIdResponse.withPlainBadRequest(replyHandler.cause().getMessage()))));
      }
    })));
  }


  @Override
  public void getCalendarEvents(String from, String to, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
    vertxContext.runOnContext(v -> {
      try {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("open=").append(true).append(" AND active=").append(true);

        if (from != null) {
          queryBuilder.append(" AND startDate >= ").append(CalendarUtils.DATE_FORMATTER.print(CalendarUtils.BASIC_DATE_FORMATTER.parseDateTime(from)));
        }
        if (to != null) {
          queryBuilder.append(" AND endDate <= ").append(CalendarUtils.DATE_FORMATTER.print(calculateEndOfTheDay(CalendarUtils.BASIC_DATE_FORMATTER.parseDateTime(to).toDate())));
        }
        CQL2PgJSON cql2pgJson = new CQL2PgJSON(EVENT + JSONB_POSTFIX);
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
  public void getCalendarEventdescriptions(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);

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
  public void postCalendarEventdescriptions(Description description, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
    if (description.getCreationDate() == null) {
      description.setCreationDate(new Date());
    }

    vertxContext.runOnContext(v -> {
      try {
        checkDescriptionInput(description);
        CQL2PgJSON cql2pgJson = new CQL2PgJSON(EVENT + JSONB_POSTFIX);
        CQLWrapper cql = new CQLWrapper(cql2pgJson, buildQueryForExistingEventsByDescription(description, null));
        postgresClient.get(EVENT, Event.class, cql, true,
          replyOfGetEventsByDate -> {
            if (replyOfGetEventsByDate.failed()) {
              asyncResultHandler.handle(Future.succeededFuture(
                PostCalendarEventdescriptionsResponse.withPlainInternalServerError(
                  "Error while listing events.")));
            } else if (replyOfGetEventsByDate.result().getResults().isEmpty()) {
              Future<Description> eventDescriptionSaveResponse = saveEventDescription(postgresClient, description);
              eventDescriptionSaveResponse.setHandler(saveHandler -> {
                if (saveHandler.succeeded()) {
                  asyncResultHandler.handle(Future.succeededFuture(PostCalendarEventdescriptionsResponse.withJsonCreated(eventDescriptionSaveResponse.result())));
                } else {
                  asyncResultHandler.handle(Future.succeededFuture(
                    PostCalendarEventdescriptionsResponse.withPlainInternalServerError(
                      saveHandler.cause().getMessage())));
                }
              });
            } else {
              asyncResultHandler.handle(Future.succeededFuture(
                PostCalendarEventdescriptionsResponse.withPlainConflict(
                  "Intervals can not overlap.")));
            }
          });
      } catch (CalendarIntervalException e) {
        log.warn(e.getMessage());
        asyncResultHandler.handle(Future.succeededFuture(
          PostCalendarEventdescriptionsResponse.withPlainBadRequest(e.getMessage())));
      } catch (Exception e) {
        asyncResultHandler.handle(Future.succeededFuture(
          PostCalendarEventdescriptionsResponse.withPlainBadRequest(e.getMessage())));
      }
    });
  }

  private String buildQueryForExistingEventsByDescription(Description description, String descriptionId) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("startDate >= ").append(CalendarUtils.DATE_FORMATTER.print(description.getStartDate().getTime()))
      .append(" AND endDate <= ").append(CalendarUtils.DATE_FORMATTER.print(calculateEndOfTheDay(description.getEndDate())))
      .append(" AND eventType = ").append(description.getDescriptionType().toString());
    if (descriptionId != null) {
      queryBuilder.append(" AND descriptionId <> ").append(descriptionId);
    }
    return queryBuilder.toString();
  }

  private Future<Description> saveEventDescription(PostgresClient postgresClient, Description description) {
    Future<Description> future = Future.future();
    postgresClient.startTx(beginTx -> {
      try {
        postgresClient.save(beginTx, EVENT_DESCRIPTION, description, replyDescriptor -> {
          if (replyDescriptor.succeeded()) {
            List<Object> events = CalendarUtils.separateEvents(description, replyDescriptor.result());
            if (events.isEmpty()) {
              postgresClient.rollbackTx(beginTx, rollbackHandler ->
                future.fail("No events can be generated in the given interval.")
              );
            } else {
              Description createdDescription = description;
              createdDescription.setId(replyDescriptor.result());
              Future<Void> batchFuture = saveEventBatch(postgresClient, events);
              batchFuture.setHandler(batchSaveResponse -> {
                if (batchSaveResponse.succeeded()) {
                  postgresClient.endTx(beginTx, done ->
                    {
                      Future subFuture;
                      if (DescriptionType.OPENING_DAY == description.getDescriptionType()) {
                        subFuture = deactivateEvents(postgresClient, description);
                      } else {
                        subFuture = updateEventStatusByException(postgresClient, description, Boolean.FALSE);
                      }
                      subFuture.setHandler(subHandler ->
                        future.complete(createdDescription)
                      );
                    }
                  );
                } else {
                  postgresClient.rollbackTx(beginTx, rollbackHandler ->
                    future.fail(batchSaveResponse.cause())
                  );
                }
              });
            }

          } else {
            future.fail(replyDescriptor.cause());
          }
        });

      } catch (Exception e) {
        future.fail(e);
      }
    });
    return future;
  }

  private Future<Void> saveEventBatch(PostgresClient postgresClient, List<Object> events) {
    Future<Void> future = Future.future();
    try {
      postgresClient.saveBatch(EVENT, events, replyEvent -> {
        if (!replyEvent.succeeded()) {
          future.fail(replyEvent.cause());
        } else {
          future.complete();
        }
      });
    } catch (Exception e) {
      future.fail(e);
    }
    return future;
  }

  @Override
  public void deleteCalendarEventdescriptionsByEventDescriptionId(String eventDescriptionId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
    vertxContext.runOnContext(v -> {
      Future<Void> future = reactivateEventStatusByDescriptionId(postgresClient, eventDescriptionId);
      future.setHandler(response -> {
        if (response.failed()) {
          asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse
            .withPlainInternalServerError(response.cause().getMessage())));
        } else {
          Future<Void> eventDeleteFuture = deleteEventsByDescriptionId(postgresClient, eventDescriptionId);
          eventDeleteFuture.setHandler(eventDeleteResponse -> {
            if (eventDeleteResponse.failed()) {
              asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse
                .withPlainInternalServerError(eventDeleteResponse.cause().getMessage())));
            } else {
              Future<Void> eventDescriptionDeleteResponse = deleteObject(postgresClient, eventDescriptionId, EVENT_DESCRIPTION);
              eventDescriptionDeleteResponse.setHandler(resultHandler -> {
                if (resultHandler.succeeded()) {
                  asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse.withNoContent()));
                } else {
                  asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarEventdescriptionsByEventDescriptionIdResponse
                    .withPlainBadRequest(resultHandler.cause().getMessage())));
                }
              });
            }
          });
        }
      });
    });
  }

  private static Future<Void> updateEventStatusByException(PostgresClient postgresClient, Description eventDescription, Boolean status) {
    Future<Void> future = Future.future();
    try {
      Criterion cr = new Criterion();
      Criteria crit = new Criteria();
      crit.addField("'startDate'");
      crit.setOperation(Criteria.OP_GREATER_THAN_EQ);
      crit.setValue(CalendarUtils.DATE_FORMATTER.print(eventDescription.getStartDate().getTime()));

      Criteria otherCrit = new Criteria();
      otherCrit.addField("'endDate'");
      otherCrit.setOperation(Criteria.OP_LESS_THAN_EQ);
      otherCrit.setValue(CalendarUtils.DATE_FORMATTER.print(calculateEndOfTheDay(eventDescription.getEndDate())));

      cr.addCriterion(crit, Criteria.OP_AND, otherCrit, Criteria.OP_AND);
      Criteria eventTypeCrit = new Criteria();
      eventTypeCrit.addField("'eventType'");
      eventTypeCrit.setOperation(Criteria.OP_EQUAL);
      eventTypeCrit.setValue(DescriptionType.OPENING_DAY.toString());
      cr.addCriterion(eventTypeCrit);

      UpdateSection us = new UpdateSection();
      us.addField("active");
      us.setValue(status);

      postgresClient.update(EVENT, us, cr, true, handler -> {
        if (handler.succeeded()) {
          log.debug("Successfully updated events.");
          future.complete();
        } else {
          future.fail(FAILED_TO_UPDATE_EVENTS);
        }
      });
    } catch (Exception exc) {
      future.fail(FAILED_TO_UPDATE_EVENTS);
    }
    return future;
  }

  private Future deactivateEvents(PostgresClient postgresClient, Description eventDescription) {
    Future future = Future.future();
    try {
      StringBuilder queryBuilder = new StringBuilder();
      queryBuilder
        .append("startDate <= ").append(CalendarUtils.DATE_FORMATTER.print(calculateEndOfTheDay(eventDescription.getEndDate())))
        .append(" AND endDate >= ").append(CalendarUtils.DATE_FORMATTER.print(eventDescription.getStartDate().getTime()))
        .append(" AND descriptionType = ").append(DescriptionType.EXCEPTION);
      CQL2PgJSON descriptionCql2pgJson = new CQL2PgJSON(EVENT_DESCRIPTION + JSONB_POSTFIX);
      CQLWrapper descriptionCql = new CQLWrapper(descriptionCql2pgJson, queryBuilder.toString());
      postgresClient.get(EVENT_DESCRIPTION, Description.class, descriptionCql, true,
        replyOfGetEventDescriptionsByDate -> {
          if (replyOfGetEventDescriptionsByDate.succeeded()) {
            if (replyOfGetEventDescriptionsByDate.result().getResults() == null || replyOfGetEventDescriptionsByDate.result().getResults().isEmpty()) {
              log.debug("No events to update.");
              future.complete();
            } else {
              UpdateSection us = new UpdateSection().addField("active");
              us.setValue(Boolean.FALSE);
              try {
                postgresClient.update(EVENT, us, buildCriterionForEventUpdate(replyOfGetEventDescriptionsByDate.result().getResults()), true, handler -> {
                  if (handler.succeeded()) {
                    log.debug("Successfully updated events.");
                    future.complete();
                  } else {
                    future.fail(FAILED_TO_UPDATE_EVENTS);
                  }
                });
              } catch (Exception exc) {
                future.fail(FAILED_TO_UPDATE_EVENTS);
              }
            }
          } else {
            future.fail(FAILED_TO_UPDATE_EVENTS);
          }
        }
      );
    } catch (Exception exc) {
      future.fail(FAILED_TO_UPDATE_EVENTS);
    }
    return future;
  }

  private Criterion buildCriterionForEventUpdate(List<?> results) {
    Criterion cr = new Criterion();

    for (int i = 0; i < results.size(); i++) {
      Description currDescription = (Description) results.get(i);
      Criteria crit = new Criteria();
      crit.addField("'startDate'");
      crit.setOperation(Criteria.OP_GREATER_THAN_EQ);
      crit.setValue(CalendarUtils.DATE_FORMATTER.print(currDescription.getStartDate().getTime()));

      Criteria otherCrit = new Criteria();
      otherCrit.addField("'endDate'");
      otherCrit.setOperation(Criteria.OP_LESS_THAN_EQ);
      otherCrit.setValue(CalendarUtils.DATE_FORMATTER.print(calculateEndOfTheDay(currDescription.getEndDate())));

      if (i < results.size() - 1) {
        cr.addCriterion(crit, Criteria.OP_AND, otherCrit, Criteria.OP_OR);
      } else {
        cr.addCriterion(crit, Criteria.OP_AND, otherCrit, Criteria.OP_AND);
      }
    }
    Criteria eventTypeCrit = new Criteria();
    eventTypeCrit.addField("'eventType'");
    eventTypeCrit.setOperation(Criteria.OP_EQUAL);
    eventTypeCrit.setValue(DescriptionType.OPENING_DAY.toString());
    cr.addCriterion(eventTypeCrit);

    return cr;
  }

  private static Future<Void> reactivateEventStatusByDescriptionId(PostgresClient postgresClient, String eventDescriptionId) {
    Future<Void> future = Future.future();
    try {
      CQLWrapper cql = new CQLWrapper();
      cql.setField(new CQL2PgJSON(EVENT_DESCRIPTION + JSONB_POSTFIX));
      cql.setQuery(ID_FIELD + "==" + eventDescriptionId);
      postgresClient.get(EVENT_DESCRIPTION, Description.class, cql, true,
        replyOfGetDescriptionById -> {
          try {
            if (replyOfGetDescriptionById.succeeded()) {
              if (replyOfGetDescriptionById.result().getResults().isEmpty()) {
                future.fail("There is no event description with this id: " + eventDescriptionId);
              } else if (replyOfGetDescriptionById.result().getResults().size() > 1) {
                future.fail("Could not find specific event description with id: " + eventDescriptionId);
              } else if (replyOfGetDescriptionById.result() != null
                && replyOfGetDescriptionById.result().getResults() != null
                && !replyOfGetDescriptionById.result().getResults().isEmpty()
                && replyOfGetDescriptionById.result().getResults().get(0) instanceof Description) {
                Future<Void> updateOpeningEvents = updateEventStatusByException(postgresClient, (Description) replyOfGetDescriptionById.result().getResults().get(0), Boolean.TRUE);
                updateOpeningEvents.setHandler(subHandler -> {
                  if (subHandler.succeeded()) {
                    future.complete();
                  } else {
                    future.fail("Failed to update opening events.");
                  }
                });
              } else {
                future.fail("Failed to reactivate events.");
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
    return future;
  }

  @Override
  public void putCalendarEventdescriptionsByEventDescriptionId(String eventDescriptionId, Description description, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PostgresClient postgresClientForEventDelete = getPostgresClient(okapiHeaders, vertxContext);
    vertxContext.runOnContext(v -> {

      Future<Void> future = deleteEventsByDescriptionId(postgresClientForEventDelete, eventDescriptionId);

      future.setHandler(resultHandler -> {
        if (resultHandler.succeeded()) {
          try {
            PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
            vertxContext.runOnContext(vc -> {
              try {
                checkDescriptionInput(description);
                CQL2PgJSON cql2pgJson = new CQL2PgJSON(EVENT + JSONB_POSTFIX);
                CQLWrapper cql = new CQLWrapper(cql2pgJson, buildQueryForExistingEventsByDescription(description, description.getId()));
                postgresClient.get(EVENT, Event.class, cql, true,
                  replyOfGetEventsByDate -> {
                    if (replyOfGetEventsByDate.failed()) {
                      asyncResultHandler.handle(Future.succeededFuture(
                        PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(
                          "Error while listing events.")));
                    } else if (replyOfGetEventsByDate.result().getResults().isEmpty()) {

                      updateEventDescription(postgresClient, description).setHandler(updateFutureResponse -> {
                        if (updateFutureResponse.failed()) {
                          asyncResultHandler.handle(Future.succeededFuture(
                            PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(
                              updateFutureResponse.cause().getMessage())));
                        } else {
                          asyncResultHandler.handle(
                            Future.succeededFuture(PutCalendarEventdescriptionsByEventDescriptionIdResponse.withNoContent()));
                        }
                      });
                    } else {
                      asyncResultHandler.handle(Future.succeededFuture(
                        PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(
                          "Intervals can not overlap.")));
                    }
                  });
              } catch (CalendarIntervalException e) {
                log.warn(e.getMessage());
                asyncResultHandler.handle(Future.succeededFuture(PutCalendarEventdescriptionsByEventDescriptionIdResponse
                  .withPlainBadRequest(String.valueOf(e.getMessage()))));
              } catch (Exception e) {
                asyncResultHandler.handle(Future.succeededFuture(
                  PutCalendarEventdescriptionsByEventDescriptionIdResponse.withPlainInternalServerError(
                    e.getMessage())));
              }
            });
          } catch (Exception e) {
            asyncResultHandler.handle(Future.succeededFuture(PutCalendarEventdescriptionsByEventDescriptionIdResponse
              .withPlainInternalServerError(String.valueOf(resultHandler.result()))));
          }
        } else {
          asyncResultHandler.handle(Future.succeededFuture(PutCalendarEventdescriptionsByEventDescriptionIdResponse
            .withPlainInternalServerError(resultHandler.cause().getMessage())));
        }
      });
    });
  }

  private static final String TIME_PATTERN = "HH:mm:ss.SSS'Z'";
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern(TIME_PATTERN);

  private static void checkDescriptionInput(Description description) throws CalendarIntervalException {
    for (OpeningDay openingDay : description.getOpeningDays()) {
      List<Interval> intervals = openingHoursToList(openingDay);
      IntervalValidation.validate(intervals);
    }
  }

  private static List<Interval> openingHoursToList(OpeningDay openingDay) throws CalendarIntervalException {
    List<Interval> intervals = new ArrayList<>();
    for (OpeningHour openingHour : openingDay.getOpeningHour()) {
      if (openingHour.getStartTime() != null && openingHour.getEndTime() != null) {
        LocalTime from;
        LocalTime to;
        try {
          from = TIME_FORMATTER.parseLocalTime(openingHour.getStartTime());
          to = TIME_FORMATTER.parseLocalTime(openingHour.getEndTime());
        } catch (IllegalArgumentException e) {
          throw new CalendarIntervalException("The time format is not valid", e);
        }
        try {
          intervals.add(new Interval(from.toDateTimeToday(), to.toDateTimeToday()));
        } catch (IllegalArgumentException e) {
          throw new CalendarIntervalException("The start time must be before the end time", e);
        }
      }
    }
    return intervals;
  }

  private Future<Void> updateEventDescription(PostgresClient postgresClient, Description description) {
    Future<Void> future = Future.future();
    try {
      postgresClient.update(EVENT_DESCRIPTION, description, description.getId(), replyDescriptor -> {
        if (replyDescriptor.succeeded()) {
          List<Object> events = CalendarUtils.separateEvents(description, description.getId());
          if (events.isEmpty()) {
            future.fail("Can not add empty event set.");
          } else {
            Future<Void> batchSaveFuture = saveEventBatch(postgresClient, events);
            batchSaveFuture.setHandler(batchSaveHandler -> {
              if (!batchSaveHandler.succeeded()) {
                future.fail(batchSaveHandler.cause());
              } else {
                Future subFuture;
                if (DescriptionType.OPENING_DAY == description.getDescriptionType()) {
                  subFuture = deactivateEvents(postgresClient, description);
                } else {
                  subFuture = updateEventStatusByException(postgresClient, description, Boolean.FALSE);
                }
                subFuture.setHandler(future.completer());
              }
            });
          }

        } else {
          future.fail(replyDescriptor.cause());
        }
      });
    } catch (Exception e) {
      future.fail(e);
    }
    return future;
  }

  private static Future<Void> deleteEventsByDescriptionId(PostgresClient postgresClient, String eventDescriptionId) {
    Future future = Future.future();
    try {
      StringBuilder queryBuilder = new StringBuilder();
      queryBuilder.append(DESCRIPTION_ID_FIELD).append("=").append(eventDescriptionId);
      CQL2PgJSON cql2pgJson = new CQL2PgJSON(EVENT + JSONB_POSTFIX);
      CQLWrapper cql = new CQLWrapper(cql2pgJson, queryBuilder.toString());
      postgresClient.get(EVENT, Event.class, cql, true,
        replyGetEventId -> {
          if (replyGetEventId.failed()) {
            future.fail(replyGetEventId.cause());
          } else {
            List<Future> deleteFutureResults = new ArrayList<>();
            for (Object eventObj : replyGetEventId.result().getResults()) {
              if (!(eventObj instanceof Event)) {
                continue;
              }
              Event event = (Event) eventObj;
              String eventId = event.getId();
              Future deleteFuture = Future.future();
              deleteObject(postgresClient, eventId, EVENT).setHandler(deleteFuture.completer());
              deleteFutureResults.add(deleteFuture);
            }
            CompositeFuture.all(deleteFutureResults).setHandler(ar -> {
              if (ar.succeeded()) {

                Future<Void> subFuture = reactivateEventStatusByDescriptionId(postgresClient, eventDescriptionId);
                subFuture.setHandler(subHandler -> {
                  if (subHandler.succeeded()) {
                    future.complete();
                  } else {
                    future.fail("Failed to reactivate events.");
                  }
                });

              } else {
                future.fail("Failed to delete all events.");
              }
            });
          }
        });
    } catch (Exception e) {
      future.fail(e);
    }
    return future;
  }

  private static Future<Void> deleteObject(PostgresClient postgresClient, String objectId, String table) {
    Future future = Future.future();
    try {
      postgresClient.delete(table, objectId, replyDeleteObject -> {
        if (replyDeleteObject.failed()) {
          future.fail(replyDeleteObject.cause());
        } else {
          future.complete();
        }
      });
    } catch (Exception e) {
      future.fail(e);
    }
    return future;
  }

  private static PostgresClient getPostgresClient(Map<String, String> okapiHeaders, Context vertxContext) {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    return PostgresClient.getInstance(vertxContext.owner(), tenantId);
  }

  private static long calculateEndOfTheDay(Date date) {
    Calendar endDate = Calendar.getInstance();
    endDate.setTime(date);
    endDate.add(Calendar.HOUR_OF_DAY, 23);
    endDate.add(Calendar.MINUTE, 59);
    endDate.add(Calendar.SECOND, 59);
    return endDate.getTimeInMillis();
  }
*/
}
