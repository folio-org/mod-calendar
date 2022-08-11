package org.folio.calendar.integration.migration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.integration.api.calendar.BaseCalendarApiTest;
import org.junit.jupiter.api.Test;

class EmptyMigrationTest extends AbstractMigrationTest {

  @Test
  void testEmptyMigration() {
    loadMigrationSql();
    runMigration();

    CalendarCollectionDTO collection = ra()
      .get(getRequestUrl(BaseCalendarApiTest.COLLECTION_CALENDAR_API_ROUTE))
      .getBody()
      .as(CalendarCollectionDTO.class);

    assertThat(
      "The returned collection has no calendars, indicating no migrated calendars",
      collection.getCalendars(),
      hasSize(0)
    );
  }
}
