package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import org.folio.calendar.domain.dto.PeriodCollection;
import org.folio.calendar.repository.PeriodQueryFilter;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.utils.PeriodCollectionUtils;
import org.junit.jupiter.api.Test;

class PeriodCollectionUtilsGetPeriodsFromCalendarListTest {

  @Test
  void testConversionWithNormalHoursAndOpeningDays() {
    PeriodCollection collection = PeriodCollectionUtils.getPeriodsFromCalendarList(
      Arrays.asList(
        Calendars.CALENDAR_FULL_EXAMPLE_A,
        Calendars.CALENDAR_FULL_EXAMPLE_B,
        Calendars.CALENDAR_FULL_EXAMPLE_C,
        Calendars.CALENDAR_FULL_EXCEPTIONAL_A,
        Calendars.CALENDAR_FULL_EXCEPTIONAL_B
      ),
      PeriodQueryFilter.NORMAL_HOURS,
      true
    );
    assertThat(
      "The created collection has three elements",
      collection.getOpeningPeriods(),
      hasSize(3)
    );
    assertThat(
      "The created collection has three records",
      collection.getTotalRecords(),
      is(equalTo(3))
    );
    assertThat(
      "The created collection contains equivalent periods with opening days",
      collection.getOpeningPeriods(),
      is(
        equalTo(
          Arrays.asList(
            Periods.PERIOD_FULL_EXAMPLE_A,
            Periods.PERIOD_FULL_EXAMPLE_B,
            Periods.PERIOD_FULL_EXAMPLE_C
          )
        )
      )
    );
  }

  @Test
  void testConversionWithNormalHoursWithoutOpeningDays() {
    PeriodCollection collection = PeriodCollectionUtils.getPeriodsFromCalendarList(
      Arrays.asList(
        Calendars.CALENDAR_FULL_EXAMPLE_A,
        Calendars.CALENDAR_FULL_EXAMPLE_B,
        Calendars.CALENDAR_FULL_EXAMPLE_C,
        Calendars.CALENDAR_FULL_EXCEPTIONAL_A,
        Calendars.CALENDAR_FULL_EXCEPTIONAL_B
      ),
      PeriodQueryFilter.NORMAL_HOURS,
      false
    );
    assertThat(
      "The created collection has three elements",
      collection.getOpeningPeriods(),
      hasSize(3)
    );
    assertThat(
      "The created collection has three records",
      collection.getTotalRecords(),
      is(equalTo(3))
    );
    assertThat(
      "The created collection contains equivalent periods without opening days",
      collection.getOpeningPeriods(),
      is(
        equalTo(
          Arrays.asList(
            Periods.PERIOD_FULL_EXAMPLE_A.withOpeningDays(new ArrayList<>()),
            Periods.PERIOD_FULL_EXAMPLE_B.withOpeningDays(new ArrayList<>()),
            Periods.PERIOD_FULL_EXAMPLE_C.withOpeningDays(new ArrayList<>())
          )
        )
      )
    );
  }

  @Test
  void testConversionWithExceptionsAndOpeningDays() {
    PeriodCollection collection = PeriodCollectionUtils.getPeriodsFromCalendarList(
      Arrays.asList(
        Calendars.CALENDAR_FULL_EXAMPLE_A,
        Calendars.CALENDAR_FULL_EXAMPLE_B,
        Calendars.CALENDAR_FULL_EXAMPLE_C,
        Calendars.CALENDAR_FULL_EXCEPTIONAL_A,
        Calendars.CALENDAR_FULL_EXCEPTIONAL_B
      ),
      PeriodQueryFilter.EXCEPTIONS,
      true
    );
    assertThat(
      "The created collection has two elements",
      collection.getOpeningPeriods(),
      hasSize(2)
    );
    assertThat(
      "The created collection has two records",
      collection.getTotalRecords(),
      is(equalTo(2))
    );
    assertThat(
      "The created collection contains equivalent periods with opening days",
      collection.getOpeningPeriods(),
      is(
        equalTo(Arrays.asList(Periods.PERIOD_FULL_EXCEPTIONAL_A, Periods.PERIOD_FULL_EXCEPTIONAL_B))
      )
    );
  }

