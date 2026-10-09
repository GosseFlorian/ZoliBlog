package fr.ada.java_blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerMockMvcTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void getById_existant_retourne200() throws Exception {
    mockMvc
        .perform(get("/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.pseudo").value("alice_dev"))
        .andExpect(jsonPath("$.mail").value("alice@example.com"));
  }

  @Test
  void getById_inexistant_retourne404() throws Exception {
    mockMvc
        .perform(get("/users/99999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Utilisateur introuvable"));
  }
}
