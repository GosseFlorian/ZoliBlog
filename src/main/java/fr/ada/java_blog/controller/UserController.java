package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.UserResponse;
import fr.ada.java_blog.mapper.UserMapper;
import fr.ada.java_blog.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/{id}")
  public UserResponse byId(@PathVariable int id) {
    return userRepository
        .findById(id)
        .map(UserMapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
  }
}
