package org.folio.calendar.integration.service;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Set;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.entity.ExceptionRange;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.service.CalendarValidationService;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.ExceptionHours;
import org.folio.calendar.testconstants.ExceptionRanges;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CalendarValidationServiceExceptionMetadataTest extends BaseApiTest {

  @Autowired
  CalendarValidationService validationService;

  @Test
  void testExceptionNameValidation() {
    assertThat(
      "An exception with no name results in an error",
      validationService.validateExceptionRangeNames(
        Set.of(ExceptionRange.builder().name("").build())
      ),
      isPresent()
    );
    assertThat(
      "A set of exceptions with only one missing a name results in an error",
      validationService.validateExceptionRangeNames(
        Set.of(
          ExceptionRange.builder().name("").build(),
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31
        )
      ),
      isPresent()
    );
    assertThat(
      "A set of exceptions with names does not result in an error",
      validationService.validateExceptionRangeNames(
        Set.of(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_04,
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31
        )
      ),
      isEmpty()
    );
    assertThat(
      "No exceptions results in no error",
      validationService.validateExceptionRangeNames(Set.of()),
      isEmpty()
    );
  }

  @Test
  void testExceptionNameValidationException() {
    InvalidDataException exception = validationService
      .validateExceptionRangeNames(Set.of(ExceptionRange.builder().name("").build()))
      .get();
    assertThat(exception.getErrorCode(), is(ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_NAME));
    assertThat(exception.getMessage(), is("At least one exception is missing a name"));
  }

  @Test
  void testExceptionDateIntegrity() {
    assertThat(
      "An exception range with backward dates results in an error",
      validationService.validateExceptionRangeDateOrder(
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_04)
            .endDate(Dates.DATE_2021_01_01)
            .build()
        )
      ),
      isPresentAnd(hasSize(1))
    );
    assertThat(
      "An exception range with backward dates results in an error",
      validationService.validateExceptionRangeDateOrder(
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_04)
            .endDate(Dates.DATE_2021_01_01)
            .build(),
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31
        )
      ),
      isPresentAnd(hasSize(1))
    );
    assertThat(
      "An exception range with multiple backward dates results in multiple errors",
      validationService.validateExceptionRangeDateOrder(
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_04)
            .endDate(Dates.DATE_2021_01_01)
            .build(),
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_03_16)
            .endDate(Dates.DATE_2021_01_01)
            .build()
        )
      ),
      isPresentAnd(hasSize(2))
    );
    assertThat(
      "An exception range for a single day is valid",
      validationService.validateExceptionRangeDateOrder(
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_01)
            .build()
        )
      ),
      isEmpty()
    );
    assertThat(
      "A exception range for multiple days in the proper order is valid",
      validationService.validateExceptionRangeDateOrder(
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_04)
            .build()
        )
      ),
      isEmpty()
    );
    assertThat(
      "A set of proper exception ranges is valid",
      validationService.validateExceptionRangeDateOrder(
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_04)
            .build(),
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_01)
            .build(),
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31
        )
      ),
      isEmpty()
    );
    assertThat(
      "A set of no exception ranges is valid",
      validationService.validateExceptionRangeDateOrder(Set.of()),
      isEmpty()
    );
  }

  @Test
  void testExceptionDateIntegrityException() {
    List<InvalidDataException> exceptions = validationService
      .validateExceptionRangeDateOrder(
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_04)
            .endDate(Dates.DATE_2021_01_01)
            .build(),
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31
        )
      )
      .get();
    assertThat(exceptions, hasSize(1));
    InvalidDataException exception = exceptions.get(0);
    assertThat(exception.getErrorCode(), is(ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_DATE_ORDER));
    assertThat(
      exception.getMessage(),
      is(
        "The start date (Jan 4, 2021) is after the end date (Jan 1, 2021) for exception “Untitled exception”"
      )
    );
  }

  @Test
  void testExceptionDateBounds() {
    assertThat(
      "An exception range outside of the calendar dates results in an error",
      validationService.validateExceptionRangeDateBounds(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_04,
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_03_16)
            .build()
        )
      ),
      isPresentAnd(hasSize(1))
    );
    assertThat(
      "An exception range outside of the calendar dates results in an error",
      validationService.validateExceptionRangeDateBounds(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_04,
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_03_16)
            .build(),
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_01
        )
      ),
      isPresentAnd(hasSize(1))
    );
    assertThat(
      "An exception range with multiple out of bound dates results in multiple errors",
      validationService.validateExceptionRangeDateBounds(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_04)
            .build(),
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_03_16)
            .build()
        )
      ),
      isPresentAnd(hasSize(2))
    );
    assertThat(
      "A exception range in bounds is valid",
      validationService.validateExceptionRangeDateBounds(
        Calendars.CALENDAR_2021_01_01_TO_2021_07_04,
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_04)
            .build()
        )
      ),
      isEmpty()
    );
    assertThat(
      "A set of exception ranges in bounds is valid",
      validationService.validateExceptionRangeDateBounds(
        Calendars.CALENDAR_2021_01_01_TO_2021_07_04,
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_04)
            .build(),
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_01)
            .build(),
          ExceptionRanges.CLOSED_2021_01_03_TO_2021_01_04
        )
      ),
      isEmpty()
    );
    assertThat(
      "A set of no exception ranges is valid",
      validationService.validateExceptionRangeDateBounds(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Set.of()
      ),
      isEmpty()
    );
  }

  @Test
  void testExceptionDateBoundsException() {
    List<InvalidDataException> exceptions = validationService
      .validateExceptionRangeDateBounds(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_04,
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_03_16)
            .build()
        )
      )
      .get();
    assertThat(exceptions, hasSize(1));
    InvalidDataException exception = exceptions.get(0);
    assertThat(exception.getErrorCode(), is(ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_DATE_BOUNDARY));
    assertThat(
      exception.getMessage(),
      is(
        "The exception “Untitled exception” (Jan 1, 2021 to Mar 16, 2021) is not fully enclosed by the calendar's start and end dates (Jan 1, 2021 to Jan 4, 2021)"
      )
    );
  }

  @Test
  void testExceptionHourBounds() {
    assertThat(
      "An exception hour outside of the parent exception range results in an error",
      validationService.validateExceptionHourBounds(
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_01)
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2)
            .build()
        )
      ),
      hasSize(1)
    );
    assertThat(
      "An exception hour outside of the parent exception range results in an error",
      validationService.validateExceptionHourBounds(
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_01)
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2)
            .build(),
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_01,
          ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4
        )
      ),
      hasSize(1)
    );
    assertThat(
      "Multiple exception ranges with multiple out of bound openings results in multiple errors",
      validationService.validateExceptionHourBounds(
        Set.of(
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_01)
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2)
            .build(),
          ExceptionRange
            .builder()
            .startDate(Dates.DATE_2021_01_01)
            .endDate(Dates.DATE_2021_01_02)
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_3_THRU_JAN_4)
            .build()
        )
      ),
      hasSize(2)
    );
    assertThat(
      "A exception range in bounds is valid",
      validationService.validateExceptionHourBounds(
        Set.of(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_01,
          ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4
        )
      ),
      is(empty())
    );
    assertThat(
      "A set of no exception ranges is valid",
      validationService.validateExceptionHourBounds(Set.of()),
      is(empty())
    );
  }

  @Test
  void testExceptionHourBoundsExceptionSingular() {
    List<InvalidDataException> exceptions = validationService.validateExceptionHourBounds(
      Set.of(
        ExceptionRange
          .builder()
          .startDate(Dates.DATE_2021_01_01)
          .endDate(Dates.DATE_2021_01_01)
          .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2)
          .build()
      )
    );
    assertThat(exceptions, hasSize(1));
    InvalidDataException exception = exceptions.get(0);
    assertThat(
      exception.getErrorCode(),
      is(ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_OPENING_BOUNDARY)
    );
    assertThat(
      exception.getMessage(),
      is(
        "The exception “Untitled exception” has an opening outside of the exception's bounds: Jan 1, 2021 12:00 AM to Jan 2, 2021 2:59 PM"
      )
    );
  }

  @Test
  void testExceptionHourBoundsExceptionPlural() {
    List<InvalidDataException> exceptions = validationService.validateExceptionHourBounds(
      Set.of(
        ExceptionRange
          .builder()
          .startDate(Dates.DATE_2021_01_01)
          .endDate(Dates.DATE_2021_01_01)
          .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2)
          .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_3_THRU_JAN_4)
          .build()
      )
    );
    assertThat(exceptions, hasSize(1));
    InvalidDataException exception = exceptions.get(0);
    assertThat(
      exception.getErrorCode(),
      is(ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_OPENING_BOUNDARY)
    );
    assertThat(
      exception.getMessage(),
      anyOf(
        is(
          "The exception “Untitled exception” has openings outside of the exception's bounds: Jan 1, 2021 12:00 AM to Jan 2, 2021 2:59 PM and Jan 3, 2021 12:00 AM to Jan 4, 2021 2:59 PM"
        ),
        is(
          "The exception “Untitled exception” has openings outside of the exception's bounds: Jan 3, 2021 12:00 AM to Jan 4, 2021 2:59 PM and Jan 1, 2021 12:00 AM to Jan 2, 2021 2:59 PM"
        )
      )
    );
  }
}
