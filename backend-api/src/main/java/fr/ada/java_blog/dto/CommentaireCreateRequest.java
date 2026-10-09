package fr.ada.java_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentaireCreateRequest(
    @NotBlank(message = "Le contenu est obligatoire")
        @Size(
            min = 1,
            max = 2000,
            message = "Le commentaire doit contenir entre 1 et 2000 caractères")
        String contenu,
    @NotNull(message = "Le userId est obligatoire") Integer userId) {}
