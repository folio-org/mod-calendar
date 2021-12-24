package org.folio.calendar.integration;

import static org.folio.calendar.testutils.DateTimeHandler.isCurrentInstant;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.folio.calendar.domain.dto.ArithmeticRequest;
import org.folio.calendar.testutils.DateTimeHandler;
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
      .body("quotient", is(closeTo(0.75, 0.1)));
  }

  @Test
  void testArithmeticByZero() {
    Response response = ra()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(new ArithmeticRequest().a(3).b(0))
      .post(getRequestUrl(HELLO_API_ROUTE));

    // check status code is 400
    response.then().statusCode(is(HttpStatus.BAD_REQUEST.value()));

    // pull body apart for timestamp only
    JsonPath body = JsonPath.from(response.asString());
    assertThat(
      "Error timestamp is current",
      DateTimeHandler.parseTimestamp(body.get("timestamp")),
      isCurrentInstant()
    );
  }
}
