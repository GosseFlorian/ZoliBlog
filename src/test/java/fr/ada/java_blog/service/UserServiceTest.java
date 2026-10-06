package fr.ada.java_blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.ada.java_blog.dto.UserCreateRequest;
import fr.ada.java_blog.dto.UserUpdateRequest;
import fr.ada.java_blog.model.User;
import fr.ada.java_blog.model.UserRole;
import fr.ada.java_blog.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private UserService userService;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository, passwordEncoder);
  }

  @Test
  void findById_existant_retourneUserResponse() {
    User user = new User(1, "alice", "a@example.com", "hash", UserRole.USER);
    when(userRepository.findById(1)).thenReturn(Optional.of(user));

    assertEquals("alice", userService.findById(1).pseudo());
  }

  @Test
  void findById_inexistant_lance404() {
    when(userRepository.findById(99)).thenReturn(Optional.empty());

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> userService.findById(99));
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void creer_pseudoDejaPris_lance409() {
    when(userRepository.findByPseudo("bob"))
        .thenReturn(Optional.of(new User(2, "bob", "other@example.com", "h", UserRole.USER)));

    UserCreateRequest body = new UserCreateRequest("bob", "n@example.com", "password123");
    assertThrows(ResponseStatusException.class, () -> userService.creer(body));
    verify(userRepository, never()).save(any());
  }

  @Test
  void creer_mailDejaPris_lance409() {
    when(userRepository.findByPseudo("bob")).thenReturn(Optional.empty());
    when(userRepository.findByMail("n@example.com"))
        .thenReturn(Optional.of(new User(2, "other", "n@example.com", "h", UserRole.USER)));

    UserCreateRequest body = new UserCreateRequest("bob", "n@example.com", "password123");
    assertThrows(ResponseStatusException.class, () -> userService.creer(body));
  }

  @Test
  void creer_ok_enregistreUtilisateur() {
    when(userRepository.findByPseudo("bob")).thenReturn(Optional.empty());
    when(userRepository.findByMail("n@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encoded");
    when(userRepository.save(any(User.class)))
        .thenAnswer(
            inv -> {
              User u = inv.getArgument(0);
              u.setId(5);
              return u;
            });

    UserCreateRequest body = new UserCreateRequest("bob", "n@example.com", "password123");
    assertEquals(5, userService.creer(body).id());
  }

  @Test
  void modifier_conflitMail_lance409() {
    User user = new User(1, "alice", "a@example.com", "hash", UserRole.USER);
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(userRepository.existsByMailForOtherUser("taken@example.com", 1)).thenReturn(true);

    UserUpdateRequest body = new UserUpdateRequest("alice", "taken@example.com", "password123");
    assertThrows(ResponseStatusException.class, () -> userService.modifier(1, body));
  }

  @Test
  void supprimer_inexistant_lance404() {
    when(userRepository.deleteById(99)).thenReturn(false);
    assertThrows(ResponseStatusException.class, () -> userService.supprimer(99));
  }

  @Test
  void findAll_retourneListe() {
    when(userRepository.findAll())
        .thenReturn(List.of(new User(1, "a", "a@x.com", "h", UserRole.USER)));
    assertEquals(1, userService.findAll().size());
  }

  @Test
  void modifier_updateEchoue_lance404() {
    User user = new User(1, "alice", "a@example.com", "hash", UserRole.USER);
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(userRepository.existsByMailForOtherUser(any(), eq(1))).thenReturn(false);
    when(userRepository.existsByPseudoForOtherUser(any(), eq(1))).thenReturn(false);
    when(passwordEncoder.encode(any())).thenReturn("newHash");
    when(userRepository.updateById(eq(1), any(User.class))).thenReturn(false);

    UserUpdateRequest body = new UserUpdateRequest("alice2", "a2@example.com", "password123");
    assertThrows(ResponseStatusException.class, () -> userService.modifier(1, body));
  }
}
