package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.MediaCreateRequest;
import fr.ada.java_blog.dto.MediaResponse;
import fr.ada.java_blog.mapper.MediaMapper;
import fr.ada.java_blog.model.Media;
import fr.ada.java_blog.repository.MediaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/admin/medias")
public class AdminMediaController {

  private final MediaRepository mediaRepository;

  public AdminMediaController(MediaRepository mediaRepository) {
    this.mediaRepository = mediaRepository;
  }

  @PostMapping
  public ResponseEntity<MediaResponse> create(@Valid @RequestBody MediaCreateRequest body) {
    Media media = new Media(null, body.type(), body.url());
    Media sauve = mediaRepository.save(media);
    return ResponseEntity.status(HttpStatus.CREATED).body(MediaMapper.toResponse(sauve));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    if (!mediaRepository.deleteById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Média introuvable");
    }
    return ResponseEntity.noContent().build();
  }
}
