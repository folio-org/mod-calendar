package org.folio.calendar.controller;

import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.folio.calendar.domain.dto.Example;
import org.folio.calendar.rest.resource.HelloApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(value = "/")
public class HelloWorldController implements HelloApi {

  @Override
  public ResponseEntity<Example> getHelloWorld() {
    Example sampleResponse = new Example();
    sampleResponse.setHello("heya!");
    return new ResponseEntity<Example>(sampleResponse, HttpStatus.OK);
  }
}
