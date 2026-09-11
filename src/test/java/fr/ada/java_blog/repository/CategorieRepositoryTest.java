package fr.ada.java_blog.repository;

import fr.ada.java_blog.model.Categorie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CategorieRepositoryTest {

    @Autowired
    private CategorieRepository categorieRepository;

    @Test
    void findById_existant_retourneCategorie() {
        Optional<Categorie> categorie = categorieRepository.findById(1);

        assertTrue(categorie.isPresent());
        assertEquals("Java", categorie.get().getNom());
    }

    @Test
    void findByArticleId_retourneCategoriesLiees() {
        List<Categorie> categories = categorieRepository.findByArticleId(1);

        assertFalse(categories.isEmpty());
        assertEquals("Java", categories.get(0).getNom());
    }

    @Test
    void findArticlesPubliesByCategorieId_retourneArticles() {
        assertFalse(categorieRepository.findArticlesPubliesByCategorieId(1).isEmpty());
    }
}
