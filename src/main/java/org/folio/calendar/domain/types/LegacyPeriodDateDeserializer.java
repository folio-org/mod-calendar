package org.folio.calendar.domain.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import org.folio.calendar.i18n.TranslationService;
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
    try {
      return new LegacyPeriodDate(translationService, jsonParser.getText());
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
