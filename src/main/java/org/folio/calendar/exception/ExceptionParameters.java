package org.folio.calendar.exception;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Parameters for an exception, to reflect what caused an error
 */
public final class ExceptionParameters implements Serializable {

  /** Needed for varargs constructor */
  public static final int NUM_IN_A_PAIR = 2;

  @Getter
  protected final Map<String, Object> map = new HashMap<>();

  /**
   * Create a new ExceptionParameters object
   */
  public ExceptionParameters() {}

  /**
   * Create an ExceptionParameters object with the given parameters.  Primarily a convenience method.
   *
   * @param map Pairs of keys and values that reflect API parameters
   */
  public ExceptionParameters(Object... map) {
    if (map.length % NUM_IN_A_PAIR != 0) {
      throw new IllegalArgumentException(
        "An odd number of parameters were passed to new ExceptionParameters.  These are used as key-value pairs and must therefore be given in an even quantity."
      );
    }

    for (int i = 0; i < map.length; i += NUM_IN_A_PAIR) {
      this.addParameter((String) map[i], map[i + 1]);
    }
  }

  /**
   * Add a parameter to this object
   *
   * @param key The key
   * @param value The value of the parameter to add, of any type
   */
  public void addParameter(String key, Object value) {
    this.map.put(key, value);
  }
}
