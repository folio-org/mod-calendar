package org.folio.calendar.integration.api.openinghours.dates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import org.folio.calendar.domain.dto.SingleDayOpeningCollectionDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningDTO;
import org.folio.calendar.domain.dto.SingleDayOpeningRangeDTO;
import org.folio.calendar.integration.api.openinghours.BaseOpeningHourApiTest;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.ExceptionRanges;
import org.folio.calendar.testconstants.Times;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class AllOpeningsTest extends BaseOpeningHourApiTest {

  @Test
  void testEmptyGet() {
    Response response = getAllOpenings(
      UUIDs.UUID_0,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_01,
      false,
      null,
      null
    );
    response.then().statusCode(is(HttpStatus.OK.value()));

    SingleDayOpeningCollectionDTO collection = response
      .getBody()
      .as(SingleDayOpeningCollectionDTO.class);
    assertThat(collection.getTotalRecords(), is(equalTo(0)));
    assertThat(collection.getDates(), hasSize(0));
  }

  @Test
  void testEmptyGetWithClosed() {
    Response response = getAllOpenings(
      UUIDs.UUID_0,
      Dates.DATE_2021_01_01,
      Dates.DATE_2021_01_02,
      true,
      null,
      null
    );
    response.then().statusCode(is(HttpStatus.OK.value()));

    SingleDayOpeningCollectionDTO collection = response
      .getBody()
      .as(SingleDayOpeningCollectionDTO.class);
    assertThat(collection.getTotalRecords(), is(equalTo(2)));
    assertThat(
      collection.getDates(),
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
          .build()
      )
    );
  }

  @Test
  void testServicePointParameter() {
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_A);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));

    Response response = getAllOpenings(
      UUIDs.UUID_1,
      Dates.DATE_2021_01_04,
      Dates.DATE_2021_01_07,
      false,
      null,
      null
    );
    response.then().statusCode(is(HttpStatus.OK.value()));

    SingleDayOpeningCollectionDTO collection = response
      .getBody()
      .as(SingleDayOpeningCollectionDTO.class);
    assertThat(collection.getTotalRecords(), is(equalTo(2)));
    assertThat(
      collection.getDates(),
      contains(
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_04)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_04_00)
              .endTime(Times.TIME_14_59)
              .build()
          )
          .allDay(false)
          .exceptional(true)
          .exceptionName(ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4.getName())
          .build(),
        SingleDayOpeningDTO
          .builder()
          .date(Dates.DATE_2021_01_07)
          .open(true)
          .opening(
            SingleDayOpeningRangeDTO
              .builder()
              .startTime(Times.TIME_00_00)
              .endTime(Times.TIME_23_59)
              .build()
          )
          .allDay(true)
          .exceptional(false)
          .build()
      )
    );
  }
}
