package fr.ada.java_blog.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentaireControllerMockMvcTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private String bearerToken;

  @BeforeEach
  void loginAndGetToken() throws Exception {
    bearerToken = JwtTestHelper.loginAndGetToken(mockMvc, objectMapper);
  }

  @Test
  void getCommentaires_articleExistant_retourne200() throws Exception {
    mockMvc
        .perform(get("/articles/1/commentaires"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(1))))
        .andExpect(jsonPath("$[0].pseudo").value("alice_dev"));
  }

  @Test
  void getCommentaires_articleInexistant_retourne404() throws Exception {
    mockMvc.perform(get("/articles/99999/commentaires")).andExpect(status().isNotFound());
  }

  @Test
  void postCommentaire_sansToken_retourne401() throws Exception {
    String body =
        """
                                {"contenu":"Via MockMvc","userId":1}
                                """;

    mockMvc
        .perform(
            post("/articles/1/commentaires").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void postCommentaire_avecToken_retourne201() throws Exception {
    String body =
        """
                                {"contenu":"Via MockMvc","userId":1}
                                """;

    mockMvc
        .perform(
            post("/articles/1/commentaires")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.contenu").value("Via MockMvc"))
        .andExpect(jsonPath("$.pseudo").value("alice_dev"))
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  void postCommentaire_articleInexistant_retourne404() throws Exception {
    String body =
        """
                                {"contenu":"Orphelin","userId":1}
                                """;

    mockMvc
        .perform(
            post("/articles/99999/commentaires")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }
}
