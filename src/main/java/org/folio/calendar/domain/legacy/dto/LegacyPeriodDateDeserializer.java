package org.folio.calendar.domain.legacy.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.folio.spring.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Deserializer for {@link LegacyPeriodDate LegacyPeriodDate} objects from JSON strings
 */
public class LegacyPeriodDateDeserializer extends StdDeserializer<LegacyPeriodDate> {

  @Autowired
  private transient TranslationService translationService;

  public LegacyPeriodDateDeserializer() {
    this(LegacyPeriodDate.class);
  }

  public LegacyPeriodDateDeserializer(Class<?> c) {
    super(c);
  }

  @Override
  public LegacyPeriodDate deserialize(JsonParser jsonParser, DeserializationContext context)
    throws IOException {
    return new LegacyPeriodDate(translationService, jsonParser.getText());
  }
}
