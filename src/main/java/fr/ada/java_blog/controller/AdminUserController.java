package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.UserCreateRequest;
import fr.ada.java_blog.dto.UserResponse;
import fr.ada.java_blog.dto.UserUpdateRequest;
import fr.ada.java_blog.mapper.UserMapper;
import fr.ada.java_blog.model.User;
import fr.ada.java_blog.model.UserRole;
import fr.ada.java_blog.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserResponse> all() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse byId(@PathVariable int id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest body) {
        if (userRepository.findByPseudo(body.pseudo()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce pseudo est deja utilise");
        }
        if (userRepository.findByMail(body.mail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un compte existe deja avec cette adresse mail");
        }

        String hash = passwordEncoder.encode(body.mdp());
        User user = new User(null, body.pseudo(), body.mail(), hash, UserRole.USER);
        User sauve = userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserMapper.toResponse(sauve));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable int id, @Valid @RequestBody UserUpdateRequest body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        if (userRepository.existsByMailForOtherUser(body.mail(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un compte existe deja avec cette adresse mail");
        }
        if (userRepository.existsByPseudoForOtherUser(body.pseudo(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce pseudo est deja utilise");
        }

        user.setPseudo(body.pseudo());
        user.setMail(body.mail());
        user.setMdp(passwordEncoder.encode(body.mdp()));

        if (!userRepository.updateById(id, user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable");
        }
        return UserMapper.toResponse(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!userRepository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable");
        }
        return ResponseEntity.noContent().build();
    }
}