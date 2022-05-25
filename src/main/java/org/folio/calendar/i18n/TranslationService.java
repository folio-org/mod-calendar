package org.folio.calendar.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.request.TranslationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * A {@link Service Service} to manage translations as a whole.  In reality, the most important
 * method is simply the format method which will do all of the heavy lifting as needed.  This
 * service will keep track of translation files and locales, caching as possible, in order to
 * serve the user the best possible translation.
 */
@Log4j2
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TranslationService {

  private final ResourcePatternResolver resourceResolver;
  private final TranslationConfiguration translationConfiguration;

  /**
   * A map from language code -&gt; country -&gt; JSON resource containing {key -&gt; ICU format string}.
   * Will be populated the first time any translation is requested.
   * Note that files are not read here, only enumerated.  Look at the other maps for that information
   *
   * This can be null - if this is the case, the map has not been constructed yet.
   * You should call getFileMap instead of accessing this directly
   *
   * Language code * will be used if one is not denoted
   */
  @CheckForNull
  protected Map<String, Map<String, TranslationFile>> translationFileFromLanguageCountryMap = null;

  /**
   * A map from locale -&gt; key -&gt; ICU format string, filled in on-demand
   * as locales are presented.
   */
  protected Map<Locale, TranslationMap> localeTranslations = new HashMap<>();

  /**
   * Get a map from language -&gt; country -&gt; pattern resource based on the classpath contents
   * @return the map of languages to countries to pattern resources
   */
  @NotNull
  public Map<String, Map<String, TranslationFile>> getFileMap() {
    if (this.translationFileFromLanguageCountryMap == null) {
      this.translationFileFromLanguageCountryMap =
        TranslationFile.buildLanguageCountryPatternMap(
          this.translationConfiguration,
          this.resourceResolver
        );
    }
    return this.translationFileFromLanguageCountryMap;
  }

  /**
   * Get the best {@link TranslationMap TranslationMap} associated with the current locale
   * @param locale the locale to find a TranslationMap for
   * @param base the default translation that should be used as a fallback; can be null when
   *   creating the default translation
   * @return the best translation available for the current locale, potentially null if not
   *   default is null.  It checks for match of language and country, then language, then returns
   *   default if neither are available
   */
  @CheckForNull
  public TranslationMap getTranslation(Locale locale, @Nullable TranslationMap base) {
    return localeTranslations.computeIfAbsent(
      locale,
      (Locale l) -> {
        log.info("Cache miss on " + locale);

        if (this.getFileMap().containsKey(locale.getLanguage().toLowerCase())) {
          Map<String, TranslationFile> languageMap =
            this.getFileMap().get(locale.getLanguage().toLowerCase());

          TranslationFile baseFile = languageMap.get(TranslationFile.UNKNOWN_PART);
          TranslationMap baseMap = new TranslationMap(locale, baseFile, base);

          if (languageMap.containsKey(locale.getCountry().toLowerCase())) {
            return new TranslationMap(
              locale,
              languageMap.get(locale.getCountry().toLowerCase()),
              baseMap
            );
          }

          return baseMap;
        }

        return base;
      }
    );
  }

  /**
   * Get the best {@link TranslationMap TranslationMap} associated with the current locale
   * @param locale the locale to find a TranslationMap for
   * @return the best translation available for the current locale, including the server's default
   */
  public TranslationMap getTranslation(Locale locale) {
    return getTranslation(locale, getDefaultTranslation().withLocale(locale));
  }

  /**
   * Find a TranslationMap for the default locale -- used to initialize for
   * {@code getDefaultTranslations}
   * @return the best applicable translation
   * @throws IllegalStateException if no translation can be found
   */
  protected TranslationMap resolveDefaultLocale() {
    TranslationMap foundDefault = getTranslation(Locale.getDefault(), null);
    if (foundDefault == null) {
      foundDefault = getTranslation(Locale.ENGLISH, null);
    }
    if (foundDefault == null) {
      throw new IllegalStateException(
        String.format(
          "No translations are sufficient for the server's default locale %s nor %s",
          Locale.getDefault(),
          Locale.ENGLISH
        )
      );
    }
    return foundDefault;
  }

  /**
   * Get the translation map for the default locale (or en-us, if that is not possible)
   * @return the default locale's translation map
   */
  public TranslationMap getDefaultTranslation() {
    // computeIfAbsent does not work due to the resolver potentially filling multiple keys
    if (!this.localeTranslations.containsKey(Locale.getDefault())) {
      this.localeTranslations.put(Locale.getDefault(), this.resolveDefaultLocale());
    }
    return this.localeTranslations.get(Locale.getDefault());
  }

  /**
   * Get all of the locales for the current request.  This will respect the Accept-Language header
   * and quality values, as applicable, with highest quality first.
   *
   * @return a List of all Locale objects.  Invalid and wildcard Locales will likely return a Locale
   * with empty string/fields or, in some cases, the server's current Locale.
   * @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language
   */
  public List<Locale> getCurrentLocales() {
    try {
      return Collections.list(
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
          .getLocales()
      );
    } catch (IllegalStateException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  /**
   * Get the best {@link TranslationMap TranslationMap} for the current language(s).
   * This will respect the Accept-Language header and quality values, as applicable.
   *
   * @return the best TranslationMap
   * @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language
   */
  public TranslationMap getCurrentTranslation() {
    return this.getBestTranslation(this.getCurrentLocales());
  }

  /**
   * Get the best {@link TranslationMap TranslationMap} for the provides list of Locales.
   * This will return the first one that matches on at least language or, if none match,
   * the default TranslationMap.
   *
   * @param locales the ordered list of locales to consider
   * @return the best TranslationMap
   */
  public TranslationMap getBestTranslation(Iterable<Locale> locales) {
    // return the first one that is a good match
    for (Locale locale : locales) {
      TranslationMap correspondingMap = this.getTranslation(locale);
      if (correspondingMap.getQuality() != TranslationMatchQuality.NO_MATCH) {
        return correspondingMap;
      }
    }

    return this.getDefaultTranslation();
  }

  /**
   * Wraps the {@link TranslationMap#format TranslationMap#format} method on the current translation.
   * Equivalent to {@code getCurrentTranslation().format(...)}
   *
   * Format an ICU format string (found by its key), supplying a series of named arguments as key
   * value pairs.  For example: {@code format("Hello {name}", "name", parameterValue)}
   * @param key the key of the format string.  You likely want to use a constant from
   * {@link TranslationKey TranslationKey} rather than hard-coding a string.
   * @param args pairs of keys and values to interpolate
   * @return the formatted string
   */
  public String format(String key, Object... args) {
    return this.getCurrentTranslation().format(key, args);
  }

  /**
   * Format a list of strings into one cohesive string.  For example, in English, {@code [A B C D]}
   * would become {@code "A, B, C, and D"}.  The tokens between each pair is determined by the
   * values referenced by {@link TranslationKey.LIST_SEPARATORS TranslationKey.LIST_SEPARATORS}.
   *
   * @param list a list of strings to join
   * @return the joined string
   */
  public String formatList(List<String> list) {
    switch (list.size()) {
      case 0:
        return "";
      case 1:
        return list.get(0);
      case TranslationKey.LIST_SEPARATORS.LIST_TWO_COUNT:
        return (
          list.get(0) + this.format(TranslationKey.LIST_SEPARATORS.LIST_TWO_SEPARATOR) + list.get(1)
        );
      default:
        return formatListOfThreeOrMore(list);
    }
  }

  /**
   * Format a list of three or more items.  This is a helper function for
   * {@link #formatList formatList}; use that instead of this method directly.
   * @param list the list to format
   * @return a list concatenated per the description in formatList
   */
  protected String formatListOfThreeOrMore(List<String> list) {
    String threeOrMoreSeparator =
      this.format(TranslationKey.LIST_SEPARATORS.LIST_THREE_OR_MORE_SEPARATOR);
    String threeOrMoreLastSeparator =
      this.format(TranslationKey.LIST_SEPARATORS.LIST_THREE_OR_MORE_LAST_SEPARATOR);

    StringBuilder sb = new StringBuilder(list.get(0));
    for (int i = 1; i < list.size(); i++) {
      if (i == list.size() - 1) {
        sb.append(threeOrMoreLastSeparator);
      } else {
        sb.append(threeOrMoreSeparator);
      }
      sb.append(list.get(i));
    }
    return sb.toString();
  }

  /**
   * Clear the cache of any previously-read translation files.  Primarily useful for testing as the
   * translation files should not change during normal execution.
   */
  public void clearCache() {
    this.translationFileFromLanguageCountryMap = null;
    this.localeTranslations = new HashMap<>();
  }
}
