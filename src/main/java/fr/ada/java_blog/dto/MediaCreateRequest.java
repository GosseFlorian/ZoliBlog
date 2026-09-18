package fr.ada.java_blog.dto;

import fr.ada.java_blog.model.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MediaCreateRequest(
        @NotNull(message = "Le type est obligatoire") MediaType type,
        @NotBlank(message = "L'URL est obligatoire") @Size(max = 255, message = "L'URL ne peut pas dépasser 255 caractères") String url) {
}
