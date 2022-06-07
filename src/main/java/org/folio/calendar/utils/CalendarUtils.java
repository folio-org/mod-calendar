package org.folio.calendar.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.dto.SingleDayOpeningCollectionDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningRangeDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.domain.entity.NormalOpening;
import org.folio.calendar.domain.types.Weekday;

@UtilityClass
public class CalendarUtils {

  /**
   * Take a provided calendar and split it into a series of dates
   * @param calendar the calendar to split into dates
   * @param dates the map of dates to fill (in-place)
   * @param boundStartDate the first date to include; if the calendar starts
   *        before this date, the extra will be ignored
   * @param boundEndDate the last date to include; if the calendar ends after
   *        this date, the extra will be ignored
   */
  public static void splitCalendarIntoDates(
    Calendar calendar,
    Map<LocalDate, SingleDayOpeningDTO> dates,
    LocalDate boundStartDate,
    LocalDate boundEndDate
  ) {
    // limit start and end dates to the calendar range
    LocalDate startDate = DateUtils.max(boundStartDate, calendar.getStartDate());
    LocalDate endDate = DateUtils.min(boundEndDate, calendar.getEndDate());

    // add all exceptional dates to the map; other dates will be computed one at a time
    addExceptionsToDateMap(calendar, dates, startDate, endDate);

    Map<Weekday, List<TemporalRange<LocalTime, NormalOpening>>> normalOpenings = NormalOpeningUtils.initializeWeekdayMapOfRanges();
    calendar
      .getNormalHours()
      .forEach(normalOpening ->
        NormalOpeningUtils.fillWeekdayMapWithTimeTuples(normalOpenings, normalOpening)
      );

    // iterate through all other dates, checking normal openings
    addNormalOpeningsToDateMap(dates, startDate, endDate, normalOpenings);
  }

  /**
   * Take a provided calendar and split its exceptions into a series of dates
   * @param calendar the calendar with exceptions to split
   * @param dates the map of dates to fill (in-place)
   * @param startDate the first date to include; if the calendar starts
   *        before this date, the extra will be ignored
   * @param endDate the last date to include; if the calendar ends after
   *        this date, the extra will be ignored
   */
  protected static void addExceptionsToDateMap(
    Calendar calendar,
    Map<LocalDate, SingleDayOpeningDTO> dates,
    LocalDate startDate,
    LocalDate endDate
  ) {
    calendar
      .getExceptions()
      .forEach((ExceptionRange exception) ->
        DateUtils
          .getDateRangeStream(
            DateUtils.max(startDate, exception.getStartDate()),
            DateUtils.min(endDate, exception.getEndDate())
          )
          .forEach((LocalDate date) -> {
            List<SingleDayOpeningRangeDTO> openings = ExceptionRangeUtils
              .toTemporalRanges(exception.getOpenings())
              .map(dateTimeRange ->
                TemporalUtils.getLocalTimeSliceOfDateTimeRange(dateTimeRange, date)
              )
              .filter(Optional::isPresent)
              .map(Optional::get)
              .map(timeRange ->
                SingleDayOpeningRangeDTO
                  .builder()
                  .startTime(timeRange.getStart())
                  .endTime(timeRange.getEnd())
                  .build()
              )
              .collect(Collectors.toList());

            dates.put(
              date,
              SingleDayOpeningDTO
                .builder()
                .date(date)
                .exceptionName(exception.getName())
                .exceptional(true)
                .openings(openings)
                .open(!openings.isEmpty())
                .allDay(
                  openings.isEmpty() ||
                  (openings.size() == 1 && TimeUtils.isAllDay(openings.get(0)))
                )
                .build()
            );
          })
      );
  }

  /**
   * Take a provided map of weekdays/time ranges representing normal openings
   * and split them into concrete dates for each opening
   * @param dates the map of dates to fill (in-place)
   * @param startDate the first date to include; if the calendar starts
   *        before this date, the extra will be ignored
   * @param endDate the last date to include; if the calendar ends after
   *        this date, the extra will be ignored
   * @param normalOpenings a map of weekdays to their time ranges
   */
  protected static void addNormalOpeningsToDateMap(
    Map<LocalDate, SingleDayOpeningDTO> dates,
    LocalDate startDate,
    LocalDate endDate,
    Map<Weekday, List<TemporalRange<LocalTime, NormalOpening>>> normalOpenings
  ) {
    DateUtils
      .getDateRangeStream(startDate, endDate)
      .forEach((LocalDate date) -> {
        List<TemporalRange<LocalTime, NormalOpening>> openings = normalOpenings.get(
          Weekday.from(date)
        );
        if (!openings.isEmpty()) {
          // put if absent will only add this date if it was not added above (from an exception)
          dates.putIfAbsent(
            date,
            SingleDayOpeningDTO
              .builder()
              .date(date)
              .exceptional(false)
              .openings(
                openings
                  .stream()
                  .map(timeRange ->
                    SingleDayOpeningRangeDTO
                      .builder()
                      .startTime(timeRange.getStart())
                      .endTime(timeRange.getEnd())
                      .build()
                  )
                  .collect(Collectors.toList())
              )
              .open(true)
              .allDay(openings.size() == 1 && TimeUtils.isAllDay(openings.get(0)))
              .build()
          );
        }
      });
  }

  /**
   * Take a map of dates and fill in any empty slots with
   * {@link SingleDayOpeningDTO SingleDayOpeningDTOs} representing regular
   * closures
   * @param dates the map to fill (in-place)
   * @param startDate the first date to consider
   * @param endDate the last date to consider
   */
  public static void fillClosedDates(
    Map<LocalDate, SingleDayOpeningDTO> dates,
    LocalDate startDate,
    LocalDate endDate
  ) {
    DateUtils
      .getDateRangeStream(startDate, endDate)
      .forEach((LocalDate date) ->
        dates.putIfAbsent(
          date,
          SingleDayOpeningDTO
            .builder()
            .date(date)
            .exceptional(false)
            .openings(new ArrayList<>())
            .open(false)
            .allDay(true)
            .build()
        )
      );
  }

  /**
   * Convert a map of dates to {@link SingleDayOpeningDTO SingleDayOpeningDTOs}
   * into a {@link SingleDayOpeningCollectionDTO SingleDayOpeningCollectionDTO}
   * with pagination
   * @param dates the map of dates to convert
   * @param offset the number of items to skip, for pagination
   * @param limit the maximum number of items to include, for pagination
   * @return a DTO collection ready for API return
   */
  public static SingleDayOpeningCollectionDTO openingMapToCollection(
    Map<LocalDate, SingleDayOpeningDTO> dates,
    int offset,
    int limit
  ) {
    return SingleDayOpeningCollectionDTO
      .builder()
      .dates(
        dates
          .entrySet()
          .stream()
          .sorted(Map.Entry.<LocalDate, SingleDayOpeningDTO>comparingByKey())
          .map(Map.Entry::getValue)
          .skip(offset)
          .limit(limit)
          .collect(Collectors.toList())
      )
      .totalRecords(dates.size())
      .build();
  }
}
