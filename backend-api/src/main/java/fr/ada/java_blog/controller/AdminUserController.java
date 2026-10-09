package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.UserCreateRequest;
import fr.ada.java_blog.dto.UserResponse;
import fr.ada.java_blog.dto.UserUpdateRequest;
import fr.ada.java_blog.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

  private final UserService userService;

  public AdminUserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public List<UserResponse> all() {
    return userService.findAll();
  }

  @GetMapping("/{id}")
  public UserResponse byId(@PathVariable int id) {
    return userService.findById(id);
  }

  @PostMapping
  public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest body) {
    UserResponse created = userService.creer(body);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  public UserResponse update(@PathVariable int id, @Valid @RequestBody UserUpdateRequest body) {
    return userService.modifier(id, body);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    userService.supprimer(id);
    return ResponseEntity.noContent().build();
  }
}
