package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.CategorieCreateRequest;
import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.dto.CategorieUpdateRequest;
import fr.ada.java_blog.service.CategorieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategorieController {

  private final CategorieService categorieService;

  public AdminCategorieController(CategorieService categorieService) {
    this.categorieService = categorieService;
  }

  @PostMapping
  public ResponseEntity<CategorieResponse> create(@Valid @RequestBody CategorieCreateRequest body) {
    CategorieResponse created = categorieService.creer(body);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  public CategorieResponse update(
      @PathVariable int id, @Valid @RequestBody CategorieUpdateRequest body) {
    return categorieService.modifier(id, body);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    categorieService.supprimer(id);
    return ResponseEntity.noContent().build();
  }
}
