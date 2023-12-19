package org.folio.calendar.integration.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.testconstants.ExceptionRanges;
import org.folio.calendar.utils.ExceptionRangeUtils;
import org.folio.spring.i18n.service.TranslationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExceptionRangeUtilsTranslationTest extends BaseApiTest {

  @Autowired
  TranslationService translationService;

  @Test
  void testSingleDayRangeTranslations() {
    assertThat(
      ExceptionRangeUtils
        .getTranslation(ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_01)
        .apply(translationService),
      is("“comic sublime upscale utilize” on Jan 1, 2021")
    );
    assertThat(
      ExceptionRangeUtils
        .getTranslation(ExceptionRanges.OPEN_00_00_TO_14_59_JAN_1)
        .apply(translationService),
      is("“Untitled exception” on Jan 1, 2021")
    );
  }

  @Test
  void testMultiDayRangeTranslations() {
    assertThat(
      ExceptionRangeUtils
        .getTranslation(ExceptionRanges.CLOSED_2021_01_01_TO_2021_12_31)
        .apply(translationService),
      is("“sectional proving blanching deputy” from Jan 1, 2021 to Dec 31, 2021")
    );
    assertThat(
      ExceptionRangeUtils
        .getTranslation(ExceptionRanges.CLOSED_2021_01_01_TO_2021_01_04)
        .apply(translationService),
      is("“supplier grouped bride lazily” from Jan 1, 2021 to Jan 4, 2021")
    );
    assertThat(
      ExceptionRangeUtils
        .getTranslation(ExceptionRanges.OPEN_04_00_TO_14_59_JAN_1_THRU_JAN_4)
        .apply(translationService),
      is("“Untitled exception” from Jan 1, 2021 to Jan 4, 2021")
    );
  }
}
