package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.LoginRequest;
import fr.ada.java_blog.dto.LoginResponse;
import fr.ada.java_blog.dto.RegisterRequest;
import fr.ada.java_blog.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public LoginResponse login(
      @Valid @RequestBody LoginRequest body,
      @RequestHeader(value = "X-Login-Context", required = false) String loginContext) {
    return authService.login(body, loginContext);
  }

  @PostMapping("/register")
  public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest body) {
    LoginResponse response = authService.register(body);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
