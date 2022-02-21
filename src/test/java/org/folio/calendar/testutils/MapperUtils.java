package org.folio.calendar.testutils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperUtils {

  public static final ObjectMapper MAPPER = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .setSerializationInclusion(Include.NON_NULL);
}
