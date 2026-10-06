package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.CommentaireCreateRequest;
import fr.ada.java_blog.dto.CommentaireResponse;
import fr.ada.java_blog.dto.CommentaireUpdateRequest;
import fr.ada.java_blog.service.CommentaireService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentaireController {

  private final CommentaireService commentaireService;

  public CommentaireController(CommentaireService commentaireService) {
    this.commentaireService = commentaireService;
  }

  @GetMapping("/articles/{articleId}/commentaires")
  public List<CommentaireResponse> list(@PathVariable int articleId) {
    return commentaireService.listByArticleId(articleId);
  }

  @PostMapping("/articles/{articleId}/commentaires")
  public ResponseEntity<CommentaireResponse> create(
      @PathVariable int articleId,
      @Valid @RequestBody CommentaireCreateRequest body,
      Authentication authentication) {
    int userIdAuthentifie = Integer.parseInt(authentication.getName());
    CommentaireResponse created = commentaireService.creer(articleId, body, userIdAuthentifie);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping("/commentaires/{id}")
  public CommentaireResponse one(@PathVariable int id) {
    return commentaireService.findById(id);
  }

  @PatchMapping("/commentaires/{id}")
  public CommentaireResponse update(
      @PathVariable int id,
      @Valid @RequestBody CommentaireUpdateRequest body,
      Authentication authentication) {
    int userIdAuthentifie = Integer.parseInt(authentication.getName());
    return commentaireService.modifier(id, body, userIdAuthentifie);
  }

  @DeleteMapping("/commentaires/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id, Authentication authentication) {
    int userIdAuthentifie = Integer.parseInt(authentication.getName());
    commentaireService.supprimer(id, userIdAuthentifie);
    return ResponseEntity.noContent().build();
  }
}
