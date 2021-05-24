package org.folio.rest.impl;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isAnyEmpty;
import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.jaxrs.resource.Calendar.PostCalendarPeriodsPeriodByServicePointIdResponse.headersFor201;
import static org.folio.rest.service.ActualOpeningHoursService.SearchDirection.NEXT_DAY;
import static org.folio.rest.service.ActualOpeningHoursService.SearchDirection.PREVIOUS_DAY;
import static org.folio.rest.utils.CalendarConstants.END_DATE;
import static org.folio.rest.utils.CalendarConstants.EXCEPTIONAL;
import static org.folio.rest.utils.CalendarConstants.OPENINGS;
import static org.folio.rest.utils.CalendarConstants.SERVICE_POINT_ID;
import static org.folio.rest.utils.CalendarUtils.DATE_FORMATTER_SHORT;
import static org.folio.rest.utils.CalendarUtils.getOpeningDayWeekDayForTheEmptyDay;
import static org.folio.rest.utils.CalendarUtils.mapActualOpeningHoursListToOpeningDayWeekDay;
import static org.folio.rest.utils.CalendarUtils.mapExceptionToResponseResult;
import static org.folio.rest.utils.CalendarUtils.mapOpeningPeriodToOpenings;
import static org.folio.rest.utils.CalendarUtils.mapOpeningsToOpeningCollection;
import static org.folio.rest.utils.CalendarUtils.separateEvents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.okapi.common.GenericCompositeFuture;
import org.folio.rest.annotations.Validate;
import org.folio.rest.beans.ActualOpeningHours;
import org.folio.rest.beans.CalendarOpeningsRequestParameters;
import org.folio.rest.beans.Openings;
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
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.SQLConnection;
import org.folio.rest.service.ActualOpeningHoursService;
import org.folio.rest.service.OpeningsService;
import org.folio.rest.service.RegularHoursService;
import org.folio.rest.service.impl.ActualOpeningHoursServiceImpl;
import org.folio.rest.service.impl.OpeningsServiceImpl;
import org.folio.rest.service.impl.RegularHoursServiceImpl;
import org.folio.rest.tools.messages.MessageConsts;
import org.folio.rest.tools.messages.Messages;
import org.folio.rest.tools.utils.TenantTool;
import org.folio.rest.utils.CalendarUtils;
import org.joda.time.DateTime;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;


@SuppressWarnings({"squid:S1854", "squid:S1481"})
public class CalendarAPI implements Calendar {

  private static final Logger logger = LogManager.getLogger();

  private final Messages messages = Messages.getInstance();

  @Validate
  @Override
  public void postCalendarPeriodsPeriodByServicePointId(String servicePointId,
                                                        String lang,
                                                        OpeningPeriod entity,
                                                        Map<String, String> okapiHeaders,
                                                        Handler<AsyncResult<Response>> asyncResultHandler,
                                                        Context vertxContext) {

    if (entity.getOpeningDays().isEmpty() ||
      isAnyEmpty(entity.getServicePointId(), entity.getName(), entity.getId())) {

      asyncResultHandler.handle(succeededFuture(PostCalendarPeriodsPeriodByServicePointIdResponse
        .respond400WithTextPlain("Not valid json object. Missing field(s)...")));
      return;
    }

    Openings openings = mapOpeningPeriodToOpenings(entity);
    PostgresClient pgClient = PgUtil.postgresClient(vertxContext, okapiHeaders);

    OpeningsService openingsService = new OpeningsServiceImpl(pgClient);
    RegularHoursService regularHoursService = new RegularHoursServiceImpl(pgClient);
    ActualOpeningHoursService actualOpeningHoursService = new ActualOpeningHoursServiceImpl(pgClient);

    pgClient.startTx(conn -> succeededFuture()
      .compose(v -> openingsService.checkOpeningsForOverlap(conn, openings, false))
      .compose(v -> openingsService.saveOpenings(conn, openings))
      .compose(v -> regularHoursService.saveRegularHours(conn, new RegularHours(entity.getId(), entity.getOpeningDays())))
      .compose(v -> actualOpeningHoursService.saveActualOpeningHours(conn, separateEvents(entity, openings.getExceptional())))
      .onComplete(v -> {
        if (v.failed()) {
          logger.error(v.cause().getMessage());
          pgClient.rollbackTx(conn, rollback -> asyncResultHandler.handle(mapExceptionToResponseResult(v.cause())));
        } else {
          pgClient.endTx(conn, end -> asyncResultHandler.handle(succeededFuture(
            PostCalendarPeriodsPeriodByServicePointIdResponse.respond201WithApplicationJson(entity, headersFor201()))));
        }
      }));
  }

