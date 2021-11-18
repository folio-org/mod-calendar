package org.folio.calendar.controller;

import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.Greeting;
import org.folio.calendar.rest.resource.HelloApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
