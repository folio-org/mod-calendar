package org.folio.calendar.integration.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.domain.types.Weekday;
import org.folio.calendar.i18n.TranslationService;
import org.folio.calendar.integration.BaseApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WeekdayTranslationTest extends BaseApiTest {

  @Autowired
  TranslationService translationService;

  @Test
  void testShortTranslations() {
    assertThat(
      "SUNDAY => \"Sun\"",
      Weekday.SUNDAY.getShortLocalizedString().apply(translationService),
      is("Sun")
    );
    assertThat(
      "MONDAY => \"Mon\"",
      Weekday.MONDAY.getShortLocalizedString().apply(translationService),
      is("Mon")
    );
    assertThat(
      "TUESDAY => \"Tue\"",
      Weekday.TUESDAY.getShortLocalizedString().apply(translationService),
      is("Tue")
    );
    assertThat(
      "WEDNESDAY => \"Wed\"",
      Weekday.WEDNESDAY.getShortLocalizedString().apply(translationService),
      is("Wed")
    );
    assertThat(
      "THURSDAY => \"Thu\"",
      Weekday.THURSDAY.getShortLocalizedString().apply(translationService),
      is("Thu")
    );
    assertThat(
      "FRIDAY => \"Fri\"",
      Weekday.FRIDAY.getShortLocalizedString().apply(translationService),
      is("Fri")
    );
    assertThat(
      "SATURDAY => \"Sat\"",
      Weekday.SATURDAY.getShortLocalizedString().apply(translationService),
      is("Sat")
    );
  }

  @Test
  void testLongTranslations() {
    assertThat(
      "SUNDAY => \"Sunday\"",
      Weekday.SUNDAY.getLongLocalizedString().apply(translationService),
      is("Sunday")
    );
    assertThat(
      "MONDAY => \"Monday\"",
      Weekday.MONDAY.getLongLocalizedString().apply(translationService),
      is("Monday")
    );
    assertThat(
      "TUESDAY => \"Tuesday\"",
      Weekday.TUESDAY.getLongLocalizedString().apply(translationService),
      is("Tuesday")
    );
    assertThat(
      "WEDNESDAY => \"Wednesday\"",
      Weekday.WEDNESDAY.getLongLocalizedString().apply(translationService),
      is("Wednesday")
    );
    assertThat(
      "THURSDAY => \"Thursday\"",
      Weekday.THURSDAY.getLongLocalizedString().apply(translationService),
      is("Thursday")
    );
    assertThat(
      "FRIDAY => \"Friday\"",
      Weekday.FRIDAY.getLongLocalizedString().apply(translationService),
      is("Friday")
    );
    assertThat(
      "SATURDAY => \"Saturday\"",
      Weekday.SATURDAY.getLongLocalizedString().apply(translationService),
      is("Saturday")
    );
  }
}
