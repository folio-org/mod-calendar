package org.folio.rest.impl;

import static org.folio.rest.tools.ClientGenerator.*;

import java.util.Calendar;
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
import org.folio.rest.tools.utils.TenantTool;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class CalendarAPI implements CalendarResource {
  public static final String EVENT_DESCRIPTION = "event_description";
  public static final String EVENT = "event";
  public static final String DEFAULT_EVENT_TYPE = "opening days";

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
    asyncResultHandler
      .handle(Future.succeededFuture(GetCalendarEventdescriptionsResponse.withJsonOK(new CalendarEventDescriptionCollection())));
  }

  @Override
  public void postCalendarEventdescriptions(Description entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    if (entity.getCreationDate() == null) {
      entity.setCreationDate(new Date());
    }

    Event event = new Event();
    event.setId(entity.getId());
    Calendar startCal = Calendar.getInstance();
    startCal.setTimeInMillis(entity.getStartDate().getTime());
    startCal.set(Calendar.HOUR, entity.getStartHour());
    startCal.set(Calendar.MINUTE, entity.getStartMinute());
    event.setStartDate(startCal.getTime());
    Calendar endCal = Calendar.getInstance();
    endCal.setTimeInMillis(entity.getEndDate().getTime());
    endCal.set(Calendar.HOUR, entity.getEndHour());
    endCal.set(Calendar.MINUTE, entity.getEndMinute());
    event.setEndDate(endCal.getTime());
    event.setEventType(DEFAULT_EVENT_TYPE);

    vertxContext.runOnContext(v -> postgresClient.startTx(beginTx -> {
      try {
        postgresClient.save(beginTx, EVENT_DESCRIPTION, entity, replyDescriptor -> {
          if (replyDescriptor.succeeded()) {
            try {
              postgresClient.save(beginTx, EVENT, event, replyEvent -> {
                if (!replyEvent.succeeded()) {
                  event.setId(replyDescriptor.result());
                  asyncResultHandler.handle(Future.succeededFuture(
                    PostCalendarEventdescriptionsResponse.withPlainInternalServerError(replyEvent.cause().getMessage())));
                } else {
                  postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(
                    Future.succeededFuture(PostCalendarEventdescriptionsResponse.withJsonCreated(entity))));
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
  public void getCalendarExclusions(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    asyncResultHandler
      .handle(Future.succeededFuture(GetCalendarExclusionsResponse.withJsonOK(new CalendarEventExclusionDescriptionCollection())));
  }

  @Override
  public void postCalendarExclusions(Exclusion entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

  }
}
