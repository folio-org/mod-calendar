package org.folio.calendar.integration.service;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
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
import org.folio.calendar.testconstants.ExceptionHours;
import org.folio.calendar.testconstants.ExceptionRanges;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CalendarValidationServiceExceptionOverlapTest extends BaseApiTest {

  @Autowired
  CalendarValidationService validationService;

  @Test
  void testRangeNoOverlaps() {
    assertThat(validationService.validateExceptionRangeOverlaps(Set.of()), isEmpty());
    assertThat(
      validationService.validateExceptionRangeOverlaps(
        Set.of(ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31)
      ),
      isEmpty()
    );
    assertThat(
      validationService.validateExceptionRangeOverlaps(
        Set.of(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_01,
          ExceptionRanges.CLOSED_2021_01_03_TO_2021_01_04
        )
      ),
      isEmpty()
    );
  }

  @Test
  void testRangeSingleOverlaps() {
    assertThat(
      validationService.validateExceptionRangeOverlaps(
        Set.of(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31,
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_01
        )
      ),
      isPresent()
    );
    assertThat(
      validationService.validateExceptionRangeOverlaps(
        Set.of(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31,
          ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1
        )
      ),
      isPresent()
    );
    assertThat(
      validationService.validateExceptionRangeOverlaps(
        Set.of(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_01,
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02,
          ExceptionRanges.CLOSED_2021_01_03_TO_2021_01_04
        )
      ),
      isPresent()
    );
  }

  @Test
  void testRangeMultipleOverlaps() {
    assertThat(
      validationService.validateExceptionRangeOverlaps(
        Set.of(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31,
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_01,
          ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1
        )
      ),
      isPresent()
    );
  }

  @Test
  void testRangeOverlapException() {
    InvalidDataException exception = validationService
      .validateExceptionRangeOverlaps(
        Set.of(
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_01,
          ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_02,
          ExceptionRanges.CLOSED_2021_01_03_TO_2021_01_04
        )
      )
      .get();
    assertThat(exception.getErrorCode(), is(ErrorCodeDTO.CALENDAR_INVALID_EXCEPTIONS));
    assertThat(
      exception.getMessage(),
      anyOf(
        // order is non-deterministic
        is(
          "The following exceptions conflict with each other: “comic sublime upscale utilize” on Jan 1, 2021 and “replay quake aloft routine” from Jan 1, 2021 to Jan 2, 2021"
        ),
        is(
          "The following exceptions conflict with each other: “replay quake aloft routine” from Jan 1, 2021 to Jan 2, 2021 and “comic sublime upscale utilize” on Jan 1, 2021"
        )
      )
    );
  }

  @Test
  void testOpeningNoOverlaps() {
    assertThat(validationService.validateExceptionHourOverlaps(Set.of()), is(empty()));
    assertThat(
      validationService.validateExceptionHourOverlaps(
        Set.of(
          ExceptionRange.builder().opening(ExceptionHours.OPEN_ALL_DAY_JAN_1_THRU_JAN_4).build()
        )
      ),
      is(empty())
    );
    assertThat(
      validationService.validateExceptionHourOverlaps(
        Set.of(
          ExceptionRange
            .builder()
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1)
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_2_THRU_JAN_3)
            .opening(ExceptionHours.OPEN_04_00_TO_14_59_JAN_4)
            .opening(ExceptionHours.OPEN_15_00_TO_23_00_JAN_4)
            .build()
        )
      ),
      is(empty())
    );
  }

  @Test
  void testOpeningSingleOverlaps() {
    assertThat(
      validationService.validateExceptionHourOverlaps(
        Set.of(
          ExceptionRange
            .builder()
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1)
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2)
            .build()
        )
      ),
      hasSize(1)
    );
    assertThat(
      validationService.validateExceptionHourOverlaps(
        Set.of(
          ExceptionRange
            .builder()
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1)
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2)
            .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_3_THRU_JAN_4)
            .build(),
          ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1
        )
      ),
      hasSize(1)
    );
  }

  @Test
  void testOpeningOverlapException() {
    List<InvalidDataException> exceptions = validationService.validateExceptionHourOverlaps(
      Set.of(
        ExceptionRange
          .builder()
          .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1)
          .opening(ExceptionHours.OPEN_00_00_TO_14_59_JAN_1_THRU_JAN_2)
          .build()
      )
    );
    assertThat(exceptions, hasSize(1));
    InvalidDataException exception = exceptions.get(0);
    assertThat(exception.getErrorCode(), is(ErrorCodeDTO.CALENDAR_INVALID_EXCEPTION_OPENINGS));
    assertThat(
      exception.getMessage(),
      anyOf(
        // order is non-deterministic
        is(
          "The following openings within “Untitled exception” conflict with each other: Jan 1, 2021 12:00 AM to Jan 1, 2021 2:59 PM and Jan 1, 2021 12:00 AM to Jan 2, 2021 2:59 PM"
        ),
        is(
          "The following openings within “Untitled exception” conflict with each other: Jan 1, 2021 12:00 AM to Jan 2, 2021 2:59 PM and Jan 1, 2021 12:00 AM to Jan 1, 2021 2:59 PM"
        )
      )
    );
  }
}
