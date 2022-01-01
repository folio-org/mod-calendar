package org.folio.calendar.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.CalculatedOpenings;
import org.folio.calendar.domain.dto.OpeningDayConcrete;
import org.folio.calendar.domain.dto.OpeningDayConcreteCollection;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.domain.dto.Period;
import org.folio.calendar.domain.dto.PeriodCollection;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.types.LegacyPeriodDate;
import org.folio.calendar.exception.DataNotFoundException;
import org.folio.calendar.repository.PeriodQueryFilter;
import org.folio.calendar.rest.resource.CalendarApi;
import org.folio.calendar.service.CalendarService;
import org.folio.calendar.utils.DateUtils;
import org.folio.calendar.utils.PeriodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main Calendar API controller
 */
@Log4j2
@RestController
@RequestMapping(value = "/")
public final class CalendarController implements CalendarApi {

  public static final String PARAMETER_NAME_PERIOD = "period";
  public static final String PARAMETER_NAME_SERVICE_POINT_ID = "servicePointId";
  public static final String PARAMETER_NAME_PERIOD_ID = "periodId";

  @Autowired
  public CalendarService calendarService;

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Period> addNewPeriod(
    String xOkapiTenant,
    UUID servicePointId,
    Period period
  ) {
    Calendar calendar = this.calendarService.createCalendarFromPeriod(period, servicePointId);

    return new ResponseEntity<>(PeriodUtils.toPeriod(calendar), HttpStatus.CREATED);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<PeriodCollection> getPeriodsForServicePoint(
    String xOkapiTenant,
    UUID servicePointId,
    Boolean withOpeningDays,
    Boolean showPast,
    Boolean showExceptional
  ) {
    PeriodQueryFilter filter;
    if (Boolean.TRUE.equals(showExceptional)) {
      filter = PeriodQueryFilter.EXCEPTIONS;
    } else {
      filter = PeriodQueryFilter.NORMAL_HOURS;
    }

    return new ResponseEntity<>(
      this.calendarService.getPeriods(
          servicePointId,
          filter,
          Boolean.TRUE.equals(showPast),
          Boolean.TRUE.equals(withOpeningDays)
        ),
      HttpStatus.OK
    );
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Period> getPeriodById(
    String xOkapiTenant,
    UUID servicePointId,
    UUID periodId
  ) {
    return new ResponseEntity<>(
      PeriodUtils.toPeriod(this.calendarService.getCalendarById(servicePointId, periodId)),
      HttpStatus.OK
    );
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<Void> deletePeriodById(
    String xOkapiTenant,
    UUID servicePointId,
    UUID periodId
  ) {
    Calendar calendar = this.calendarService.getCalendarById(servicePointId, periodId);

    this.calendarService.deleteCalendar(calendar);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("java:S1141")
  public ResponseEntity<Void> updatePeriodById(
    String xOkapiTenant,
    UUID servicePointId,
    UUID periodId,
    Period period
  ) {
    try {
      Calendar originalCalendar = this.calendarService.getCalendarById(periodId);

      this.calendarService.replaceCalendar(originalCalendar, period, servicePointId);
    } catch (DataNotFoundException exception) {
      log.info("Current calendar does not exist; creating new one");
      log.info(exception);

      this.calendarService.createCalendarFromPeriod(period, servicePointId);
    }

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<OpeningDayConcreteCollection> getDateOpenings(
    String xOkapiTenant,
    UUID servicePointId,
    LocalDate startDate,
    LocalDate endDate,
    Boolean includeClosedDays,
    Boolean actualOpening,
    Integer offset,
    Integer limit
  ) {
    Iterable<Calendar> calendars =
      this.calendarService.getCalendars(servicePointId, startDate, endDate);

    LocalDate firstDate = startDate;
    LocalDate lastDate = endDate;

    Map<LocalDate, OpeningDayInfo> normalOpenings = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> exceptions = new HashMap<>();

    for (Calendar calendar : calendars) {
      if (startDate == null) {
        firstDate = DateUtils.min(calendar.getStartDate(), firstDate);
      }
      if (endDate == null) {
        lastDate = DateUtils.max(calendar.getEndDate(), lastDate);
      }

      PeriodUtils.mergeInto(normalOpenings, calendar.getDailyNormalOpenings(startDate, endDate));
      PeriodUtils.mergeInto(exceptions, calendar.getDailyExceptionalOpenings(startDate, endDate));
    }

    List<OpeningDayConcrete> collection = PeriodUtils.buildOpeningDayConcreteCollection(
      normalOpenings,
      exceptions,
      firstDate,
      lastDate,
      includeClosedDays,
      actualOpening
    );

    return new ResponseEntity<>(
      OpeningDayConcreteCollection
        .builder()
        .openingPeriods(collection.stream().skip(offset).limit(limit).collect(Collectors.toList()))
        .totalRecords(collection.size())
        .build(),
      HttpStatus.OK
    );
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<CalculatedOpenings> getNearestOpenings(
    String xOkapiTenant,
    UUID servicePointId,
    LegacyPeriodDate requestedDate
  ) {
    LocalDate date = requestedDate.getValue();
    Iterable<Calendar> calendars = this.calendarService.getCalendars(servicePointId, null, null);

    Map<LocalDate, OpeningDayInfo> normalOpenings = new HashMap<>();
    Map<LocalDate, OpeningDayInfo> exceptions = new HashMap<>();

    LocalDate firstDate = date;
    LocalDate lastDate = date;

    for (Calendar calendar : calendars) {
      firstDate = DateUtils.min(calendar.getStartDate(), firstDate);
      lastDate = DateUtils.max(calendar.getEndDate(), lastDate);

      PeriodUtils.mergeInto(normalOpenings, calendar.getDailyNormalOpenings(null, null));
      PeriodUtils.mergeInto(exceptions, calendar.getDailyExceptionalOpenings(null, null));
    }

    List<OpeningDayConcrete> allOpenings = PeriodUtils.buildOpeningDayConcreteCollection(
      normalOpenings,
      exceptions,
      firstDate,
      lastDate,
      false,
      true
    );

    int currentIndex = Arrays.binarySearch(
      allOpenings.stream().map(opening -> opening.getDate().getValue()).toArray(),
      date
    );

    OpeningDayInfo previous = null;
    OpeningDayInfo current = null;
    OpeningDayInfo next = null;

    OpeningDayInfo empty = OpeningDayInfo
      .builder()
      .allDay(false)
      .open(false)
      .exceptional(false)
      .openingHour(new ArrayList<>())
      .build();

    int prevIndex = -1;
    int nextIndex = -1;

    if (currentIndex >= 0) {
      current = allOpenings.get(currentIndex).getOpeningDay().withDate(requestedDate);
      prevIndex = currentIndex - 1;
      nextIndex = currentIndex + 1;
    } else {
      // per Arrays.binarySearch: currentIndex = (-(insertion point) - 1)
      // The insertion point is defined as the point at which the key would be inserted into
      // the array: the index of the first element greater than the key, or a.length if all
      // elements in the array are less than the specified key
      current = empty.withAllDay(true).withDate(requestedDate);
      nextIndex = (currentIndex + 1) * -1;
      prevIndex = nextIndex - 1;
    }

    // ensure that previous and next do not refer to adjacent closures
    while (prevIndex >= 0 && !allOpenings.get(prevIndex).getOpeningDay().isOpen()) {
      prevIndex--;
    }
    while (nextIndex < allOpenings.size() && !allOpenings.get(nextIndex).getOpeningDay().isOpen()) {
      nextIndex++;
    }

    if (prevIndex >= 0) {
      OpeningDayConcrete concrete = allOpenings.get(prevIndex);
      previous = concrete.getOpeningDay().withDate(concrete.getDate());
    }

    if (nextIndex < allOpenings.size()) {
      OpeningDayConcrete concrete = allOpenings.get(nextIndex);
      next = concrete.getOpeningDay().withDate(concrete.getDate());
    }

    if (previous == null) {
      previous = empty;
    }
    if (next == null) {
      next = empty;
    }

    return ResponseEntity.ok(new CalculatedOpenings(Arrays.asList(previous, current, next)));
  }
}
