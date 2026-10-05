package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.CommentaireResponse;
import fr.ada.java_blog.mapper.CommentaireMapper;
import fr.ada.java_blog.repository.CommentaireRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/admin/commentaires")
public class AdminCommentaireController {

  private final CommentaireRepository commentaireRepository;

  public AdminCommentaireController(CommentaireRepository commentaireRepository) {
    this.commentaireRepository = commentaireRepository;
  }

  @GetMapping
  public List<CommentaireResponse> all() {
    return commentaireRepository.findAll().stream().map(CommentaireMapper::toResponse).toList();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable int id) {
    if (!commentaireRepository.deleteById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable");
    }
  }
}
