package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.MediaResponse;
import fr.ada.java_blog.service.MediaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaController {

  private final MediaService mediaService;

  public MediaController(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @GetMapping("/articles/{articleId}/medias")
  public List<MediaResponse> byArticle(@PathVariable int articleId) {
    return mediaService.findByArticleId(articleId);
  }

  @GetMapping("/medias/{id}")
  public MediaResponse byId(@PathVariable int id) {
    return mediaService.findById(id);
  }
}
