package fr.ada.java_blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class AdminUserMockMvcTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private String bearerToken;

  @BeforeEach
  void loginAndGetToken() throws Exception {
    bearerToken = JwtTestHelper.loginAndGetToken(mockMvc, objectMapper);
  }

  @Test
  void deleteUser_avecArticlesEtCommentaires_retourne204() throws Exception {
    mockMvc
        .perform(delete("/admin/users/2").header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get("/admin/users/2").header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteUser_sansToken_retourne401() throws Exception {
    mockMvc.perform(delete("/admin/users/2")).andExpect(status().isUnauthorized());
  }

  @Test
  void deleteUser_inexistant_retourne404() throws Exception {
    mockMvc
        .perform(delete("/admin/users/99999").header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isNotFound());
  }

  @Test
  void createUser_mailDejaUtilise_retourne409() throws Exception {
    String body =
        """
                {"pseudo":"nouveau_user","mail":"alice@example.com","mdp":"motdepasse"}
                """;

    mockMvc
        .perform(
            post("/admin/users")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Un compte existe deja avec cette adresse mail"));
  }
}
