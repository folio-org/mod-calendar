package org.folio.rest.impl;

import io.vertx.core.*;
import org.folio.rest.jaxrs.model.*;
import org.folio.rest.jaxrs.resource.CalendarResource;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Map;

public class CalendarAPI implements CalendarResource {
  @Override
  public void getCalendarEvents(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    CalendarEventCollection calendarEventCollection = new CalendarEventCollection();
    calendarEventCollection.setEvents(new ArrayList<>());
    calendarEventCollection.setTotalRecords(calendarEventCollection.getEvents().size());

    asyncResultHandler
      .handle(Future.succeededFuture(GetCalendarEventsResponse.withJsonOK(calendarEventCollection)));
  }

  @Override
  public void getCalendarEventdescriptions(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    asyncResultHandler
      .handle(Future.succeededFuture(GetCalendarEventdescriptionsResponse.withJsonOK(new CalendarEventDescriptionCollection())));
  }

  @Override
  public void postCalendarEventdescriptions(Description entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

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
