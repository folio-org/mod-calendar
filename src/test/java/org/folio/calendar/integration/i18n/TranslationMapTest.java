package org.folio.calendar.integration.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Locale;
import org.folio.calendar.i18n.TranslationFile;
import org.folio.calendar.i18n.TranslationMap;
import org.folio.calendar.i18n.TranslationMatchQuality;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;

class TranslationMapTest extends BaseTranslationTest {

  @Autowired
  protected ResourcePatternResolver resourceResolver;

  TranslationFile FILE_EN_CA;
  TranslationFile FILE_EN_BASE;
  TranslationFile FILE_FR_FR;

  @BeforeAll
  void initialize() {
    FILE_EN_CA =
      new TranslationFile(
        resourceResolver.getResource("classpath:/test-translations/test-multiple/en_ca.json")
      );
    FILE_EN_BASE =
      new TranslationFile(
        resourceResolver.getResource("classpath:/test-translations/test-multiple/en.json")
      );
    FILE_FR_FR =
      new TranslationFile(
        resourceResolver.getResource("classpath:/test-translations/test-multiple/fr_fr.json")
      );
  }

  @Test
  void testDefaultTranslationMapFromFile() {
    TranslationMap france = new TranslationMap(Locale.FRANCE, FILE_FR_FR);

    assertThat(france.getQuality(), is(TranslationMatchQuality.PERFECT_MATCH));
    assertThat(france.getFallback(), is(nullValue()));
  }

  /**
   * Tests .get as a result
   */
  @Test
  void testFallbackFormat() {
    TranslationMap englishBase = new TranslationMap(Locale.FRANCE, FILE_EN_BASE);
    TranslationMap englishCa = new TranslationMap(Locale.FRANCE, FILE_EN_CA, englishBase);
    TranslationMap france = new TranslationMap(Locale.FRANCE, FILE_FR_FR, englishCa);

    assertThat(france.format("foo", "test", "bar"), is(equalTo("fr_fr bar")));
    assertThat(france.format("en_only"), is(equalTo("In en_ca!")));
    assertThat(france.format("en_base_only"), is(equalTo("In en base!")));
    assertThat(france.format("thisDoesNotExist"), is(equalTo("thisDoesNotExist")));
  }
}
