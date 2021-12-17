package org.folio.calendar.testutils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class APITestUtils {

  public static final String TENANT_ID = "test";

  public static final ObjectMapper MAPPER = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .setSerializationInclusion(Include.NON_NULL);

  /**
   * Map an object to a JSON string
   * @param object the object to map
   * @return the JSON string
   * @throws IllegalArgumentException if the object could not be converted
   */
  public static String toJsonString(Object object) {
    try {
      return MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Could not convert to JSON", e);
    }
  }
}
