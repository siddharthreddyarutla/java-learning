package com.example.javaLearning.cors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class corsOriginResourceSharingController {

  @GetMapping("/helloWorld")
  public ResponseEntity<String> helloWorld() {
    log.info("corsOriginResourceSharingController: Request came to the controller");
    return ResponseEntity.ok().header("Access-Control-Allow-Origin", "http://localhost:4200")
        .body("Hello world");
  }
}
