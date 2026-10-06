package fr.ada.java_blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import fr.ada.java_blog.dto.CommentaireCreateRequest;
import fr.ada.java_blog.dto.CommentaireUpdateRequest;
import fr.ada.java_blog.model.Article;
import fr.ada.java_blog.model.Commentaire;
import fr.ada.java_blog.repository.ArticleRepository;
import fr.ada.java_blog.repository.CommentaireRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CommentaireServiceTest {

  @Mock private CommentaireRepository commentaireRepository;
  @Mock private ArticleRepository articleRepository;

  private CommentaireService commentaireService;

  @BeforeEach
  void setUp() {
    commentaireService = new CommentaireService(commentaireRepository, articleRepository);
  }

  @Test
  void listByArticleId_articleInexistant_lance404() {
    when(articleRepository.findPublishedById(99)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> commentaireService.listByArticleId(99));
  }

  @Test
  void creer_userIdIncorrect_lance403() {
    when(articleRepository.findPublishedById(1)).thenReturn(Optional.of(articlePublie(1)));
    CommentaireCreateRequest body = new CommentaireCreateRequest("Hello", 2);
    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> commentaireService.creer(1, body, 1));
    assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
  }

  @Test
  void creer_contenuRefuse_lance400() {
    when(articleRepository.findPublishedById(1)).thenReturn(Optional.of(articlePublie(1)));
    CommentaireCreateRequest body = new CommentaireCreateRequest("DROP TABLE users;", 1);
    assertThrows(ResponseStatusException.class, () -> commentaireService.creer(1, body, 1));
  }

  @Test
  void modifier_auteurDifferent_lance403() {
    Commentaire commentaire = new Commentaire(1, "x", 2, 1, LocalDateTime.now());
    when(commentaireRepository.findById(1)).thenReturn(Optional.of(commentaire));

    assertThrows(
        ResponseStatusException.class,
        () -> commentaireService.modifier(1, new CommentaireUpdateRequest("y"), 1));
  }

  @Test
  void findAllAdmin_retourneListe() {
    when(commentaireRepository.findAll())
        .thenReturn(List.of(new Commentaire(1, "c", 1, 1, LocalDateTime.now())));
    assertEquals(1, commentaireService.findAllAdmin().size());
  }

  @Test
  void supprimerAdmin_inexistant_lance404() {
    when(commentaireRepository.deleteById(99)).thenReturn(false);
    assertThrows(ResponseStatusException.class, () -> commentaireService.supprimerAdmin(99));
  }

  private static Article articlePublie(int id) {
    return new Article(id, "T", "C", true, LocalDateTime.now(), LocalDateTime.now(), 1);
  }
}
