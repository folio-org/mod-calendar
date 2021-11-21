package org.folio.calendar.controller;

import java.math.BigDecimal;
import org.folio.calendar.domain.dto.Arithmetics;
import org.folio.calendar.domain.dto.Greeting;
import org.folio.calendar.rest.resource.HelloApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<Arithmetics> postHello(String xOkapiTenant, Integer a, Integer b) {
    Arithmetics answers = new Arithmetics();
    answers.setSum(a + b);
    answers.setProduct(a * b);
    answers.setQuotient(new BigDecimal(a).divide(new BigDecimal(b)));

    return new ResponseEntity<>(answers, HttpStatus.OK);
  }
}
