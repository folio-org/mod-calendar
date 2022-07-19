package org.folio.calendar.integration.api.calendar.calendars.get;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.integration.api.calendar.BaseCalendarApiTest;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class SearchCalendarTest extends BaseCalendarApiTest {

  @Test
  void testEmptyGet() {
    Response response = sendCalendarSearchRequest(SearchRequestParameters.builder().build());
    response.then().statusCode(is(HttpStatus.OK.value()));

    CalendarCollectionDTO collection = response.getBody().as(CalendarCollectionDTO.class);
    assertThat(collection.getTotalRecords(), is(equalTo(0)));
    assertThat(collection.getCalendars(), hasSize(0));
  }

  @Test
  void testServicePointParameter() {
    // assigned to SPs 1 and 2
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_A);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));

    Response searchResponse1 = sendCalendarSearchRequest(
      SearchRequestParameters.builder().servicePointId(UUIDs.UUID_1).build()
    );
    searchResponse1.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO result1 = searchResponse1.getBody().as(CalendarCollectionDTO.class);

    assertThat(result1.getTotalRecords(), is(equalTo(1)));
    assertThat(result1.getCalendars(), hasSize(1));

    Response searchResponse2 = sendCalendarSearchRequest(
      SearchRequestParameters.builder().servicePointId(UUIDs.UUID_2).build()
    );
    searchResponse2.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO result2 = searchResponse2.getBody().as(CalendarCollectionDTO.class);

    assertThat(result2.getTotalRecords(), is(equalTo(1)));
    assertThat(result2.getCalendars(), hasSize(1));

    Response searchResponse1and2 = sendCalendarSearchRequest(
      SearchRequestParameters
        .builder()
        .servicePointId(UUIDs.UUID_1)
        .servicePointId(UUIDs.UUID_2)
        .build()
    );
    searchResponse1and2.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO result1and2 = searchResponse1and2
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(result1and2.getTotalRecords(), is(equalTo(1)));
    assertThat(result1and2.getCalendars(), hasSize(1));

    Response searchResponse2and3 = sendCalendarSearchRequest(
      SearchRequestParameters
        .builder()
        .servicePointId(UUIDs.UUID_2)
        .servicePointId(UUIDs.UUID_3)
        .build()
    );
    searchResponse2and3.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO result2and3 = searchResponse2and3
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(result2and3.getTotalRecords(), is(equalTo(1)));
    assertThat(result2and3.getCalendars(), hasSize(1));

    Response searchResponse3 = sendCalendarSearchRequest(
      SearchRequestParameters.builder().servicePointId(UUIDs.UUID_3).build()
    );
    searchResponse3.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO result3 = searchResponse3.getBody().as(CalendarCollectionDTO.class);

    assertThat(result3.getTotalRecords(), is(equalTo(0)));
    assertThat(result3.getCalendars(), hasSize(0));
  }

  @Test
  void testDateParameters() {
    // 2021_03_16 to 2021_05_01
    Response postResponse = sendCalendarCreationRequest(Calendars.CALENDAR_COMBINED_EXAMPLE_B);
    postResponse.then().statusCode(is(HttpStatus.CREATED.value()));

    Response searchResponseBefore = sendCalendarSearchRequest(
      SearchRequestParameters.builder().endDate(Dates.DATE_2021_01_01).build()
    );
    searchResponseBefore.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultBefore = searchResponseBefore
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultBefore.getTotalRecords(), is(equalTo(0)));
    assertThat(resultBefore.getCalendars(), hasSize(0));

    Response searchResponseBeforeWithStart = sendCalendarSearchRequest(
      SearchRequestParameters
        .builder()
        .startDate(Dates.DATE_2021_01_01)
        .endDate(Dates.DATE_2021_01_01)
        .build()
    );
    searchResponseBeforeWithStart.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultBeforeWithStart = searchResponseBeforeWithStart
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultBeforeWithStart.getTotalRecords(), is(equalTo(0)));
    assertThat(resultBeforeWithStart.getCalendars(), hasSize(0));

    Response searchResponseAtBeginning = sendCalendarSearchRequest(
      SearchRequestParameters
        .builder()
        .startDate(Dates.DATE_2021_03_16)
        .endDate(Dates.DATE_2021_03_16)
        .build()
    );
    searchResponseAtBeginning.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultAtBeginning = searchResponseAtBeginning
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultAtBeginning.getTotalRecords(), is(equalTo(1)));
    assertThat(resultAtBeginning.getCalendars(), hasSize(1));

    Response searchResponseAtBeginningEndOnly = sendCalendarSearchRequest(
      SearchRequestParameters.builder().endDate(Dates.DATE_2021_03_16).build()
    );
    searchResponseAtBeginningEndOnly.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultAtBeginningEndOnly = searchResponseAtBeginningEndOnly
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultAtBeginningEndOnly.getTotalRecords(), is(equalTo(1)));
    assertThat(resultAtBeginningEndOnly.getCalendars(), hasSize(1));

    Response searchResponseMiddle = sendCalendarSearchRequest(
      SearchRequestParameters
        .builder()
        .startDate(Dates.DATE_2021_04_30)
        .endDate(Dates.DATE_2021_04_30)
        .build()
    );
    searchResponseMiddle.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultMiddle = searchResponseMiddle
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultMiddle.getTotalRecords(), is(equalTo(1)));
    assertThat(resultMiddle.getCalendars(), hasSize(1));

    Response searchResponseAfter = sendCalendarSearchRequest(
      SearchRequestParameters
        .builder()
        .startDate(Dates.DATE_2021_08_12)
        .endDate(Dates.DATE_2021_09_22)
        .build()
    );
    searchResponseAfter.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultAfter = searchResponseAfter
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultAfter.getTotalRecords(), is(equalTo(0)));
    assertThat(resultAfter.getCalendars(), hasSize(0));
  }

  @Test
  void testPagination() {
    // left as DTO for easier comparison
    List<CalendarDTO> calendars = Arrays
      .asList(
        Calendars.CALENDAR_2021_01_01_TO_2021_01_01,
        Calendars.CALENDAR_2021_01_02_TO_2021_01_02,
        Calendars.CALENDAR_2021_03_16_TO_2021_03_16,
        Calendars.CALENDAR_2021_04_30_TO_2021_04_30,
        Calendars.CALENDAR_2021_05_01_TO_2021_09_22
      )
      .stream()
      .map((Calendar cal) -> {
        Response response = sendCalendarCreationRequest(cal.withName("foo"));
        response.then().statusCode(is(HttpStatus.CREATED.value()));
        return response.getBody().as(CalendarDTO.class);
      })
      .collect(Collectors.toList());

    Response searchResponseUnlimited = sendCalendarSearchRequest(
      SearchRequestParameters.builder().limit(Integer.MAX_VALUE).build()
    );
    searchResponseUnlimited.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultAfter = searchResponseUnlimited
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultAfter.getTotalRecords(), is(equalTo(5)));
    assertThat(resultAfter.getCalendars(), is(equalTo(calendars)));

    Response searchResponseZeroLimit = sendCalendarSearchRequest(
      SearchRequestParameters.builder().limit(0).build()
    );
    searchResponseZeroLimit.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultZeroLimit = searchResponseZeroLimit
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultZeroLimit.getTotalRecords(), is(equalTo(5)));
    assertThat(resultZeroLimit.getCalendars(), hasSize(0));

    Response searchResponseFirstPage = sendCalendarSearchRequest(
      SearchRequestParameters.builder().offset(0).limit(2).build()
    );
    searchResponseFirstPage.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultFirstPage = searchResponseFirstPage
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultFirstPage.getTotalRecords(), is(equalTo(5)));
    assertThat(resultFirstPage.getCalendars(), contains(calendars.get(0), calendars.get(1)));

    Response searchResponseSecondPage = sendCalendarSearchRequest(
      SearchRequestParameters.builder().offset(2).limit(2).build()
    );
    searchResponseSecondPage.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultSecondPage = searchResponseSecondPage
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultSecondPage.getTotalRecords(), is(equalTo(5)));
    assertThat(resultSecondPage.getCalendars(), contains(calendars.get(2), calendars.get(3)));

    Response searchResponseLastPage = sendCalendarSearchRequest(
      SearchRequestParameters.builder().offset(4).limit(2).build()
    );
    searchResponseLastPage.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultLastPage = searchResponseLastPage
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultLastPage.getTotalRecords(), is(equalTo(5)));
    assertThat(resultLastPage.getCalendars(), contains(calendars.get(4)));

    Response searchResponseMiddleOfPage = sendCalendarSearchRequest(
      SearchRequestParameters.builder().offset(3).limit(2).build()
    );
    searchResponseMiddleOfPage.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultMiddleOfPage = searchResponseMiddleOfPage
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultMiddleOfPage.getTotalRecords(), is(equalTo(5)));
    assertThat(resultMiddleOfPage.getCalendars(), contains(calendars.get(3), calendars.get(4)));

    Response searchResponseOutOfBounds = sendCalendarSearchRequest(
      SearchRequestParameters.builder().offset(5).limit(2).build()
    );
    searchResponseOutOfBounds.then().statusCode(is(HttpStatus.OK.value()));
    CalendarCollectionDTO resultOutOfBounds = searchResponseOutOfBounds
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(resultOutOfBounds.getTotalRecords(), is(equalTo(5)));
    assertThat(resultOutOfBounds.getCalendars(), hasSize(0));
  }
}
