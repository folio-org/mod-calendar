package org.folio.calendar.integration.service;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import java.util.Set;
import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.service.CalendarValidationService;
import org.folio.calendar.testconstants.NormalOpenings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CalendarValidationServiceNormalOpeningTest extends BaseApiTest {

  @Autowired
  CalendarValidationService validationService;

  @Test
  void testNoOverlaps() {
    assertThat(validationService.validateNormalOpenings(Set.of()), isEmpty());
    assertThat(
      validationService.validateNormalOpenings(Set.of(NormalOpenings.SUNDAY_MONDAY_ALL_DAY)),
      isEmpty()
    );
    assertThat(
      validationService.validateNormalOpenings(
        Set.of(NormalOpenings.MONDAY_23_00_TO_04_00_WRAPAROUND)
      ),
      isEmpty()
    );
    assertThat(
      validationService.validateNormalOpenings(
        Set.of(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_23_00_TO_23_59,
          NormalOpenings.TUESDAY_00_00_TO_12_30,
          NormalOpenings.WEDNESDAY_23_00_TO_SUNDAY_23_59
        )
      ),
      isEmpty()
    );
  }

  @Test
  void testSingleOverlaps() {
    assertThat(
      validationService.validateNormalOpenings(
        Set.of(NormalOpenings.SUNDAY_MONDAY_ALL_DAY, NormalOpenings.MONDAY_23_00_TO_23_59)
      ),
      isPresent()
    );
    assertThat(
      validationService.validateNormalOpenings(
        Set.of(
          NormalOpenings.MONDAY_23_00_TO_04_00_WRAPAROUND,
          NormalOpenings.WEDNESDAY_23_00_TO_FRIDAY_23_59
        )
      ),
      isPresent()
    );
    assertThat(
      validationService.validateNormalOpenings(
        Set.of(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_04_00_TO_14_59,
          NormalOpenings.MONDAY_23_00_TO_23_59
        )
      ),
      isPresent()
    );
  }

  @Test
  void testMultipleOverlaps() {
    assertThat(
      validationService.validateNormalOpenings(
        Set.of(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_04_00_TO_14_59,
          NormalOpenings.MONDAY_04_00_TO_23_59,
          NormalOpenings.MONDAY_23_00_TO_23_59
        )
      ),
      isPresent()
    );
    // 23:00-23:59 self-conflicts
    assertThat(
      validationService.validateNormalOpenings(
        Set.of(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_04_00_TO_14_59,
          NormalOpenings.MONDAY_23_00_TO_23_59
        )
      ),
      isPresent()
    );
  }

  @Test
  void testException() {
    InvalidDataException exception = validationService
      .validateNormalOpenings(
        Set.of(
          NormalOpenings.MONDAY_00_00_TO_12_30,
          NormalOpenings.MONDAY_04_00_TO_14_59,
          NormalOpenings.MONDAY_23_00_TO_23_59
        )
      )
      .get();
    assertThat(exception.getErrorCode(), is(ErrorCodeDTO.CALENDAR_INVALID_NORMAL_OPENINGS));
    assertThat(
      exception.getMessage(),
      anyOf(
        // order is non-deterministic
        is(
          "The following openings conflict with each other: Mon 12:00 AM - Mon 12:30 PM and Mon 4:00 AM - Mon 2:59 PM"
        ),
        is(
          "The following openings conflict with each other: Mon 4:00 AM - Mon 2:59 PM and Mon 12:00 AM - Mon 12:30 PM"
        )
      )
    );
  }
}
