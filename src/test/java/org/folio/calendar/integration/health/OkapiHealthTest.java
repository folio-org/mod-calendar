package org.folio.calendar.integration.health;

import static org.hamcrest.Matchers.is;

import org.folio.calendar.integration.BaseApiTest;
import org.folio.calendar.integration.ValidationSchema;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * A good example of a test class within the application.
 * Note that the endpoint being tested is not implemented in any controller but instead built into Spring Boot.
 *
 * The only Okapi requirement for /admin/health is to respond with a 200 status code.
 * @see https://wiki.folio.org/display/DD/Back+End+Module+Health+Check+Protocol
 */
class OkapiHealthTest extends BaseApiTest {

  public static final String HEALTH_API_ROUTE = "/admin/health/";

  @Test
  void testOkStatusReport() {
    ra(ValidationSchema.NONE) // must not validate as /admin/health is not in our schema
      .get(getRequestUrl(HEALTH_API_ROUTE))
      .then()
      .statusCode(is(HttpStatus.OK.value()));
  }
}
