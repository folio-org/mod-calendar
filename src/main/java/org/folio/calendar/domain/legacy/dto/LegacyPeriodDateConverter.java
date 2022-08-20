package org.folio.calendar.domain.legacy.dto;

import org.folio.calendar.i18n.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Defines to Spring how to convert between String and LegacyPeriodDate.
 * Primarily needed for request parameter mapping to method parameters for
 * REST controllers
 */
@Component
public class LegacyPeriodDateConverter implements Converter<String, LegacyPeriodDate> {

  @Autowired
  private TranslationService translationService;

  public LegacyPeriodDate convert(String src) {
    return new LegacyPeriodDate(translationService, src);
  }
}
