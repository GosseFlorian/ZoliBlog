package fr.ada.java_blog.service;

import fr.ada.java_blog.dto.UserCreateRequest;
import fr.ada.java_blog.dto.UserResponse;
import fr.ada.java_blog.dto.UserUpdateRequest;
import fr.ada.java_blog.mapper.UserMapper;
import fr.ada.java_blog.model.User;
import fr.ada.java_blog.model.UserRole;
import fr.ada.java_blog.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserResponse findById(int id) {
    return userRepository
        .findById(id)
        .map(UserMapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
  }

  public List<UserResponse> findAll() {
    return userRepository.findAll().stream().map(UserMapper::toResponse).toList();
  }

  public UserResponse creer(UserCreateRequest body) {
    verifierPseudoDisponible(body.pseudo());
    verifierMailDisponible(body.mail());

    String hash = passwordEncoder.encode(body.mdp());
    User user = new User(null, body.pseudo(), body.mail(), hash, UserRole.USER);
    User sauve = userRepository.save(user);
    return UserMapper.toResponse(sauve);
  }

  public UserResponse modifier(int id, UserUpdateRequest body) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

    if (userRepository.existsByMailForOtherUser(body.mail(), id)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Un compte existe deja avec cette adresse mail");
    }
    if (userRepository.existsByPseudoForOtherUser(body.pseudo(), id)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce pseudo est deja utilise");
    }

    user.setPseudo(body.pseudo());
    user.setMail(body.mail());
    user.setMdp(passwordEncoder.encode(body.mdp()));

    if (!userRepository.updateById(id, user)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable");
    }
    return UserMapper.toResponse(user);
  }

  public void supprimer(int id) {
    if (!userRepository.deleteById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable");
    }
  }

  private void verifierPseudoDisponible(String pseudo) {
    if (userRepository.findByPseudo(pseudo).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce pseudo est deja utilise");
    }
  }

  private void verifierMailDisponible(String mail) {
    if (userRepository.findByMail(mail).isPresent()) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Un compte existe deja avec cette adresse mail");
    }
  }
}
