package fr.ada.java_blog.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ada.java_blog.util.JwtTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminArticleMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String bearerToken;

    @BeforeEach
    void loginAndGetToken() throws Exception {
        bearerToken = JwtTestHelper.loginAndGetToken(mockMvc, objectMapper);
    }

    @Test
    void listArticles_retourne200() throws Exception {
        mockMvc.perform(get("/admin/articles")
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(1))));
    }

    @Test
    void getById_existant_retourne200() throws Exception {
        mockMvc.perform(get("/admin/articles/1")
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titre").value("Article test CI"));
    }

    @Test
    void getById_inexistant_retourne404() throws Exception {
        mockMvc.perform(get("/admin/articles/99999")
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void modifierArticle_retourne200() throws Exception {
        String body = """
                {"titre":"Modifié","contenu":"Nouveau contenu","publie":true}
                """;

        mockMvc.perform(put("/admin/articles/1")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Modifié"))
                .andExpect(jsonPath("$.publie").value(true));
    }

    @Test
    void publierArticle_retourne204() throws Exception {
        mockMvc.perform(patch("/admin/articles/2/publier")
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void depublierArticle_retourne204() throws Exception {
        mockMvc.perform(patch("/admin/articles/1/depublier")
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void categoriesArticle_retourne200() throws Exception {
        mockMvc.perform(get("/admin/articles/1/categories")
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(1))));
    }

    @Test
    void commentairesArticle_retourne200() throws Exception {
        mockMvc.perform(get("/admin/articles/1/commentaires")
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(1))));
    }

    @Test
    void remplacerCategories_retourne204() throws Exception {
        String body = """
                {"categorieIds":[1]}
                """;

        mockMvc.perform(put("/admin/articles/1/categories")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    void lierMedia_retourne201() throws Exception {
        MvcResult mediaResult = mockMvc.perform(post("/admin/medias")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"type":"image","url":"https://example.com/img.png"}
                        """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode mediaJson = objectMapper.readTree(mediaResult.getResponse().getContentAsString());
        int mediaId = mediaJson.get("id").asInt();

        mockMvc.perform(post("/admin/articles/1/medias")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"mediaId":%d}
                        """.formatted(mediaId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/articles/1/medias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
