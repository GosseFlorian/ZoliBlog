package fr.ada.java_blog.dto;

import fr.ada.java_blog.model.MediaType;

public record MediaResponse(Integer id, MediaType type, String url) {}
