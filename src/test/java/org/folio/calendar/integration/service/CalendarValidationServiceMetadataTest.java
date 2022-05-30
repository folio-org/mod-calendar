package org.folio.calendar.integration.service;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.domain.dto.ErrorCodeDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.service.CalendarValidationService;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Names;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CalendarValidationServiceMetadataTest extends BaseApiTest {

  @Autowired
  CalendarValidationService validationService;

  @Test
  void testCalendarNameValidation() {
    assertThat(
      "A calendar with no name results in an error",
      validationService.validateCalendarName(Calendar.builder().name("").build()),
      isPresent()
    );
    assertThat(
      "A calendar with a whitespace name results in an error",
      validationService.validateCalendarName(Calendar.builder().name(" \t\n\r ").build()),
      isPresent()
    );
    assertThat(
      "A calendar with a non-whitespace name does not result in an error",
      validationService.validateCalendarName(Calendar.builder().name(Names.NAME_1).build()),
      isEmpty()
    );
  }

  @Test
  void testCalendarNameException() {
    InvalidDataException exception = validationService
      .validateCalendarName(Calendar.builder().name("").build())
      .get();
    assertThat(exception.getErrorCode(), is(ErrorCodeDTO.CALENDAR_NO_NAME));
    assertThat(exception.getMessage(), is("Please provide a non-empty calendar name"));
  }

  @Test
  void testCalendarDateValidation() {
    assertThat(
      "A calendar with backward dates results in an error",
      validationService.validateCalendarDates(
        Calendar.builder().startDate(Dates.DATE_2021_01_04).endDate(Dates.DATE_2021_01_01).build()
      ),
      isPresent()
    );
    assertThat(
      "A calendar for a single day is valid",
      validationService.validateCalendarDates(
        Calendar.builder().startDate(Dates.DATE_2021_01_01).endDate(Dates.DATE_2021_01_01).build()
      ),
      isEmpty()
    );
    assertThat(
      "A calendar for multiple days in the proper order is valid",
      validationService.validateCalendarDates(
        Calendar.builder().startDate(Dates.DATE_2021_01_01).endDate(Dates.DATE_2021_01_04).build()
      ),
      isEmpty()
    );
  }

  @Test
  void testCalendarDateException() {
    InvalidDataException exception = validationService
      .validateCalendarDates(
        Calendar.builder().startDate(Dates.DATE_2021_01_04).endDate(Dates.DATE_2021_01_01).build()
      )
      .get();
    assertThat(exception.getErrorCode(), is(ErrorCodeDTO.CALENDAR_INVALID_DATE_RANGE));
    assertThat(
      exception.getMessage(),
      is("Start date (Jan 4, 2021) cannot be after end date (Jan 1, 2021)")
    );
  }
}
