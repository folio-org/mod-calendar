package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.folio.calendar.domain.dto.OpeningDayConcrete;
import org.folio.calendar.domain.dto.OpeningDayInfo;
import org.folio.calendar.domain.types.LegacyPeriodDate;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.OpeningDayInfoRelativeConstants;
import org.folio.calendar.utils.DateUtils;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

class PeriodUtilsCollectionTest {

  static final Map<LocalDate, OpeningDayInfo> TEST_NORMAL_OPENINGS = new HashMap<>();
  static final Map<LocalDate, OpeningDayInfo> TEST_EXCEPTIONS = new HashMap<>();

  static {
    TEST_NORMAL_OPENINGS.put(
      Dates.DATE_2021_01_02,
      OpeningDayInfoRelativeConstants.OPEN_04_00_TO_14_59
    );
    TEST_NORMAL_OPENINGS.put(
      Dates.DATE_2021_01_04,
      OpeningDayInfoRelativeConstants.OPEN_00_00_TO_14_59
    );

    TEST_EXCEPTIONS.put(
      Dates.DATE_2021_01_01,
      OpeningDayInfoRelativeConstants.EXCEPTIONAL_OPEN_00_00_TO_12_30_AND_23_00_TO_23_59
    );
    TEST_EXCEPTIONS.put(
      Dates.DATE_2021_01_02,
      OpeningDayInfoRelativeConstants.EXCEPTIONAL_CLOSED_ALL_DAY
    );
  }

