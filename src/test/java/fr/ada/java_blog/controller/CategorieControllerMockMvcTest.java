package fr.ada.java_blog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategorieControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listCategories_retourne200() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(1))));
    }

    @Test
    void getById_existant_retourne200() throws Exception {
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Java"));
    }

    @Test
    void getById_inexistant_retourne404() throws Exception {
        mockMvc.perform(get("/categories/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void articlesByCategory_existant_retourne200() throws Exception {
        mockMvc.perform(get("/categories/1/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
