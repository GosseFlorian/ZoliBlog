package fr.ada.java_blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ada.java_blog.util.JwtTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminCategorieMockMvcTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private String bearerToken;

  @BeforeEach
  void loginAndGetToken() throws Exception {
    bearerToken = JwtTestHelper.loginAndGetToken(mockMvc, objectMapper);
  }

  @Test
  void deleteCategorie_lieeAUnArticle_retourne204() throws Exception {
    mockMvc
        .perform(delete("/admin/categories/1").header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/categories/1")).andExpect(status().isNotFound());
  }

  @Test
  void deleteCategorie_sansToken_retourne401() throws Exception {
    mockMvc.perform(delete("/admin/categories/1")).andExpect(status().isUnauthorized());
  }

  @Test
  void deleteCategorie_inexistante_retourne404() throws Exception {
    mockMvc
        .perform(delete("/admin/categories/99999").header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isNotFound());
  }
}
