package org.folio.calendar.integration.migration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.integration.api.calendar.BaseCalendarApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
class InvalidMigrationTest extends AbstractMigrationTest {

  @Autowired
  CalendarMapper calendarMapper;

  // tests the valid calendar in the bad data
  @Test
  void testCalendars() {
    loadMigrationSql("database-migrate-invalid.sql");
    runMigration();

    CalendarCollectionDTO collection = ra()
      .get(getRequestUrl(BaseCalendarApiTest.COLLECTION_CALENDAR_API_ROUTE))
      .getBody()
      .as(CalendarCollectionDTO.class);

    // fix comparison nuances for valid calendar
    collection.getCalendars().forEach(c -> c.setId(null));
    assertThat("Only 2 periods were valid enough", collection.getCalendars(), hasSize(2));
    assertThat(
      "One calendar had invalid openings stripped",
      collection.getCalendars().stream().anyMatch(c -> c.getNormalHours().isEmpty()),
      is(true)
    );
    log.info(collection.getCalendars());
    assertThat(
      "The one valid calendar was copied properly",
      collection
        .getCalendars()
        .stream()
        .anyMatch(c ->
          c.getExceptions().size() == 1 && c.getExceptions().get(0).getOpenings().size() == 4
        ),
      is(true)
    );
  }
}
