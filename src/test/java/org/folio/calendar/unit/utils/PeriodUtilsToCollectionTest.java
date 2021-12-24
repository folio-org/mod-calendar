package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import org.folio.calendar.domain.dto.PeriodCollection;
import org.folio.calendar.testconstants.Periods;
import org.folio.calendar.utils.PeriodUtils;
import org.junit.jupiter.api.Test;

public class PeriodUtilsToCollectionTest {

  @Test
  void testConversionToPeriodCollection() {
    PeriodCollection collection = PeriodUtils.toCollection(
      Arrays.asList(
        Periods.PERIOD_FULL_EXAMPLE_A,
        Periods.PERIOD_FULL_EXAMPLE_B,
        Periods.PERIOD_FULL_EXAMPLE_C
      )
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
      "The created collection contains the original periods",
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
  void testConversionToEmptyPeriodCollection() {
    PeriodCollection collection = PeriodUtils.toCollection(Arrays.asList());
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
