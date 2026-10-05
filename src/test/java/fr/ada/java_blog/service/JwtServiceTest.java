package fr.ada.java_blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.ada.java_blog.model.User;
import fr.ada.java_blog.model.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService("test-jwt-secret-minimum-32-characters", 3_600_000L);
  }

  @Test
  void generateTokenPuisParseToken_retourneUserId() {
    User user = new User(1, "alice_dev", "alice@example.com", "hash", UserRole.ADMIN);

    String token = jwtService.generateToken(user);
    Claims claims = jwtService.parseToken(token);

    assertEquals("1", claims.getSubject());
    assertEquals("alice_dev", claims.get("pseudo", String.class));
    assertEquals("ADMIN", claims.get("role", String.class));
    assertEquals(1, jwtService.extractUserId(claims));
  }

  @Test
  void parseToken_invalide_lanceException() {
    assertThrows(Exception.class, () -> jwtService.parseToken("token.bidon"));
  }
}
