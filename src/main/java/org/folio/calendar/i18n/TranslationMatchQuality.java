package org.folio.calendar.i18n;

import java.util.Locale;

/**
 * How good a {@link TranslationMap TranslationMap} relates to a given {@link java.util.Locale Locale}
 */
public enum TranslationMatchQuality {
  /** If the paired translation is a perfect match for language and country */
  PERFECT_MATCH,
  /** If the paired translation is a match for language only and not country (or not applicable to country) */
  LANG_ONLY,
  /** If the paired translation is not a match for language or country (should be the server's default locale) */
  NO_MATCH;

  /**
   * Determine the quality of a locale versus a provided translation file's name
   * @param locale the locale to compare against
   * @param file the {@link TranslationFile TranslationFile}
   * @return a quality enum value indicating the match, determined with the following rules:
   * <ol>
   *   <li>If the filename is empty, {@code NO_MATCH}</li>
   *   <li>If the language (component 1) is a mismatch, {@code NO_MATCH}</li>
   *   <li>If the filename only specifies language and the locale does not specify a country, {@code PERFECT_MATCH}</li>
   *   <li>If the filename only specifies language and the locale does specify a country, {@code LANG_ONLY}</li>
   *   <li>If the filename country (component 2) matches the locale's country, {@code PERFECT_MATCH}</li>
   *   <li>Otherwise, {@code LANG_ONLY}</li>
   * </ol>
   */
  public static TranslationMatchQuality getQuality(Locale locale, TranslationFile file) {
    // certain this will be exactly size two
    String[] parts = file.getParts();

    // language mismatch (also handles empty filename)
    if (!locale.getLanguage().equalsIgnoreCase(parts[0])) {
      return NO_MATCH;
    }

    // no country from file
    if (parts[1].equalsIgnoreCase(TranslationFile.UNKNOWN_PART)) {
      if ("".equals(locale.getCountry())) {
        return PERFECT_MATCH;
      } else {
        return LANG_ONLY;
      }
    }

    if (locale.getCountry().equalsIgnoreCase(parts[1])) {
      return PERFECT_MATCH;
    } else {
      return LANG_ONLY;
    }
  }
}
