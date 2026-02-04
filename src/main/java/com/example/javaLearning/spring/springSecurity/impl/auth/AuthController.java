package com.example.javaLearning.spring.springSecurity.impl.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthenticationService authenticationService;

  @PostMapping("/register")
  public AuthenticationResponse register(@RequestBody RegisterRequest registerRequest) {
    return authenticationService.register(registerRequest);
  }

  @PostMapping("/authenticate")
  public AuthenticationResponse authenticate(
      @RequestBody AuthenticationRequest authenticationRequest) {
    return authenticationService.authenticate(authenticationRequest);
  }
}
