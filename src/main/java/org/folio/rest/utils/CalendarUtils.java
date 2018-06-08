package org.folio.rest.utils;

import org.apache.commons.lang.BooleanUtils;
import org.folio.rest.beans.ActualOpeningHours;
import org.folio.rest.jaxrs.model.*;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

public class CalendarUtils {

  public static final String DAY_PATTERN = "EEEE";

  /*
  private static final String TIME_PATTERN = "HH:mm:ss.SSS'Z'";
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern(TIME_PATTERN);
  private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_PATTERN).withZoneUTC();
  public static final DateTimeFormatter BASIC_DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
  */

  private CalendarUtils() {
  }

  public static DayOfWeek dayOfDate(Date inputDate) {
    return DayOfWeek.valueOf(new SimpleDateFormat(DAY_PATTERN, Locale.ENGLISH).format(inputDate).toUpperCase());
  }


  public static List<Object> separateEvents(OpeningPeriod_ entity, String generatedId) {
    List<Object> actualOpeningHours = new ArrayList<>();

    Calendar startDay = Calendar.getInstance();
    startDay.setTimeInMillis(entity.getStartDate().getTime());
    startDay.set(Calendar.SECOND, 0);
    startDay.set(Calendar.MILLISECOND, 0);

    Calendar endDay = Calendar.getInstance();
    endDay.setTimeInMillis(entity.getEndDate().getTime());
    endDay.set(Calendar.SECOND, 0);
    endDay.set(Calendar.MILLISECOND, 1);

    Map<DayOfWeek, OpeningDay_> openingDays = getOpeningDays(entity);

    while (startDay.before(endDay)) {
      DayOfWeek dayOfWeek = dayOfDate(startDay.getTime());
      OpeningDay_ openingDay = openingDays.get(dayOfWeek);
      if (openingDay != null) {
        List<ActualOpeningHours> event = createEvents(openingDay.getOpeningDay(), startDay, generatedId);
        actualOpeningHours.addAll(event);
      }
      startDay.add(Calendar.DAY_OF_MONTH, 1);
    }

    return actualOpeningHours;
  }

  private static Map<DayOfWeek, OpeningDay_> getOpeningDays(OpeningPeriod_ entity) {

    EnumMap openingDays = new EnumMap(DayOfWeek.class);

    for (OpeningDay_ openingDay : entity.getOpeningDays()) {
      openingDays.put(DayOfWeek.valueOf(openingDay.getWeekdays().getDay().toString()), openingDay);
    }

    return openingDays;
  }

  private static List<ActualOpeningHours> createEvents(OpeningDay openingDay, Calendar actualDay, String generatedId) {
    Calendar currentStartDate = Calendar.getInstance();
    currentStartDate.setTimeInMillis(actualDay.getTimeInMillis());

    Calendar currentEndDate = Calendar.getInstance();
    currentEndDate.setTimeInMillis(actualDay.getTimeInMillis());

    boolean allDay = true;
    boolean open = false;
    if (openingDay != null) {
      allDay = openingDay.getAllDay();
      open = BooleanUtils.isTrue(openingDay.getOpen());
    }

    List<ActualOpeningHours> actualOpeningHours = new ArrayList<>();

    if (openingDay != null) {
      if (openingDay.getAllDay() || BooleanUtils.isFalse(openingDay.getOpen())) {
        currentStartDate.set(Calendar.HOUR_OF_DAY, 0);
        currentStartDate.set(Calendar.MINUTE, 0);
        currentEndDate.set(Calendar.HOUR_OF_DAY, 23);
        currentEndDate.set(Calendar.MINUTE, 59);

        ActualOpeningHours actualOpeningHour = new ActualOpeningHours();
        actualOpeningHour.setId(generatedId);
        actualOpeningHour.setOpeningId(generatedId);
        actualOpeningHour.setActualDay(currentStartDate.getTime());
        actualOpeningHour.setStartTime("00:00");
        actualOpeningHour.setEndTime("23:59");
        actualOpeningHour.setOpen(open);
        actualOpeningHour.setAllDay(allDay);
        actualOpeningHours.add(actualOpeningHour);
      } else {
        for (OpeningHour openingHour : openingDay.getOpeningHour()) {
          ActualOpeningHours actualOpeningHour = new ActualOpeningHours();
          actualOpeningHour.setId(generatedId);
          actualOpeningHour.setOpeningId(generatedId);
          actualOpeningHour.setActualDay(currentStartDate.getTime());
          actualOpeningHour.setStartTime(openingHour.getStartTime());
          actualOpeningHour.setEndTime(openingHour.getEndTime());
          actualOpeningHour.setOpen(open);
          actualOpeningHour.setAllDay(allDay);
          actualOpeningHours.add(actualOpeningHour);
        }
      }
    }

    return actualOpeningHours;
  }
}
