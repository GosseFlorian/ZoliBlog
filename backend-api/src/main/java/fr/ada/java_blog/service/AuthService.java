package fr.ada.java_blog.service;

import fr.ada.java_blog.dto.LoginRequest;
import fr.ada.java_blog.dto.LoginResponse;
import fr.ada.java_blog.dto.RegisterRequest;
import fr.ada.java_blog.model.User;
import fr.ada.java_blog.model.UserRole;
import fr.ada.java_blog.repository.UserRepository;
import fr.ada.java_blog.util.LogSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public LoginResponse login(LoginRequest body, String loginContext) {
    User user =
        userRepository
            .findByMail(body.mail())
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

  public LoginResponse register(RegisterRequest body) {
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
    return new LoginResponse(token, sauve.getPseudo(), sauve.getId(), sauve.getRole().name());
  }

  private static ResponseStatusException unauthorized() {
    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
  }

  private static ResponseStatusException forbiddenAdmin() {
    return new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces reserve aux administrateurs.");
  }
}
