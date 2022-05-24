package org.folio.calendar.domain.request;

import lombok.experimental.UtilityClass;

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
public class LegacyTranslationKey {

  public static final String ERROR_DATE_INVALID = "error.dateInvalid";

  @UtilityClass
  public class ERROR_DATE_INVALID_P {

    public static final String USER_INPUTTED_DATE_STRING = "userInputtedDateString";
  }

  public static final String ERROR_PERIOD_ID_CONFLICT = "error.periodIdConflict";

  @UtilityClass
  public class ERROR_PERIOD_ID_CONFLICT_P {

    public static final String CONFLICTING_UUID = "conflictingUuid";
  }

  public static final String ERROR_SERVICE_POINT_EXISTING_MISMATCH =
    "error.servicePointExistingMismatch";

  @UtilityClass
  public class ERROR_SERVICE_POINT_EXISTING_MISMATCH_P {

    public static final String REQUESTED_ID = "requestedId";
  }

  public static final String ERROR_SERVICE_POINT_URL_MISMATCH = "error.servicePointUrlMismatch";

  @UtilityClass
  public class ERROR_SERVICE_POINT_URL_MISMATCH_P {

    public static final String UUID_1 = "uuid1";
    public static final String UUID_2 = "uuid2";
  }

  public static final String ERROR_NAME_EMPTY = "error.nameEmpty";
}
