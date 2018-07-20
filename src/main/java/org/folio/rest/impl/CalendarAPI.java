package org.folio.rest.impl;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.rest.annotations.Validate;
import org.folio.rest.beans.*;
import org.folio.rest.jaxrs.model.*;
import org.folio.rest.jaxrs.resource.CalendarResource;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.interfaces.Results;
import org.folio.rest.tools.messages.MessageConsts;
import org.folio.rest.tools.messages.Messages;
import org.folio.rest.tools.utils.TenantTool;
import org.folio.rest.utils.CalendarUtils;
import org.joda.time.DateTime;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.utils.CalendarConstants.*;
import static org.folio.rest.utils.CalendarUtils.DATE_FORMATTER_SHORT;


public class CalendarAPI implements CalendarResource {

  private final Messages messages = Messages.getInstance();
  private final Logger logger = LoggerFactory.getLogger(CalendarAPI.class);

  public CalendarAPI() {
    //stub
  }

  @Validate
  @Override
  public void postCalendarPeriodsByServicePointIdPeriod(String servicePointId, String lang, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    boolean isExceptional = entity.getOpeningDays().stream().noneMatch(p -> p.getWeekdays() != null);
    Openings openingsTable = new Openings(entity.getId(), entity.getServicePointId(), entity.getName(), entity.getStartDate(), entity.getEndDate(), isExceptional);
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
    Criterion criterionForId = assembleCriterionForCheckingOverlap(entity.getId(), servicePointId, entity.getStartDate(), entity.getEndDate(), isExceptional);
    vertxContext.runOnContext(v ->
      postgresClient.startTx(beginTx ->

        postgresClient.get(beginTx, OPENINGS, Openings.class, criterionForId, true, false, resultOfSelectOpenings -> {
          if (resultOfSelectOpenings.failed()) {
            asyncResultHandler.handle(Future.succeededFuture(
              PostCalendarPeriodsByServicePointIdPeriodResponse.withPlainInternalServerError(
                "Error while listing events.")));
          } else if (resultOfSelectOpenings.result().getResults().isEmpty()) {
            PostCalendarPeriodsRequestParams postCalendarPeriodsRequestParams = new PostCalendarPeriodsRequestParams(lang, entity, isExceptional, openingsTable);
            postgresClient.save(beginTx, OPENINGS, openingsTable, replyOfSavingOpenings ->
              saveRegularHours(postCalendarPeriodsRequestParams, postgresClient, asyncResultHandler, beginTx, replyOfSavingOpenings));
          } else {
            asyncResultHandler.handle(Future.succeededFuture(
              PostCalendarPeriodsByServicePointIdPeriodResponse.withPlainInternalServerError(
                "Intervals can not overlap.")));
          }
        })
      )
    );

  }


  private void saveRegularHours(PostCalendarPeriodsRequestParams postCalendarPeriodsRequestParams, PostgresClient postgresClient, Handler<AsyncResult<Response>> asyncResultHandler, AsyncResult<Object> beginTx, AsyncResult<String> replyOfSavingOpenings) {
    if (replyOfSavingOpenings.succeeded()) {
      RegularHours regularHours = new RegularHours(replyOfSavingOpenings.result(), postCalendarPeriodsRequestParams.getOpeningsTable().getId(), postCalendarPeriodsRequestParams.getEntity().getOpeningDays());
      postgresClient.save(REGULAR_HOURS, regularHours, replyOfSavingRegularHours -> {
        if (replyOfSavingRegularHours.succeeded()) {
          saveActualOpeningHours(postCalendarPeriodsRequestParams.getEntity(), postCalendarPeriodsRequestParams.getLang(), postCalendarPeriodsRequestParams.isExceptional(), asyncResultHandler, postgresClient, beginTx);
        } else {
          postgresClient.rollbackTx(beginTx, done ->
            asyncResultHandler.handle(Future.succeededFuture(PostCalendarPeriodsByServicePointIdPeriodResponse.withPlainInternalServerError(messages.getMessage(postCalendarPeriodsRequestParams.getLang(), MessageConsts.InternalServerError)))));
        }
      });
    } else {
      postgresClient.rollbackTx(beginTx, done ->
        asyncResultHandler.handle(Future.succeededFuture(PostCalendarPeriodsByServicePointIdPeriodResponse.withPlainInternalServerError(messages.getMessage(
          postCalendarPeriodsRequestParams.getLang(), MessageConsts.InternalServerError)))));
    }
  }

