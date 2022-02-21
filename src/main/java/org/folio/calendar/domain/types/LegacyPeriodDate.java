package org.folio.calendar.domain.types;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
import lombok.Data;
import org.folio.calendar.exception.ExceptionParameters;
import org.folio.calendar.exception.InvalidDataException;
import org.folio.calendar.i18n.TranslationService;

/**
 * A JSON wrapper for LocalDates that supports serialization/deserialization as midnight GMT timestamps
 *
 * This is needed to support the custom "date" format that the legacy Period-based API uses
 */
@Data
@JsonDeserialize(using = LegacyPeriodDateDeserializer.class)
public class LegacyPeriodDate {

  public static final int ISO_DATE_LENGTH = "2021-01-31".length();
  public static final String LEGACY_DATE_APPEND_STRING = "T00:00:00.000+00:00";

  protected LocalDate value = null;

  /**
   * Create a new LegacyPeriodDate wrapping a given LocalDate
   * @param date the date to wrap
   */
  public LegacyPeriodDate(LocalDate date) {
    this.value = date;
  }

  /**
   * Create a new LegacyPeriodDate from an input string to parse
   * @param translationService the {@link TranslationService TranslationService} to use for formatting an error message, if necessary
   * @param input the string to parse, optionally with T00:00... at the end
   */
  public LegacyPeriodDate(TranslationService translationService, String input) {
    try {
      this.value = LocalDate.parse(input.substring(0, ISO_DATE_LENGTH));
    } catch (RuntimeException e) {
      throw new InvalidDataException(
        e,
        new ExceptionParameters("date", input),
        translationService.format("error.dateInvalid", "userInputtedDateString", input)
      );
    }
  }

  /**
   * Create a new LegacyPeriodDate object wrapping the provided date
   * @param date date to wrap
   * @return wrapped object
   */
  public static LegacyPeriodDate from(LocalDate date) {
    return new LegacyPeriodDate(date);
  }

  public String toString() {
    return this.value.toString();
  }

  @JsonValue
  public String serialize() {
    return this.value.toString() + LEGACY_DATE_APPEND_STRING;
  }
}
