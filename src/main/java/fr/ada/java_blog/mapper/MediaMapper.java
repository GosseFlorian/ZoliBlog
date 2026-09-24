package fr.ada.java_blog.mapper;

import fr.ada.java_blog.dto.MediaResponse;
import fr.ada.java_blog.model.Media;

public final class MediaMapper {

  private MediaMapper() {}

  public static MediaResponse toResponse(Media media) {
    return new MediaResponse(media.getId(), media.getType(), media.getUrl());
  }
}
