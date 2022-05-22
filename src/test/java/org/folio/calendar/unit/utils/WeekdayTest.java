package org.folio.calendar.unit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import org.folio.calendar.domain.types.Weekday;
import org.folio.calendar.testconstants.Dates;
import org.junit.jupiter.api.Test;

class WeekdayTest {

  /**
   * Exhaustively test previous weekdays (due to the small cases and the switch usage in the code)
   */
  @Test
  void testPreviousWeekday() {
    assertThat("Sunday is before Monday", Weekday.MONDAY.previous(), is(Weekday.SUNDAY));
    assertThat("Monday is before Tuesday", Weekday.TUESDAY.previous(), is(Weekday.MONDAY));
    assertThat("Tuesday is before Wednesday", Weekday.WEDNESDAY.previous(), is(Weekday.TUESDAY));
    assertThat("Wednesday is before Thursday", Weekday.THURSDAY.previous(), is(Weekday.WEDNESDAY));
    assertThat("Thursday is before Friday", Weekday.FRIDAY.previous(), is(Weekday.THURSDAY));
    assertThat("Friday is before Saturday", Weekday.SATURDAY.previous(), is(Weekday.FRIDAY));
    assertThat("Saturday is before Sunday", Weekday.SUNDAY.previous(), is(Weekday.SATURDAY));
    assertThrows(
      "Invalid weekdays have no previous weekday",
      IllegalArgumentException.class,
      () -> Weekday.INVALID.previous()
    );
  }

  /**
   * Exhaustively test next weekdays (due to the small cases and the switch usage in the code)
   */
  @Test
  void testNextWeekday() {
    assertThat("Monday is after Sunday", Weekday.SUNDAY.next(), is(Weekday.MONDAY));
    assertThat("Tuesday is after Monday", Weekday.MONDAY.next(), is(Weekday.TUESDAY));
    assertThat("Wednesday is after Tuesday", Weekday.TUESDAY.next(), is(Weekday.WEDNESDAY));
    assertThat("Thursday is after Wednesday", Weekday.WEDNESDAY.next(), is(Weekday.THURSDAY));
    assertThat("Friday is after Thursday", Weekday.THURSDAY.next(), is(Weekday.FRIDAY));
    assertThat("Saturday is after Friday", Weekday.FRIDAY.next(), is(Weekday.SATURDAY));
    assertThat("Sunday is after Saturday", Weekday.SATURDAY.next(), is(Weekday.SUNDAY));
    assertThrows(
      "Invalid weekdays have no next weekday",
      IllegalArgumentException.class,
      () -> Weekday.INVALID.next()
    );
  }

  @Test
  void testSingleWeekdayRange() {
    assertThat(
      "Monday to Monday is {Monday}",
      Weekday.getRange(Weekday.MONDAY, Weekday.MONDAY),
      is(Arrays.asList(Weekday.MONDAY))
    );
    assertThat(
      "Saturday to Saturday is {Saturday}",
      Weekday.getRange(Weekday.SATURDAY, Weekday.SATURDAY),
      is(Arrays.asList(Weekday.SATURDAY))
    );
  }

  @Test
  void testMultipleWeekdayRange() {
    assertThat(
      "Monday to Friday is {Monday, Tuesday, Wednesday, Thursday, Friday}",
      Weekday.getRange(Weekday.MONDAY, Weekday.FRIDAY),
      is(
        Arrays.asList(
          Weekday.MONDAY,
          Weekday.TUESDAY,
          Weekday.WEDNESDAY,
          Weekday.THURSDAY,
          Weekday.FRIDAY
        )
      )
    );
    assertThat(
      "Friday to Tuesday is {Friday, Saturday, Sunday, Monday, Tuesday}",
      Weekday.getRange(Weekday.FRIDAY, Weekday.TUESDAY),
      is(
        Arrays.asList(
          Weekday.FRIDAY,
          Weekday.SATURDAY,
          Weekday.SUNDAY,
          Weekday.MONDAY,
          Weekday.TUESDAY
        )
      )
    );
  }

  @Test
  void testWeekdayConversion() {
    assertThat("Jan 1 2021 => Fri", Weekday.from(Dates.DATE_2021_01_01), is(Weekday.FRIDAY));
    assertThat("Jan 2 2021 => Sat", Weekday.from(Dates.DATE_2021_01_02), is(Weekday.SATURDAY));
    assertThat("Jan 3 2021 => Sun", Weekday.from(Dates.DATE_2021_01_03), is(Weekday.SUNDAY));
    assertThat("Jan 4 2021 => Mon", Weekday.from(Dates.DATE_2021_01_04), is(Weekday.MONDAY));
    assertThat("Mar 16 2021 => Tue", Weekday.from(Dates.DATE_2021_03_16), is(Weekday.TUESDAY));
    assertThat("Sep 22 2021 => Wed", Weekday.from(Dates.DATE_2021_09_22), is(Weekday.WEDNESDAY));
    assertThat("Dec 30 2021 => Thu", Weekday.from(Dates.DATE_2021_12_30), is(Weekday.THURSDAY));
  }
}
