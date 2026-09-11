package fr.ada.java_blog.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminMediaControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String bearerToken;

    @BeforeEach
    void loginAndGetToken() throws Exception {
        bearerToken = JwtTestHelper.loginAndGetToken(mockMvc, objectMapper);
    }

    @Test
    void createMedia_retourne201() throws Exception {
        mockMvc.perform(post("/admin/medias")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"type":"image","url":"https://example.com/test.png"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("image"));
    }

    @Test
    void deleteMedia_existant_retourne204() throws Exception {
        MvcResult created = mockMvc.perform(post("/admin/medias")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"type":"gif","url":"https://example.com/anim.gif"}
                        """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(created.getResponse().getContentAsString());
        int id = json.get("id").asInt();

        mockMvc.perform(delete("/admin/medias/" + id)
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMedia_inexistant_retourne404() throws Exception {
        mockMvc.perform(delete("/admin/medias/99999")
                .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isNotFound());
    }
}
