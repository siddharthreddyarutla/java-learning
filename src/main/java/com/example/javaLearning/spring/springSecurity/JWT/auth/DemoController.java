package com.example.javaLearning.spring.springSecurity.JWT.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DemoController {

  @GetMapping("/get")
  public String greet() {
    return "Hello, greet from secure endpoint";
  }
}
