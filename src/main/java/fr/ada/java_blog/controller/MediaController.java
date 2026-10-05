package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.MediaResponse;
import fr.ada.java_blog.mapper.MediaMapper;
import fr.ada.java_blog.repository.MediaRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MediaController {

  private final MediaRepository mediaRepository;

  public MediaController(MediaRepository mediaRepository) {
    this.mediaRepository = mediaRepository;
  }

  @GetMapping("/articles/{articleId}/medias")
  public List<MediaResponse> byArticle(@PathVariable int articleId) {
    return mediaRepository.findByArticleId(articleId).stream()
        .map(MediaMapper::toResponse)
        .toList();
  }

  @GetMapping("/medias/{id}")
  public MediaResponse byId(@PathVariable int id) {
    return mediaRepository
        .findById(id)
        .map(MediaMapper::toResponse)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Média introuvable"));
  }
}
