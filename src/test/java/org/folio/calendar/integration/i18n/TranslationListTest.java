package org.folio.calendar.integration.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import org.folio.calendar.i18n.TranslationService;
import org.folio.calendar.integration.BaseApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TranslationListTest extends BaseApiTest {

  @Autowired
  TranslationService translationService;

  @Test
  void testListTranslation() {
    assertThat("Empty list [] => \"\"", translationService.formatList(Arrays.asList()), is(""));

    assertThat(
      "Single list [A] => \"A\"",
      translationService.formatList(Arrays.asList("A")),
      is("A")
    );

    assertThat(
      "Double list [A B] => \"A and B\"",
      translationService.formatList(Arrays.asList("A", "B")),
      is("A and B")
    );

    assertThat(
      "Triple list [A B C] => \"A, B, and C\"",
      translationService.formatList(Arrays.asList("A", "B", "C")),
      is("A, B, and C")
    );

    assertThat(
      "Quadruple list [A B C D] => \"A, B, C, and D\"",
      translationService.formatList(Arrays.asList("A", "B", "C", "D")),
      is("A, B, C, and D")
    );
  }
}
