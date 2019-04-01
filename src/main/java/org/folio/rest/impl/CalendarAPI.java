package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.SQLConnection;
import joptsimple.internal.Strings;
import org.folio.rest.annotations.Validate;
import org.folio.rest.beans.ActualOpeningHours;
import org.folio.rest.beans.CalendarOpeningsRequestParameters;
import org.folio.rest.beans.Openings;
import org.folio.rest.beans.PostCalendarPeriodsRequestParams;
import org.folio.rest.beans.RegularHours;
import org.folio.rest.jaxrs.model.OpeningCollection;
import org.folio.rest.jaxrs.model.OpeningDay;
import org.folio.rest.jaxrs.model.OpeningDayWeekDay;
import org.folio.rest.jaxrs.model.OpeningHour;
import org.folio.rest.jaxrs.model.OpeningHoursCollection;
import org.folio.rest.jaxrs.model.OpeningHoursPeriod;
import org.folio.rest.jaxrs.model.OpeningPeriod;
import org.folio.rest.jaxrs.resource.Calendar;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.interfaces.Results;
import org.folio.rest.service.ActualOpeningHoursService;
import org.folio.rest.service.ActualOpeningHoursServiceImpl;
import org.folio.rest.tools.messages.MessageConsts;
import org.folio.rest.tools.messages.Messages;
import org.folio.rest.tools.utils.TenantTool;
import org.folio.rest.utils.CalendarUtils;
import org.joda.time.DateTime;

import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.service.ActualOpeningHoursService.SearchDirection.NEXT_DAY;
import static org.folio.rest.service.ActualOpeningHoursService.SearchDirection.PREVIOUS_DAY;
import static org.folio.rest.utils.CalendarConstants.ACTUAL_DAY;
import static org.folio.rest.utils.CalendarConstants.ACTUAL_OPENING_HOURS;
import static org.folio.rest.utils.CalendarConstants.END_DATE;
import static org.folio.rest.utils.CalendarConstants.EXCEPTIONAL;
import static org.folio.rest.utils.CalendarConstants.ID_FIELD;
import static org.folio.rest.utils.CalendarConstants.OPENINGS;
import static org.folio.rest.utils.CalendarConstants.OPENING_ID;
import static org.folio.rest.utils.CalendarConstants.REGULAR_HOURS;
import static org.folio.rest.utils.CalendarConstants.SERVICE_POINT_ID;
import static org.folio.rest.utils.CalendarConstants.START_DATE;
import static org.folio.rest.utils.CalendarUtils.DATE_FORMATTER_SHORT;
import static org.folio.rest.utils.CalendarUtils.getOpeningDayWeekDayForTheEmptyDay;
import static org.folio.rest.utils.CalendarUtils.mapActualOpeningHoursListToOpeningDayWeekDay;


public class CalendarAPI implements Calendar {

  private static final Logger logger = LoggerFactory.getLogger(CalendarAPI.class);
  private static final String ERROR_MESSAGE = "Period with openingId=%s not found";

  private final Messages messages = Messages.getInstance();

  private ActualOpeningHoursService actualOpeningHoursService;

