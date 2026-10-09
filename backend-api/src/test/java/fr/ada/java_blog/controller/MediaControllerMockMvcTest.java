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
class MediaControllerMockMvcTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void byArticle_sansMedia_retourneTableauVide() throws Exception {
    mockMvc
        .perform(get("/articles/1/medias"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void byId_inexistant_retourne404() throws Exception {
    mockMvc
        .perform(get("/medias/99999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Média introuvable"));
  }
}
