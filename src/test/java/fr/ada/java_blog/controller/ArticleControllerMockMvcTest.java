package fr.ada.java_blog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArticleControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRecents_retourne200EtUnTableau() throws Exception {
        mockMvc.perform(get("/articles/recents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(1))));
    }

    @Test
    void getById_existant_retourne200() throws Exception {
        mockMvc.perform(get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titre").value("Article test CI"));
    }

    @Test
    void getById_inexistant_retourne404() throws Exception {
        mockMvc.perform(get("/articles/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_brouillon_retourne404() throws Exception {
        mockMvc.perform(get("/articles/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void ping_retourne200() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("pong"));
    }

    @Test
    void listArticles_retourne200() throws Exception {
        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCategories_retourne200() throws Exception {
        mockMvc.perform(get("/articles/1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(1))));
    }
}