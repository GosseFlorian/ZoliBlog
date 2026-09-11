package fr.ada.java_blog.mapper;

import fr.ada.java_blog.dto.UserResponse;
import fr.ada.java_blog.model.User;
import fr.ada.java_blog.model.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void toResponse_copieIdPseudoEtMail_sansMotDePasse() {
        User user = new User(7, "carol", "carol@example.com", "hash-secret", UserRole.USER);

        UserResponse response = UserMapper.toResponse(user);

        assertEquals(7, response.id());
        assertEquals("carol", response.pseudo());
        assertEquals("carol@example.com", response.mail());
    }
}
