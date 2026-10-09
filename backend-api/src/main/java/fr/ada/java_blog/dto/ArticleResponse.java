package fr.ada.java_blog.dto;

import java.time.LocalDateTime;

public record ArticleResponse(
    Integer id, String titre, String contenu, boolean publie, LocalDateTime date) {}
