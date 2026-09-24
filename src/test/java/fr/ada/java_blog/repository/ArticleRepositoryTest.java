package fr.ada.java_blog.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.ada.java_blog.model.Article;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(ArticleRepository.class)
@Transactional
class ArticleRepositoryTest {

  @Autowired private ArticleRepository articleRepository;

  @Test
  void findRecents_retourneLesArticlesDuSeed() {
    List<Article> recents = articleRepository.findRecents(5);

    assertTrue(recents.size() >= 1);
  }

  @Test
  void findById_existant_retourneArticle() {
    Optional<Article> opt = articleRepository.findById(1);

    assertTrue(opt.isPresent());
    assertEquals("Article test CI", opt.get().getTitre());
  }

  @Test
  void save_assigneUnId() {
    Article nouveau =
        new Article(null, "Nouveau", "Corps", false, LocalDateTime.now(), LocalDateTime.now(), 1);

    Article sauve = articleRepository.save(nouveau);

    assertTrue(sauve.getId() != null && sauve.getId() > 0);
  }

  @Test
  void deleteById_supprimeLaLigne() {
    Article nouveau =
        articleRepository.save(
            new Article(
                null, "A supprimer", "x", false, LocalDateTime.now(), LocalDateTime.now(), 1));

    boolean supprime = articleRepository.deleteById(nouveau.getId());

    assertTrue(supprime);
    assertTrue(articleRepository.findById(nouveau.getId()).isEmpty());
  }
}
