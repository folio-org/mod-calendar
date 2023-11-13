package org.folio.calendar.domain.request;

import java.util.Map;
import lombok.experimental.UtilityClass;
import org.folio.calendar.domain.types.Weekday;

/**
 * A class with constants for each translation key.
 *
 * Translation keys with parameters have sub-classes here, with their parameter
 * names enumerated similarly.
 *
 * For example, to get the translation key {@code error.endpointNotFound},
 * the constant {@code ERROR_ENDPOINT_NOT_FOUND} should be used and its
 * parameters referenced by {@code ERROR_ENDPOINT_NOT_FOUND_P.method}, for
 * example.
 *
 * These are provided to make changing of keys easier in the future.
 */
@UtilityClass
// allow different naming convention for parameter keys
@SuppressWarnings("java:S101")
public class TranslationKey {

  /**
   * Weekdays in their long representation, such as Monday, etc
   */
  public static final Map<Weekday, String> WEEKDAY_LONG = Map.of(
    Weekday.SUNDAY,
    "mod-calendar.weekday.long.sunday",
    Weekday.MONDAY,
    "mod-calendar.weekday.long.monday",
    Weekday.TUESDAY,
    "mod-calendar.weekday.long.tuesday",
    Weekday.WEDNESDAY,
    "mod-calendar.weekday.long.wednesday",
    Weekday.THURSDAY,
    "mod-calendar.weekday.long.thursday",
    Weekday.FRIDAY,
    "mod-calendar.weekday.long.friday",
    Weekday.SATURDAY,
    "mod-calendar.weekday.long.saturday"
  );

  /**
   * Weekdays in their short representation, such as Mon, Tue, etc
   */
  public static final Map<Weekday, String> WEEKDAY_SHORT = Map.of(
    Weekday.SUNDAY,
    "mod-calendar.weekday.short.sunday",
    Weekday.MONDAY,
    "mod-calendar.weekday.short.monday",
    Weekday.TUESDAY,
    "mod-calendar.weekday.short.tuesday",
    Weekday.WEDNESDAY,
    "mod-calendar.weekday.short.wednesday",
    Weekday.THURSDAY,
    "mod-calendar.weekday.short.thursday",
    Weekday.FRIDAY,
    "mod-calendar.weekday.short.friday",
    Weekday.SATURDAY,
    "mod-calendar.weekday.short.saturday"
  );

  /**
   * A string representation of a normal opening, such as "Mon 7:00 AM - Fri 11:00 PM"
   *
   * Parameters {@link NORMAL_OPENING_P}: {@code START_WEEKDAY_SHORT},
   * {@code START_TIME}, {@code END_WEEKDAY_SHORT}, and {@code END_TIME}.
   * It is recommended to use short weekday formatting (as given in {@code WEEKDAY_SHORT})
   * for the weekdays.
   */
  public static final String NORMAL_OPENING = "mod-calendar.normalOpening";

  @UtilityClass
  public static class NORMAL_OPENING_P {

    /**
     * The use of short weekdays, as given in {@code WEEKDAY_SHORT}, such as
     * Mon, Tue, etc., is recommended for this parameter.
     */
    public static final String START_WEEKDAY_SHORT = "startWeekdayShort";
    public static final String START_TIME = "startTime";
    /**
     * The use of short weekdays, as given in {@code WEEKDAY_SHORT}, such as
     * Mon, Tue, etc., is recommended for this parameter.
     */
    public static final String END_WEEKDAY_SHORT = "endWeekdayShort";
    public static final String END_TIME = "endTime";
  }

  /**
   * A string representation of an exception's range, such as
   * ""Foo" on Jan 12, 2022"
   *
   * Parameters {@link EXCEPTION_RANGE_SINGLE_DAY_P}: {@code NAME} and
   * {@code DATE}.
   *
   * For multiple-day exceptions, use {@code EXCEPTION_RANGE_MULTIPLE_DAYS}
   */
  public static final String EXCEPTION_RANGE_SINGLE_DAY = "mod-calendar.exceptionRangeSingleDay";

