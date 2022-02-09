package org.folio.calendar.exception;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Value;
import org.folio.calendar.utils.MapUtils;

/**
 * Parameters for an exception, to reflect what caused an error
 */
@Value
public final class ExceptionParameters implements Serializable {

  protected final Map<String, Object> map = new HashMap<>();

  /**
   * Create a new ExceptionParameters object
   */
  public ExceptionParameters() {}

  /**
   * Create an ExceptionParameters object with the given parameters.  Primarily a convenience method.
   *
   * @param args Pairs of keys and values that reflect API parameters
   */
  public ExceptionParameters(Object... args) {
    this.map.putAll(MapUtils.buildMap(args));
  }
}
