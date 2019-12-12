package org.folio.rest.utils;

import static io.vertx.core.Future.succeededFuture;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import io.vertx.core.AsyncResult;

import org.apache.commons.lang.BooleanUtils;
import org.folio.rest.exceptions.OverlapIntervalException;
import org.folio.rest.jaxrs.model.Error;
import org.folio.rest.jaxrs.model.Errors;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.folio.rest.beans.ActualOpeningHours;
import org.folio.rest.beans.CalendarOpeningsRequestParameters;
import org.folio.rest.beans.Openings;
import org.folio.rest.jaxrs.model.OpeningCollection;
import org.folio.rest.jaxrs.model.OpeningDay;
import org.folio.rest.jaxrs.model.OpeningDayWeekDay;
import org.folio.rest.jaxrs.model.OpeningHour;
import org.folio.rest.jaxrs.model.OpeningHoursPeriod;
import org.folio.rest.jaxrs.model.OpeningPeriod;

public class CalendarUtils {

  public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  private static final String DATE_PATTERN_SHORT = "yyyy-MM-dd";
  public static final DateTimeFormatter DATE_FORMATTER_SHORT = DateTimeFormat.forPattern(DATE_PATTERN_SHORT).withZoneUTC();
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_PATTERN).withZoneUTC();
  private static final String DAY_PATTERN = "EEEE";

  private static final String ERROR_CODE_INTERVALS_OVERLAP = "intervalsOverlap";

  private CalendarUtils() {
  }

  private static DayOfWeek dayOfDate(Date inputDate) {
    SimpleDateFormat format = new SimpleDateFormat(DAY_PATTERN, Locale.ENGLISH);
    format.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
    return DayOfWeek.valueOf(format.format(inputDate).toUpperCase());
  }


  public static List<Object> separateEvents(OpeningPeriod entity, boolean isExceptional) {
    List<Object> actualOpeningHours = new ArrayList<>();

    Calendar startDay = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    startDay.setTimeInMillis(entity.getStartDate().getTime());
    startDay.set(Calendar.SECOND, 0);
    startDay.set(Calendar.MILLISECOND, 0);

    Calendar endDay = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    endDay.setTimeInMillis(entity.getEndDate().getTime());
    endDay.set(Calendar.SECOND, 0);
    endDay.set(Calendar.MILLISECOND, 1);

    if (isExceptional) {
      while (startDay.before(endDay)) {
        List<ActualOpeningHours> event = createEvents(entity.getOpeningDays().get(0).getOpeningDay(), startDay, entity.getId(), true);
        actualOpeningHours.addAll(event);
        startDay.add(Calendar.DAY_OF_MONTH, 1);
      }
    } else {
      Map<DayOfWeek, OpeningDayWeekDay> openingDays = getOpeningDays(entity);

      while (startDay.before(endDay)) {
        DayOfWeek dayOfWeek = dayOfDate(startDay.getTime());
        OpeningDayWeekDay openingDay = openingDays.get(dayOfWeek);
        if (openingDay != null) {
          List<ActualOpeningHours> event = createEvents(openingDay.getOpeningDay(), startDay, entity.getId(), false);
          actualOpeningHours.addAll(event);
        }
        startDay.add(Calendar.DAY_OF_MONTH, 1);
      }
    }


    return actualOpeningHours;
  }

  private static Map<DayOfWeek, OpeningDayWeekDay> getOpeningDays(OpeningPeriod entity) {

    EnumMap openingDays = new EnumMap(DayOfWeek.class);

    for (OpeningDayWeekDay openingDay : entity.getOpeningDays()) {
      openingDays.put(DayOfWeek.valueOf(openingDay.getWeekdays().getDay().toString()), openingDay);
    }

    return openingDays;
  }

  private static List<ActualOpeningHours> createEvents(OpeningDay openingDay, Calendar actualDay, String generatedId, boolean isExceptional) {
    Calendar currentStartDate = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    currentStartDate.setTimeInMillis(actualDay.getTimeInMillis());

    Calendar currentEndDate = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    currentEndDate.setTimeInMillis(actualDay.getTimeInMillis());

    boolean allDay = true;
    boolean open = false;
    if (openingDay != null) {
      allDay = BooleanUtils.isTrue(openingDay.getAllDay());
      open = BooleanUtils.isTrue(openingDay.getOpen());
    }

    List<ActualOpeningHours> actualOpeningHours = new ArrayList<>();

    if (openingDay != null) {
      if (allDay) {
        ActualOpeningHours actualOpeningHour = new ActualOpeningHours();
        actualOpeningHour.setId(UUID.randomUUID().toString());
        actualOpeningHour.setOpeningId(generatedId);
        actualOpeningHour.setActualDay(currentStartDate.getTime());
        actualOpeningHour.setStartTime("00:00");
        actualOpeningHour.setEndTime("23:59");
        actualOpeningHour.setOpen(open);
        actualOpeningHour.setAllDay(allDay);
        actualOpeningHour.setExceptional(isExceptional);
        actualOpeningHours.add(actualOpeningHour);
      } else {
        for (OpeningHour openingHour : openingDay.getOpeningHour()) {
          ActualOpeningHours actualOpeningHour = new ActualOpeningHours();
          actualOpeningHour.setId(UUID.randomUUID().toString());
          actualOpeningHour.setOpeningId(generatedId);
          actualOpeningHour.setActualDay(currentStartDate.getTime());
          actualOpeningHour.setStartTime(openingHour.getStartTime());
          actualOpeningHour.setEndTime(openingHour.getEndTime());
          actualOpeningHour.setOpen(open);
          actualOpeningHour.setAllDay(allDay);
          actualOpeningHour.setExceptional(isExceptional);
          actualOpeningHours.add(actualOpeningHour);
        }
      }
    }

    return actualOpeningHours;
  }

  public static void addClosedDaysToOpenings(List<OpeningHoursPeriod> openingPeriods, CalendarOpeningsRequestParameters calendarOpeningsRequestParameters) {
    String startDate = calendarOpeningsRequestParameters.getStartDate();
    String endDate = calendarOpeningsRequestParameters.getEndDate();
    openingPeriods.sort(Comparator.comparing(OpeningHoursPeriod::getDate));
    if (startDate == null) {
      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
      if (openingPeriods.stream().findFirst().isPresent()) {
        calendar.setTimeInMillis(openingPeriods.stream().findFirst().orElse(new OpeningHoursPeriod()).getDate().getTime());
      }
      startDate = DATE_FORMATTER_SHORT.print(new DateTime(calendar));
    }
    if (endDate == null) {
      long count = (long) openingPeriods.size();
      Stream<OpeningHoursPeriod> stream = openingPeriods.stream();
      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
      if (!openingPeriods.isEmpty()) {
        calendar.setTimeInMillis(stream.skip(count - 1).findFirst().orElse(new OpeningHoursPeriod()).getDate().getTime());
      }
      endDate = DATE_FORMATTER_SHORT.print(new DateTime(calendar));
    }

    Calendar startDay = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    startDay.setTimeInMillis(DateTime.parse(startDate, DATE_FORMATTER_SHORT).getMillis());
    startDay.set(Calendar.SECOND, 0);
    startDay.set(Calendar.MILLISECOND, 0);

    Calendar endDay = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    endDay.setTimeInMillis(DateTime.parse(endDate, DATE_FORMATTER_SHORT).getMillis());
    endDay.set(Calendar.SECOND, 0);
    endDay.set(Calendar.MILLISECOND, 1);


    while (startDay.before(endDay)) {
      OpeningHoursPeriod openingPeriod = new OpeningHoursPeriod();
      openingPeriod.setDate(startDay.getTime());
      if (openingPeriods.stream().anyMatch(o -> o.getDate().equals(openingPeriod.getDate()))) {
        startDay.add(Calendar.DAY_OF_MONTH, 1);
      } else {
        OpeningDay openingDay = new OpeningDay();
        openingDay.setOpen(false);
        openingDay.setAllDay(true);
        openingDay.setExceptional(false);

        List<OpeningHour> openingHours = new ArrayList<>();
        OpeningHour openingHour = new OpeningHour();
        openingHour.setStartTime("00:00");
        openingHour.setEndTime("23:59");
        openingHours.add(openingHour);
        openingDay.setOpeningHour(openingHours);
        openingPeriod.setOpeningDay(openingDay);
        openingPeriods.add(openingPeriod);
        startDay.add(Calendar.DAY_OF_MONTH, 1);
      }
    }
  }

  public static Date getDateWithoutHoursAndMinutes(Date date) {
    return date;
  }

  public static OpeningDayWeekDay mapActualOpeningHoursListToOpeningDayWeekDay(List<ActualOpeningHours> list) {

    boolean exceptional = list.stream()
      .anyMatch(ActualOpeningHours::getExceptional);

    boolean allDay = list.stream()
      .filter(o -> o.getExceptional() == exceptional)
      .anyMatch(ActualOpeningHours::getAllDay);

    boolean open = list.stream()
      .filter(o -> o.getExceptional() == exceptional)
      .anyMatch(ActualOpeningHours::getOpen);

    Date date = list.stream()
      .filter(o -> o.getExceptional() == exceptional).findAny()
      .map(ActualOpeningHours::getActualDay)
      .orElse(null);

    List<OpeningHour> hours = list.stream()
      .filter(o -> o.getExceptional() == exceptional).map(o -> new OpeningHour()
        .withStartTime(o.getStartTime())
        .withEndTime(o.getEndTime()))
      .collect(Collectors.toList());

    SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
    df.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

    OpeningDay openingDay = new OpeningDay();
    openingDay.setExceptional(exceptional);
    openingDay.setAllDay(allDay);
    openingDay.setOpen(open);
    openingDay.setDate(date == null ? null : df.format(date));
    openingDay.setOpeningHour(hours);

    OpeningDayWeekDay openingDayWeekDay = new OpeningDayWeekDay();
    openingDayWeekDay.setOpeningDay(openingDay);

    return openingDayWeekDay;
  }

  public static OpeningDayWeekDay getOpeningDayWeekDayForTheEmptyDay(Date date) {

    SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
    format.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

    OpeningDay openingDay = new OpeningDay();
    openingDay.setDate(format.format(date));
    openingDay.setOpen(false);
    openingDay.setExceptional(false);
    openingDay.setAllDay(true);

    OpeningDayWeekDay openingDayWeekDay = new OpeningDayWeekDay();
    openingDayWeekDay.setOpeningDay(openingDay);

    return openingDayWeekDay;
  }

  public static Openings mapOpeningPeriodToOpenings(OpeningPeriod entity) {

    boolean exceptional = entity.getOpeningDays()
      .stream()
      .noneMatch(p -> p.getWeekdays() != null);

    Openings openings = new Openings();
    openings.setId(entity.getId());
    openings.setName(entity.getName());
    openings.setServicePointId(entity.getServicePointId());
    openings.setStartDate(entity.getStartDate());
    openings.setEndDate(entity.getEndDate());
    openings.setExceptional(exceptional);

    return openings;
  }

  public static OpeningCollection mapOpeningsToOpeningCollection(List<Openings> openings) {

    List<OpeningPeriod> openingPeriods = openings.stream()
      .map(CalendarUtils::mapOpeningsToOpeningPeriod)
      .collect(Collectors.toList());

    return new OpeningCollection()
      .withTotalRecords(openings.size())
      .withOpeningPeriods(openingPeriods);
  }

  public static OpeningPeriod mapOpeningsToOpeningPeriod(Openings openings) {

    return new OpeningPeriod()
      .withId(openings.getId())
      .withName(openings.getName())
      .withServicePointId(openings.getServicePointId())
      .withStartDate(openings.getStartDate())
      .withEndDate(openings.getEndDate());
  }

  public static AsyncResult<Response> mapExceptionToResponseResult(Throwable e) {
    Response errResponse;
    if (e.getClass() == NotFoundException.class) {
      errResponse = buildErrorResponse(404, TEXT_PLAIN, e.getMessage());
    } else if (e.getClass() == OverlapIntervalException.class) {
      errResponse = buildErrorResponse(422, APPLICATION_JSON, e.getMessage());
    } else {
      errResponse = buildErrorResponse(500, TEXT_PLAIN, e.getMessage());
    }

    return succeededFuture(errResponse);
  }

  private static Response buildErrorResponse(int status, String contentType, String errMessage) {
    return Response
      .status(status)
      .header(CONTENT_TYPE, contentType)
      .entity(createErrorMsg(errMessage))
      .build();
  }

  private static Errors createErrorMsg(String errMessage) {
    Error error = new Error()
      .withMessage(errMessage)
      .withCode(ERROR_CODE_INTERVALS_OVERLAP);
    return new Errors()
      .withErrors(Collections.singletonList(error));
  }
}
