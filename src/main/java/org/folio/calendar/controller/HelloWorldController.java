package org.folio.calendar.controller;

import io.swagger.annotations.ApiParam;
import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.Arithmetics;
import org.folio.calendar.domain.dto.Greeting;
import org.folio.calendar.domain.dto.InvalidParameterError;
import org.folio.calendar.rest.resource.HelloApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(value = "/")
public class HelloWorldController implements HelloApi {

  @Override
  public ResponseEntity<Greeting> getHelloWorld(String tenantId, String salutation) {
    Greeting sampleResponse = new Greeting();
    sampleResponse.setHello(salutation + " " + tenantId + "!");
    return new ResponseEntity<>(sampleResponse, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Arithmetics> postHello(
    @ApiParam(value = "X-Okapi-Tenant Value", required = true) @RequestHeader(
      value = "x-okapi-tenant",
      required = true
    ) String xOkapiTenant,
    @NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(
      value = "a",
      required = true
    ) Integer a,
    @NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(
      value = "b",
      required = true
    ) Integer b
  ) {
    Arithmetics answers = new Arithmetics();
    answers.setSum(a + b);
    answers.setProduct(a * b);
    // try {
    answers.setQuotient(new BigDecimal(a).divide(new BigDecimal(b)));
    // } catch (ArithmeticException e) {
    //   var error = new InvalidParameterError();
    //   error.setError(""); // ERROR TODO
    // }

    return new ResponseEntity<>(answers, HttpStatus.OK);
  }
}
