package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.ArticleResponse;
import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.mapper.ArticleMapper;
import fr.ada.java_blog.mapper.CategorieMapper;
import fr.ada.java_blog.repository.CategorieRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/categories")
public class CategorieController {

  private final CategorieRepository categorieRepository;

  public CategorieController(CategorieRepository categorieRepository) {
    this.categorieRepository = categorieRepository;
  }

  @GetMapping
  public List<CategorieResponse> all() {
    return categorieRepository.findAll().stream().map(CategorieMapper::toResponse).toList();
  }

  @GetMapping("/{id}")
  public CategorieResponse byId(@PathVariable int id) {
    return categorieRepository
        .findById(id)
        .map(CategorieMapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable"));
  }

  @GetMapping("/{id}/articles")
  public List<ArticleResponse> articlesByCategorieId(@PathVariable int id) {
    return categorieRepository.findArticlesPubliesByCategorieId(id).stream()
        .map(ArticleMapper::toResponse)
        .toList();
  }
}
