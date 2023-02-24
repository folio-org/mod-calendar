package org.folio.calendar.integration.migration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.folio.calendar.domain.dto.CalendarCollectionDTO;
import org.folio.calendar.domain.entity.Calendar;
import org.folio.calendar.domain.mapper.CalendarMapper;
import org.folio.calendar.integration.api.calendar.BaseCalendarApiTest;
import org.folio.calendar.testconstants.Dates;
import org.folio.calendar.testconstants.ExceptionRanges;
import org.folio.calendar.testconstants.Names;
import org.folio.calendar.testconstants.NormalOpenings;
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
      .get(getRequestUrl(BaseCalendarApiTest.COLLECTION_CALENDAR_API_ROUTE))
      .getBody()
      .as(CalendarCollectionDTO.class);

    // fix comparison nuances for valid calendar
    List<Calendar> calendars = collection
      .getCalendars()
      .stream()
      .map(calendarMapper::fromDto)
      .toList();
    calendars.forEach(Calendar::clearIds);

    assertThat("Five periods were consolidated into three calendars", calendars, hasSize(3));

    Calendar withExceptions = calendars
      .stream()
      .filter(cal -> cal.getName().equals(Names.NAME_1))
      .findFirst()
      .get();
    Calendar withoutExceptions = calendars
      .stream()
      .filter(cal -> cal.getName().equals(Names.NAME_4))
      .findFirst()
      .get();
    Calendar orphaned = calendars
      .stream()
      .filter(cal -> cal.getName().equals("Orphaned exception (replay quake aloft routine)"))
      .findFirst()
      .get();

    checkCalendarWithExceptions(withExceptions);
    checkCalendarWithoutExceptions(withoutExceptions);
    checkCalendarOrphaned(orphaned);
  }

  void checkCalendarWithExceptions(Calendar calendar) {
    assertThat(
      "Calendar with exceptions has expected service points",
      calendar.getServicePoints(),
      hasSize(1)
    );
    assertThat(
      "Calendar with exceptions has expected service points",
      calendar.getServicePoints().stream().toList().get(0).getServicePointId(),
      is(UUIDs.UUID_0)
    );
    assertThat(
      "Calendar with exceptions has expected start date",
      calendar.getStartDate(),
      is(Dates.DATE_2021_01_01)
    );
    assertThat(
      "Calendar with exceptions has expected end date",
      calendar.getEndDate(),
      is(Dates.DATE_2021_12_31)
    );
    assertThat(
      "Calendar with exceptions has expected normal openings",
      calendar.getNormalHours(),
      containsInAnyOrder(
        NormalOpenings.MONDAY_04_00_TO_14_59,
        NormalOpenings.TUESDAY_ALL_DAY,
        NormalOpenings.WEDNESDAY_23_00_TO_SUNDAY_23_59
      )
    );
    assertThat(
      "Calendar with exceptions has expected exceptions",
      calendar.getExceptions(),
      containsInAnyOrder(
        ExceptionRanges.OPEN_ALL_DAY_MAR_16_TO_APR_30.withName(Names.NAME_2),
        ExceptionRanges.CLOSED_2021_07_04_TO_2021_09_22.withName(Names.NAME_3)
      )
    );
  }

  void checkCalendarWithoutExceptions(Calendar calendar) {
    assertThat(
      "Calendar without exceptions has expected service points",
      calendar.getServicePoints(),
      hasSize(1)
    );
    assertThat(
      "Calendar without exceptions has expected service points",
      calendar.getServicePoints().stream().toList().get(0).getServicePointId(),
      is(UUIDs.UUID_1)
    );
    assertThat(
      "Calendar without exceptions has expected start date",
      calendar.getStartDate(),
      is(Dates.DATE_2021_05_01)
    );
    assertThat(
      "Calendar without exceptions has expected end date",
      calendar.getEndDate(),
      is(Dates.DATE_2021_09_22)
    );
    assertThat(
      "Calendar without exceptions has expected normal openings",
      calendar.getNormalHours(),
      containsInAnyOrder(
        NormalOpenings.MONDAY_23_00_TO_23_59,
        NormalOpenings.MONDAY_00_00_TO_12_30,
        NormalOpenings.THURSDAY_ALL_DAY
      )
    );
    assertThat(
      "Calendar without exceptions has no exceptions",
      calendar.getExceptions(),
      hasSize(0)
    );
  }

  void checkCalendarOrphaned(Calendar calendar) {
    assertThat(
      "Calendar with orphaned exception has expected service points",
      calendar.getServicePoints(),
      hasSize(1)
    );
    assertThat(
      "Calendar with orphaned exception has expected service points",
      calendar.getServicePoints().stream().toList().get(0).getServicePointId(),
      is(UUIDs.UUID_5)
    );
    assertThat(
      "Calendar with orphaned exception has expected start date",
      calendar.getStartDate(),
      is(Dates.DATE_2021_01_01)
    );
    assertThat(
      "Calendar with orphaned exception has expected end date",
      calendar.getEndDate(),
      is(Dates.DATE_2021_01_04)
    );
    assertThat(
      "Calendar with orphaned exception has no normal openings",
      calendar.getNormalHours(),
      hasSize(0)
    );
    assertThat(
      "Calendar with orphaned exception has expected exceptions",
      calendar.getExceptions(),
      containsInAnyOrder(
        ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4.withName(Names.NAME_3)
      )
    );
  }
}
