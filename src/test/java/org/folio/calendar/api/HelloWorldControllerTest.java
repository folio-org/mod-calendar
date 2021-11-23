package org.folio.calendar.api;

import static io.restassured.RestAssured.get;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.folio.calendar.utils.APITestUtils.TENANT_ID;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.response.ValidatableResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class PasswordValidatorControllerApiTest extends BaseApiTest {

  public static final String HELLO_API_ROUTE = "/hello";

  @Test
  void testDefaultHello() {
    ValidatableResponse response = get(HELLO_API_ROUTE).then();
    response.body("hello", equalTo(String.format("Welcome %s!", TENANT_ID)));
    response.assertThat().body(matchesJsonSchemaInClasspath("greeting.yaml"));
  }
}
