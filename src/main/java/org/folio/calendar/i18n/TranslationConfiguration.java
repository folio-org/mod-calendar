package org.folio.calendar.i18n;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * An {@link org.springframework.context.annotation.Configuration Configuration} bean to hold the
 * location of the current translation directory.  By default, this is set from the application
 * property {@code folio.translationDirectory} in your {@code application.properties} or
 * {@code application.yaml} file.  For example, to find classpath directory translations/foo, use
 * {@code /translations/foo/}.  Note that the proceeding and trailing are required.
 */
@Configuration
public class TranslationConfiguration {

  @Getter
  @Setter
  @Value("${folio.translationDirectory}")
  private String translationDirectory;

  private Environment env;

  @Autowired
  public TranslationConfiguration(
    @Value("${folio.translationDirectory}") String translationDirectory,
    Environment env
  ) {
    this.translationDirectory = translationDirectory;
    this.env = env;
  }

  /**
   * Reset the translation directory
   */
  public void reset() {
    this.setTranslationDirectory(env.getProperty("folio.translationDirectory"));
  }
}
