package org.folio.calendar.i18n;

import com.ibm.icu.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.annotation.CheckForNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.types.LegacyPeriodDate;
import org.folio.calendar.utils.MapUtils;

/**
 * This object represents the relationship between a locale and a
 * {@link TranslationFile TranslationFile}.  The quality of this relationship is determined by how
 * well the file's locale matches up with the one in use.  This object will also contain fallbacks,
 * such as alternative languages for the user, or the server's default locale, to provide more
 * seamless support for missing translations.
 */
@Log4j2
@ToString
@EqualsAndHashCode
public class TranslationMap {

  @Getter
  protected final Locale locale;

  @Getter
  protected final TranslationMatchQuality quality;

  @Getter
  protected final TranslationFile file;

  protected final Map<String, String> patterns;

  @Getter
  @CheckForNull
  protected final TranslationMap fallback;

  /**
   * Create a TranslationMap for a given locale based off of a file with contents and a given fallback.
   * @param locale the associated locale
   * @param file the {@link TranslationFile TranslationFile} to infer quality from
   * @param patterns the patterns to use
   * @param fallback the TranslationMap to search if a given translation cannot be found
   */
  protected TranslationMap(
    Locale locale,
    TranslationFile file,
    Map<String, String> patterns,
    @CheckForNull TranslationMap fallback
  ) {
    this.locale = locale;
    this.quality = TranslationMatchQuality.getQuality(locale, file);
    this.file = file;
    this.patterns = patterns;
    this.fallback = fallback;
  }

  /**
   * Create a TranslationMap for a given locale based off of a file with a given fallback.
   * @param locale the associated locale
   * @param file the {@link TranslationFile TranslationFile} to read from
   * @param fallback the TranslationMap to search if a given translation cannot be found
   */
  public TranslationMap(
    Locale locale,
    TranslationFile file,
    @CheckForNull TranslationMap fallback
  ) {
    this(locale, file, file.getMap(), fallback);
  }

  /**
   * Create a <em>default</em> TranslationMap for a given locale based off of a file.
   * This constructor should only be used for default translation maps; all others
   * should use the other constructor and provide a fallback.
   * @param locale the associated locale
   * @param file the {@link TranslationFile TranslationFile} to read from
   */
  public TranslationMap(Locale locale, TranslationFile file) {
    this(locale, file, null);
  }

  /**
   * Create a new TranslationMap for a given locale.  This is primarily used when cloning
   * existing TranslationMaps for use as fallbacks for another (for caching reasons, this
   * makes more sense than re-reading files each time)
   * @param newLocale the locale for the new TranslationMap to use
   * @return the TranslationMap with {@code newLocale}
   */
  public TranslationMap withLocale(Locale newLocale) {
    return new TranslationMap(newLocale, this.file, this.patterns, this.fallback);
  }

  /**
   * Get the ICU format string associated with a given key for this translation
   * @param key the key to lookup
   * @return one of the following, with this precedence:
   * <ol>
   *   <li>the ICU format string for this locale, if it is known</li>
   *   <li>the ICU format string for a locale with a worse {@code TranslationMatchQuality},
   *     such as one without a specific country, recursing through the {@code fallback}</li>
   *   <li>the ICU format string for the server's default locale</li>
   *   <li>the provided key</li>
   * </ol>
   */
  public String get(String key) {
    if (this.patterns.containsKey(key)) {
      return this.patterns.get(key);
    } else if (this.fallback != null) {
      return this.fallback.get(key);
    } else {
      log.error("Could not resolve key " + key + " in any translation");
      return key;
    }
  }

  /**
   * Format an ICU format string (found by its key), supplying a series of named arguments as key
   * value pairs.  For example: {@code format("Hello {name}", "name", parameterValue)}
   * @param key the key of the format string
   * @param args pairs of keys and values to interpolate
   * @return the formatted string
   */
  public String format(String key, Object... args) {
    for (int i = 0; i < args.length; i++) {
      // Convert LegacyPeriodDate to LocalDate as needed
      if (args[i] instanceof LegacyPeriodDate) {
        args[i] = ((LegacyPeriodDate) args[i]).getValue();
      }
      // Convert LocalDate to Date
      // Sadly, ICU formatting strings only support date formats with the old Date class :(
      if (args[i] instanceof LocalDate) {
        args[i] = Date.from(((LocalDate) args[i]).atStartOfDay(ZoneId.systemDefault()).toInstant());
      }
      // Same for LocalTime
      if (args[i] instanceof LocalTime) {
        args[i] =
          Date.from(
            ((LocalTime) args[i]).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant()
          );
      }
    }
    return MessageFormat.format(this.get(key), MapUtils.buildMap(args));
  }
}
