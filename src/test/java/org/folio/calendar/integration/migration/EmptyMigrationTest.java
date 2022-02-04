package org.folio.calendar.integration.migration;

import static org.folio.calendar.integration.calendar.periods.get.GetPeriodAbstractTest.GET_PERIOD_API_ROUTE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import org.folio.calendar.domain.dto.OpeningDayConcreteCollection;
import org.junit.jupiter.api.Test;

public class EmptyMigrationTest extends AbstractMigrationTest {

  @Test
  void testEmptyMigration() {
    loadMigrationSql();
    runMigration();

    OpeningDayConcreteCollection collection = ra()
      .get(getRequestUrl(GET_PERIOD_API_ROUTE))
      .getBody()
      .as(OpeningDayConcreteCollection.class);

    assertThat(
      "The returned collection has no opening information indicating no migrated calendars",
      collection.getOpeningPeriods(),
      hasSize(0)
    );
  }
}
