package fr.ada.java_blog.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public final class JwtTestHelper {

  private JwtTestHelper() {}

  public static String loginAndGetToken(MockMvc mockMvc, ObjectMapper objectMapper)
      throws Exception {
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
    return json.get("token").asText();
  }
}
