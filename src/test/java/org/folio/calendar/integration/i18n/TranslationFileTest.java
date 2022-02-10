package org.folio.calendar.integration.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.folio.calendar.i18n.TranslationFile;
import org.junit.jupiter.api.Test;

class TranslationFileTest extends BaseTranslationTest {

  @Test
  void testSingleFileGetMap() {
    translationConfiguration.setTranslationDirectory("/translations/test-normal/");

    List<TranslationFile> files = TranslationFile.getAvailableTranslationFiles(
      translationConfiguration,
      resourceResolver
    );

    assertThat("test-normal has exactly one TranslationFile", files, hasSize(1));

    Map<String, String> map = files.get(0).getMap();

    assertThat(map.getOrDefault("foo", null), is("bar"));
  }

  @Test
  void testEmptyFileList() {
    translationConfiguration.setTranslationDirectory("/translations/test-empty/");

    assertThrows(
      IllegalStateException.class,
      () ->
        TranslationFile.getAvailableTranslationFiles(translationConfiguration, resourceResolver),
      "No available files should result in an IllegalStateException"
    );
  }

  @Test
  void testLanguageCountryMap() {
    translationConfiguration.setTranslationDirectory("/translations/test-multiple/");

    Map<String, Map<String, TranslationFile>> map = TranslationFile.buildLanguageCountryPatternMap(
      translationConfiguration,
      resourceResolver
    );

    assertThat(map.containsKey("en"), is(true));
    assertThat(map.get("en").containsKey("*"), is(true));
    assertThat(map.get("en").containsKey("ca"), is(true));
    assertThat(map.get("en").containsKey("us"), is(false));
    assertThat(map.get("en").get("*"), is(not(equalTo(map.get("en").get("ca")))));
    assertThat(map.containsKey("es"), is(true));
    assertThat(map.get("es").containsKey("*"), is(true));
    assertThat(map.get("es").containsKey("mx"), is(false));
    assertThat(map.containsKey("fr"), is(true));
    assertThat(map.get("fr").containsKey("*"), is(true));
    assertThat(map.get("fr").containsKey("fr"), is(true));
    assertThat(map.get("fr").containsKey("zz"), is(false));
    assertThat(map.get("fr").get("*"), is(equalTo(map.get("fr").get("fr"))));
    assertThat(map.containsKey("zz"), is(false));
  }
}
