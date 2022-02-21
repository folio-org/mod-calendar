package org.folio.calendar.domain.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.format.DateTimeParseException;

/**
 * Deserializer for {@link LegacyPeriodDate LegacyPeriodDate} objects from JSON strings
 */
public class LegacyPeriodDateDeserializer extends StdDeserializer<LegacyPeriodDate> {

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
      return new LegacyPeriodDate(jsonParser.getText());
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
