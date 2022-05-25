package org.folio.calendar.unit.domain.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
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
  void testWeekdayDateConversion() {
    assertThat("Jan 1 2021 => Fri", Weekday.from(Dates.DATE_2021_01_01), is(Weekday.FRIDAY));
    assertThat("Jan 2 2021 => Sat", Weekday.from(Dates.DATE_2021_01_02), is(Weekday.SATURDAY));
    assertThat("Jan 3 2021 => Sun", Weekday.from(Dates.DATE_2021_01_03), is(Weekday.SUNDAY));
    assertThat("Jan 4 2021 => Mon", Weekday.from(Dates.DATE_2021_01_04), is(Weekday.MONDAY));
    assertThat("Mar 16 2021 => Tue", Weekday.from(Dates.DATE_2021_03_16), is(Weekday.TUESDAY));
    assertThat("Sep 22 2021 => Wed", Weekday.from(Dates.DATE_2021_09_22), is(Weekday.WEDNESDAY));
    assertThat("Dec 30 2021 => Thu", Weekday.from(Dates.DATE_2021_12_30), is(Weekday.THURSDAY));
  }

  @Test
  void testWeekdayStringConversion() {
    assertThat("\"SUNDAY\" => SUNDAY", Weekday.from("SUNDAY"), is(Weekday.SUNDAY));
    assertThat("\"Sunday\" => SUNDAY", Weekday.from("Sunday"), is(Weekday.SUNDAY));
    assertThat("\"sunday\" => SUNDAY", Weekday.from("sunday"), is(Weekday.SUNDAY));
    assertThat("\"MONDAY\" => MONDAY", Weekday.from("MONDAY"), is(Weekday.MONDAY));
    assertThat("\"Monday\" => MONDAY", Weekday.from("Monday"), is(Weekday.MONDAY));
    assertThat("\"monday\" => MONDAY", Weekday.from("monday"), is(Weekday.MONDAY));
    assertThat("\"TUESDAY\" => TUESDAY", Weekday.from("TUESDAY"), is(Weekday.TUESDAY));
    assertThat("\"Tuesday\" => TUESDAY", Weekday.from("Tuesday"), is(Weekday.TUESDAY));
    assertThat("\"tuesday\" => TUESDAY", Weekday.from("tuesday"), is(Weekday.TUESDAY));
    assertThat("\"WEDNESDAY\" => WEDNESDAY", Weekday.from("WEDNESDAY"), is(Weekday.WEDNESDAY));
    assertThat("\"Wednesday\" => WEDNESDAY", Weekday.from("Wednesday"), is(Weekday.WEDNESDAY));
    assertThat("\"wednesday\" => WEDNESDAY", Weekday.from("wednesday"), is(Weekday.WEDNESDAY));
    assertThat("\"THURSDAY\" => THURSDAY", Weekday.from("THURSDAY"), is(Weekday.THURSDAY));
    assertThat("\"Thursday\" => THURSDAY", Weekday.from("Thursday"), is(Weekday.THURSDAY));
    assertThat("\"thursday\" => THURSDAY", Weekday.from("thursday"), is(Weekday.THURSDAY));
    assertThat("\"FRIDAY\" => FRIDAY", Weekday.from("FRIDAY"), is(Weekday.FRIDAY));
    assertThat("\"Friday\" => FRIDAY", Weekday.from("Friday"), is(Weekday.FRIDAY));
    assertThat("\"friday\" => FRIDAY", Weekday.from("friday"), is(Weekday.FRIDAY));
    assertThat("\"SATURDAY\" => SATURDAY", Weekday.from("SATURDAY"), is(Weekday.SATURDAY));
    assertThat("\"Saturday\" => SATURDAY", Weekday.from("Saturday"), is(Weekday.SATURDAY));
    assertThat("\"saturday\" => SATURDAY", Weekday.from("saturday"), is(Weekday.SATURDAY));

    assertThrows(
      "Invalid strings cannot be converted",
      IllegalArgumentException.class,
      () -> Weekday.from("foo")
    );
  }

  @Test
  void testGetAll() {
    assertThat(Weekday.getAll(), hasSize(7));
    assertThat(
      Weekday.getAll(),
      containsInAnyOrder(
        Weekday.SUNDAY,
        Weekday.MONDAY,
        Weekday.TUESDAY,
        Weekday.WEDNESDAY,
        Weekday.THURSDAY,
        Weekday.FRIDAY,
        Weekday.SATURDAY
      )
    );
  }
}
