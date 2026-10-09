package fr.ada.java_blog.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import fr.ada.java_blog.dto.ArticleResponse;
import fr.ada.java_blog.model.Article;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ArticleMapperTest {

  @Test
  void toResponse_copieLesChampsPublics() {
    LocalDateTime date = LocalDateTime.of(2026, 3, 1, 10, 0);

    Article article = new Article(42, "Titre test", "Contenu test", true, date, date, 99);

    ArticleResponse response = ArticleMapper.toResponse(article);

    assertEquals(42, response.id());
    assertEquals("Titre test", response.titre());
    assertEquals("Contenu test", response.contenu());
    assertEquals(true, response.publie());
    assertEquals(date, response.date());
  }

  @Test
  void toResponse_neExposePasUserIdNiUpdate() {
    Article article = new Article(1, "A", "B", false, LocalDateTime.now(), LocalDateTime.now(), 5);

    ArticleResponse response = ArticleMapper.toResponse(article);

    assertFalse(response.publie());
  }
}