  @Validate
  @Override
  public void deleteCalendarPeriodsPeriodByServicePointIdAndPeriodId(String servicePointId,
                                                                     String periodId,
                                                                     String lang,
                                                                     Map<String, String> okapiHeaders,
                                                                     Handler<AsyncResult<Response>> asyncResultHandler,
                                                                     Context vertxContext) {


    PostgresClient pgClient = PgUtil.postgresClient(vertxContext, okapiHeaders);

    OpeningsServiceImpl openingsService = new OpeningsServiceImpl(pgClient);
    RegularHoursService regularHoursService = new RegularHoursServiceImpl(pgClient);
    ActualOpeningHoursService actualOpeningHoursService = new ActualOpeningHoursServiceImpl(pgClient);

    pgClient.startTx(conn -> succeededFuture()
      .compose(v -> openingsService.deleteOpeningsById(conn, periodId))
      .compose(v -> regularHoursService.deleteRegularHoursByOpeningsId(conn, periodId))
      .compose(v -> actualOpeningHoursService.deleteActualOpeningHoursByOpeningsId(conn, periodId))
      .onComplete(v -> {
        if (v.failed()) {
          logger.error(v.cause().getMessage());
          pgClient.rollbackTx(conn, rollback -> asyncResultHandler.handle(mapExceptionToResponseResult(v.cause())));
        } else {
          pgClient.endTx(conn, end -> asyncResultHandler.handle(succeededFuture(
            DeleteCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond204())));
        }
      })
    );
  }

  @Validate
  @Override
  public void putCalendarPeriodsPeriodByServicePointIdAndPeriodId(String servicePointId,
                                                                  String openingId,
                                                                  String lang,
                                                                  OpeningPeriod entity,
                                                                  Map<String, String> okapiHeaders,
                                                                  Handler<AsyncResult<Response>> asyncResultHandler,
                                                                  Context vertxContext) {

    Openings openings = mapOpeningPeriodToOpenings(entity);
    PostgresClient pgClient = PgUtil.postgresClient(vertxContext, okapiHeaders);

    OpeningsServiceImpl openingsService = new OpeningsServiceImpl(pgClient);
    RegularHoursService regularHoursService = new RegularHoursServiceImpl(pgClient);
    ActualOpeningHoursService actualOpeningHoursService = new ActualOpeningHoursServiceImpl(pgClient);

    RegularHours regularHours = new RegularHours(openingId, openings.getId(), entity.getOpeningDays());

    pgClient.startTx(conn -> succeededFuture()
      .compose(v -> openingsService.checkOpeningsForOverlap(conn, openings, true))
      .compose(v -> openingsService.updateOpenings(conn, openings))
      .compose(v -> regularHoursService.updateRegularHours(conn, regularHours))
      .compose(v -> actualOpeningHoursService.deleteActualOpeningHoursByOpeningsId(conn, entity.getId()))
      .compose(v -> actualOpeningHoursService.saveActualOpeningHours(conn, separateEvents(entity, openings.getExceptional())))
      .onComplete(v -> {
        if (v.failed()) {
          logger.error(v.cause().getMessage());
          pgClient.rollbackTx(conn, rollback -> asyncResultHandler.handle(mapExceptionToResponseResult(v.cause())));
        } else {
          pgClient.endTx(conn, end -> asyncResultHandler.handle(succeededFuture(
            PutCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond204())));
        }
      })
    );
  }

