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
    "weekday.long.sunday",
    Weekday.MONDAY,
    "weekday.long.monday",
    Weekday.TUESDAY,
    "weekday.long.tuesday",
    Weekday.WEDNESDAY,
    "weekday.long.wednesday",
    Weekday.THURSDAY,
    "weekday.long.thursday",
    Weekday.FRIDAY,
    "weekday.long.friday",
    Weekday.SATURDAY,
    "weekday.long.saturday"
  );

  /**
   * Weekdays in their short representation, such as Mon, Tue, etc
   */
  public static final Map<Weekday, String> WEEKDAY_SHORT = Map.of(
    Weekday.SUNDAY,
    "weekday.short.sunday",
    Weekday.MONDAY,
    "weekday.short.monday",
    Weekday.TUESDAY,
    "weekday.short.tuesday",
    Weekday.WEDNESDAY,
    "weekday.short.wednesday",
    Weekday.THURSDAY,
    "weekday.short.thursday",
    Weekday.FRIDAY,
    "weekday.short.friday",
    Weekday.SATURDAY,
    "weekday.short.saturday"
  );

  /**
   * A string representation of a normal opening, such as "Mon 7:00 AM - Fri 11:00 PM"
   *
   * Parameters {@link NORMAL_OPENING_P}: {@code START_WEEKDAY_SHORT},
   * {@code START_TIME}, {@code END_WEEKDAY_SHORT}, and {@code END_TIME}.
   * It is recommended to use short weekday formatting (as given in {@code WEEKDAY_SHORT})
   * for the weekdays.
   */
  public static final String NORMAL_OPENING = "normalOpening";

  @UtilityClass
  public class NORMAL_OPENING_P {

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
  public static final String EXCEPTION_RANGE_SINGLE_DAY = "exceptionRangeSingleDay";

  @UtilityClass
  public class EXCEPTION_RANGE_SINGLE_DAY_P {

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
  public static final String EXCEPTION_RANGE_MULTIPLE_DAYS = "exceptionRangeMultipleDays";

  @UtilityClass
  public class EXCEPTION_RANGE_MULTIPLE_DAYS_P {

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
  public static final String EXCEPTION_OPENING = "exceptionOpening";

  @UtilityClass
  public class EXCEPTION_OPENING_P {

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
   * Delimiters for list items
   */
  @UtilityClass
  public class LIST_SEPARATORS {

    /**
     * The number of elements in the list to indicate that
     * {@code LIST_TWO_SEPARATOR} should be used.
     */
    public static final int LIST_TWO_COUNT = 2;

    /**
     * A separator for two items only.  For example, with {@code [A, B]} and
     * {@code list.twoSeparator: " and "}, the result will be {@code "A and B"}
     */
    public static final String LIST_TWO_SEPARATOR = "list.twoSeparator";
    /**
     * A separator, with {@link LIST_THREE_OR_MORE_LAST_SEPARATOR}, for lists
     * of three or more items.  This separator is placed between every pair of
     * items except for the last pair.
     *
     * If {@code list.threeOrMoreSeparator: ", "} and
     * {@code list.threeOrMoreLastSeparator: ", and "} then a list of
     * {@code [A B C]} will result in {@code "A, B, and C"}.  {@code [A B C D]}
     * will yield {@code "A, B, C, and D"}
     */
    public static final String LIST_THREE_OR_MORE_SEPARATOR = "list.threeOrMoreSeparator";
    /**
     * A separator, with {@link LIST_THREE_OR_MORE_SEPARATOR}, for lists
     * of three or more items.  This separator is placed between the last pair
     * of items in the list.  In English, {@code ", and "} would be used.
     *
     * If {@code list.threeOrMoreSeparator: ", "} and
     * {@code list.threeOrMoreLastSeparator: ", and "} then a list of
     * {@code [A B C]} will result in {@code "A, B, and C"}.  {@code [A B C D]}
     * will yield {@code "A, B, C, and D"}
     */
    public static final String LIST_THREE_OR_MORE_LAST_SEPARATOR = "list.threeOrMoreLastSeparator";
  }

  /**
   * An error message for when an endpoint is not found.
   *
   * Parameters {@link ERROR_ENDPOINT_NOT_FOUND_P}: {@code METHOD} and {@code URL}
   */
  public static final String ERROR_ENDPOINT_NOT_FOUND = "error.endpointNotFound";

  @UtilityClass
  public class ERROR_ENDPOINT_NOT_FOUND_P {

    public static final String METHOD = "method";
    public static final String URL = "url";
  }

  /**
   * An error message for when an endpoint exists, however, does not support the provided method.
   *
   * Parameters {@link ERROR_ENDPOINT_METHOD_INVALID_P}: {@code METHOD} and {@code METHOD_LIST}
   */
  public static final String ERROR_ENDPOINT_METHOD_INVALID = "error.endpointMethodInvalid";

  @UtilityClass
  public class ERROR_ENDPOINT_METHOD_INVALID_P {

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
  public static final String ERROR_UNPARSABLE_DATA = "error.unparsableData";

  @UtilityClass
  public class ERROR_UNPARSABLE_DATA_P {

    /**
     * An error message from the parser, likely not localized
     */
    public static final String UNLOCALIZED_ERROR_MESSAGE = "unLocalizedErrorMessage";
  }

  /**
   * Catch all internal server error.
   *
   * Parameters {@link ERROR_INTERNAL_SERVER_ERROR_P}: {@code CLASS_NAME} and {@code ERROR_MESSAGE}
   */
  public static final String ERROR_INTERNAL_SERVER_ERROR = "error.internalServerError";

  @UtilityClass
  public class ERROR_INTERNAL_SERVER_ERROR_P {

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
  public static final String ERROR_CALENDAR_NOT_FOUND = "error.calendarNotFound";

  /**
   * An error message for when a calendar overlaps with an existing one
   *
   * Parameters {@link ERROR_CALENDAR_OVERLAP_P}: {@code OVERLAP_NAME}, {@code OVERLAP_START_DATE}, and {@code OVERLAP_END_DATE}
   */
  public static final String ERROR_CALENDAR_OVERLAP = "error.calendarOverlap";

  @UtilityClass
  public class ERROR_CALENDAR_OVERLAP_P {

    public static final String OVERLAP_NAME = "name";
    public static final String OVERLAP_START_DATE = "startDate";
    public static final String OVERLAP_END_DATE = "endDate";
  }

  /**
   * The provided calendar is missing a name (or it is only whitespace).  No parameters
   */
  public static final String ERROR_CALENDAR_NAME_EMPTY = "error.calendarNameEmpty";

  /**
   * The calendar's start date is after the end date.
   *
   * Parameters {@link ERROR_DATE_RANGE_INVALID_P}: {@code START_DATE} and {@code END_DATE}
   */
  public static final String ERROR_DATE_RANGE_INVALID = "error.dateRangeInvalid";

  @UtilityClass
  public class ERROR_DATE_RANGE_INVALID_P {

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
    "error.calendarInvalidNormalOpenings";

  @UtilityClass
  public class ERROR_CALENDAR_INVALID_NORMAL_OPENINGS_P {

    /**
     * This should be a list as formatted by
     * {@link org.folio.calendar.i18n.TranslationService#formatList(java.util.List) TranslationService#formatList}
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
    "error.calendarInvalidExceptionRangeName";

  /**
   * The calendar has an exception with a start date after its end date.
   *
   * Parameters {@link ERROR_CALENDAR_INVALID_EXCEPTION_DATE_ORDER_P}:
   * {@code START_DATE}, {@code END_DATE}, and {@code NAME}.
   */
  public static final String ERROR_CALENDAR_INVALID_EXCEPTION_DATE_ORDER =
    "error.calendarInvalidExceptionRangeDateOrder";

  @UtilityClass
  public class ERROR_CALENDAR_INVALID_EXCEPTION_DATE_ORDER_P {

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
    "error.calendarInvalidExceptionRangeDateOutOfBounds";

  @UtilityClass
  public class ERROR_CALENDAR_INVALID_EXCEPTION_DATE_OUT_OF_BOUNDS_P {

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
    "error.calendarInvalidExceptionRanges";

  @UtilityClass
  public class ERROR_CALENDAR_INVALID_EXCEPTION_RANGES_P {

    /**
     * This should be a list as formatted by
     * {@link org.folio.calendar.i18n.TranslationService#formatList(java.util.List) TranslationService#formatList}
     */
    public static final String EXCEPTION_LIST = "exceptionList";
  }

  /**
   * The calendar's exception hours/openings conflict.  The list will always
   * contain at least two items (otherwise there would be no conflict).
   *
   * Parameters {@link ERROR_CALENDAR_INVALID_EXCEPTION_OPENINGS_P}: {@code OPENING_LIST}
   */
  public static final String ERROR_CALENDAR_INVALID_EXCEPTION_OPENINGS =
    "error.calendarInvalidExceptionOpenings";

  @UtilityClass
  public class ERROR_CALENDAR_INVALID_EXCEPTION_OPENINGS_P {

    /**
     * This should be a list as formatted by
     * {@link org.folio.calendar.i18n.TranslationService#formatList(java.util.List) TranslationService#formatList}
     */
    public static final String OPENING_LIST = "openingList";
  }
}
