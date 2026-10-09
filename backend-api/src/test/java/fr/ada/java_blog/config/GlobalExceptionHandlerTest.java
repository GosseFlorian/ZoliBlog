package fr.ada.java_blog.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleStatus_retourneMessage() {
    ResponseEntity<Map<String, String>> response =
        handler.handleStatus(new ResponseStatusException(HttpStatus.NOT_FOUND, "Introuvable"));

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Introuvable", response.getBody().get("message"));
  }

  @Test
  void handleIntegrity_pseudoDuplique() {
    var ex = new DataIntegrityViolationException("duplicate key users_pseudo_key (pseudo)");
    ResponseEntity<Map<String, String>> response = handler.handleIntegrity(ex);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertTrue(response.getBody().get("message").contains("pseudo"));
  }

  @Test
  void handleIntegrity_mediaDejaLie() {
    var ex = new DataIntegrityViolationException("articles_medias_unique");
    ResponseEntity<Map<String, String>> response = handler.handleIntegrity(ex);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertTrue(response.getBody().get("message").contains("media"));
  }

  @Test
  void handleGeneric_retourne500() {
    ResponseEntity<Map<String, String>> response =
        handler.handleGeneric(new RuntimeException("boom"));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Erreur interne du serveur", response.getBody().get("message"));
  }
}
