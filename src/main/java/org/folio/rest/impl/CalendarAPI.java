package org.folio.rest.impl;

import io.vertx.core.*;
import io.vertx.ext.sql.SQLConnection;
import joptsimple.internal.Strings;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.rest.annotations.Validate;
import org.folio.rest.beans.*;
import org.folio.rest.jaxrs.model.*;
import org.folio.rest.jaxrs.resource.Calendar;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.utils.CalendarConstants.*;
import static org.folio.rest.utils.CalendarUtils.DATE_FORMATTER_SHORT;


public class CalendarAPI implements Calendar {

  private final Messages messages = Messages.getInstance();

  public CalendarAPI() {
    //stub
  }

  @Validate
  @Override
  public void postCalendarPeriodsPeriodByServicePointId(String servicePointId, String lang, OpeningPeriod entity,
    Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    boolean isExceptional = entity.getOpeningDays().stream().noneMatch(p -> p.getWeekdays() != null);
    Openings openingsTable = new Openings(entity.getId(), entity.getServicePointId(), entity.getName(), entity.getStartDate(), entity.getEndDate(), isExceptional);
    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
    Criterion criterionForId = assembleCriterionForCheckingOverlap(entity.getId(), servicePointId, entity.getStartDate(), entity.getEndDate(), isExceptional);
    if (entity.getOpeningDays().isEmpty() || Strings.isNullOrEmpty(entity.getServicePointId())
      || Strings.isNullOrEmpty(entity.getName()) || Strings.isNullOrEmpty(entity.getId())) {
      asyncResultHandler.handle(Future.succeededFuture(
        PostCalendarPeriodsPeriodByServicePointIdResponse.respond400WithTextPlain(
          "Not valid json object. Missing field(s)...")));
    }
    vertxContext.runOnContext(v
      -> postgresClient.startTx(beginTx
        -> postgresClient.get(beginTx, OPENINGS, Openings.class, criterionForId, true, false, resultOfSelectOpenings -> {
        if (resultOfSelectOpenings.failed()) {
          asyncResultHandler.handle(Future.succeededFuture(
            PostCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(
              "Error while listing events.")));
        } else if (resultOfSelectOpenings.result().getResults().isEmpty()) {
          PostCalendarPeriodsRequestParams postCalendarPeriodsRequestParams = new PostCalendarPeriodsRequestParams(lang, entity, isExceptional, openingsTable);
          postgresClient.save(beginTx, OPENINGS, openingsTable, replyOfSavingOpenings
            -> saveRegularHours(postCalendarPeriodsRequestParams, postgresClient, asyncResultHandler, beginTx, replyOfSavingOpenings));
        } else {
          asyncResultHandler.handle(Future.succeededFuture(
            PostCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(
              "Intervals can not overlap.")));
        }
      })
      )
    );

  }

