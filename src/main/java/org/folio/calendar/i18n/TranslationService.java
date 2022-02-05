package org.folio.calendar.i18n;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TranslationService {

  private final ResourcePatternResolver resourceResolver;

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
        TranslationFile.buildLanguageCountryPatternMap(this.resourceResolver);
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
    return Collections.list(
      ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
        .getLocales()
    );
  }

  /**
   * Get the best {@link TranslationMap TranslationMap} for the current language(s).
   * This will respect the Accept-Language header and quality values, as applicable.
   *
   * @return the best TranslationMap
   * @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language
   */
  public TranslationMap getCurrentTranslation() {
    // return the first one that is a good match
    for (Locale locale : this.getCurrentLocales()) {
      TranslationMap correspondingMap = this.getTranslation(locale);
      if (correspondingMap.getQuality() != TranslationMatchQuality.NO_MATCH) {
        return correspondingMap;
      }
    }

    return this.getDefaultTranslation();
  }
}
