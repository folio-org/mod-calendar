package org.folio.calendar.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MapUtils {

  /** Used for validation in {@link #buildMap buildMap} */
  public static final int NUM_IN_A_PAIR = 2;

  /**
   * Get a Map&lt;String, Object&gt; from a set of String -&gt; Object pairs
   * @param args pairs of elements, e.g. key1, value1, key2, value2, ...
   * @return the map of each key =&gt; value
   * @throws IllegalArgumentException if an odd number of parameters is passed
   */
  public static Map<String, Object> buildMap(Object... args) {
    Map<String, Object> map = new HashMap<>();

    if (args.length % NUM_IN_A_PAIR != 0) {
      throw new IllegalArgumentException(
        "An odd number of parameters were passed to buildMap.  These are used as key-value pairs and must therefore be given in an even quantity."
      );
    }

    for (int i = 0; i < args.length; i += NUM_IN_A_PAIR) {
      map.put((String) args[i], args[i + 1]);
    }

    return map;
  }
}
