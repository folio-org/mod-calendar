package org.folio.calendar.integration.api.openinghours.dates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import java.util.List;
import org.folio.calendar.domain.dto.SingleDayOpeningDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningRangeDTO;
import org.folio.calendar.domain.dto.SurroundingOpeningsDTO;
import org.folio.calendar.integration.api.openinghours.BaseOpeningHourApiTest;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class SurroundingOpeningsTest extends BaseOpeningHourApiTest {

  @Test
  void testEmptyGet() {
    Response response = getSurroundingOpenings(UUIDs.UUID_0, Dates.DATE_2021_01_02);
    response.then().statusCode(is(HttpStatus.OK.value()));
    List<SingleDayOpeningDTO> openings = response
      .getBody()
      .as(SurroundingOpeningsDTO.class)
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_01)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_02)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_03)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build()
      )
    );
  }

  @Test
  void testSampleWithExceptionsAndNormalOpenings() {
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_E);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));

    Response response = getSurroundingOpenings(UUIDs.UUID_2, Dates.DATE_2021_01_05);
    response.then().statusCode(is(HttpStatus.OK.value()));
    List<SingleDayOpeningDTO> openings = response
      .getBody()
      .as(SurroundingOpeningsDTO.class)
      .getOpenings();

    assertThat(openings, hasSize(3));
    assertThat(
      openings,
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_04)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_05)
          .open(false)
          .allDay(true)
          .exceptional(false)
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_08_19)
          .open(true)
          .allDay(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .exceptional(false)
          .build()
      )
    );
  }
}
