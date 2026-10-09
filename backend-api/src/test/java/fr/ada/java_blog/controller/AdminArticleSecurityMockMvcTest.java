package fr.ada.java_blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class AdminArticleSecurityMockMvcTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private String bearerToken;

  @BeforeEach
  void loginAndGetToken() throws Exception {
    String body =
        """
                {"mail":"alice@example.com","mdp":"demo1234"}
                """;

    MvcResult result =
        mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    bearerToken = json.get("token").asText();
  }

  @Test
  void postAdmin_avecTokenUtilisateurOrdinaire_retourne403() throws Exception {
    String registerBody =
        """
                {"pseudo":"visiteur","mail":"visiteur@example.com","mdp":"motdepasse"}
                """;

    MvcResult registered =
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registerBody))
            .andExpect(status().isCreated())
            .andReturn();

    JsonNode registeredJson = objectMapper.readTree(registered.getResponse().getContentAsString());
    String userToken = registeredJson.get("token").asText();

    String body =
        """
                {"titre":"Hack","contenu":"Sans role admin","userId":1}
                """;

    mockMvc
        .perform(
            post("/admin/articles")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isForbidden());
  }

  @Test
  void postAdmin_sansToken_retourne401() throws Exception {
    String body =
        """
                {"titre":"Hack","contenu":"Sans auth","userId":1}
                """;

    mockMvc
        .perform(post("/admin/articles").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void postAdmin_avecToken_retourne201() throws Exception {
    String body =
        """
                {"titre":"Via test","contenu":"Créé en CI","userId":1}
                """;

    mockMvc
        .perform(
            post("/admin/articles")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated());
  }

  @Test
  void deleteAdmin_avecToken_retourne204() throws Exception {
    String createBody =
        """
                {"titre":"Temp","contenu":"x","userId":1}
                """;
    MvcResult created =
        mockMvc
            .perform(
                post("/admin/articles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + bearerToken)
                    .content(createBody))
            .andExpect(status().isCreated())
            .andReturn();

    JsonNode json = objectMapper.readTree(created.getResponse().getContentAsString());
    int id = json.get("id").asInt();

    mockMvc
        .perform(delete("/admin/articles/" + id).header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isNoContent());
  }
}
