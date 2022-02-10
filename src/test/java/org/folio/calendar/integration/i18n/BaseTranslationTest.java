package org.folio.calendar.integration.i18n;

import org.folio.calendar.i18n.TranslationConfiguration;
import org.folio.calendar.i18n.TranslationService;
import org.folio.calendar.integration.BaseApiTest;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;

abstract class BaseTranslationTest extends BaseApiTest {

  @Autowired
  TranslationService translationService;

  @Autowired
  TranslationConfiguration translationConfiguration;

  @Autowired
  ResourcePatternResolver resourceResolver;

  @AfterEach
  void reset() {
    translationService.clearCache();
    translationConfiguration.reset();
  }
}
