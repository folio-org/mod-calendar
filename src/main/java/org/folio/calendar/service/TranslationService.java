package org.folio.calendar.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Log4j2
@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class TranslationService {

  private ResourcePatternResolver resourceResolver;

  /**
   * Get all available translations in the classpath
   * @return a list of available translation files
   */
  public List<ClassPathResource> getAvailableTranslationFiles() {
    try {
      List<ClassPathResource> files = Arrays
        .asList(this.resourceResolver.getResources("classpath:/translations/mod-calendar/*"))
        .stream()
        .filter(Resource::isReadable)
        .map(ClassPathResource.class::cast)
        .collect(Collectors.toList());

      if (files.isEmpty()) {
        throw new IOException("No translation files exist");
      }

      return files;
    } catch (IOException e) {
      log.error("Could not retrieve translation files:");
      log.error(e);
      throw new IllegalStateException("Could not retrieve translation files", e);
    }
  }

  /**
   * Get all of the locales for the current request.  This will respect the Accept-Language header
   * and quality values, as applicable.
   *
   * @return a List of all Locale objects.  Invalid and wildcard Locales will likely return a Locale
   * with empty string/fields or, in some cases, the server's current Locale.
   * @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language
   */
  public List<Locale> getLocales() {
    return Collections.list(
      ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
        .getLocales()
    );
  }
}