  public CalendarAPI(Vertx vertx, String tenantId) {
    actualOpeningHoursService = new ActualOpeningHoursServiceImpl(vertx, tenantId);
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
      return;
    }
    vertxContext.runOnContext(v -> {
        try {
          postgresClient.startTx(beginTx -> {
              try {
                postgresClient.get(beginTx, OPENINGS, Openings.class, criterionForId, true, false, resultOfSelectOpenings -> {
                  if (resultOfSelectOpenings.failed()) {
                    postgresClient.endTx(beginTx, end -> asyncResultHandler.handle(Future.succeededFuture(
                      PostCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(
                        "Error while listing events."))));
                  } else if (resultOfSelectOpenings.result().getResults().isEmpty()) {
                    PostCalendarPeriodsRequestParams postCalendarPeriodsRequestParams = new PostCalendarPeriodsRequestParams(lang, entity, isExceptional, openingsTable);
                    postgresClient.save(beginTx, OPENINGS, openingsTable, replyOfSavingOpenings
                      -> saveRegularHours(postCalendarPeriodsRequestParams, postgresClient, asyncResultHandler, beginTx, replyOfSavingOpenings));
                  } else {
                    postgresClient.endTx(beginTx, end -> asyncResultHandler.handle(Future.succeededFuture(
                      PostCalendarPeriodsPeriodByServicePointIdResponse
                        .respond500WithTextPlain("Intervals can not overlap."))));
                  }
                });
              } catch (Exception ex) {
                logger.error(ex);
                postgresClient.rollbackTx(beginTx, end -> asyncResultHandler.handle(Future.succeededFuture(
                  PostCalendarPeriodsPeriodByServicePointIdResponse
                    .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
              }
            }
          );
        } catch (Exception ex) {
          logger.error(ex);
          asyncResultHandler.handle(Future.succeededFuture(
            PostCalendarPeriodsPeriodByServicePointIdResponse
              .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
        }
      }
    );
  }

  @Validate
  @Override
  public void deleteCalendarPeriodsPeriodByServicePointIdAndPeriodId(String servicePointId, String periodId,
                                                                     String lang, Map<String, String> okapiHeaders,
                                                                     Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    PostgresClient postgresClient = getPostgresClient(okapiHeaders, vertxContext);
    Criterion criterionForOpeningHours = new Criterion(new Criteria().addField(OPENING_ID)
      .setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + periodId + "'"));
    Criterion criterionForOpenings = new Criterion(new Criteria().addField(ID_FIELD)
      .setJSONB(true).setOperation(Criteria.OP_EQUAL).setValue("'" + periodId + "'"));

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
  public void putCalendarPeriodsPeriodByServicePointIdAndPeriodId(String servicePointId, String openingId, String lang, OpeningPeriod entity,
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
                                                                 String requestedDate,
                                                                 String lang,
                                                                 Map<String, String> okapiHeaders,
                                                                 Handler<AsyncResult<Response>> asyncResultHandler,
                                                                 Context vertxContext) {

    vertxContext.runOnContext(v -> {
      try {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        Date date = df.parse(requestedDate);

        CompositeFuture.all(
          actualOpeningHoursService.findActualOpeningHoursForClosestOpenDay(servicePointId, date, PREVIOUS_DAY),
          actualOpeningHoursService.findActualOpeningHoursForGivenDay(servicePointId, date),
          actualOpeningHoursService.findActualOpeningHoursForClosestOpenDay(servicePointId, date, NEXT_DAY)
        ).setHandler(result -> {
          List<ActualOpeningHours> prev = result.result().resultAt(0);
          List<ActualOpeningHours> current = result.result().resultAt(1);
          List<ActualOpeningHours> next = result.result().resultAt(2);

          List<OpeningDayWeekDay> openingDays = new ArrayList<>();
          openingDays.add(mapActualOpeningHoursListToOpeningDayWeekDay(prev));
          openingDays.add(current.isEmpty() ?
            getOpeningDayWeekDayForTheEmptyDay(date) : mapActualOpeningHoursListToOpeningDayWeekDay(current));
          openingDays.add(mapActualOpeningHoursListToOpeningDayWeekDay(next));

          OpeningPeriod period = new OpeningPeriod();
          period.setOpeningDays(openingDays);

          asyncResultHandler.handle(Future.succeededFuture(
            GetCalendarPeriodsCalculateopeningByServicePointIdResponse.respond200WithApplicationJson(period)));
        });
      } catch (ParseException e) {
        asyncResultHandler.handle(Future.succeededFuture(
          GetCalendarPeriodsCalculateopeningByServicePointIdResponse.respond400WithTextPlain(e)));
      } catch (Exception e) {
        logger.error(e);
        asyncResultHandler.handle(Future.succeededFuture(
          GetCalendarPeriodsCalculateopeningByServicePointIdResponse
            .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
      }
    });
  }

  private void saveRegularHours(PostCalendarPeriodsRequestParams postCalendarPeriodsRequestParams, PostgresClient postgresClient,
                                Handler<AsyncResult<Response>> asyncResultHandler, AsyncResult<SQLConnection> beginTx,
                                AsyncResult<String> replyOfSavingOpenings) {
    try {
      if (replyOfSavingOpenings.succeeded()) {
        RegularHours regularHours = new RegularHours(replyOfSavingOpenings.result(), postCalendarPeriodsRequestParams.getOpeningsTable().getId(), postCalendarPeriodsRequestParams.getEntity().getOpeningDays());
        postgresClient.save(beginTx, REGULAR_HOURS, regularHours, replyOfSavingRegularHours -> {
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
    } catch (Exception ex) {
      logger.error(ex);
      postgresClient.rollbackTx(beginTx, done ->
        asyncResultHandler.handle(Future.succeededFuture(
          PostCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(messages.getMessage(
            postCalendarPeriodsRequestParams.getLang(), MessageConsts.InternalServerError)))));
    }
  }

  private void saveActualOpeningHours(OpeningPeriod entity, String lang, boolean isExceptional,
                                      Handler<AsyncResult<Response>> asyncResultHandler, PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {
    try {
      List<Object> actualOpeningHours = CalendarUtils.separateEvents(entity, isExceptional);
      if (!actualOpeningHours.isEmpty()) {
        postgresClient.saveBatch(beginTx, ACTUAL_OPENING_HOURS, actualOpeningHours, replyOfSavingActualOpeningHours -> {
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
        postgresClient.endTx(beginTx, done -> asyncResultHandler.handle(Future.succeededFuture(
          PostCalendarPeriodsPeriodByServicePointIdResponse.respond201WithApplicationJson(entity,
            PostCalendarPeriodsPeriodByServicePointIdResponse.headersFor201().withLocation(lang)))));
      }
    } catch (Exception ex) {
      logger.error(ex);
      postgresClient.rollbackTx(beginTx, end -> asyncResultHandler.handle(Future.succeededFuture(
        PostCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(
          messages.getMessage(lang, MessageConsts.InternalServerError)))));
    }
  }

  private void deleteOpenings(Handler<AsyncResult<Response>> asyncResultHandler,
                              PostgresClient postgresClient, Criterion criterionForOpenings,
                              String lang, AsyncResult<SQLConnection> beginTx) {

    postgresClient.delete(beginTx, OPENINGS, criterionForOpenings,
      replyOfDeletingOpenings -> {
        if (replyOfDeletingOpenings.failed()) {
          postgresClient.rollbackTx(beginTx, done
            -> asyncResultHandler.handle(Future.succeededFuture(
            DeleteCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond500WithTextPlain(
              messages.getMessage(lang, MessageConsts.InternalServerError)))));
          return;
        }

        int numberOfRowsDeleted = replyOfDeletingOpenings.result().getUpdated();
        if (numberOfRowsDeleted <= 0) {
          postgresClient.rollbackTx(beginTx, done
            -> asyncResultHandler.handle(Future.succeededFuture(
            DeleteCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse
              .respond404WithTextPlain(messages.getMessage(lang, MessageConsts.ObjectDoesNotExist)))));
          return;
        }

        postgresClient.endTx(beginTx, done
          -> asyncResultHandler.handle(Future.succeededFuture(
          DeleteCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond204())));
      });
  }

  private void putActualOpeningHours(OpeningPeriod entity, Criterion openingId, String lang,
    boolean isExceptional, Handler<AsyncResult<Response>> asyncResultHandler,
    PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {

    List<Object> actualOpeningHours = CalendarUtils.separateEvents(entity, isExceptional);
    postgresClient.delete(beginTx, ACTUAL_OPENING_HOURS, openingId, replyOfDeletingActualOpeningHours -> {
      if (replyOfDeletingActualOpeningHours.succeeded()) {
        postgresClient.saveBatch(beginTx, ACTUAL_OPENING_HOURS, actualOpeningHours, replyOfSavingActualOpeningHours -> {
          if (replyOfSavingActualOpeningHours.succeeded()) {
            postgresClient.endTx(beginTx, done
              -> asyncResultHandler.handle(Future.succeededFuture(
                PutCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond204())));
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

    Future<Void> future = getOpeningDays(postgresClient, beginTx, openingCollection);
    future.setHandler(querys -> {
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

  private void getOpeningDaysByOpeningIdFuture(Handler<AsyncResult<Response>> asyncResultHandler, OpeningCollection openingCollection,
                                               String lang, String openingId, PostgresClient postgresClient, AsyncResult<SQLConnection> beginTx) {
    Future<Void> future = getOpeningDays(postgresClient, beginTx, openingCollection);
    future.setHandler(resultHandler -> {
      if (resultHandler.failed()) {
        postgresClient.endTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(
            GetCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse
              .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
        return;
      }

      List<OpeningPeriod> openingPeriods = openingCollection.getOpeningPeriods();
      if (openingPeriods.isEmpty()) {
        postgresClient.endTx(beginTx, done ->
          asyncResultHandler.handle(Future.succeededFuture(
            GetCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse
              .respond404WithTextPlain(String.format(ERROR_MESSAGE, openingId)))));
        return;
      }

      OpeningPeriod firstOpeningPeriod = openingPeriods.get(0);
      postgresClient.endTx(beginTx, done ->
        asyncResultHandler.handle(Future.succeededFuture(
          GetCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.
            respond200WithApplicationJson(firstOpeningPeriod))));
    });
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
    for (Openings openings : resultOfSelectOpenings.result().getResults()) {
      OpeningPeriod openingPeriod = new OpeningPeriod();
      openingPeriod.setStartDate(openings.getStartDate());
      openingPeriod.setEndDate(openings.getEndDate());
      openingPeriod.setName(openings.getName());
      openingPeriod.setServicePointId(openings.getServicePointId());
      openingPeriod.setId(openings.getId());
      openingCollection.getOpeningPeriods().add(openingPeriod);
    }
  }

  private Future<Void> getOpeningDays(PostgresClient pgClient, AsyncResult<SQLConnection> beginTx,
                                      OpeningCollection openingCollection) {
    Future<Void> future = Future.succeededFuture();
    for (OpeningPeriod period : openingCollection.getOpeningPeriods()) {
      future = future.compose(handler -> fillOpeningDays(openingCollection, pgClient, beginTx, period));
    }
    return future;
  }

  private Future<Void> fillOpeningDays(OpeningCollection openingCollection, PostgresClient postgresClient,
                                       AsyncResult<SQLConnection> beginTx, OpeningPeriod openingPeriod) {
    Future<Void> future = Future.future();

    Criteria criteria = new Criteria()
      .addField(OPENING_ID)
      .setJSONB(true)
      .setOperation(Criteria.OP_EQUAL)
      .setValue("'" + openingPeriod.getId() + "'");

    postgresClient.get(beginTx, REGULAR_HOURS, RegularHours.class, new Criterion(criteria), true, false,
      resultOfSelectRegularHours -> {
        if (resultOfSelectRegularHours.succeeded()) {

          List<RegularHours> regularHoursList = resultOfSelectRegularHours.result().getResults();
          for (RegularHours regularHours : regularHoursList) {
            Map<String, OpeningPeriod> openingPeriods = openingCollection.getOpeningPeriods().stream()
              .collect(Collectors.toMap(OpeningPeriod::getId, Function.identity()));
            List<OpeningDayWeekDay> openingDays = regularHours.getOpeningDays();
            openingPeriods.get(regularHours.getOpeningId()).setOpeningDays(openingDays);
          }

          future.complete();
        } else {
          future.fail(resultOfSelectRegularHours.cause());
        }
      });

    return future;
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
            List<ActualOpeningHours> actualOpeningHours = resultOfSelectActualOpeningHours.result().getResults();
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
