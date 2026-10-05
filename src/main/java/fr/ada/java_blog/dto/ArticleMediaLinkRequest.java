package fr.ada.java_blog.dto;

import jakarta.validation.constraints.NotNull;

public record ArticleMediaLinkRequest(
    @NotNull(message = "Le mediaId est obligatoire") Integer mediaId) {}
