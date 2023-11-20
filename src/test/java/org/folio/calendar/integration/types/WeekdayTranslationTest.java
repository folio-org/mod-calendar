package org.folio.calendar.integration.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Arrays;
import java.util.List;
import org.folio.calendar.domain.types.Weekday;
import org.folio.calendar.integration.BaseApiTest;
import org.folio.spring.i18n.service.TranslationService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class WeekdayTranslationTest extends BaseApiTest {

  @Autowired
  TranslationService translationService;

  static List<Arguments> shortCases() {
    return Arrays.asList(
      arguments(Weekday.SUNDAY, "Sun"),
      arguments(Weekday.MONDAY, "Mon"),
      arguments(Weekday.TUESDAY, "Tue"),
      arguments(Weekday.WEDNESDAY, "Wed"),
      arguments(Weekday.THURSDAY, "Thu"),
      arguments(Weekday.FRIDAY, "Fri"),
      arguments(Weekday.SATURDAY, "Sat")
    );
  }

  @ParameterizedTest
  @MethodSource("shortCases")
  void testShort(Weekday weekday, String expected) {
    assertThat(
      weekday + " => \"" + expected + "\"",
      weekday.getShortLocalizedString().apply(translationService),
      is(expected)
    );
  }

  static List<Arguments> longCases() {
    return Arrays.asList(
      arguments(Weekday.SUNDAY, "Sunday"),
      arguments(Weekday.MONDAY, "Monday"),
      arguments(Weekday.TUESDAY, "Tuesday"),
      arguments(Weekday.WEDNESDAY, "Wednesday"),
      arguments(Weekday.THURSDAY, "Thursday"),
      arguments(Weekday.FRIDAY, "Friday"),
      arguments(Weekday.SATURDAY, "Saturday")
    );
  }

  @ParameterizedTest
  @MethodSource("longCases")
  void testLong(Weekday weekday, String expected) {
    assertThat(
      weekday + " => \"" + expected + "\"",
      weekday.getLongLocalizedString().apply(translationService),
      is(expected)
    );
  }
}
