package org.folio.calendar.api;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

/**
 * A good example of a test class within the application.
 * Note that the endpoint being tested is not implemented in any controller but instead built into Spring Boot.
 *
 * The only Okapi requirement for /admin/health is to respond with a 200 status code.
 * @see https://wiki.folio.org/display/DD/Back+End+Module+Health+Check+Protocol
 */
@ActiveProfiles("test")
class OkapiHealthTest extends BaseApiTest {

  public static final String HEALTH_API_ROUTE = "/admin/health/";

  @Test
  void testOkSTatusReport() {
    ra(false) // must not validate as /admin/health is not in our schema
      .get(getRequestUrl(HEALTH_API_ROUTE))
      .then()
      .statusCode(is(HttpStatus.OK.value()));
  }
}
