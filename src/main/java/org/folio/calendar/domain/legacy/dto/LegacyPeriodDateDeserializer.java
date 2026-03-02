package org.folio.calendar.domain.legacy.dto;

import org.folio.spring.i18n.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

/**
 * Deserializer for {@link LegacyPeriodDate LegacyPeriodDate} objects from JSON strings
 */
public class LegacyPeriodDateDeserializer extends StdDeserializer<LegacyPeriodDate> {

  @Autowired
  private TranslationService translationService;

  public LegacyPeriodDateDeserializer() {
    this(LegacyPeriodDate.class);
  }

  public LegacyPeriodDateDeserializer(Class<?> c) {
    super(c);
  }

  @Override
  public LegacyPeriodDate deserialize(JsonParser jsonParser, DeserializationContext context) {
    return new LegacyPeriodDate(translationService, jsonParser.getString());
  }
}
