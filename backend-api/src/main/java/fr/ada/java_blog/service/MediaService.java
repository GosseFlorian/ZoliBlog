package fr.ada.java_blog.service;

import fr.ada.java_blog.dto.MediaCreateRequest;
import fr.ada.java_blog.dto.MediaResponse;
import fr.ada.java_blog.mapper.MediaMapper;
import fr.ada.java_blog.model.Media;
import fr.ada.java_blog.repository.MediaRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MediaService {

  private final MediaRepository mediaRepository;

  public MediaService(MediaRepository mediaRepository) {
    this.mediaRepository = mediaRepository;
  }

  public List<MediaResponse> findByArticleId(int articleId) {
    return mediaRepository.findByArticleId(articleId).stream()
        .map(MediaMapper::toResponse)
        .toList();
  }

  public MediaResponse findById(int id) {
    return mediaRepository
        .findById(id)
        .map(MediaMapper::toResponse)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Média introuvable"));
  }

  public MediaResponse creer(MediaCreateRequest body) {
    Media media = new Media(null, body.type(), body.url());
    Media sauve = mediaRepository.save(media);
    return MediaMapper.toResponse(sauve);
  }

  public void supprimer(int id) {
    if (!mediaRepository.deleteById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Média introuvable");
    }
  }
}
