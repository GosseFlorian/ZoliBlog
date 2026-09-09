package fr.ada.java_blog.dto;

import fr.ada.java_blog.model.MediaType;

public record MediaCreateRequest(
        MediaType type,
        String url) {
}
