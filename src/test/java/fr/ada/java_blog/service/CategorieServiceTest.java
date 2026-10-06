package fr.ada.java_blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import fr.ada.java_blog.dto.CategorieCreateRequest;
import fr.ada.java_blog.dto.CategorieUpdateRequest;
import fr.ada.java_blog.model.Article;
import fr.ada.java_blog.model.Categorie;
import fr.ada.java_blog.repository.CategorieRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CategorieServiceTest {

  @Mock private CategorieRepository categorieRepository;

  private CategorieService categorieService;

  @BeforeEach
  void setUp() {
    categorieService = new CategorieService(categorieRepository);
  }

  @Test
  void findById_inexistant_lance404() {
    when(categorieRepository.findById(99)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> categorieService.findById(99));
  }

  @Test
  void creer_retourneCategorie() {
    when(categorieRepository.save(any(Categorie.class)))
        .thenAnswer(
            inv -> {
              Categorie c = inv.getArgument(0);
              c.setId(3);
              return c;
            });

    var result = categorieService.creer(new CategorieCreateRequest("Java", "Desc"));
    assertEquals(3, result.id());
    assertEquals("Java", result.nom());
  }

  @Test
  void modifier_ok_metAJour() {
    Categorie categorie = new Categorie(1, "Old", "D");
    when(categorieRepository.findById(1)).thenReturn(Optional.of(categorie));
    when(categorieRepository.updateById(anyInt(), any(Categorie.class))).thenReturn(true);

    var result = categorieService.modifier(1, new CategorieUpdateRequest("New", "Desc2"));
    assertEquals("New", result.nom());
  }

  @Test
  void supprimer_inexistant_lance404() {
    when(categorieRepository.deleteById(99)).thenReturn(false);
    assertThrows(ResponseStatusException.class, () -> categorieService.supprimer(99));
  }

  @Test
  void articlesPubliesByCategorieId_retourneArticles() {
    Article article = new Article(1, "T", "C", true, LocalDateTime.now(), LocalDateTime.now(), 1);
    when(categorieRepository.findArticlesPubliesByCategorieId(2)).thenReturn(List.of(article));

    assertEquals(1, categorieService.articlesPubliesByCategorieId(2).size());
  }
}
