package org.folio.calendar.unit.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.folio.calendar.i18n.TranslationFile;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

class TranslationFileTest {

  @Test
  void testFullPartsExtraction() {
    assertThat(
      "en_us.json parses to [en, us]",
      TranslationFile.getParts("en_us.json"),
      is(arrayContaining("en", "us"))
    );
    assertThat(
      "en_us parses to [en, us]",
      TranslationFile.getParts("en_us"),
      is(arrayContaining("en", "us"))
    );
    assertThat(
      "EN_US parses to [en, us]",
      TranslationFile.getParts("EN_US"),
      is(arrayContaining("en", "us"))
    );
  }

  @Test
  void testPartialPartsExtraction() {
    assertThat(
      "en.json parses to [en, *]",
      TranslationFile.getParts("en.json"),
      is(arrayContaining("en", "*"))
    );
    assertThat(
      "en_us_extra parses to [en, us]",
      TranslationFile.getParts("en_us_extra"),
      is(arrayContaining("en", "us"))
    );
    assertThat(
      "en_ parses to [en, *]",
      TranslationFile.getParts("en_"),
      is(arrayContaining("en", "*"))
    );
    assertThat(
      "en__foo parses to [en, *]",
      TranslationFile.getParts("en__foo"),
      is(arrayContaining("en", "*"))
    );
    assertThat(
      "_us parses to [*, us]",
      TranslationFile.getParts("_us"),
      is(arrayContaining("*", "us"))
    );
  }

  @Test
  void testEmptyPartsExtraction() {
    assertThat(
      "\"\" parses to [*, *]",
      TranslationFile.getParts(""),
      is(arrayContaining("*", "*"))
    );
    assertThat("_ parses to [*, *]", TranslationFile.getParts("_"), is(arrayContaining("*", "*")));
    assertThat(
      "null parses to [*, *]",
      TranslationFile.getParts(null),
      is(arrayContaining("*", "*"))
    );
  }

  @Test
  void testInstancePartsExtraction() {
    assertThat(
      "Regular en_us.json parses as an instance the same as statically",
      new TranslationFile(new FileSystemResource("en_us.json")).getParts(),
      is(equalTo(TranslationFile.getParts("en_us.json")))
    );
  }

  @Test
  void testEmptyGetMap() {
    assertThat(
      new TranslationFile(new FileSystemResource("invalid.json")).getMap().values(),
      is(empty())
    );
  }
}
