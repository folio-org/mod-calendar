package org.folio.calendar.integration.i18n;

import java.util.Locale;
import org.folio.calendar.i18n.TranslationConfiguration;
import org.folio.calendar.i18n.TranslationService;
import org.folio.calendar.integration.BaseApiTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;

abstract class BaseTranslationTest extends BaseApiTest {

  @Autowired
  TranslationService translationService;

  @Autowired
  TranslationConfiguration translationConfiguration;

  @Autowired
  ResourcePatternResolver resourceResolver;

  @BeforeEach
  @AfterEach
  void reset() {
    Locale.setDefault(Locale.US);
    translationService.clearCache();
    translationConfiguration.reset();
  }
}
