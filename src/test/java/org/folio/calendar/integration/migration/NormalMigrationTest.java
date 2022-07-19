package org.folio.calendar.integration.migration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.stream.Collectors;
import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.domain.dto.CalendarDTO;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.integration.api.calendar.BaseCalendarApiTest;
import org.folio.calendar.testconstants.Calendars;
import org.folio.calendar.testconstants.UUIDs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NormalMigrationTest extends AbstractMigrationTest {

  @Autowired
  CalendarMapper calendarMapper;

  @BeforeAll
  void migrate() {
    loadMigrationSql("database-migrate-data.sql");
    runMigration();
  }

  @Test
  void testGetCalendars() {
    CalendarCollectionDTO collection = ra()
      .get(getRequestUrl(BaseCalendarApiTest.GET_SEARCH_CALENDAR_API_ROUTE))
      .getBody()
      .as(CalendarCollectionDTO.class);

    // fix comparison nuances for valid calendar
    collection.getCalendars().forEach(c -> c.setId(UUIDs.UUID_A));
    assertThat(
      "The regular period was properly converted",
      collection
        .getCalendars()
        .stream()
        .map(CalendarDTO::getNormalHours)
        .collect(Collectors.toList()),
      hasItem(
        containsInAnyOrder(
          calendarMapper.toDto(Calendars.CALENDAR_FULL_EXAMPLE_F).getNormalHours().toArray()
        )
      )
    );
    assertThat(
      "The exceptional period was properly converted",
      collection
        .getCalendars()
        .stream()
        .filter(c -> !c.getExceptions().isEmpty())
        .anyMatch(c -> c.getExceptions().get(0).getOpenings().size() == 4),
      is(true)
    );
  }
}