  @UtilityClass
  public static class EXCEPTION_RANGE_SINGLE_DAY_P {

    public static final String NAME = "name";
    /**
     * This expects a {@code LocalDate}
     */
    public static final String DATE = "date";
  }

  /**
   * A string representation of an exception's range, such as
   * ""Foo" from Jan 12, 2022 to Jan 13, 2022"
   *
   * Parameters {@link EXCEPTION_RANGE_MULTIPLE_DAYS_P}: {@code NAME},
   * {@code START_DATE}, and {@code END_DATE}.
   *
   * For single-day exceptions, use {@code EXCEPTION_RANGE_SINGLE_DAY}
   */
  public static final String EXCEPTION_RANGE_MULTIPLE_DAYS =
    "mod-calendar.exceptionRangeMultipleDays";

  @UtilityClass
  public static class EXCEPTION_RANGE_MULTIPLE_DAYS_P {

    public static final String NAME = "name";
    /**
     * This expects a {@code LocalDate}
     */
    public static final String START_DATE = "startDate";
    /**
     * This expects a {@code LocalDate}
     */
    public static final String END_DATE = "endDate";
  }

  /**
   * A string representation of an exception's range, such as
   * ""Foo" from Jan 12, 2022 to Jan 13, 2022"
   *
   * Parameters {@link EXCEPTION_RANGE_MULTIPLE_DAYS_P}: {@code NAME},
   * {@code START_DATE}, and {@code END_DATE}.
   *
   * For single-day exceptions, use {@code EXCEPTION_RANGE_SINGLE_DAY}
   */
  public static final String EXCEPTION_OPENING = "mod-calendar.exceptionOpening";

  @UtilityClass
  public static class EXCEPTION_OPENING_P {

    /**
     * This expects a {@code LocalDate}
     */
    public static final String START_DATE = "startDate";
    /**
     * This expects a {@code LocalTime}
     */
    public static final String START_TIME = "startTime";
    /**
     * This expects a {@code LocalDate}
     */
    public static final String END_DATE = "endDate";
    /**
     * This expects a {@code LocalTime}
     */
    public static final String END_TIME = "endTime";
  }

  /**
   * An error message for when an endpoint is not found.
   *
   * Parameters {@link ERROR_ENDPOINT_NOT_FOUND_P}: {@code METHOD} and {@code URL}
   */
  public static final String ERROR_ENDPOINT_NOT_FOUND = "mod-calendar.error.endpointNotFound";

  @UtilityClass
  public static class ERROR_ENDPOINT_NOT_FOUND_P {

    public static final String METHOD = "method";
    public static final String URL = "url";
  }

  /**
   * An error message for when an endpoint exists, however, does not support the provided method.
   *
   * Parameters {@link ERROR_ENDPOINT_METHOD_INVALID_P}: {@code METHOD} and {@code METHOD_LIST}
   */
  public static final String ERROR_ENDPOINT_METHOD_INVALID =
    "mod-calendar.error.endpointMethodInvalid";

  @UtilityClass
  public static class ERROR_ENDPOINT_METHOD_INVALID_P {

    /**
     * The attempted, invalid method
     */
    public static final String METHOD = "method";
    /**
     * All supported methods
     */
    public static final String METHOD_LIST = "methodList";
  }

  /**
   * An error message for when a request could not be parsed at all.
   *
   * Parameters {@link ERROR_UNPARSABLE_DATA_P}: {@code UNLOCALIZED_ERROR_MESSAGE}
   */
  public static final String ERROR_UNPARSABLE_DATA = "mod-calendar.error.unparsableData";

  @UtilityClass
  public static class ERROR_UNPARSABLE_DATA_P {

    /**
     * An error message from the parser, likely not localized
     */
    public static final String UNLOCALIZED_ERROR_MESSAGE = "unlocalizedErrorMessage";
  }