  @Validate
  @Override
  public void getCalendarPeriods(String servicePointId,
                                 String startDate,
                                 String endDate,
                                 boolean includeClosedDays,
                                 boolean actualOpenings,
                                 int offset,
                                 int limit,
                                 String lang,
                                 Map<String, String> okapiHeaders,
                                 Handler<AsyncResult<Response>> asyncResultHandler,
                                 Context vertxContext) {

    CalendarOpeningsRequestParameters params =
      new CalendarOpeningsRequestParameters(startDate, endDate, offset, limit, includeClosedDays, actualOpenings);

    PostgresClient pgClient = PgUtil.postgresClient(vertxContext, okapiHeaders);

    OpeningsServiceImpl openingsService = new OpeningsServiceImpl(pgClient);
    ActualOpeningHoursService actualOpeningHoursService = new ActualOpeningHoursServiceImpl(pgClient);

    pgClient.startTx(conn -> succeededFuture()
      .compose(v -> openingsService.findOpeningsByServicePointId(conn, servicePointId))
      .map(CalendarUtils::mapOpeningsToOpeningCollection)
      .compose(collection -> getOpeningDaysByDatesFuture(actualOpeningHoursService, conn, collection, params))
      .onComplete(result -> {
        if (result.failed()) {
          logger.error(result.cause().getMessage());
          pgClient.rollbackTx(conn, done -> asyncResultHandler.handle(mapExceptionToResponseResult(result.cause())));
        } else {
          pgClient.endTx(conn, done -> asyncResultHandler.handle(succeededFuture(
            GetCalendarPeriodsResponse.respond200WithApplicationJson(result.result()))));
        }
      }));
  }

  @Override
  public void getCalendarPeriodsPeriodByServicePointIdAndPeriodId(String servicePointId,
                                                                  String openingId,
                                                                  String lang,
                                                                  Map<String, String> okapiHeaders,
                                                                  Handler<AsyncResult<Response>> asyncResultHandler,
                                                                  Context vertxContext) {

    PostgresClient pgClient = PgUtil.postgresClient(vertxContext, okapiHeaders);

    OpeningsServiceImpl openingsService = new OpeningsServiceImpl(pgClient);
    RegularHoursService regularHoursService = new RegularHoursServiceImpl(pgClient);

    pgClient.startTx(conn -> openingsService.findOpeningsById(conn, openingId)
      .compose(openings -> openings.isEmpty() ?
        failedFuture(new NotFoundException(format("Openings with id '%s' is not found", openingId))) : succeededFuture(openings))
      .map(openings -> openings.get(0))
      .map(CalendarUtils::mapOpeningsToOpeningPeriod)
      .compose(period -> setOpeningDaysForOpeningPeriod(regularHoursService, conn, period))
      .onComplete(period -> {
        if (period.failed()) {
          logger.error(period.cause().getMessage());
          pgClient.rollbackTx(conn, done -> asyncResultHandler.handle(mapExceptionToResponseResult(period.cause())));
        } else {
          pgClient.endTx(conn, done -> asyncResultHandler.handle(succeededFuture(
            GetCalendarPeriodsPeriodByServicePointIdAndPeriodIdResponse.respond200WithApplicationJson(period.result()))));
        }
      })
    );
  }

  @Validate
  @Override
  public void getCalendarPeriodsPeriodByServicePointId(String servicePointId,
                                                       boolean withOpeningDays,
                                                       boolean showPast,
                                                       boolean exceptional,
                                                       String lang,
                                                       Map<String, String> okapiHeaders,
                                                       Handler<AsyncResult<Response>> asyncResultHandler,
                                                       Context vertxContext) {

    String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(OKAPI_HEADER_TENANT));
    PostgresClient pgClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
    Criterion criterionForOpeningHours = assembleCriterionByServicePointId(servicePointId, showPast, exceptional);

    RegularHoursService regularHoursService = new RegularHoursServiceImpl(pgClient);

