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
   * An error message for when an endpoint is not found.
   *
   * Parameters {@link ERROR_ENDPOINT_NOT_FOUND_P} {@code METHOD} and {@code URL}
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
   * Parameters {@link ERROR_ENDPOINT_METHOD_INVALID_P} {@code METHOD} and {@code METHOD_LIST}
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
   * Parameters {@link ERROR_UNPARSABLE_DATA_P} {@code UNLOCALIZED_ERROR_MESSAGE}
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
   * Parameters {@link ERROR_INTERNAL_SERVER_ERROR_P} {@code CLASS_NAME} and {@code ERROR_MESSAGE}
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
   * Parameters {@link ERROR_CALENDAR_OVERLAP_P} {@code OVERLAP_NAME}, {@code OVERLAP_START_DATE}, and {@code OVERLAP_END_DATE}
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
   * Parameters {@link ERROR_DATE_RANGE_INVALID_P} {@code START_DATE} and {@code END_DATE}
   */
  public static final String ERROR_DATE_RANGE_INVALID = "error.dateRangeInvalid";

  @UtilityClass
  public class ERROR_DATE_RANGE_INVALID_P {

    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
  }
}
