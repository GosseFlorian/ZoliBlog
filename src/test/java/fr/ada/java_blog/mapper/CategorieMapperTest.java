package fr.ada.java_blog.mapper;

import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.model.Categorie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategorieMapperTest {

    @Test
    void toResponse_copieIdNomEtDescription() {
        Categorie categorie = new Categorie(3, "Spring", "Articles Spring Boot");

        CategorieResponse response = CategorieMapper.toResponse(categorie);

        assertEquals(3, response.id());
        assertEquals("Spring", response.nom());
        assertEquals("Articles Spring Boot", response.description());
    }
}
