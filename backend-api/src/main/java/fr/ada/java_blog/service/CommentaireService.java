package fr.ada.java_blog.service;

import fr.ada.java_blog.dto.CommentaireCreateRequest;
import fr.ada.java_blog.dto.CommentaireResponse;
import fr.ada.java_blog.dto.CommentaireUpdateRequest;
import fr.ada.java_blog.mapper.CommentaireMapper;
import fr.ada.java_blog.repository.ArticleRepository;
import fr.ada.java_blog.repository.CommentaireRepository;
import fr.ada.java_blog.util.InputSanitizer;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommentaireService {

  private final CommentaireRepository commentaireRepository;
  private final ArticleRepository articleRepository;

  public CommentaireService(
      CommentaireRepository commentaireRepository, ArticleRepository articleRepository) {
    this.commentaireRepository = commentaireRepository;
    this.articleRepository = articleRepository;
  }

  public List<CommentaireResponse> listByArticleId(int articleId) {
    verifierArticlePublieExiste(articleId);
    return commentaireRepository.findByArticleId(articleId).stream()
        .map(CommentaireMapper::toResponse)
        .toList();
  }

  public CommentaireResponse creer(
      int articleId, CommentaireCreateRequest body, int userIdAuthentifie) {
    verifierArticlePublieExiste(articleId);
    verifierUserIdCorrespond(body.userId(), userIdAuthentifie);
    String contenu = sanitizeContenu(body.contenu());
    var saved = commentaireRepository.save(articleId, contenu, body.userId());
    return CommentaireMapper.toResponse(saved);
  }

  public CommentaireResponse findById(int id) {
    return commentaireRepository
        .findById(id)
        .map(CommentaireMapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable"));
  }

  public CommentaireResponse modifier(
      int id, CommentaireUpdateRequest body, int userIdAuthentifie) {
    var commentaire =
        commentaireRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable"));

    verifierUserIdCorrespond(commentaire.getUserId(), userIdAuthentifie);

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

  public void supprimer(int id, int userIdAuthentifie) {
    var commentaire =
        commentaireRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable"));

    verifierUserIdCorrespond(commentaire.getUserId(), userIdAuthentifie);

    if (!commentaireRepository.deleteById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable");
    }
  }

  public List<CommentaireResponse> findAllAdmin() {
    return commentaireRepository.findAll().stream().map(CommentaireMapper::toResponse).toList();
  }

  public void supprimerAdmin(int id) {
    if (!commentaireRepository.deleteById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable");
    }
  }

  private void verifierArticlePublieExiste(int articleId) {
    articleRepository
        .findPublishedById(articleId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable"));
  }

  private static void verifierUserIdCorrespond(Integer userId, int userIdAuthentifie) {
    if (!Objects.equals(userId, userIdAuthentifie)) {
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
