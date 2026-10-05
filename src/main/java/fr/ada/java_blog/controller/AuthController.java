package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.LoginRequest;
import fr.ada.java_blog.dto.LoginResponse;
import fr.ada.java_blog.dto.RegisterRequest;
import fr.ada.java_blog.model.User;
import fr.ada.java_blog.model.UserRole;
import fr.ada.java_blog.repository.UserRepository;
import fr.ada.java_blog.service.JwtService;
import fr.ada.java_blog.util.LogSanitizer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

  public AuthController(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @PostMapping("/login")
  public LoginResponse login(
      @Valid @RequestBody LoginRequest body,
      @RequestHeader(value = "X-Login-Context", required = false) String loginContext) {
    User user =
        userRepository
            .findByMail(body.mail())
            // Échec login (user inconnu ou mauvais mot de passe) :
            .orElseThrow(
                () -> {
                  log.warn(
                      "Echec login - utilisateur inconnu (mail={})",
                      LogSanitizer.maskEmail(body.mail()));
                  return unauthorized();
                });

    if (!passwordEncoder.matches(body.mdp(), user.getMdp())) {
      log.warn(
          "Echec login - mot de passe incorrect (mail={})", LogSanitizer.maskEmail(body.mail()));
      throw unauthorized();
    }

    boolean adminLogin = "admin".equalsIgnoreCase(loginContext);
    if (adminLogin && user.getRole() != UserRole.ADMIN) {
      log.warn(
          "Echec login admin - role insuffisant (userId={}, mail={}, role={})",
          user.getId(),
          LogSanitizer.maskEmail(body.mail()),
          user.getRole());
      throw forbiddenAdmin();
    }

    if (adminLogin) {
      log.info(
          "Login admin reussi (userId={}, mail={})",
          user.getId(),
          LogSanitizer.maskEmail(body.mail()));
    } else {
      log.info(
          "Login reussi (userId={}, mail={}, role={})",
          user.getId(),
          LogSanitizer.maskEmail(body.mail()),
          user.getRole());
    }

    String token = jwtService.generateToken(user);
    return new LoginResponse(token, user.getPseudo(), user.getId(), user.getRole().name());
  }

  /**
   * Inscription publique — il n'existait auparavant aucune route non protégée pour créer un compte
   * (POST /admin/users exige déjà un JWT). Retourne directement un token comme /auth/login pour
   * connecter l'utilisateur dès la création de son compte.
   */
  @PostMapping("/register")
  public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest body) {
    if (userRepository.findByPseudo(body.pseudo()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce pseudo est deja utilise");
    }
    if (userRepository.findByMail(body.mail()).isPresent()) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Un compte existe deja avec cette adresse mail");
    }

    String hash = passwordEncoder.encode(body.mdp());
    User user = new User(null, body.pseudo(), body.mail(), hash, UserRole.USER);
    User sauve = userRepository.save(user);

    log.info(
        "Inscription reussie (userId={}, mail={})",
        sauve.getId(),
        LogSanitizer.maskEmail(body.mail()));
    String token = jwtService.generateToken(sauve);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new LoginResponse(token, sauve.getPseudo(), sauve.getId(), sauve.getRole().name()));
  }

  private static ResponseStatusException unauthorized() {
    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
  }

  private static ResponseStatusException forbiddenAdmin() {
    return new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces reserve aux administrateurs.");
  }
}
