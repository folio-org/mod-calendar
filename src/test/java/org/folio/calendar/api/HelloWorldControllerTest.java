package org.folio.calendar.api;

import static org.folio.calendar.utils.APITestUtils.TENANT_ID;
import static org.hamcrest.Matchers.*;

import org.folio.calendar.domain.dto.ArithmeticRequest;
import org.folio.calendar.domain.dto.ErrorResponse.ErrorCodeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class HelloWorldControllerTest extends BaseApiTest {

  public static final String HELLO_API_ROUTE = "/hello";

  @Test
  void testDefaultSalutation() {
    ra()
      .get(getRequestUrl(HELLO_API_ROUTE))
      .then()
      .statusCode(is(HttpStatus.OK.value()))
      .body("hello", is(equalTo(String.format("Welcome %s!", TENANT_ID))));
  }

  @Test
  void testCustomSalutation() {
    ra()
      .queryParam("salutation", "Bonjour")
      .get(getRequestUrl(HELLO_API_ROUTE))
      .then()
      .statusCode(is(HttpStatus.OK.value()))
      .body("hello", is(equalTo(String.format("Bonjour %s!", TENANT_ID))));
  }

  @Test
  void testArithmeticSuccess() {
    ra()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(new ArithmeticRequest().a(3).b(4))
      .post(getRequestUrl(HELLO_API_ROUTE))
      .then()
      .statusCode(is(HttpStatus.OK.value()))
      .body("product", is(12))
      .body("sum", is(7))
      .body("quotient", is(closeTo(0.75, 1.0)));
  }

  @Test
  void testArithmeticByZero() {
    ra()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(new ArithmeticRequest().a(3).b(0))
      .post(getRequestUrl(HELLO_API_ROUTE))
      .then()
      .statusCode(is(HttpStatus.BAD_REQUEST.value()))
      .body("error_code", is(equalTo(ErrorCodeEnum.HELLO_POST_BAD_ARITHMETIC.getValue())));
  }
}
