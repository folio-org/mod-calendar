package org.folio.calendar.integration.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.Locale;
import org.folio.calendar.i18n.TranslationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TranslationServiceTest extends BaseTranslationTest {

  @Autowired
  TranslationService translationService;

  @Test
  void testGetTranslationPresent() {
    translationConfiguration.setTranslationDirectory("/test-translations/test-multiple/");

    assertThat(translationService.getTranslation(Locale.CHINA, null), is(nullValue()));

    assertThat(
      translationService.getTranslation(Locale.CANADA, null).get("en_base_only"),
      is("In en base!")
    );
    assertThat(
      translationService.getTranslation(Locale.CANADA, null).get("en_ca_only"),
      is("In en_ca!")
    );
    assertThat(
      translationService.getTranslation(Locale.CANADA, null).get("en_only"),
      is("In en_ca!")
    );

    assertThat(
      translationService.getTranslation(new Locale("es", "sp"), null).get("es_only"),
      is("In es base!")
    );

    assertThat(
      translationService.getTranslation(Locale.UK, null).get("en_base_only"),
      is("In en base!")
    );
    assertThat(
      translationService.getTranslation(Locale.UK, null).get("en_ca_only"),
      is("en_ca_only")
    );
  }

  @Test
  void testDefaultLocale() {
    translationConfiguration.setTranslationDirectory("/test-translations/test-normal/");

    Locale.setDefault(new Locale("test", ""));
    assertThat(translationService.getDefaultTranslation().getLocale(), is(new Locale("test", "")));
  }

  @Test
  void testDefaultLocaleFallback() {
    translationConfiguration.setTranslationDirectory("/test-translations/test-multiple/");

    Locale.setDefault(new Locale("test", ""));
    assertThat(translationService.getDefaultTranslation().getLocale(), is(Locale.ENGLISH));
  }

  @Test
  void testDefaultLocaleException() {
    translationConfiguration.setTranslationDirectory("/test-translations/test-normal/");

    // only available are test.json, so FR_FR and EN_US don't match
    Locale.setDefault(Locale.FRANCE);
    assertThrows(
      "No available translations causes an IllegalStateException",
      IllegalStateException.class,
      () -> translationService.getDefaultTranslation()
    );
  }

  @Test
  void testBestTranslation() {
    translationConfiguration.setTranslationDirectory("/test-translations/test-multiple/");

    assertThat(
      translationService.getBestTranslation(Arrays.asList(Locale.US, Locale.FRANCE)).getLocale(),
      is(Locale.US)
    );
    assertThat(
      translationService.getBestTranslation(Arrays.asList(Locale.FRANCE, Locale.US)).getLocale(),
      is(Locale.FRANCE)
    );
    assertThat(
      translationService.getBestTranslation(Arrays.asList(Locale.CHINESE, Locale.US)).getLocale(),
      is(Locale.US)
    );
    assertThat(
      translationService.getBestTranslation(Arrays.asList(Locale.CHINESE)).getLocale(),
      is(Locale.US) // server default
    );
    assertThat(
      translationService.getBestTranslation(Arrays.asList()).getLocale(),
      is(Locale.US) // server default
    );
  }
}