  @Test
  void testActualOpenDays() {
    List<OpeningDayConcrete> expected = new ArrayList<>();
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_01)
        .openingDay(TEST_EXCEPTIONS.get(Dates.DATE_2021_01_01))
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_02)
        .openingDay(TEST_EXCEPTIONS.get(Dates.DATE_2021_01_02))
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_04)
        .openingDay(TEST_NORMAL_OPENINGS.get(Dates.DATE_2021_01_04))
        .build()
    );

    assertThat(
      "actualOpening=true and includeClosedDays=false should produce three days of information",
      PeriodUtils.buildOpeningDayConcreteCollection(
        TEST_NORMAL_OPENINGS,
        TEST_EXCEPTIONS,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_12_31,
        false,
        true
      ),
      is(equalTo(expected))
    );
  }

  @Test
  void testNonActualOpenDays() {
    List<OpeningDayConcrete> expected = new ArrayList<>();
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_01)
        .openingDay(TEST_EXCEPTIONS.get(Dates.DATE_2021_01_01))
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_02)
        .openingDay(TEST_EXCEPTIONS.get(Dates.DATE_2021_01_02))
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_02)
        .openingDay(TEST_NORMAL_OPENINGS.get(Dates.DATE_2021_01_02))
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_04)
        .openingDay(TEST_NORMAL_OPENINGS.get(Dates.DATE_2021_01_04))
        .build()
    );

    assertThat(
      "actualOpening=false and includeClosedDays=false should produce three days of information over four OpeningDayConcrete",
      PeriodUtils.buildOpeningDayConcreteCollection(
        TEST_NORMAL_OPENINGS,
        TEST_EXCEPTIONS,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_12_31,
        false,
        false
      ),
      is(equalTo(expected))
    );
  }

  @Test
  void testActualWithClosedDays() {
    List<OpeningDayConcrete> expected = new ArrayList<>();
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_01)
        .openingDay(TEST_EXCEPTIONS.get(Dates.DATE_2021_01_01))
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_02)
        .openingDay(TEST_EXCEPTIONS.get(Dates.DATE_2021_01_02))
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_03)
        .openingDay(OpeningDayInfoRelativeConstants.CLOSED_ALL_DAY)
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_04)
        .openingDay(TEST_NORMAL_OPENINGS.get(Dates.DATE_2021_01_04))
        .build()
    );

    assertThat(
      "actualOpening=true and includeClosedDays=true from Jan 1 to Jan 4 should produce four days of information",
      PeriodUtils.buildOpeningDayConcreteCollection(
        TEST_NORMAL_OPENINGS,
        TEST_EXCEPTIONS,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_04,
        true,
        true
      ),
      is(equalTo(expected))
    );
  }

  @Test
  void testNonActualWithClosedDays() {
    List<OpeningDayConcrete> expected = new ArrayList<>();
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_01)
        .openingDay(TEST_EXCEPTIONS.get(Dates.DATE_2021_01_01))
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_02)
        .openingDay(TEST_EXCEPTIONS.get(Dates.DATE_2021_01_02))
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_02)
        .openingDay(TEST_NORMAL_OPENINGS.get(Dates.DATE_2021_01_02))
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_03)
        .openingDay(OpeningDayInfoRelativeConstants.CLOSED_ALL_DAY)
        .build()
    );
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_04)
        .openingDay(TEST_NORMAL_OPENINGS.get(Dates.DATE_2021_01_04))
        .build()
    );

    assertThat(
      "actualOpening=false and includeClosedDays=true from Jan 1 to Jan 4 should produce four days of information with five OpeningDayConcrete",
      PeriodUtils.buildOpeningDayConcreteCollection(
        TEST_NORMAL_OPENINGS,
        TEST_EXCEPTIONS,
        Dates.DATE_2021_01_01,
        Dates.DATE_2021_01_04,
        true,
        false
      ),
      is(equalTo(expected))
    );
  }

  @Test
  void testLargeSetOfClosedDays() {
    List<OpeningDayConcrete> expected = new ArrayList<>();
    for (LocalDate date : DateUtils.getDateRange(Dates.DATE_2021_03_16, Dates.DATE_2021_12_30)) {
      expected.add(
        OpeningDayConcrete
          .builder()
          .date(LegacyPeriodDate.from(date))
          .openingDay(OpeningDayInfoRelativeConstants.CLOSED_ALL_DAY)
          .build()
      );
    }

    assertThat(
      "actualOpening=true and includeClosedDays=true with no normal hours nor exceptions should produce a slew of closed results",
      PeriodUtils.buildOpeningDayConcreteCollection(
        TEST_NORMAL_OPENINGS,
        TEST_EXCEPTIONS,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_12_30,
        true,
        true
      ),
      is(equalTo(expected))
    );

    assertThat(
      "actualOpening=false and includeClosedDays=true with no normal hours nor exceptions should produce a slew of closed results",
      PeriodUtils.buildOpeningDayConcreteCollection(
        TEST_NORMAL_OPENINGS,
        TEST_EXCEPTIONS,
        Dates.DATE_2021_03_16,
        Dates.DATE_2021_12_30,
        true,
        false
      ),
      is(equalTo(expected))
    );
  }

  @Test
  void testDateSubset() {
    List<OpeningDayConcrete> expected = new ArrayList<>();
    expected.add(
      OpeningDayConcrete
        .builder()
        .date(Dates.LDATE_2021_01_02)
        .openingDay(TEST_EXCEPTIONS.get(Dates.DATE_2021_01_02))
        .build()
    );

    assertThat(
      "actualOpening=true and includeClosedDays=false should produce three days of information",
      PeriodUtils.buildOpeningDayConcreteCollection(
        TEST_NORMAL_OPENINGS,
        TEST_EXCEPTIONS,
        Dates.DATE_2021_01_02,
        Dates.DATE_2021_01_02,
        false,
        true
      ),
      is(equalTo(expected))
    );
  }

  @Test
  void testEmptyDateSubset() {
    List<OpeningDayConcrete> expected = new ArrayList<>();

    assertThat(
      "includeClosedDays=false should, with a date range with no openings, produce an empty list",
      PeriodUtils.buildOpeningDayConcreteCollection(
        TEST_NORMAL_OPENINGS,
        TEST_EXCEPTIONS,
        Dates.DATE_2021_01_03,
        Dates.DATE_2021_01_03,
        false,
        true
      ),
      is(equalTo(expected))
    );
  }
}
