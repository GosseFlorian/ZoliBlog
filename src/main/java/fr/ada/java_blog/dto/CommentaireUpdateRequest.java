package fr.ada.java_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentaireUpdateRequest(
    @NotBlank(message = "Le contenu est obligatoire")
        @Size(
            min = 1,
            max = 2000,
            message = "Le commentaire doit contenir entre 1 et 2000 caractères")
        String contenu) {}