  private void saveActualOpeningHours(OpeningPeriod_ entity, String lang, boolean isExceptional, Handler<AsyncResult<Response>> asyncResultHandler, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Object> actualOpeningHours = CalendarUtils.separateEvents(entity, isExceptional);
    postgresClient.saveBatch(ACTUAL_OPENING_HOURS, actualOpeningHours, replyOfSavingActualOpeningHours -> {
      if (replyOfSavingActualOpeningHours.succeeded()) {
        postgresClient.endTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(PostCalendarPeriodsByServicePointIdPeriodResponse.withJsonCreated(lang, entity))));
      } else {
        postgresClient.rollbackTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(PostCalendarPeriodsByServicePointIdPeriodResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
      }
    });
  }

  @Validate
  @Override
  public void deleteCalendarPeriodsByServicePointIdPeriodByPeriodId(String openingId, String servicePointId, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);

    Criterion criterionForOpeningHours = new Criterion(new Criteria().addField(OPENING_ID).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingId + "'"));
    Criterion criterionForOpenings = new Criterion(new Criteria().addField(ID_FIELD).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingId + "'"));

    vertxContext.runOnContext(v ->
      postgresClient.startTx(beginTx ->
        postgresClient.delete(beginTx, ACTUAL_OPENING_HOURS, criterionForOpeningHours, replyOfDeletingActualOpeningHours -> {
          if (replyOfDeletingActualOpeningHours.succeeded()) {
            postgresClient.delete(beginTx, REGULAR_HOURS, criterionForOpeningHours, replyOfDeletingRegularHours -> {
              if (replyOfDeletingRegularHours.succeeded()) {
                deleteOpenings(asyncResultHandler, postgresClient, criterionForOpenings, lang, beginTx);
              } else {
                postgresClient.rollbackTx(beginTx, done ->
                  asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
              }
            });
          } else {
            postgresClient.rollbackTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        })
      )
    );
  }


  private void deleteOpenings(Handler<AsyncResult<Response>> asyncResultHandler, PostgresClient postgresClient, Criterion criterionForOpenings, String lang, AsyncResult<Object> beginTx) {
    postgresClient.delete(beginTx, OPENINGS, criterionForOpenings, replyOfDeletingOpenings -> {
      if (replyOfDeletingOpenings.succeeded()) {
        postgresClient.endTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withNoContent())));
      } else {
        postgresClient.rollbackTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(DeleteCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
      }
    });
  }

  @Validate
  @Override
  public void putCalendarPeriodsByServicePointIdPeriodByPeriodId(String openingId, String servicePointId, String lang, OpeningPeriod_ entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    boolean isExceptional = entity.getOpeningDays().stream().noneMatch(p -> p.getWeekdays() != null);
    Openings openingsTable = new Openings(entity.getId(), entity.getServicePointId(), entity.getName(), entity.getStartDate(), entity.getEndDate(), isExceptional);
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
    Criterion criterionForOpeningHours = new Criterion(new Criteria().addField(OPENING_ID).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingId + "'"));
    Criterion criterionForOpenings = new Criterion(new Criteria().addField(ID_FIELD).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingId + "'"));

    vertxContext.runOnContext(v ->
      postgresClient.startTx(beginTx ->
        postgresClient.update(OPENINGS, openingsTable, criterionForOpenings, true, replyOfSavingOpenings -> {
          if (replyOfSavingOpenings.succeeded()) {
            RegularHours regularHours = new RegularHours(openingId, openingsTable.getId(), entity.getOpeningDays());
            postgresClient.update(REGULAR_HOURS, regularHours, criterionForOpeningHours, true, replyOfSavingRegularHours -> {
              if (replyOfSavingRegularHours.succeeded()) {
                putActualOpeningHours(entity, criterionForOpeningHours, lang, isExceptional, asyncResultHandler, postgresClient, beginTx);
              } else {
                postgresClient.rollbackTx(beginTx, done ->
                  asyncResultHandler.handle(Future.succeededFuture(PutCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
              }
            });
          } else {
            postgresClient.rollbackTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(PutCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        })
      )
    );
  }

  private void putActualOpeningHours(OpeningPeriod_ entity, Criterion openingId, String lang, boolean isExceptional, Handler<AsyncResult<Response>> asyncResultHandler, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Object> actualOpeningHours = CalendarUtils.separateEvents(entity, isExceptional);
    postgresClient.delete(beginTx, ACTUAL_OPENING_HOURS, openingId, replyOfDeletingActualOpeningHours -> {
      if (replyOfDeletingActualOpeningHours.succeeded()) {
        postgresClient.saveBatch(ACTUAL_OPENING_HOURS, actualOpeningHours, replyOfSavingActualOpeningHours -> {
          if (replyOfSavingActualOpeningHours.succeeded()) {
            postgresClient.endTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(PutCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withJsonNoContent(entity))));
          } else {
            postgresClient.rollbackTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(PutCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        });
      } else {
        postgresClient.rollbackTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(PutCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
      }
    });
  }

  @Validate
  @Override
  public void getCalendarPeriods(String servicePointId, String startDate, String endDate, boolean includeClosedDays, boolean actualOpenings, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    OpeningCollection openingCollection = new OpeningCollection();
    CalendarOpeningsRequestParameters calendarOpeningsRequestParameters = new CalendarOpeningsRequestParameters(startDate, endDate, offset, limit, lang, includeClosedDays, actualOpenings);
    try {
      logger.info("getCalendarPeriods...");
      vertxContext.runOnContext(a -> {

        String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
        PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
        Criteria critServicePoint = new Criteria().addField(SERVICE_POINT_ID).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + servicePointId + "'");
        Criterion criterionForServicePoint = new Criterion().addCriterion(critServicePoint);

        postgresClient.startTx(beginTx ->
          postgresClient.get(beginTx, OPENINGS, Openings.class, criterionForServicePoint, true, false, resultOfSelectOpenings -> {
            if (resultOfSelectOpenings.succeeded()) {
              addOpeningPeriodsToCollection(openingCollection, resultOfSelectOpenings);
              getOpeningDaysByDatesFuture(asyncResultHandler, openingCollection, calendarOpeningsRequestParameters, postgresClient, beginTx);
            } else {
              postgresClient.endTx(beginTx, done ->
                asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
            }
          }));
      });
    } catch (Exception ex) {
      logger.error("error: {}", ex.getCause());
      asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsResponse.withPlainBadRequest(
        ex.getLocalizedMessage())));
    }
  }

  @Override
  public void getCalendarPeriodsByServicePointIdPeriodByPeriodId(String openingId, String servicePointId, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    logger.info("getCalendarPeriodsByServicePointIdPeriodByPeriodId...");
    OpeningCollection openingCollection = new OpeningCollection();
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    Criteria critOpeningId = new Criteria().addField(ID_FIELD).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingId + "'");
    Criterion criterionForOpeningHours = new Criterion();
    criterionForOpeningHours.addCriterion(critOpeningId, Criteria.OP_AND);

    vertxContext.runOnContext(a ->
      postgresClient.startTx(beginTx ->
        postgresClient.get(beginTx, OPENINGS, Openings.class, criterionForOpeningHours, true, false, resultOfSelectOpenings -> {
          if (resultOfSelectOpenings.succeeded()) {
            addOpeningPeriodsToCollection(openingCollection, resultOfSelectOpenings);
            getOpeningDaysByOpeningIdFuture(asyncResultHandler, openingCollection, lang, openingId, postgresClient, beginTx);
          } else {
            postgresClient.endTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        })));
  }

  @Validate
  @Override
  public void getCalendarPeriodsByServicePointIdPeriod(String servicePointId, boolean withOpeningDays, boolean showPast, boolean exceptional, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    logger.info("getCalendarPeriodsByServicePointIdPeriod...");
    OpeningCollection openingCollection = new OpeningCollection();
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    Criterion criterionForOpeningHours = assembleCriterionByServicePointId(servicePointId, showPast, exceptional);

    vertxContext.runOnContext(a ->
      postgresClient.startTx(beginTx ->
        postgresClient.get(beginTx, OPENINGS, Openings.class, criterionForOpeningHours, true, false, resultOfSelectOpenings -> {
          if (resultOfSelectOpenings.succeeded()) {
            addOpeningPeriodsToCollection(openingCollection, resultOfSelectOpenings);
            if (withOpeningDays) {
              getOpeningDaysByServicePointIdFuture(asyncResultHandler, openingCollection, lang, postgresClient, beginTx);
            } else {
              postgresClient.endTx(beginTx, done ->
                asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsByServicePointIdPeriodResponse.withJsonOK(openingCollection))));
            }
          } else {
            postgresClient.endTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsByServicePointIdPeriodResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        })));
  }

  private void getOpeningDaysByServicePointIdFuture(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection, String lang, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Future> futures = getOpeningDays(asyncResultHandler, openingCollection, lang, postgresClient, beginTx);

    CompositeFuture.all(futures).setHandler(querys -> {
      if (querys.succeeded()) {
        postgresClient.endTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsByServicePointIdPeriodResponse.withJsonOK(openingCollection))));
      } else {
        postgresClient.endTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsByServicePointIdPeriodResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
      }
    });
  }

  private void getOpeningDaysByDatesFuture(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection, CalendarOpeningsRequestParameters calendarOpeningsRequestParameters, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    OpeningHoursCollection openingHoursCollection = new OpeningHoursCollection();
    List<Future> futures = getOpeningDaysByDate(asyncResultHandler, openingHoursCollection, openingCollection, calendarOpeningsRequestParameters, postgresClient, beginTx);
    CompositeFuture.all(futures).setHandler(querys -> {
      if (querys.succeeded()) {
        if (calendarOpeningsRequestParameters.isIncludeClosedDays()) {
          CalendarUtils.addClosedDaysToOpenings(openingHoursCollection.getOpeningPeriods(), calendarOpeningsRequestParameters);
        }
        openingHoursCollection.setTotalRecords(openingHoursCollection.getOpeningPeriods().size());
        openingHoursCollection.getOpeningPeriods().sort(Comparator.comparing(OpeningPeriod::getDate));
        openingHoursCollection.setOpeningPeriods(openingHoursCollection.getOpeningPeriods().stream().skip(calendarOpeningsRequestParameters.getOffset()).limit(calendarOpeningsRequestParameters.getLimit()).collect(Collectors.toList()));
        postgresClient.endTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsResponse.withJsonOK(openingHoursCollection))));
      } else {
        postgresClient.endTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsResponse.withPlainInternalServerError(messages.getMessage(calendarOpeningsRequestParameters.getLang(), MessageConsts.InternalServerError)))));
      }
    });
  }

  private void getOpeningDaysByOpeningIdFuture(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection, String lang, String openingId, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Future> futures = getOpeningDays(asyncResultHandler, openingCollection, lang, postgresClient, beginTx);
    if (!futures.isEmpty()) {
      CompositeFuture.all(futures).setHandler(querys -> {
        if (querys.succeeded()) {
          postgresClient.endTx(beginTx, done ->
            asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withJsonOK(openingCollection.getOpeningPeriods().get(0)))));
        } else {
          postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withPlainNotFound(openingId))));
        }
      });
    } else {
      postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsByServicePointIdPeriodByPeriodIdResponse.withPlainNotFound(openingId))));
    }
  }

  private Criterion assembleCriterionByServicePointId(String servicePointId, boolean showPast, boolean exceptional) {
    Criteria critServicePoint = new Criteria().addField(SERVICE_POINT_ID).setJSONB(true).setOperation("=").setValue("'" + servicePointId + "'");
    Criteria critShowPast = new Criteria();
    Criteria critExceptional = new Criteria().addField(EXCEPTIONAL).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + exceptional + "'");

    critShowPast.addField(END_DATE);
    critShowPast.setOperation(Criteria.OP_GREATER_THAN_EQ);
    critShowPast.setValue(DATE_FORMATTER_SHORT.print(new DateTime()));

    Criterion criterionForOpeningHours = new Criterion();
    if (!showPast) {
      criterionForOpeningHours.addCriterion(critServicePoint, Criteria.OP_AND, critShowPast);
    } else {
      criterionForOpeningHours.addCriterion(critServicePoint, Criteria.OP_AND);
    }
    criterionForOpeningHours.addCriterion(critExceptional, Criteria.OP_AND);
    return criterionForOpeningHours;
  }


  private Criterion assembleCriterionByRange(String openingId, String startDate, String endDate) {
    Criteria critOpeningId = new Criteria().addField(OPENING_ID).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingId + "'");
    Criteria critStartDate = new Criteria();
    critStartDate.addField(ACTUAL_DAY);
    critStartDate.setOperation(Criteria.OP_GREATER_THAN_EQ);
    critStartDate.setValue(DATE_FORMATTER_SHORT.print(new DateTime(startDate)));

    Criteria critEndDate = new Criteria();
    critEndDate.addField(ACTUAL_DAY);
    critEndDate.setOperation(Criteria.OP_LESS_THAN_EQ);
    critEndDate.setValue(DATE_FORMATTER_SHORT.print(new DateTime(endDate)));

    Criterion criterionForOpeningHours = new Criterion();

    criterionForOpeningHours.addCriterion(critOpeningId, Criteria.OP_AND);
    if (startDate != null) {
      criterionForOpeningHours.addCriterion(critStartDate, Criteria.OP_AND);
    }
    if (endDate != null) {
      criterionForOpeningHours.addCriterion(critEndDate, Criteria.OP_AND);
    }
    return criterionForOpeningHours;
  }


  private Criterion assembleCriterionForCheckingOverlap(String openingId, String servicePointId, Date startDate, Date endDate, boolean exceptional) {
    Criteria critOpeningId = new Criteria().addField(ID_FIELD).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingId + "'");
    Criteria critServicePoint = new Criteria().addField(SERVICE_POINT_ID).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + servicePointId + "'");
    Criteria critExceptional = new Criteria().addField(EXCEPTIONAL).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + exceptional + "'");
    Criteria critStartDate = new Criteria();
    critStartDate.addField(START_DATE);
    critStartDate.setOperation(Criteria.OP_LESS_THAN_EQ);
    critStartDate.setValue(CalendarUtils.DATE_FORMATTER.print(new DateTime(CalendarUtils.getDateWithoutHoursAndMinutes(startDate))));

    Criteria critEndDate = new Criteria();
    critEndDate.addField(END_DATE);
    critEndDate.setOperation(Criteria.OP_GREATER_THAN_EQ);
    critEndDate.setValue(CalendarUtils.DATE_FORMATTER.print(new DateTime(CalendarUtils.getDateWithoutHoursAndMinutes(endDate))));

    Criterion criterionForOpeningHours = new Criterion();
    criterionForOpeningHours.addCriterion(critExceptional, Criteria.OP_AND);
    criterionForOpeningHours.addCriterion(critServicePoint, Criteria.OP_AND);
    criterionForOpeningHours.addCriterion(critStartDate, Criteria.OP_AND, critEndDate);
    criterionForOpeningHours.addCriterion(critOpeningId, Criteria.OP_OR);


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

  private List<Future> getOpeningDays(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection, String lang, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Future> futures = new ArrayList<>();
    for (OpeningPeriod_ openingPeriod : openingCollection.getOpeningPeriods()) {
      Criterion criterionForRegularHours = new Criterion(new Criteria().addField(OPENING_ID).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingPeriod.getId() + "'"));
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
            }

            future.complete();
          } catch (ClassCastException ex) {
            future.fail(resultOfSelectRegularHours.cause());
            postgresClient.endTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsByServicePointIdPeriodResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        } else {
          future.fail(resultOfSelectRegularHours.cause());
          postgresClient.endTx(beginTx, done ->
            asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsByServicePointIdPeriodResponse.withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError)))));
        }
      });
    }

    return futures;
  }

  private List<Future> getOpeningDaysByDate(Handler<AsyncResult<Response>> asyncResultHandler, OpeningHoursCollection openingHoursCollection, OpeningCollection openingCollection, CalendarOpeningsRequestParameters calendarOpeningsRequestParameters, PostgresClient postgresClient, AsyncResult<Object> beginTx) {
    List<Future> futures = new ArrayList<>();
    List<OpeningPeriod> openingPeriods = new ArrayList<>();
    for (OpeningPeriod_ openingPeriod_ : openingCollection.getOpeningPeriods()) {
      Criterion criterionForOpeningHours = assembleCriterionByRange(openingPeriod_.getId(), calendarOpeningsRequestParameters.getStartDate(), calendarOpeningsRequestParameters.getEndDate());
      Future<Void> future = Future.future();
      futures.add(future);
      postgresClient.get(ACTUAL_OPENING_HOURS, ActualOpeningHours.class, criterionForOpeningHours, true, false, resultOfSelectActualOpeningHours -> {
        if (resultOfSelectActualOpeningHours.succeeded()) {
          try {
            List<ActualOpeningHours> actualOpeningHours = (List<ActualOpeningHours>) resultOfSelectActualOpeningHours.result().getResults();
            for (ActualOpeningHours actualOpeningHour : actualOpeningHours) {
              setOpeningPeriods(openingHoursCollection, openingPeriods, actualOpeningHour);
            }
            future.complete();
          } catch (ClassCastException ex) {
            future.fail(resultOfSelectActualOpeningHours.cause());
            postgresClient.endTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsResponse.withPlainInternalServerError(messages.getMessage(calendarOpeningsRequestParameters.getLang(), MessageConsts.InternalServerError)))));
          }
        } else {
          future.fail(resultOfSelectActualOpeningHours.cause());
          postgresClient.endTx(beginTx, done ->
            asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsResponse.withPlainInternalServerError(messages.getMessage(calendarOpeningsRequestParameters.getLang(), MessageConsts.InternalServerError)))));
        }
      });
    }

    return futures;
  }


  private void setOpeningPeriods(OpeningHoursCollection openingHoursCollection, List<OpeningPeriod> openingPeriods, ActualOpeningHours actualOpeningHour) {
    OpeningPeriod openingPeriod = new OpeningPeriod();
    OpeningDay openingDay = new OpeningDay();
    openingDay.setOpen(actualOpeningHour.getOpen());
    openingDay.setAllDay(actualOpeningHour.getAllDay());
    openingDay.setExceptional(actualOpeningHour.getExceptional());
    List<OpeningHour> openingHours = new ArrayList<>();
    OpeningHour openingHour = new OpeningHour();
    openingHour.setStartTime(actualOpeningHour.getStartTime());
    openingHour.setEndTime(actualOpeningHour.getEndTime());
    openingHours.add(openingHour);

    openingDay.setOpeningHour(openingHours);
    openingPeriod.setOpeningDay(openingDay);

    openingPeriod.setDate(actualOpeningHour.getActualDay());

    if (openingPeriods.stream().anyMatch(o -> o.getDate().equals(actualOpeningHour.getActualDay()))) {
      OpeningPeriod previousOpeningPeriod = openingPeriods.stream().filter(o -> o.getDate().equals(actualOpeningHour.getActualDay())).findFirst().orElse(openingPeriod);
      previousOpeningPeriod.getOpeningDay().getOpeningHour().add(openingHour);
    } else {
      openingPeriods.add(openingPeriod);
    }
    openingHoursCollection.setOpeningPeriods(openingPeriods);
  }

  private static PostgresClient getPostgresClient(Map<String, String> okapiHeaders, Context vertxContext) {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    return PostgresClient.getInstance(vertxContext.owner(), tenantId);
  }


}
