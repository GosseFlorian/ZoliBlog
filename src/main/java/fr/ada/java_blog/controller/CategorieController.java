package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.ArticleResponse;
import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.service.CategorieService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class CategorieController {

  private final CategorieService categorieService;

  public CategorieController(CategorieService categorieService) {
    this.categorieService = categorieService;
  }

  @GetMapping
  public List<CategorieResponse> all() {
    return categorieService.findAll();
  }

  @GetMapping("/{id}")
  public CategorieResponse byId(@PathVariable int id) {
    return categorieService.findById(id);
  }

  @GetMapping("/{id}/articles")
  public List<ArticleResponse> articlesByCategorieId(@PathVariable int id) {
    return categorieService.articlesPubliesByCategorieId(id);
  }
}