    pgClient.startTx(beginTx ->
      pgClient.get(beginTx, OPENINGS, Openings.class, criterionForOpeningHours, true, false, get -> {
        if (get.succeeded()) {
          OpeningCollection openingCollection = mapOpeningsToOpeningCollection(get.result().getResults());
          if (withOpeningDays) {
            getOpeningDaysByServicePointIdFuture(asyncResultHandler, openingCollection, lang,pgClient, beginTx, regularHoursService);
          } else {
            pgClient.endTx(beginTx, done ->
              asyncResultHandler.handle(succeededFuture(
                GetCalendarPeriodsPeriodByServicePointIdResponse.respond200WithApplicationJson(openingCollection))));
          }
        } else {
          pgClient.endTx(beginTx, done ->
            asyncResultHandler.handle(succeededFuture(
              GetCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError)))));
        }
      }));
  }

  @Validate
  @Override
  public void getCalendarPeriodsCalculateopeningByServicePointId(String servicePointId,
                                                                 String requestedDate,
                                                                 String lang,
                                                                 Map<String, String> okapiHeaders,
                                                                 Handler<AsyncResult<Response>> asyncResultHandler,
                                                                 Context vertxContext) {

    try {
      PostgresClient pgClient = PgUtil.postgresClient(vertxContext, okapiHeaders);
      String tenant = okapiHeaders.get(OKAPI_HEADER_TENANT);
      ActualOpeningHoursService actualOpeningHoursService =
        new ActualOpeningHoursServiceImpl(pgClient);
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      df.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
      Date date = df.parse(requestedDate);

      GenericCompositeFuture.all(List.of(
        actualOpeningHoursService.findActualOpeningHoursForClosestOpenDay(servicePointId, date, PREVIOUS_DAY, tenant),
        actualOpeningHoursService.findActualOpeningHoursForGivenDay(servicePointId, date, tenant),
        actualOpeningHoursService.findActualOpeningHoursForClosestOpenDay(servicePointId, date, NEXT_DAY, tenant)
      )).onComplete(result -> {
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

        asyncResultHandler.handle(succeededFuture(
          GetCalendarPeriodsCalculateopeningByServicePointIdResponse.respond200WithApplicationJson(period)));
      });
    } catch (ParseException e) {
      asyncResultHandler.handle(succeededFuture(
        GetCalendarPeriodsCalculateopeningByServicePointIdResponse.respond400WithTextPlain(e)));
    } catch (Exception e) {
      logger.error(e);
      asyncResultHandler.handle(succeededFuture(
        GetCalendarPeriodsCalculateopeningByServicePointIdResponse
          .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
    }
  }

  private Future<OpeningPeriod> setOpeningDaysForOpeningPeriod(RegularHoursService regularHoursService,
                                                               AsyncResult<SQLConnection> conn,
                                                               OpeningPeriod period) {

    return regularHoursService.findRegularHoursByOpeningId(conn, period.getId())
      .map(rhs -> period.withOpeningDays(rhs.get(0).getOpeningDays()));
  }

  private void getOpeningDaysByServicePointIdFuture(Handler<AsyncResult<Response>> asyncResultHandler,
                                                    OpeningCollection openingCollection,
                                                    String lang,
                                                    PostgresClient postgresClient,
                                                    AsyncResult<SQLConnection> beginTx,
                                                    RegularHoursService regularHoursService) {

    Future<Void> future = getOpeningDays(regularHoursService, beginTx, openingCollection.getOpeningPeriods());
    future.onComplete(querys -> {
      if (querys.succeeded()) {
        postgresClient.endTx(beginTx, done
          -> asyncResultHandler.handle(succeededFuture(
            GetCalendarPeriodsPeriodByServicePointIdResponse.respond200WithApplicationJson(
              openingCollection))));
      } else {
        postgresClient.endTx(beginTx, done
          -> asyncResultHandler.handle(succeededFuture(
            GetCalendarPeriodsPeriodByServicePointIdResponse.respond500WithTextPlain(
              messages.getMessage(lang, MessageConsts.InternalServerError)))));
      }
    });
  }

  private Future<OpeningHoursCollection> getOpeningDaysByDatesFuture(ActualOpeningHoursService service,
                                                                     AsyncResult<SQLConnection> conn,
                                                                     OpeningCollection openingCollection,
                                                                     CalendarOpeningsRequestParameters params) {

    Promise<OpeningHoursCollection> promise = Promise.promise();
    OpeningHoursCollection openingHoursCollection = new OpeningHoursCollection();

    getOpeningDaysByDate(service, conn, openingHoursCollection, openingCollection, params)
      .onComplete(querys -> {
      if (querys.succeeded()) {
        if (params.isIncludeClosedDays() && !openingHoursCollection.getOpeningPeriods().isEmpty()) {
          CalendarUtils.addClosedDaysToOpenings(openingHoursCollection.getOpeningPeriods(), params);
        }
        openingHoursCollection.getOpeningPeriods()
          .sort(Comparator.comparing(OpeningHoursPeriod::getDate).thenComparing(o -> !o.getOpeningDay().getExceptional()));
        if (params.isActualOpenings()) {
          overrideOpeningPeriodsByExceptionalPeriods(openingHoursCollection);
        }
        openingHoursCollection.setTotalRecords(openingHoursCollection.getOpeningPeriods().size());
        openingHoursCollection.setOpeningPeriods(openingHoursCollection.getOpeningPeriods().stream().skip(params.getOffset()).limit(params.getLimit()).collect(Collectors.toList()));

        promise.complete(openingHoursCollection);
      }
    });

    return promise.future();
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

  private Criterion assembleCriterionByServicePointId(String servicePointId, boolean showPast, boolean exceptional) {
    Criteria critServicePoint = new Criteria()
      .addField(SERVICE_POINT_ID)
      .setOperation("=")
      .setVal(servicePointId);

    Criteria critExceptional = new Criteria()
      .addField(EXCEPTIONAL)
      .setOperation("=")
      .setVal(String.valueOf(exceptional));

    Criteria critShowPast = new Criteria()
      .addField(END_DATE)
      .setOperation(">=")
      .setVal(DATE_FORMATTER_SHORT.print(new DateTime()));

    Criterion criterionForOpeningHours = new Criterion();

    if (!showPast) {
      criterionForOpeningHours.addCriterion(critServicePoint, "AND", critShowPast);
    } else {
      criterionForOpeningHours.addCriterion(critServicePoint, "AND");
    }
    criterionForOpeningHours.addCriterion(critExceptional, "AND");

    return criterionForOpeningHours;
  }

  private Future<Void> getOpeningDays(RegularHoursService regularHoursService,
                                      AsyncResult<SQLConnection> conn,
                                      List<OpeningPeriod> openingPeriods) {

    Future<Void> future = succeededFuture();

    for (OpeningPeriod period : openingPeriods) {
      future = future.compose(handler -> regularHoursService.findRegularHoursByOpeningId(conn, period.getId())
        .compose(rhs -> fillOpeningDays(rhs, openingPeriods)));
    }
    return future;
  }

  private Future<Void> fillOpeningDays(List<RegularHours> regularHours, List<OpeningPeriod> openingPeriods) {

    regularHours
      .forEach(rh -> openingPeriods.stream()
        .filter(period -> period.getId().equals(rh.getOpeningId()))
        .findFirst()
        .ifPresent(period -> period.setOpeningDays(rh.getOpeningDays())));

    return succeededFuture();
  }

  private Future<Void> getOpeningDaysByDate(ActualOpeningHoursService service,
                                            AsyncResult<SQLConnection> conn,
                                            OpeningHoursCollection openingHoursCollection,
                                            OpeningCollection openingCollection,
                                            CalendarOpeningsRequestParameters params) {

    List<OpeningHoursPeriod> openingPeriods = new ArrayList<>();
    Future<Void> future = succeededFuture();

    for (OpeningPeriod period : openingCollection.getOpeningPeriods()) {
      future = future
        .compose(v -> service.findActualOpeningHoursByOpeningIdAndRange(
          conn, period.getId(), params.getStartDate(), params.getEndDate()))
        .compose(aohs -> setOpeningPeriodsForEachActualOpeningHour(aohs, openingHoursCollection, openingPeriods));
    }

    return future;
  }

  private Future<Void> setOpeningPeriodsForEachActualOpeningHour(List<ActualOpeningHours> actualOpeningHoursList,
                                                                 OpeningHoursCollection openingHoursCollection,
                                                                 List<OpeningHoursPeriod> openingPeriods) {

    actualOpeningHoursList.forEach(r -> setOpeningPeriods(openingHoursCollection, openingPeriods, r));
    return succeededFuture();
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
}
