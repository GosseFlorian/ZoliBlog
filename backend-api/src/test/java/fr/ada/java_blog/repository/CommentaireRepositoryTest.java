package fr.ada.java_blog.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.ada.java_blog.model.Commentaire;
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
@Import(CommentaireRepository.class)
@Transactional
class CommentaireRepositoryTest {

  @Autowired private CommentaireRepository commentaireRepository;

  @Test
  void findByArticleId_retourneLeSeed() {
    List<Commentaire> liste = commentaireRepository.findByArticleId(1);

    assertTrue(liste.size() >= 1);
    assertEquals("Commentaire test", liste.get(0).getContenu());
    assertEquals("alice_dev", liste.get(0).getPseudo());
  }

  @Test
  void save_assigneUnId() {
    Commentaire saved = commentaireRepository.save(1, "Nouveau commentaire", 1);

    assertTrue(saved.getId() != null && saved.getId() > 0);
    assertEquals("Nouveau commentaire", saved.getContenu());
  }

  @Test
  void deleteById_supprimeLaLigne() {
    Commentaire saved = commentaireRepository.save(1, "À supprimer", 1);

    boolean ok = commentaireRepository.deleteById(saved.getId());

    assertTrue(ok);
    assertTrue(commentaireRepository.findById(saved.getId()).isEmpty());
  }

  @Test
  void findById_inexistant_retourneVide() {
    Optional<Commentaire> opt = commentaireRepository.findById(99999);

    assertTrue(opt.isEmpty());
  }
}
