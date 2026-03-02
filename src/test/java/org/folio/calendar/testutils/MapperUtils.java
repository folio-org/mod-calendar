package org.folio.calendar.testutils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.experimental.UtilityClass;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
public class MapperUtils {

  public static final ObjectMapper MAPPER = JsonMapper
    .builder()
    .changeDefaultPropertyInclusion(i -> i.withContentInclusion(JsonInclude.Include.NON_NULL))
    .build();
}
