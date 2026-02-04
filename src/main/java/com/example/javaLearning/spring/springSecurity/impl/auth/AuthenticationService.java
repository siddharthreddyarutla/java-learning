package com.example.javaLearning.spring.springSecurity.impl.auth;

import com.example.javaLearning.spring.springSecurity.impl.ROLE;
import com.example.javaLearning.spring.springSecurity.impl.User;
import com.example.javaLearning.spring.springSecurity.impl.UserRepository;
import com.example.javaLearning.spring.springSecurity.impl.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest registerRequest) {

    User user = User.builder().firstname(registerRequest.getFirstname())
        .lastname(registerRequest.getLastname()).email(registerRequest.getEmail())
        .password(passwordEncoder.encode(registerRequest.getPassword())).role(ROLE.USER).build();

    userRepository.save(user);

    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
    authenticationResponse.setToken(
        jwtService.generateToken(Map.of("name", (user.getFirstname() + user.getLastname())), user));
    return authenticationResponse;
  }

  public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
            authenticationRequest.getPassword()));

    Optional<User> user = userRepository.findByEmail(authenticationRequest.getEmail());

    String token = null;
    if (user.isPresent()) {
      token = jwtService.generateToken(user.get());
    }

    return AuthenticationResponse.builder().token(token).build();
  }
}