  @Validate
  @Override
  public void deleteCalendarPeriodsPeriodByServicePointIdAndPeriodId(String openingId, String servicePointId, String lang,
    Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
    Criterion criterionForOpeningHours = new Criterion(new Criteria().addField(OPENING_ID)
      .setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingId + "'"));
    Criterion criterionForOpenings = new Criterion(new Criteria().addField(ID_FIELD)
      .setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingId + "'"));

    vertxContext.runOnContext(v
      -> postgresClient.startTx(beginTx
        -> postgresClient.delete(beginTx, ACTUAL_OPENING_HOURS, criterionForOpeningHours, replyOfDeletingActualOpeningHours -> {
        if (replyOfDeletingActualOpeningHours.succeeded()) {
          postgresClient.delete(beginTx, REGULAR_HOURS, criterionForOpeningHours, replyOfDeletingRegularHours -> {
            if (replyOfDeletingRegularHours.succeeded()) {
              deleteOpenings(asyncResultHandler, postgresClient, criterionForOpenings, lang, beginTx);
            } else {
              postgresClient.rollbackTx(beginTx, done
                -> asyncResultHandler.handle(Future.succeededFuture(
                  DeleteCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.
                    respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
            }
          });
        } else {
          postgresClient.rollbackTx(beginTx, done
            -> asyncResultHandler.handle(Future.succeededFuture(
              DeleteCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.
                respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
        }
      })
      )
    );
  }

  @Validate
  @Override
  public void putCalendarPeriodsPeriodByServicePointIdAndPeriodId(String openingId, String servicePointId, String lang, OpeningPeriod entity,
    Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

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
                  asyncResultHandler.handle(Future.succeededFuture(
                    PutCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
              }
            });
          } else {
            postgresClient.rollbackTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(PutCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        })
      )
    );
  }

  @Validate
  @Override
  public void getCalendarPeriods(String servicePointId, String startDate, String endDate, boolean includeClosedDays, boolean actualOpenings, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    OpeningCollection openingCollection = new OpeningCollection();
    CalendarOpeningsRequestParameters calendarOpeningsRequestParameters = new CalendarOpeningsRequestParameters(startDate, endDate, offset, limit, lang, includeClosedDays, actualOpenings);

    vertxContext.runOnContext(a -> {

      String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
      PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
      Criteria critServicePoint;
      Criterion criterionForServicePoint;
      if (servicePointId != null) {
        critServicePoint = new Criteria().addField(SERVICE_POINT_ID).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + servicePointId + "'");
        criterionForServicePoint = new Criterion().addCriterion(critServicePoint);
      } else {
        criterionForServicePoint = new Criterion();
      }

      postgresClient.startTx(beginTx ->
        postgresClient.get(beginTx, OPENINGS, Openings.class, criterionForServicePoint, true, false, resultOfSelectOpenings -> {
          if (resultOfSelectOpenings.succeeded()) {
            addOpeningPeriodsToCollection(openingCollection, resultOfSelectOpenings);
            getOpeningDaysByDatesFuture(asyncResultHandler, openingCollection, calendarOpeningsRequestParameters, postgresClient, beginTx);
          } else {
            postgresClient.endTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsResponse.respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        }));
    });
  }

  @Override
  public void getCalendarPeriodsPeriodByServicePointIdAndPeriodId(String servicePointId, String openingId, String lang,
    Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

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
              asyncResultHandler.handle(Future.succeededFuture(
                GetCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        })));
  }

  @Validate
  @Override
  public void getCalendarPeriodsPeriodByServicePointId(String servicePointId, boolean withOpeningDays, boolean showPast, boolean exceptional,
    String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

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
                asyncResultHandler.handle(Future.succeededFuture(
                  GetCalendarPeriodsPeriodByServicePointIdResponse.respond200WithApplicationJson(openingCollection))));
            }
          } else {
            postgresClient.endTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(
                GetCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        })));
  }

  @Validate
  @Override
  public void getCalendarPeriodsCalculateopeningByServicePointId(String servicePointId,
                                                                 String startDate,
                                                                 CalendarPeriodsServicePointIdCalculateopeningGetUnit unit,
                                                                 int amount,
                                                                 String lang,
                                                                 Map<String, String> okapiHeaders,
                                                                 Handler<AsyncResult<Response>> asyncResultHandler,
                                                                 Context vertxContext) {

    if (unit != null && unit != CalendarPeriodsServicePointIdCalculateopeningGetUnit.DAY && unit != CalendarPeriodsServicePointIdCalculateopeningGetUnit.HOUR) {
      asyncResultHandler.handle(Future.succeededFuture(
        GetCalendarPeriodsCalculateopeningByServicePointIdResponse.respond400WithTextPlain(
         messages.getMessage(lang, MessageConsts.InvalidParameters))));
    }
    if (amount <= 0) {
      asyncResultHandler.handle(Future.succeededFuture(
        GetCalendarPeriodsCalculateopeningByServicePointIdResponse.respond400WithTextPlain(
          messages.getMessage(lang, MessageConsts.InvalidParameters))));
    }

    OpeningCollection openingCollection = new OpeningCollection();
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);

    ZonedDateTime loanStartDateTime;
    ZonedDateTime loanEndDateTime;
    if (unit != null && unit == CalendarPeriodsServicePointIdCalculateopeningGetUnit.DAY) {
      loanStartDateTime = ZonedDateTime.of(LocalDate.parse(startDate).atStartOfDay(), ZoneId.of("UTC"));
      loanEndDateTime = loanStartDateTime.plusDays(amount);
    } else{
      loanStartDateTime = ZonedDateTime.of(LocalDate.parse(startDate).atTime(LocalTime.now()), ZoneId.of("UTC"));
      loanEndDateTime = loanStartDateTime.plusHours(amount);
    }

    Criteria critOpeningServicePointId = new Criteria().addField(SERVICE_POINT_ID).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + servicePointId + "'");
    Criteria critOpeningByStartDateTime = new Criteria().addField(START_DATE).setJSONB(true).setOperation(Criteria.OP_LESS_THAN_EQ).setValue(loanStartDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    Criteria critOpeningByEndDateTime = new Criteria().addField(END_DATE).setJSONB(true).setOperation(Criteria.OP_GREATER_THAN_EQ).setValue(loanEndDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    Criterion criterionForOpeningHours = new Criterion();
    criterionForOpeningHours.addCriterion(critOpeningServicePointId, Criteria.OP_AND);
    criterionForOpeningHours.addCriterion(critOpeningByStartDateTime, Criteria.OP_AND, critOpeningByEndDateTime);

    vertxContext.runOnContext(a ->
      postgresClient.startTx(beginTx ->
        postgresClient.get(beginTx, OPENINGS, Openings.class, criterionForOpeningHours, true, false, resultOfSelectOpenings -> {
          if (resultOfSelectOpenings.succeeded()) {
            addOpeningPeriodsToCollection(openingCollection, resultOfSelectOpenings);
            if (CollectionUtils.isNotEmpty(openingCollection.getOpeningPeriods()) && openingCollection.getOpeningPeriods().size() == 1) {
              String openingId = openingCollection.getOpeningPeriods().get(0).getId();
              getOpening3DaysByOpeningIdFuture(asyncResultHandler, openingCollection, lang, openingId, loanEndDateTime, postgresClient, beginTx);
            } else {
              postgresClient.endTx(beginTx, done ->
                asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsCalculateopeningByServicePointIdResponse.respond404WithTextPlain(messages.getMessage(lang, MessageConsts.ObjectDoesNotExist)))));
            }
          } else {
            postgresClient.endTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsCalculateopeningByServicePointIdResponse.respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        })));
  }

  private void saveRegularHours(PostCalendarPeriodsRequestParams postCalendarPeriodsRequestParams, PostgresClient postgresClient, Handler<AsyncResult<Response>> asyncResultHandler, AsyncResult<SQLConnection> beginTx, AsyncResult<String> replyOfSavingOpenings) {
    if (replyOfSavingOpenings.succeeded()) {
      RegularHours regularHours = new RegularHours(replyOfSavingOpenings.result(), postCalendarPeriodsRequestParams.getOpeningsTable().getId(), postCalendarPeriodsRequestParams.getEntity().getOpeningDays());
      postgresClient.save(REGULAR_HOURS, regularHours, replyOfSavingRegularHours -> {
        if (replyOfSavingRegularHours.succeeded()) {
          saveActualOpeningHours(postCalendarPeriodsRequestParams.getEntity(), postCalendarPeriodsRequestParams.getLang(), postCalendarPeriodsRequestParams.isExceptional(), asyncResultHandler, postgresClient, beginTx);
        } else {
          postgresClient.rollbackTx(beginTx, done ->
            asyncResultHandler.handle(Future.succeededFuture(
              PostCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(messages.getMessage(postCalendarPeriodsRequestParams.getLang(), MessageConsts.InternalServerError)))));
        }
      });
    } else {
      postgresClient.rollbackTx(beginTx, done ->
        asyncResultHandler.handle(Future.succeededFuture(
          PostCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(messages.getMessage(
          postCalendarPeriodsRequestParams.getLang(), MessageConsts.InternalServerError)))));
    }
  }

  private void saveActualOpeningHours(OpeningPeriod entity, String lang, boolean isExceptional,
    Handler<AsyncResult<Response>> asyncResultHandler, PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {

    List<Object> actualOpeningHours = CalendarUtils.separateEvents(entity, isExceptional);
    if (!actualOpeningHours.isEmpty()) {
      postgresClient.saveBatch(ACTUAL_OPENING_HOURS, actualOpeningHours, replyOfSavingActualOpeningHours -> {
        if (replyOfSavingActualOpeningHours.succeeded()) {
          postgresClient.endTx(beginTx, done
            -> asyncResultHandler.handle(Future.succeededFuture(
              PostCalendarPeriodsPeriodByServicePointIdResponse.respond201WithApplicationJson(entity,
                PostCalendarPeriodsPeriodByServicePointIdResponse.headersFor201().withLocation(lang)))));
        } else {
          postgresClient.rollbackTx(beginTx, done
            -> asyncResultHandler.handle(Future.succeededFuture(
              PostCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(
                messages.getMessage(lang, MessageConsts.InternalServerError)))));
        }
      });
    } else {
      postgresClient.endTx(beginTx, done
        -> asyncResultHandler.handle(Future.succeededFuture(
          PostCalendarPeriodsPeriodByServicePointIdResponse.respond201WithApplicationJson(entity,
            PostCalendarPeriodsPeriodByServicePointIdResponse.headersFor201().withLocation(lang)))));
    }
  }

  private void deleteOpenings(Handler<AsyncResult<Response>> asyncResultHandler,
    PostgresClient postgresClient, Criterion criterionForOpenings, String lang, AsyncResult<SQLConnection> beginTx) {

    postgresClient.delete(beginTx, OPENINGS, criterionForOpenings, replyOfDeletingOpenings -> {
      if (replyOfDeletingOpenings.succeeded()) {
        postgresClient.endTx(beginTx, done
          -> asyncResultHandler.handle(Future.succeededFuture(
            DeleteCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond204())));
      } else {
        postgresClient.rollbackTx(beginTx, done
          -> asyncResultHandler.handle(Future.succeededFuture(
            DeleteCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond500WithTextPlain(
              messages.getMessage(lang, MessageConsts.InternalServerError)))));
      }
    });
  }

  private void putActualOpeningHours(OpeningPeriod entity, Criterion openingId, String lang,
    boolean isExceptional, Handler<AsyncResult<Response>> asyncResultHandler,
    PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {

    List<Object> actualOpeningHours = CalendarUtils.separateEvents(entity, isExceptional);
    postgresClient.delete(beginTx, ACTUAL_OPENING_HOURS, openingId, replyOfDeletingActualOpeningHours -> {
      if (replyOfDeletingActualOpeningHours.succeeded()) {
        postgresClient.saveBatch(ACTUAL_OPENING_HOURS, actualOpeningHours, replyOfSavingActualOpeningHours -> {
          if (replyOfSavingActualOpeningHours.succeeded()) {
            postgresClient.endTx(beginTx, done
              -> asyncResultHandler.handle(Future.succeededFuture(
                PutCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.
                  respond204WithApplicationJson(entity))));
          } else {
            postgresClient.rollbackTx(beginTx, done
              -> asyncResultHandler.handle(Future.succeededFuture(
                PutCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond500WithTextPlain(
                  messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        });
      } else {
        postgresClient.rollbackTx(beginTx, done
          -> asyncResultHandler.handle(Future.succeededFuture(
            PutCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond500WithTextPlain(
              messages.getMessage(lang, MessageConsts.InternalServerError)))));
      }
    });
  }

  private void getOpeningDaysByServicePointIdFuture(Handler<AsyncResult<Response>> asyncResultHandler,
    OpeningCollection openingCollection, String lang, PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {

    List<Future> futures = getOpeningDays(asyncResultHandler, openingCollection, lang, null, postgresClient, beginTx);
    CompositeFuture.all(futures).setHandler(querys -> {
      if (querys.succeeded()) {
        postgresClient.endTx(beginTx, done
          -> asyncResultHandler.handle(Future.succeededFuture(
            GetCalendarPeriodsPeriodByServicePointIdResponse.respond200WithApplicationJson(
              openingCollection))));
      } else {
        postgresClient.endTx(beginTx, done
          -> asyncResultHandler.handle(Future.succeededFuture(
            GetCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(
              messages.getMessage(lang, MessageConsts.InternalServerError)))));
      }
    });
  }

  private void getOpeningDaysByDatesFuture(Handler<AsyncResult<Response>> asyncResultHandler,
    OpeningCollection openingCollection, CalendarOpeningsRequestParameters calendarOpeningsRequestParameters,
    PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {

    OpeningHoursCollection openingHoursCollection = new OpeningHoursCollection();
    List<Future> futures = getOpeningDaysByDate(asyncResultHandler, openingHoursCollection, openingCollection, calendarOpeningsRequestParameters, postgresClient, beginTx);
    CompositeFuture.all(futures).setHandler(querys -> {
      if (querys.succeeded()) {
        if (calendarOpeningsRequestParameters.isIncludeClosedDays() && !openingHoursCollection.getOpeningPeriods().isEmpty()) {
          CalendarUtils.addClosedDaysToOpenings(openingHoursCollection.getOpeningPeriods(), calendarOpeningsRequestParameters);
        }
        openingHoursCollection.getOpeningPeriods().sort(Comparator.comparing(OpeningHoursPeriod::getDate).thenComparing(o -> 
          !o.getOpeningDay().getExceptional()));
        if (calendarOpeningsRequestParameters.isActualOpenings()) {
          overrideOpeningPeriodsByExceptionalPeriods(openingHoursCollection);
        }
        openingHoursCollection.setTotalRecords(openingHoursCollection.getOpeningPeriods().size());
        openingHoursCollection.setOpeningPeriods(openingHoursCollection.getOpeningPeriods().stream().skip(calendarOpeningsRequestParameters.getOffset()).limit(calendarOpeningsRequestParameters.getLimit()).collect(Collectors.toList()));
        postgresClient.endTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(
            GetCalendarPeriodsResponse.respond200WithApplicationJson(openingHoursCollection))));
      } else {
        postgresClient.endTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsResponse.respond500WithTextPlain(messages.getMessage(calendarOpeningsRequestParameters.getLang(), MessageConsts.InternalServerError)))));
      }
    });
  }

  private void overrideOpeningPeriodsByExceptionalPeriods(OpeningHoursCollection openingHoursCollection) {
    openingHoursCollection.setOpeningPeriods(openingHoursCollection.getOpeningPeriods().stream().reduce(new ArrayList<>(), (List<OpeningHoursPeriod> accumulator, OpeningHoursPeriod openingPeriod) ->
    {
      if (accumulator.stream().noneMatch(op ->
        op.getDate().equals(openingPeriod.getDate())
          && !op.getOpeningDay().getExceptional().equals(openingPeriod.getOpeningDay().getExceptional()))) {
        accumulator.add(openingPeriod);
      }
      return accumulator;
    }, (acc1, acc2) -> {
      acc1.addAll(acc2);
      return acc1;
    }));
  }

  private void getOpeningDaysByOpeningIdFuture(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection, String lang, String openingId, PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {
    List<Future> futures = getOpeningDays(asyncResultHandler, openingCollection, lang, postgresClient, beginTx);
    endCalculation(asyncResultHandler, futures, openingCollection, openingId, postgresClient, beginTx);
  }

  private void getOpening3DaysByOpeningIdFuture(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection, String lang, String openingId, ZonedDateTime loanEndDateTime, PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {
    List<Future> futures = getOpeningDays(asyncResultHandler, openingCollection, lang, loanEndDateTime, postgresClient, beginTx);
    endCalculation(asyncResultHandler, futures, openingCollection, openingId, postgresClient, beginTx);
  }

  private void endCalculation(Handler<AsyncResult<Response>> asyncResultHandler, List<Future> futures, OpeningCollection openingCollection, String openingId, PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {
    if (!futures.isEmpty()) {
      CompositeFuture.all(futures).setHandler(querys -> {
        if (querys.succeeded()) {
          postgresClient.endTx(beginTx, done
            -> asyncResultHandler.handle(Future.succeededFuture(
              GetCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.
                respond200WithApplicationJson(openingCollection.getOpeningPeriods().get(0)))));
        } else {
          postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(
            GetCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond404WithTextPlain(openingId))));
        }
      });
    } else {
      postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(
        GetCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond404WithTextPlain(openingId))));
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

  private void addOpeningPeriodsToCollection(OpeningCollection openingCollection, AsyncResult<Results<Openings>> resultOfSelectOpenings) {
    openingCollection.setTotalRecords(resultOfSelectOpenings.result().getResults().size());
    for (Object object : resultOfSelectOpenings.result().getResults()) {
      if (object instanceof Openings) {
        Openings openings = (Openings) object;
        OpeningPeriod openingPeriod = new OpeningPeriod();
        openingPeriod.setStartDate(openings.getStartDate());
        openingPeriod.setEndDate(openings.getEndDate());
        openingPeriod.setName(openings.getName());
        openingPeriod.setServicePointId(openings.getServicePointId());
        openingPeriod.setId(openings.getId());
        openingCollection.getOpeningPeriods().add(openingPeriod);
      }
    }
  }

  private List<Future> getOpeningDays(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection, String lang, PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {
    return getOpeningDays(asyncResultHandler, openingCollection, lang, null, postgresClient, beginTx);
  }

  private List<Future> getOpeningDays(Handler<AsyncResult<Response>> asyncResultHandler,
    OpeningCollection openingCollection, String lang, ZonedDateTime loanEndDateTime, PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {
 
    List<Future> futures = new ArrayList<>();
    for (OpeningPeriod openingPeriod : openingCollection.getOpeningPeriods()) {
      Criterion criterionForRegularHours = new Criterion(new Criteria().addField(OPENING_ID).setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + openingPeriod.getId() + "'"));
      Future<Void> future = Future.future();
      futures.add(future);
      postgresClient.get(REGULAR_HOURS, RegularHours.class, criterionForRegularHours, true, false, resultOfSelectRegularHours -> {
        if (resultOfSelectRegularHours.succeeded()) {
          try {
            List<RegularHours> regularHoursList = (List<RegularHours>) resultOfSelectRegularHours.result().getResults();

            for (RegularHours regularHours : regularHoursList) {
              Map<String, OpeningPeriod> openingPeriods = openingCollection.getOpeningPeriods().stream()
                .collect(Collectors.toMap(OpeningPeriod::getId, Function.identity()));
              List<OpeningDayWeekDay> openingDays = loanEndDateTime != null ? processCalculation(loanEndDateTime, regularHours, openingPeriod) : regularHours.getOpeningDays();
              openingPeriods.get(regularHours.getOpeningId()).setOpeningDays(openingDays);
            }

            future.complete();
          } catch (ClassCastException ex) {
            future.fail(resultOfSelectRegularHours.cause());
            postgresClient.endTx(beginTx, done ->
              asyncResultHandler.handle(Future.succeededFuture(
                GetCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(
                  messages.getMessage(lang, MessageConsts.InternalServerError)))));
          }
        } else {
          future.fail(resultOfSelectRegularHours.cause());
          postgresClient.endTx(beginTx, done ->
            asyncResultHandler.handle(Future.succeededFuture(
              GetCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(
                messages.getMessage(lang, MessageConsts.InternalServerError)))));
        }
      });
    }

    return futures;
  }

  private List<OpeningDayWeekDay> processCalculation(ZonedDateTime loanEndDateTime, RegularHours regularHours, OpeningPeriod openingPeriod) {
    String loanEndDayOfWeek = loanEndDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
    //if loan end date is opened by schedule
    List<OpeningDayWeekDay> currentPrevNextList = new ArrayList<>();
    OpeningDayWeekDay next = null;
    OpeningDayWeekDay prev = null;
    for (int i = 0; i < regularHours.getOpeningDays().size(); i++) {
      OpeningDayWeekDay day = regularHours.getOpeningDays().get(i);
      if (day.getWeekdays().getDay().toString().equalsIgnoreCase(loanEndDayOfWeek)) {
        day.getOpeningDay().setDate(loanEndDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE));
        // one day in a week open option
        if (regularHours.getOpeningDays().size() == 1) {
          prev = createCopy(day);
          next = createCopy(day);
          processOneDaySchedule(prev, next, loanEndDateTime, openingPeriod, false);
        } else {
          prev = findPrevDayInMultipleDaysSchedule(i, regularHours, false);
          next = findNextDayInMultipleDaysSchedule(i, regularHours, false);
          fillPrevAndNextDate(prev.getWeekdays().getDay().toString(),
            next.getWeekdays().getDay().toString(),
            loanEndDateTime, openingPeriod.getStartDate(), openingPeriod.getEndDate(), next, prev);
        }
        currentPrevNextList.add(prev);
        currentPrevNextList.add(day);
        currentPrevNextList.add(next);
        break;
      }
    }

    //if loan end date is closed by schedule
    if (CollectionUtils.isEmpty(currentPrevNextList)) {
      OpeningDayWeekDay nextIfClosed = null;
      OpeningDayWeekDay prevIfClosed = null;
      // loop through 6 days of week, do not look for closed day again
      for (int j = 1; j < 7; j++) {
        ZonedDateTime loanEndDateTimeNext = loanEndDateTime.plusDays(j);
        String loanEndDayOfWeekNext = loanEndDateTimeNext.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());

        OpeningDayWeekDay current = new OpeningDayWeekDay();
        OpeningDay openingDayClosed = new OpeningDay().withAllDay(false).withOpen(false).withExceptional(false);
        openingDayClosed.setDate(loanEndDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE));
        current.setWeekdays(new Weekdays().withDay(Weekdays.Day.fromValue(loanEndDayOfWeek.toUpperCase())));
        current.setOpeningDay(openingDayClosed);

        for (int i = 0; i < regularHours.getOpeningDays().size(); i++) {
          OpeningDayWeekDay nextDay = regularHours.getOpeningDays().get(i);
          if (nextDay.getWeekdays().getDay().toString().equalsIgnoreCase(loanEndDayOfWeekNext)) {
            nextDay.getOpeningDay().setDate(loanEndDateTimeNext.format(DateTimeFormatter.ISO_OFFSET_DATE));
            if (regularHours.getOpeningDays().size() == 1) {
              prevIfClosed = createCopy(nextDay);
              nextIfClosed = createCopy(nextDay);
              processOneDaySchedule(prevIfClosed, nextIfClosed, loanEndDateTimeNext, openingPeriod, true);
            } else {
              prevIfClosed = findPrevDayInMultipleDaysSchedule(i, regularHours, true);
              nextIfClosed = findNextDayInMultipleDaysSchedule(i, regularHours, true);
              fillPrevAndNextDate(prevIfClosed.getWeekdays().getDay().toString(),
                nextIfClosed.getWeekdays().getDay().toString(),
                loanEndDateTimeNext, openingPeriod.getStartDate(), openingPeriod.getEndDate(), nextIfClosed, prevIfClosed);

            }
            break;
          }
        }
        if (prevIfClosed != null && nextIfClosed != null) {
          currentPrevNextList.add(prevIfClosed);
          currentPrevNextList.add(current);
          currentPrevNextList.add(nextIfClosed);
          break;
        }
      }
    }
    return currentPrevNextList;
  }

  private OpeningDayWeekDay findPrevDayInMultipleDaysSchedule(int i, RegularHours regularHours, boolean requestedDayIsClosed) {
    OpeningDayWeekDay prev;
    if (i == 0) {
      prev = regularHours.getOpeningDays().get(regularHours.getOpeningDays().size() - 1);
    } else if (i == regularHours.getOpeningDays().size() - 1) {
      int index = requestedDayIsClosed ? 0 : i - 1;
      prev = regularHours.getOpeningDays().get(index);
    } else {
      prev = regularHours.getOpeningDays().get(i - 1);
    }
    return prev;
  }

  private OpeningDayWeekDay findNextDayInMultipleDaysSchedule(int i, RegularHours regularHours, boolean requestedDayIsClosed) {
    OpeningDayWeekDay next;
    OpeningDayWeekDay nextDay = regularHours.getOpeningDays().get(i);
    if (i == 0) {
      next = requestedDayIsClosed ? nextDay : regularHours.getOpeningDays().get(i + 1);
    } else if (i == regularHours.getOpeningDays().size() - 1) {
      next = requestedDayIsClosed ? nextDay : regularHours.getOpeningDays().get(0);
    } else {
      next = requestedDayIsClosed ? nextDay : regularHours.getOpeningDays().get(i + 1);
    }
    return next;
  }

  private void processOneDaySchedule(OpeningDayWeekDay prev, OpeningDayWeekDay next, ZonedDateTime loanEndDateTime, OpeningPeriod openingPeriod, boolean requestedDayIsClosed) {
    if (requestedDayIsClosed) {
      ZonedDateTime prevDate = loanEndDateTime.minusDays(7);
      setClosedIfOut(prevDate, openingPeriod.getStartDate(), prev, true);
      prev.getOpeningDay().setDate(prevDate.format(DateTimeFormatter.ISO_OFFSET_DATE));

      setClosedIfOut(loanEndDateTime, openingPeriod.getEndDate(), next, false);
      next.getOpeningDay().setDate(loanEndDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE));
    } else {
      ZonedDateTime prevDate = loanEndDateTime.minusDays(DayOfWeek.values().length);
      setClosedIfOut(prevDate, openingPeriod.getStartDate(), prev, true);
      prev.getOpeningDay().setDate(prevDate.format(DateTimeFormatter.ISO_OFFSET_DATE));

      ZonedDateTime nextDate = loanEndDateTime.plusDays(DayOfWeek.values().length);
      setClosedIfOut(nextDate, openingPeriod.getEndDate(), next, false);
      next.getOpeningDay().setDate(nextDate.format(DateTimeFormatter.ISO_OFFSET_DATE));
    }
  }

  private void setClosedIfOut(ZonedDateTime targetDate, Date dateToCompare, OpeningDayWeekDay openingDay, boolean isAfter) {
    if (isAfter) {
      if (!targetDate.isAfter(dateToCompare.toInstant().atZone(ZoneId.of("UTC")))) {
        openingDay.setOpeningDay(new OpeningDay().withAllDay(false).withOpen(false).withExceptional(false));
      }
    } else {
      if (!targetDate.isBefore(dateToCompare.toInstant().atZone(ZoneId.of("UTC")))) {
        openingDay.setOpeningDay(new OpeningDay().withAllDay(false).withOpen(false).withExceptional(false));
      }
    }
  }

  private void fillPrevAndNextDate(String prevDayOfWeek, String nextDayOfWeek, ZonedDateTime loanEndDateTime, Date startDate, Date endDate, OpeningDayWeekDay next, OpeningDayWeekDay prev) {
    for (int d = 0; d <= DayOfWeek.values().length; d++) {
      ZonedDateTime calculatedDateTime = loanEndDateTime.minusDays(d);
      if (prevDayOfWeek.equalsIgnoreCase(calculatedDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()))) {
        prev.getOpeningDay().setDate(calculatedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE));
        if (!calculatedDateTime.isAfter(startDate.toInstant().atZone(ZoneId.of("UTC")))) {
          prev.setOpeningDay(new OpeningDay().withAllDay(false).withOpen(false).withExceptional(false));
        }
        break;
      }
    }
    for (int d = 0; d <= DayOfWeek.values().length; d++) {
      ZonedDateTime calculatedDateTime = loanEndDateTime.plusDays(d);
      if (nextDayOfWeek.equalsIgnoreCase(calculatedDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()))) {
        next.getOpeningDay().setDate(calculatedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE));
        if (!calculatedDateTime.isBefore(endDate.toInstant().atZone(ZoneId.of("UTC")))) {
          next.setOpeningDay(new OpeningDay().withAllDay(false).withOpen(false).withExceptional(false));
        }
        break;
      }
    }
  }

  private OpeningDayWeekDay createCopy(OpeningDayWeekDay sample) {
    OpeningDayWeekDay copy = new OpeningDayWeekDay().withWeekdays(sample.getWeekdays());
    copy.setOpeningDay(
      new OpeningDay()
        .withAllDay(sample.getOpeningDay().getAllDay())
        .withExceptional(sample.getOpeningDay().getExceptional())
        .withOpen(sample.getOpeningDay().getOpen())
        .withOpeningHour(sample.getOpeningDay().getOpeningHour())
        .withDate(sample.getOpeningDay().getDate()));
    return copy;
  }

  private List<Future> getOpeningDaysByDate(Handler<AsyncResult<Response>> asyncResultHandler, OpeningHoursCollection openingHoursCollection, OpeningCollection openingCollection, CalendarOpeningsRequestParameters calendarOpeningsRequestParameters, PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {
    List<Future> futures = new ArrayList<>();
    List<OpeningHoursPeriod> openingPeriods = new ArrayList<>();
    for (OpeningPeriod openingPeriod_ : openingCollection.getOpeningPeriods()) {
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
              asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsResponse
                .respond500WithTextPlain(messages.getMessage(calendarOpeningsRequestParameters.getLang(), MessageConsts.InternalServerError)))));
          }
        } else {
          future.fail(resultOfSelectActualOpeningHours.cause());
          postgresClient.endTx(beginTx, done ->
            asyncResultHandler.handle(Future.succeededFuture(GetCalendarPeriodsResponse
              .respond500WithTextPlain(messages.getMessage(calendarOpeningsRequestParameters.getLang(), MessageConsts.InternalServerError)))));
        }
      });
    }

    return futures;
  }


  private void setOpeningPeriods(OpeningHoursCollection openingHoursCollection, List<OpeningHoursPeriod> openingPeriods, ActualOpeningHours actualOpeningHour) {
    OpeningHoursPeriod openingPeriod = new OpeningHoursPeriod();
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
    openingPeriods.sort(Comparator.comparing(OpeningHoursPeriod::getDate));
    if (openingPeriods.stream().anyMatch(o -> o.getDate().equals(actualOpeningHour.getActualDay()))) {
      OpeningHoursPeriod previousOpeningPeriod = openingPeriods.stream().filter(o -> o.getDate().equals(actualOpeningHour.getActualDay())).
        filter(o -> o.getOpeningDay().getExceptional().equals(actualOpeningHour.getExceptional())).findFirst()
        .orElse(getElsePreviousOpeningPeriod(openingPeriods, actualOpeningHour, openingPeriod, !actualOpeningHour.getExceptional()));
      if (previousOpeningPeriod.getOpeningDay().getExceptional().equals(actualOpeningHour.getExceptional())) {
        previousOpeningPeriod.getOpeningDay().getOpeningHour().add(openingHour);
      } else {
        openingPeriods.add(openingPeriod);
      }
    } else {
      openingPeriods.add(openingPeriod);
    }
    openingHoursCollection.setOpeningPeriods(openingPeriods);
  }

  private OpeningHoursPeriod getElsePreviousOpeningPeriod(List<OpeningHoursPeriod> openingPeriods, ActualOpeningHours actualOpeningHour, OpeningHoursPeriod openingPeriod, boolean isExceptional) {
    return openingPeriods.stream().filter(o -> o.getDate().equals(actualOpeningHour.getActualDay())).
      filter(o -> o.getOpeningDay().getExceptional().equals(isExceptional)).findFirst().orElse(openingPeriod);
  }

  private static PostgresClient getPostgresClient(Map<String, String> okapiHeaders, Context vertxContext) {
    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    return PostgresClient.getInstance(vertxContext.owner(), tenantId);
  }


}
