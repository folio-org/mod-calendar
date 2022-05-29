package org.folio.calendar.unit.utils;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.utils.TemporalRange;
import org.folio.calendar.utils.TemporalUtils;
import org.junit.jupiter.api.Test;

class TemporalUtilsDateOverlapTest {

  TemporalRange<LocalDate, String> RANGE_2021_01_01_TO_2021_01_01_A = new TemporalRange<>(
    "A",
    Dates.DATE_2021_01_01,
    Dates.DATE_2021_01_01
  );
  TemporalRange<LocalDate, String> RANGE_2021_01_01_TO_2021_01_02_B = new TemporalRange<>(
    "B",
    Dates.DATE_2021_01_01,
    Dates.DATE_2021_01_02
  );
  TemporalRange<LocalDate, String> RANGE_2021_01_01_TO_2021_01_04_C = new TemporalRange<>(
    "C",
    Dates.DATE_2021_01_01,
    Dates.DATE_2021_01_04
  );
  TemporalRange<LocalDate, String> RANGE_2021_01_02_TO_2021_01_03_D = new TemporalRange<>(
    "D",
    Dates.DATE_2021_01_02,
    Dates.DATE_2021_01_03
  );
  TemporalRange<LocalDate, String> RANGE_2021_01_03_TO_2021_03_16_E = new TemporalRange<>(
    "E",
    Dates.DATE_2021_01_03,
    Dates.DATE_2021_03_16
  );
  TemporalRange<LocalDate, String> RANGE_2021_01_04_TO_2021_03_16_F = new TemporalRange<>(
    "F",
    Dates.DATE_2021_01_04,
    Dates.DATE_2021_03_16
  );

  @Test
  void testNoOverlaps() {
    assertThat(TemporalUtils.getOverlaps(new ArrayList<>()), isEmpty());
    assertThat(
      TemporalUtils.getOverlaps(Arrays.asList(RANGE_2021_01_01_TO_2021_01_01_A)),
      isEmpty()
    );
    assertThat(
      TemporalUtils.getOverlaps(
        Arrays.asList(
          RANGE_2021_01_01_TO_2021_01_01_A,
          RANGE_2021_01_02_TO_2021_01_03_D,
          RANGE_2021_01_04_TO_2021_03_16_F
        )
      ),
      isEmpty()
    );
  }

  @Test
  void testSingleOverlaps() {
    assertThat(
      TemporalUtils.getOverlaps(
        Arrays.asList(RANGE_2021_01_01_TO_2021_01_02_B, RANGE_2021_01_01_TO_2021_01_04_C)
      ),
      isPresentAnd(containsInAnyOrder("B", "C"))
    );
    assertThat(
      TemporalUtils.getOverlaps(
        Arrays.asList(RANGE_2021_01_01_TO_2021_01_02_B, RANGE_2021_01_01_TO_2021_01_02_B)
      ),
      isPresentAnd(hasItem("B"))
    );
    assertThat(
      TemporalUtils.getOverlaps(
        Arrays.asList(
          RANGE_2021_01_01_TO_2021_01_02_B,
          RANGE_2021_01_03_TO_2021_03_16_E,
          RANGE_2021_01_04_TO_2021_03_16_F
        )
      ),
      isPresentAnd(allOf(hasItems("E", "F"), not(hasItem("B"))))
    );
  }

  @Test
  void testMultipleOverlaps() {
    assertThat(
      TemporalUtils.getOverlaps(
        Arrays.asList(
          RANGE_2021_01_01_TO_2021_01_02_B,
          RANGE_2021_01_01_TO_2021_01_04_C,
          RANGE_2021_01_04_TO_2021_03_16_F
        )
      ),
      isPresentAnd(containsInAnyOrder("B", "C", "F"))
    );
  }
}
