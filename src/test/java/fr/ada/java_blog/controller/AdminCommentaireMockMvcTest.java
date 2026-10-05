package fr.ada.java_blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminCommentaireMockMvcTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private String bearerToken;

  @BeforeEach
  void loginAndGetToken() throws Exception {
    bearerToken = JwtTestHelper.loginAndGetToken(mockMvc, objectMapper);
  }

  @Test
  void getCommentairesBrouillon_avecTokenAdmin_retourne200() throws Exception {
    mockMvc
        .perform(
            get("/admin/articles/2/commentaires").header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isOk());
  }

  @Test
  void deleteCommentaire_sansToken_retourne401() throws Exception {
    mockMvc.perform(delete("/admin/commentaires/1")).andExpect(status().isUnauthorized());
  }

  @Test
  void deleteCommentaire_avecToken_retourne204() throws Exception {
    // Créer un commentaire à supprimer
    String createBody =
        """
                {"contenu":"Temporaire","userId":1}
                """;
    MvcResult created =
        mockMvc
            .perform(
                post("/articles/1/commentaires")
                    .header("Authorization", "Bearer " + bearerToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createBody))
            .andExpect(status().isCreated())
            .andReturn();

    JsonNode json = objectMapper.readTree(created.getResponse().getContentAsString());
    int id = json.get("id").asInt();

    mockMvc
        .perform(
            delete("/admin/commentaires/" + id).header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteCommentaire_inexistant_retourne404() throws Exception {
    mockMvc
        .perform(
            delete("/admin/commentaires/99999").header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isNotFound());
  }
}
