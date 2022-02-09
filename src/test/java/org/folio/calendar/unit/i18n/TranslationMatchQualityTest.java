package org.folio.calendar.unit.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Locale;
import org.folio.calendar.i18n.TranslationFile;
import org.folio.calendar.i18n.TranslationMatchQuality;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

class TranslationMatchQualityTest {

  protected static TranslationFile FILE_NO_NAME = new TranslationFile(
    null,
    new FileSystemResource("")
  );
  protected static TranslationFile FILE_EN = new TranslationFile(
    null,
    new FileSystemResource("en.json")
  );
  protected static TranslationFile FILE_EN_US = new TranslationFile(
    null,
    new FileSystemResource("en_us.json")
  );

  protected static Locale LOCALE_EN = Locale.ENGLISH;
  protected static Locale LOCALE_EN_US = Locale.US;
  protected static Locale LOCALE_EN_CA = Locale.CANADA;
  protected static Locale LOCALE_FR_FR = Locale.FRANCE;

  @Test
  void testMismatchedLanguageBase() {
    assertThat(
      TranslationMatchQuality.getQuality(LOCALE_EN_US, FILE_NO_NAME),
      is(TranslationMatchQuality.NO_MATCH)
    );
    assertThat(
      TranslationMatchQuality.getQuality(LOCALE_EN, FILE_NO_NAME),
      is(TranslationMatchQuality.NO_MATCH)
    );
    assertThat(
      TranslationMatchQuality.getQuality(LOCALE_FR_FR, FILE_EN),
      is(TranslationMatchQuality.NO_MATCH)
    );
    assertThat(
      TranslationMatchQuality.getQuality(LOCALE_FR_FR, FILE_EN_US),
      is(TranslationMatchQuality.NO_MATCH)
    );
  }

  @Test
  void testLanguageOnlyFile() {
    assertThat(
      TranslationMatchQuality.getQuality(LOCALE_EN, FILE_EN),
      is(TranslationMatchQuality.PERFECT_MATCH)
    );
    assertThat(
      TranslationMatchQuality.getQuality(LOCALE_EN_US, FILE_EN),
      is(TranslationMatchQuality.LANG_ONLY)
    );
  }

  @Test
  void testFullFilename() {
    assertThat(
      TranslationMatchQuality.getQuality(LOCALE_EN, FILE_EN_US),
      is(TranslationMatchQuality.LANG_ONLY)
    );
    assertThat(
      TranslationMatchQuality.getQuality(LOCALE_EN_CA, FILE_EN_US),
      is(TranslationMatchQuality.LANG_ONLY)
    );
    assertThat(
      TranslationMatchQuality.getQuality(LOCALE_EN_US, FILE_EN_US),
      is(TranslationMatchQuality.PERFECT_MATCH)
    );
  }
}
