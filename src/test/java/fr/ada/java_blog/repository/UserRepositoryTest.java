package fr.ada.java_blog.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.ada.java_blog.model.User;
import fr.ada.java_blog.model.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Test
  void findById_existant_retourneUser() {
    Optional<User> user = userRepository.findById(1);

    assertTrue(user.isPresent());
    assertEquals("alice_dev", user.get().getPseudo());
    assertEquals(UserRole.ADMIN, user.get().getRole());
  }

  @Test
  void findByMail_existant_retourneUser() {
    Optional<User> user = userRepository.findByMail("bob@example.com");

    assertTrue(user.isPresent());
    assertEquals("bob_martin", user.get().getPseudo());
  }

  @Test
  void findAll_retourneAuMoinsDeuxUsers() {
    assertTrue(userRepository.findAll().size() >= 2);
  }
}
