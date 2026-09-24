package fr.ada.java_blog.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
class AdminUserControllerMockMvcTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private String bearerToken;

  @BeforeEach
  void loginAndGetToken() throws Exception {
    bearerToken = JwtTestHelper.loginAndGetToken(mockMvc, objectMapper);
  }

  @Test
  void listUsers_retourne200() throws Exception {
    mockMvc
        .perform(get("/admin/users").header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(2))));
  }

  @Test
  void getById_existant_retourne200() throws Exception {
    mockMvc
        .perform(get("/admin/users/1").header("Authorization", "Bearer " + bearerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pseudo").value("alice_dev"));
  }
}
