package org.folio.calendar.integration.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    translationConfiguration.setTranslationDirectory("/translations/test-empty");

    assertThrows(
      IllegalStateException.class,
      () ->
        TranslationFile.getAvailableTranslationFiles(translationConfiguration, resourceResolver),
      "No available files should result in an IllegalStateException"
    );
  }
}
