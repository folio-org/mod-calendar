package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import org.folio.calendar.domain.dto.OpeningDayRelative;
import org.folio.calendar.testconstants.NormalOpenings;
import org.folio.calendar.testconstants.OpeningDayRelativeConstants;
import org.folio.calendar.testconstants.UUIDs;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class PeriodUtilsConsolidationTest {

  @Test
  void testNoOpeningDaysToNormalOpenings() {
    assertThat(
      "No openings should be consolidated to nothing",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        new ArrayList<OpeningDayRelative>(),
        UUIDs.UUID_0
      ),
      is(empty())
    );
  }

  @Test
  void testClosedOpeningToNormalOpenings() {
    assertThat(
      "A closed \"Opening\" (OpeningDayRelative) should be consolidated to nothing",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(OpeningDayRelativeConstants.MONDAY_CLOSED),
        UUIDs.UUID_0
      ),
      is(empty())
    );
  }

  @Test
  void testInvalidOpeningToNormalOpenings() {
    assertThat(
      "An invalid (end time before start time) \"Opening\" (OpeningDayRelative) should be consolidated to nothing",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(OpeningDayRelativeConstants.MONDAY_INVALID),
        UUIDs.UUID_0
      ),
      is(empty())
    );
  }

  @Test
  void testSingleAllDayOpeningToNormalOpenings() {
    assertThat(
      "A single all day legacy opening is equivalent to a 00:00 to 23:59 NormalOpening",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(OpeningDayRelativeConstants.MONDAY_OPEN_ALL_DAY),
        UUIDs.UUID_0
      ),
      is(Arrays.asList(NormalOpenings.MONDAY_ALL_DAY.withCalendarId(UUIDs.UUID_0)))
    );
  }

  @Test
  void testSinglePartialDayOpeningToNormalOpenings() {
    assertThat(
      "A single partial (04:00 to 14:59) day legacy opening is equivalent to a 04:00 to 14:59 NormalOpening",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(OpeningDayRelativeConstants.MONDAY_OPEN_04_00_TO_14_59),
        UUIDs.UUID_0
      ),
      is(Arrays.asList(NormalOpenings.MONDAY_04_00_TO_14_59.withCalendarId(UUIDs.UUID_0)))
    );
  }

  @Test
  void testAdjacentOverDayBoundaryToNormalOpenings() {
    assertThat(
      "Adjacent openings across day boundaries (one ends at 23:59 and next starts at 00:00) should be consolidated",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(
          OpeningDayRelativeConstants.SUNDAY_OPEN_ALL_DAY,
          OpeningDayRelativeConstants.MONDAY_OPEN_ALL_DAY
        ),
        UUIDs.UUID_0
      ),
      is(Arrays.asList(NormalOpenings.SUNDAY_MONDAY_ALL_DAY.withCalendarId(UUIDs.UUID_0)))
    );
  }

  @Test
  void testAdjacentOnSameDayToNormalOpenings() {
    assertThat(
      "Two adjacent openings on the same day should be consolidated to a single normal opening",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(
          OpeningDayRelativeConstants.MONDAY_OPEN_04_00_TO_14_59,
          OpeningDayRelativeConstants.MONDAY_OPEN_15_00_TO_23_59
        ),
        UUIDs.UUID_0
      ),
      is(Arrays.asList(NormalOpenings.MONDAY_04_00_TO_23_59.withCalendarId(UUIDs.UUID_0)))
    );
  }

  @Test
  void testMultipleAdjacentToNormalOpenings() {
    assertThat(
      "Multiple, consecutively adjacent openings should be consolidated into one",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(
          OpeningDayRelativeConstants.MONDAY_OPEN_04_00_TO_14_59,
          OpeningDayRelativeConstants.MONDAY_OPEN_15_00_TO_23_59,
          OpeningDayRelativeConstants.TUESDAY_OPEN_00_00_TO_12_30
        ),
        UUIDs.UUID_0
      ),
      is(Arrays.asList(NormalOpenings.MONDAY_04_00_TO_TUESDAY_12_30.withCalendarId(UUIDs.UUID_0)))
    );
  }

  @Test
  void testAdjacentWraparoundToNormalOpenings() {
    assertThat(
      "Two adjacent openings that are adjacent from [size - 1] to [0] should be consolidated",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(
          OpeningDayRelativeConstants.MONDAY_OPEN_ALL_DAY,
          OpeningDayRelativeConstants.SUNDAY_OPEN_ALL_DAY
        ),
        UUIDs.UUID_0
      ),
      is(Arrays.asList(NormalOpenings.SUNDAY_MONDAY_ALL_DAY.withCalendarId(UUIDs.UUID_0)))
    );
  }

  @Test
  void testMultipleNonAdjacentToNormalOpenings() {
    assertThat(
      "Multiple non-adjacent openings should not be consolidated",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(
          OpeningDayRelativeConstants.MONDAY_OPEN_04_00_TO_14_59,
          OpeningDayRelativeConstants.TUESDAY_OPEN_00_00_TO_12_30
        ),
        UUIDs.UUID_0
      ),
      is(
        Arrays.asList(
          NormalOpenings.MONDAY_04_00_TO_14_59.withCalendarId(UUIDs.UUID_0),
          NormalOpenings.TUESDAY_00_00_TO_12_30.withCalendarId(UUIDs.UUID_0)
        )
      )
    );
  }

  @Test
  void testManyConsolidations() {
    assertThat(
      "Multiple adjacent and non-adjacent openings should only be consolidated when adjacent",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(
          OpeningDayRelativeConstants.MONDAY_OPEN_04_00_TO_14_59,
          OpeningDayRelativeConstants.MONDAY_OPEN_15_00_TO_23_59,
          OpeningDayRelativeConstants.TUESDAY_OPEN_00_00_TO_12_30,
          OpeningDayRelativeConstants.WEDNESDAY_OPEN_23_00_TO_23_59,
          OpeningDayRelativeConstants.THURSDAY_OPEN_ALL_DAY,
          OpeningDayRelativeConstants.FRIDAY_OPEN_ALL_DAY,
          OpeningDayRelativeConstants.SATURDAY_OPEN_ALL_DAY,
          OpeningDayRelativeConstants.SUNDAY_OPEN_ALL_DAY
        ),
        UUIDs.UUID_0
      ),
      is(
        Arrays.asList(
          NormalOpenings.MONDAY_04_00_TO_TUESDAY_12_30.withCalendarId(UUIDs.UUID_0),
          NormalOpenings.WEDNESDAY_23_00_TO_SUNDAY_23_59.withCalendarId(UUIDs.UUID_0)
        )
      )
    );
  }

  @Test
  void testManyConsolidationsWrapped() {
    assertThat(
      "Multiple adjacent and non-adjacent openings should only be consolidated when adjacent",
      PeriodUtils.convertOpeningDayRelativeToNormalOpening(
        Arrays.asList(
          OpeningDayRelativeConstants.SATURDAY_OPEN_ALL_DAY,
          OpeningDayRelativeConstants.SUNDAY_OPEN_ALL_DAY,
          OpeningDayRelativeConstants.MONDAY_OPEN_04_00_TO_14_59,
          OpeningDayRelativeConstants.MONDAY_OPEN_15_00_TO_23_59,
          OpeningDayRelativeConstants.TUESDAY_OPEN_00_00_TO_12_30,
          OpeningDayRelativeConstants.WEDNESDAY_OPEN_23_00_TO_23_59,
          OpeningDayRelativeConstants.THURSDAY_OPEN_ALL_DAY,
          OpeningDayRelativeConstants.FRIDAY_OPEN_ALL_DAY
        ),
        UUIDs.UUID_0
      ),
      is(
        Arrays.asList(
          NormalOpenings.MONDAY_04_00_TO_TUESDAY_12_30.withCalendarId(UUIDs.UUID_0),
          NormalOpenings.WEDNESDAY_23_00_TO_SUNDAY_23_59.withCalendarId(UUIDs.UUID_0)
        )
      )
    );
  }
}
