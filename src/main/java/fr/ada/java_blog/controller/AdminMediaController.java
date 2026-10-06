package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.MediaCreateRequest;
import fr.ada.java_blog.dto.MediaResponse;
import fr.ada.java_blog.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/medias")
public class AdminMediaController {

  private final MediaService mediaService;

  public AdminMediaController(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @PostMapping
  public ResponseEntity<MediaResponse> create(@Valid @RequestBody MediaCreateRequest body) {
    MediaResponse created = mediaService.creer(body);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    mediaService.supprimer(id);
    return ResponseEntity.noContent().build();
  }
}
