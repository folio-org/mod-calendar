package org.folio.calendar.integration.calendar.periods.servicepointid.calculateopening.get;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import java.util.Arrays;
import org.folio.calendar.domain.dto.CalculatedOpenings;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.OpeningDayInfoConcreteConstants;
import org.folio.calendar.testconstants.OpeningDayInfoRelativeConstants;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CalculateOpeningTest extends CalculateOpeningAbstractTest {

  @Test
  void testNoCalendars() {
    Response response = sendRequest(UUIDs.UUID_F, Dates.DATE_2021_01_01);
    response.then().statusCode(is(HttpStatus.OK.value()));
    CalculatedOpenings result = response.getBody().as(CalculatedOpenings.class);
    assertThat(
      "No openings were found",
      result.getOpeningDays(),
      is(
        equalTo(
          Arrays.asList(
            OpeningDayRelative
              .builder()
              .openingDay(OpeningDayInfoConcreteConstants.NO_OPENING_ADJACENT)
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoConcreteConstants.NO_OPENING_ON_REQUESTED_DAY.withDate(
                  Dates.LDATE_2021_01_01
                )
              )
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(OpeningDayInfoConcreteConstants.NO_OPENING_ADJACENT)
              .build()
          )
        )
      )
    );
  }

  @Test
  void testBeforeAll() {
    Response response = sendRequest(UUIDs.UUID_1, Dates.DATE_2021_01_01);
    response.then().statusCode(is(HttpStatus.OK.value()));
    CalculatedOpenings result = response.getBody().as(CalculatedOpenings.class);
    assertThat(
      "Only the next opening is found",
      result.getOpeningDays(),
      is(
        equalTo(
          Arrays.asList(
            OpeningDayRelative
              .builder()
              .openingDay(OpeningDayInfoConcreteConstants.NO_OPENING_ADJACENT)
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoConcreteConstants.NO_OPENING_ON_REQUESTED_DAY.withDate(
                  Dates.LDATE_2021_01_01
                )
              )
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59.withDate(
                  Dates.LDATE_2021_05_03
                )
              )
              .build()
          )
        )
      )
    );
  }

  @Test
  void testAfterAll() {
    Response response = sendRequest(UUIDs.UUID_1, Dates.DATE_2021_12_31);
    response.then().statusCode(is(HttpStatus.OK.value()));
    CalculatedOpenings result = response.getBody().as(CalculatedOpenings.class);
    assertThat(
      "Only the next opening is found",
      result.getOpeningDays(),
      is(
        equalTo(
          Arrays.asList(
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59.withDate(
                  Dates.LDATE_2021_09_20
                )
              )
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoConcreteConstants.NO_OPENING_ON_REQUESTED_DAY.withDate(
                  Dates.LDATE_2021_12_31
                )
              )
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(OpeningDayInfoConcreteConstants.NO_OPENING_ADJACENT)
              .build()
          )
        )
      )
    );
  }

  @Test
  void testSurroundedAndOpen() {
    Response response = sendRequest(UUIDs.UUID_1, Dates.DATE_2021_08_16);
    response.then().statusCode(is(HttpStatus.OK.value()));
    CalculatedOpenings result = response.getBody().as(CalculatedOpenings.class);
    assertThat(
      "All adjacent openings are found with closed days being skipped",
      result.getOpeningDays(),
      is(
        equalTo(
          Arrays.asList(
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoRelativeConstants.OPEN_ALL_DAY.withDate(Dates.LDATE_2021_08_12)
              )
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59.withDate(
                  Dates.LDATE_2021_08_16
                )
              )
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoRelativeConstants.OPEN_ALL_DAY.withDate(Dates.LDATE_2021_08_19)
              )
              .build()
          )
        )
      )
    );
  }

  @Test
  void testSurroundedAndClosed() {
    Response response = sendRequest(UUIDs.UUID_1, Dates.DATE_2021_08_15);
    response.then().statusCode(is(HttpStatus.OK.value()));
    CalculatedOpenings result = response.getBody().as(CalculatedOpenings.class);
    assertThat(
      "All adjacent openings are found with closed days being skipped",
      result.getOpeningDays(),
      is(
        equalTo(
          Arrays.asList(
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoRelativeConstants.OPEN_ALL_DAY.withDate(Dates.LDATE_2021_08_12)
              )
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoConcreteConstants.NO_OPENING_ON_REQUESTED_DAY.withDate(
                  Dates.LDATE_2021_08_15
                )
              )
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoRelativeConstants.OPEN_00_00_TO_12_30_AND_23_00_TO_23_59.withDate(
                  Dates.LDATE_2021_08_16
                )
              )
              .build()
          )
        )
      )
    );
  }

  @Test
  void testClosedException() {
    Response response = sendRequest(UUIDs.UUID_0, Dates.DATE_2021_08_15);
    response.then().statusCode(is(HttpStatus.OK.value()));
    CalculatedOpenings result = response.getBody().as(CalculatedOpenings.class);
    assertThat(
      "All adjacent openings are found with closed days being skipped",
      result.getOpeningDays(),
      is(
        equalTo(
          Arrays.asList(
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoRelativeConstants.OPEN_ALL_DAY.withDate(Dates.LDATE_2021_07_01)
              )
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(
                OpeningDayInfoConcreteConstants.EXCEPTIONALLY_CLOSED_ON_REQUESTED_DAY.withDate(
                  Dates.LDATE_2021_08_15
                )
              )
              .build(),
            OpeningDayRelative
              .builder()
              .openingDay(OpeningDayInfoConcreteConstants.NO_OPENING_ADJACENT)
              .build()
          )
        )
      )
    );
  }
}
