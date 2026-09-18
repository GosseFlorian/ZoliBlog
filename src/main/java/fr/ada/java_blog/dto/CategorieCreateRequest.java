package fr.ada.java_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategorieCreateRequest(
        @NotBlank(message = "Le nom est obligatoire") @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères") String nom,
        @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères") String description) {
}
