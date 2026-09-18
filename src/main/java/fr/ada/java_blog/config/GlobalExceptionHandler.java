package fr.ada.java_blog.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Réponses d'erreur homogènes — pas de stack trace vers le client (A05).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> champs = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            champs.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> body = Map.of(
                "message", "Données invalides",
                "champs", champs);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleIntegrity(DataIntegrityViolationException ex) {
        String detail = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();
        String message = resolveIntegrityMessage(detail);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleStatus(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : "Erreur";
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("message", message));
    }

    private static String resolveIntegrityMessage(String detail) {
        if (detail == null) {
            return "Conflit de donnees";
        }
        String lower = detail.toLowerCase();
        if (lower.contains("(pseudo)") || lower.contains("users_pseudo")) {
            return "Ce pseudo est deja utilise";
        }
        if (lower.contains("(mail)") || lower.contains("users_mail")) {
            return "Un compte existe deja avec cette adresse mail";
        }
        if (lower.contains("articles_medias")) {
            return "Ce media est deja lie a cet article";
        }
        if (lower.contains("articles_categories")) {
            return "Cette categorie est deja liee a cet article";
        }
        if (lower.contains("violates not-null constraint")) {
            return "Donnees incompletes";
        }
        if (lower.contains("violates foreign key constraint")) {
            return "Reference invalide";
        }
        return "Conflit de donnees";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur interne du serveur"));
    }
}