  /**
   * Catch all internal server error.
   *
   * Parameters {@link ERROR_INTERNAL_SERVER_ERROR_P}: {@code CLASS_NAME} and {@code ERROR_MESSAGE}
   */
  public static final String ERROR_INTERNAL_SERVER_ERROR = "mod-calendar.error.internalServerError";

  @UtilityClass
  public static class ERROR_INTERNAL_SERVER_ERROR_P {

    /**
     * The thrown exception class's name
     */
    public static final String CLASS_NAME = "className";
    /**
     * An error message from the parser, likely not localized
     */
    public static final String ERROR_MESSAGE = "errorMessage";
  }

  /**
   * A generic message for when a calendar is not found.  No parameters
   */
  public static final String ERROR_CALENDAR_NOT_FOUND = "mod-calendar.error.calendarNotFound";

  /**
   * An error message for when a calendar overlaps with an existing one
   *
   * Parameters {@link ERROR_CALENDAR_OVERLAP_P}: {@code OVERLAP_NAME}, {@code OVERLAP_START_DATE}, and {@code OVERLAP_END_DATE}
   */
  public static final String ERROR_CALENDAR_OVERLAP = "mod-calendar.error.calendarOverlap";

  @UtilityClass
  public static class ERROR_CALENDAR_OVERLAP_P {

    public static final String OVERLAP_NAME = "name";
    public static final String OVERLAP_START_DATE = "startDate";
    public static final String OVERLAP_END_DATE = "endDate";
  }

  /**
   * The provided calendar is missing a name (or it is only whitespace).  No parameters
   */
  public static final String ERROR_CALENDAR_NAME_EMPTY = "mod-calendar.error.calendarNameEmpty";

  /**
   * The calendar's start date is after the end date.
   *
   * Parameters {@link ERROR_DATE_RANGE_INVALID_P}: {@code START_DATE} and {@code END_DATE}
   */
  public static final String ERROR_DATE_RANGE_INVALID = "mod-calendar.error.dateRangeInvalid";

  @UtilityClass
  public static class ERROR_DATE_RANGE_INVALID_P {

    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
  }

  /**
   * The calendar's normal openings conflict.  The list will always contain at
   * least two openings (otherwise there would be no conflict).
   *
   * Parameters {@link ERROR_CALENDAR_INVALID_NORMAL_OPENINGS_P}: {@code OPENING_LIST}
   */
  public static final String ERROR_CALENDAR_INVALID_NORMAL_OPENINGS =
    "mod-calendar.error.calendarInvalidNormalOpenings";

  @UtilityClass
  public static class ERROR_CALENDAR_INVALID_NORMAL_OPENINGS_P {

    /**
     * This should be a list as formatted by
     * {@link org.folio.spring.service.TranslationService#formatList(java.util.List) TranslationService#formatList}
     */
    public static final String OPENING_LIST = "openingList";
  }

  /**
   * The calendar has an exception with no name (or one of only whitespace)
   *
   * No parameters are available (since it is difficult to refer to a specific
   * exception when it has no name)
   */
  public static final String ERROR_CALENDAR_INVALID_EXCEPTION_NAME =
    "mod-calendar.error.calendarInvalidExceptionRangeName";

  /**
   * The calendar has an exception with a start date after its end date.
   *
   * Parameters {@link ERROR_CALENDAR_INVALID_EXCEPTION_DATE_ORDER_P}:
   * {@code START_DATE}, {@code END_DATE}, and {@code NAME}.
   */
  public static final String ERROR_CALENDAR_INVALID_EXCEPTION_DATE_ORDER =
    "mod-calendar.error.calendarInvalidExceptionRangeDateOrder";

  @UtilityClass
  public static class ERROR_CALENDAR_INVALID_EXCEPTION_DATE_ORDER_P {

    /**
     * This expects a {@code LocalDate}
     */
    public static final String START_DATE = "startDate";
    /**
     * This expects a {@code LocalDate}
     */
    public static final String END_DATE = "endDate";
    public static final String NAME = "name";
  }

