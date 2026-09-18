package fr.ada.java_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArticleCreateRequest(
        @NotBlank(message = "Le titre est obligatoire") @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères") String titre,
        @NotBlank(message = "Le contenu est obligatoire") String contenu,
        @NotNull(message = "Le userId est obligatoire") Integer userId) {
}
