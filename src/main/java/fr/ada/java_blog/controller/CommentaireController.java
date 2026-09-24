package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.CommentaireCreateRequest;
import fr.ada.java_blog.dto.CommentaireResponse;
import fr.ada.java_blog.dto.CommentaireUpdateRequest;
import fr.ada.java_blog.mapper.CommentaireMapper;
import fr.ada.java_blog.repository.ArticleRepository;
import fr.ada.java_blog.repository.CommentaireRepository;
import fr.ada.java_blog.util.InputSanitizer;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CommentaireController {

  private final CommentaireRepository commentaireRepository;
  private final ArticleRepository articleRepository;

  public CommentaireController(
      CommentaireRepository commentaireRepository, ArticleRepository articleRepository) {
    this.commentaireRepository = commentaireRepository;
    this.articleRepository = articleRepository;
  }

  @GetMapping("/articles/{articleId}/commentaires")
  public List<CommentaireResponse> list(@PathVariable int articleId) {
    verifierArticleExiste(articleId);
    return commentaireRepository.findByArticleId(articleId).stream()
        .map(CommentaireMapper::toResponse)
        .toList();
  }

  @PostMapping("/articles/{articleId}/commentaires")
  public ResponseEntity<CommentaireResponse> create(
      @PathVariable int articleId,
      @Valid @RequestBody CommentaireCreateRequest body,
      Authentication authentication) {
    verifierArticleExiste(articleId);
    verifierUserIdCorrespondAuToken(body.userId(), authentication);
    String contenu = sanitizeContenu(body.contenu());
    var saved = commentaireRepository.save(articleId, contenu, body.userId());
    return ResponseEntity.status(HttpStatus.CREATED).body(CommentaireMapper.toResponse(saved));
  }

  @GetMapping("/commentaires/{id}")
  public CommentaireResponse one(@PathVariable int id) {
    return commentaireRepository
        .findById(id)
        .map(CommentaireMapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable"));
  }

  @PatchMapping("/commentaires/{id}")
  public CommentaireResponse update(
      @PathVariable int id,
      @Valid @RequestBody CommentaireUpdateRequest body,
      Authentication authentication) {
    var commentaire =
        commentaireRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable"));

    verifierUserIdCorrespondAuToken(commentaire.getUserId(), authentication);

    String contenu = sanitizeContenu(body.contenu());
    if (!commentaireRepository.updateById(id, contenu)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable");
    }

    var misAJour =
        commentaireRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable"));

    return CommentaireMapper.toResponse(misAJour);
  }

  private void verifierArticleExiste(int articleId) {
    articleRepository
        .findPublishedById(articleId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable"));
  }

  @DeleteMapping("/commentaires/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id, Authentication authentication) {
    var commentaire =
        commentaireRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable"));

    verifierUserIdCorrespondAuToken(commentaire.getUserId(), authentication);

    if (!commentaireRepository.deleteById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable");
    }

    return ResponseEntity.noContent().build();
  }

  /**
   * Le principal posé par JwtAuthFilter est le userId (subject du JWT) sous forme de String. On
   * refuse si le userId attendu (celui du corps pour create, celui de l'auteur en base pour update)
   * ne correspond pas à celui du token — empêche de poster ou modifier "au nom" d'un autre
   * utilisateur via un appel direct à l'API.
   */
  private void verifierUserIdCorrespondAuToken(Integer userId, Authentication authentication) {
    int userIdDuToken = Integer.parseInt(authentication.getName());
    if (!Objects.equals(userId, userIdDuToken)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Le userId envoyé ne correspond pas à l'utilisateur authentifié");
    }
  }

  private static String sanitizeContenu(String contenu) {
    if (InputSanitizer.looksLikeSqlInjection(contenu)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contenu refusé");
    }
    return InputSanitizer.stripDangerousHtml(contenu);
  }
}
