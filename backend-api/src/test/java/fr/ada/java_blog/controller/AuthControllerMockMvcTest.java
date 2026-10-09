package fr.ada.java_blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerMockMvcTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void login_identifiantsValides_retourneToken() throws Exception {
    String body =
        """
                {"mail":"alice@example.com","mdp":"demo1234"}
                """;

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty())
        .andExpect(jsonPath("$.pseudo").value("alice_dev"))
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.role").value("ADMIN"));
  }

  @Test
  void login_adminContext_avecRoleUser_retourne403() throws Exception {
    String body =
        """
                {"mail":"bob@example.com","mdp":"demo1234"}
                """;

    mockMvc
        .perform(
            post("/auth/login")
                .header("X-Login-Context", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Acces reserve aux administrateurs."));
  }

  @Test
  void login_adminContext_avecRoleAdmin_retourneToken() throws Exception {
    String body =
        """
                {"mail":"alice@example.com","mdp":"demo1234"}
                """;

    mockMvc
        .perform(
            post("/auth/login")
                .header("X-Login-Context", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("ADMIN"));
  }

  @Test
  void login_motDePasseIncorrect_retourne401() throws Exception {
    String body =
        """
                {"mail":"alice@example.com","mdp":"wrongpass"}
                """;

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void register_nouveauMail_creeUnCompteEtRetourneToken() throws Exception {
    String body =
        """
                {"pseudo":"nouveau_visiteur","mail":"nouveau@example.com","mdp":"motdepasse"}
                """;

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").isNotEmpty())
        .andExpect(jsonPath("$.pseudo").value("nouveau_visiteur"))
        .andExpect(jsonPath("$.userId").isNotEmpty());
  }

  @Test
  void register_mailDejaUtilise_retourne409() throws Exception {
    String body =
        """
                {"pseudo":"alice_bis","mail":"alice@example.com","mdp":"autremdp"}
                """;

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Un compte existe deja avec cette adresse mail"));
  }

  @Test
  void register_pseudoDejaUtilise_retourne409() throws Exception {
    String body =
        """
                {"pseudo":"alice_dev","mail":"autre@example.com","mdp":"motdepasse"}
                """;

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Ce pseudo est deja utilise"));
  }

  @Test
  void register_pseudoEtMailDejaUtilises_retourne409Pseudo() throws Exception {
    String body =
        """
                {"pseudo":"alice_dev","mail":"alice@example.com","mdp":"motdepasse"}
                """;

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Ce pseudo est deja utilise"));
  }
}