  /**
   * The calendar has an exception whose dates are not fully enclosed by the
   * parent calendar.
   *
   * Parameters {@link ERROR_CALENDAR_INVALID_EXCEPTION_DATE_OUT_OF_BOUNDS_P}:
   * {@code START_DATE}, {@code END_DATE}, and {@code NAME}.
   */
  public static final String ERROR_CALENDAR_INVALID_EXCEPTION_DATE_OUT_OF_BOUNDS =
    "mod-calendar.error.calendarInvalidExceptionRangeDateOutOfBounds";

  @UtilityClass
  public static class ERROR_CALENDAR_INVALID_EXCEPTION_DATE_OUT_OF_BOUNDS_P {

    public static final String NAME = "name";
    /**
     * This expects a {@code LocalDate}
     */
    public static final String EXCEPTION_START_DATE = "startDateException";
    /**
     * This expects a {@code LocalDate}
     */
    public static final String EXCEPTION_END_DATE = "endDateException";
    /**
     * This expects a {@code LocalDate}
     */
    public static final String CALENDAR_START_DATE = "startDateCalendar";
    /**
     * This expects a {@code LocalDate}
     */
    public static final String CALENDAR_END_DATE = "endDateCalendar";
  }

  /**
   * The calendar's exception ranges conflict.  The list will always contain at
   * least two items (otherwise there would be no conflict).
   *
   * Parameters {@link ERROR_CALENDAR_INVALID_EXCEPTION_RANGES_P}: {@code EXCEPTION_LIST}
   */
  public static final String ERROR_CALENDAR_INVALID_EXCEPTION_RANGES =
    "mod-calendar.error.calendarInvalidExceptionRanges";

  @UtilityClass
  public static class ERROR_CALENDAR_INVALID_EXCEPTION_RANGES_P {

    /**
     * This should be a list as formatted by
     * {@link org.folio.spring.service.TranslationService#formatList(java.util.List) TranslationService#formatList}
     */
    public static final String EXCEPTION_LIST = "exceptionList";
  }

  /**
   * The calendar's exception hours/openings conflict.  The list will always
   * contain at least two items (otherwise there would be no conflict).
   *
   * Parameters {@link ERROR_CALENDAR_INVALID_EXCEPTION_OPENINGS_P}:
   * {@code NAME} and {@code OPENING_LIST}
   */
  public static final String ERROR_CALENDAR_INVALID_EXCEPTION_OPENINGS =
    "mod-calendar.error.calendarInvalidExceptionOpenings";

  @UtilityClass
  public static class ERROR_CALENDAR_INVALID_EXCEPTION_OPENINGS_P {

    /**
     * The exception's name
     */
    public static final String NAME = "name";
    /**
     * This should be a list as formatted by
     * {@link org.folio.spring.service.TranslationService#formatList(java.util.List) TranslationService#formatList}
     */
    public static final String OPENING_LIST = "openingList";
  }

  /**
   * The exception's openings are outside of its own range.
   *
   * Parameters {@link ERROR_CALENDAR_INVALID_EXCEPTION_HOUR_OUT_OF_BOUNDS_P}:
   * {@code NAME}, {@code NUM_ERRORS}, and {@code LIST}.
   */
  public static final String ERROR_CALENDAR_INVALID_EXCEPTION_HOUR_OUT_OF_BOUNDS =
    "mod-calendar.error.calendarInvalidExceptionHourOutOfBounds";

  @UtilityClass
  public static class ERROR_CALENDAR_INVALID_EXCEPTION_HOUR_OUT_OF_BOUNDS_P {

    /**
     * The exception's name
     */
    public static final String NAME = "name";
    /**
     * The number of openings that exceeded the bounds, used for plurality.
     * Integer or similar is expected.
     */
    public static final String NUM_ERRORS = "n";
    public static final String LIST = "list";
  }
}
