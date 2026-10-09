package fr.ada.java_blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "L'adresse mail est obligatoire")
        @Email(message = "Format de mail invalide")
        String mail,
    @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(
            min = 8,
            max = 128,
            message = "Le mot de passe doit contenir entre 8 et 128 caractères")
        String mdp) {}