  @Test
  void testConversionWithExceptionsWithoutOpeningDays() {
    PeriodCollection collection = PeriodCollectionUtils.getPeriodsFromCalendarList(
      Arrays.asList(
        Calendars.CALENDAR_FULL_EXAMPLE_A,
        Calendars.CALENDAR_FULL_EXAMPLE_B,
        Calendars.CALENDAR_FULL_EXAMPLE_C,
        Calendars.CALENDAR_FULL_EXCEPTIONAL_A,
        Calendars.CALENDAR_FULL_EXCEPTIONAL_B
      ),
      PeriodQueryFilter.EXCEPTIONS,
      false
    );
    assertThat(
      "The created collection has two elements",
      collection.getOpeningPeriods(),
      hasSize(2)
    );
    assertThat(
      "The created collection has two records",
      collection.getTotalRecords(),
      is(equalTo(2))
    );
    assertThat(
      "The created collection contains equivalent periods without opening days",
      collection.getOpeningPeriods(),
      is(
        equalTo(
          Arrays.asList(
            Periods.PERIOD_FULL_EXCEPTIONAL_A.withOpeningDays(new ArrayList<>()),
            Periods.PERIOD_FULL_EXCEPTIONAL_B.withOpeningDays(new ArrayList<>())
          )
        )
      )
    );
  }

  @Test
  void testConversionToEmptyPeriodCollection() {
    PeriodCollection collection = PeriodCollectionUtils.getPeriodsFromCalendarList(
      Arrays.asList(),
      PeriodQueryFilter.NORMAL_HOURS,
      true
    );
    assertThat(
      "The created collection has no elements",
      collection.getOpeningPeriods(),
      hasSize(0)
    );
    assertThat(
      "The created collection has no records",
      collection.getTotalRecords(),
      is(equalTo(0))
    );
    assertThat(
      "The created collection contains no periods",
      collection.getOpeningPeriods(),
      is(empty())
    );
  }

  @Test
  void testNonMatchingConversionToEmptyPeriodCollection() {
    PeriodCollection collection = PeriodCollectionUtils.getPeriodsFromCalendarList(
      Arrays.asList(Calendars.CALENDAR_FULL_EXCEPTIONAL_A, Calendars.CALENDAR_FULL_EXCEPTIONAL_B),
      PeriodQueryFilter.NORMAL_HOURS,
      true
    );
    assertThat(
      "The created collection has no elements",
      collection.getOpeningPeriods(),
      hasSize(0)
    );
    assertThat(
      "The created collection has no records",
      collection.getTotalRecords(),
      is(equalTo(0))
    );
    assertThat(
      "The created collection contains no periods",
      collection.getOpeningPeriods(),
      is(empty())
    );
  }

  @Test
  void testInvalidConversionToEmptyPeriodCollection() {
    PeriodCollection collection = PeriodCollectionUtils.getPeriodsFromCalendarList(
      Arrays.asList(
        Calendars.CALENDAR_WITH_MULTIPLE_EXCEPTIONS,
        Calendars.CALENDAR_WITH_NORMAL_HOURS_AND_EXCEPTIONS,
        Calendars.CALENDAR_WITH_NO_SERVICE_POINTS,
        Calendars.CALENDAR_WITH_TWO_SERVICE_POINTS
      ),
      PeriodQueryFilter.EXCEPTIONS,
      true
    );
    assertThat(
      "The created collection has no elements",
      collection.getOpeningPeriods(),
      hasSize(0)
    );
    assertThat(
      "The created collection has no records",
      collection.getTotalRecords(),
      is(equalTo(0))
    );
    assertThat(
      "The created collection contains no periods",
      collection.getOpeningPeriods(),
      is(empty())
    );
  }
}
