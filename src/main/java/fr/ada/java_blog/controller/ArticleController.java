package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.ArticleResponse;
import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.mapper.ArticleMapper;
import fr.ada.java_blog.mapper.CategorieMapper;
import fr.ada.java_blog.repository.ArticleRepository;
import fr.ada.java_blog.repository.CategorieRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/articles")
public class ArticleController {

  private static final int LIMITE_RECENTS = 5;

  private final ArticleRepository articleRepository;
  private final CategorieRepository categorieRepository;

  public ArticleController(
      ArticleRepository articleRepository, CategorieRepository categorieRepository) {
    this.articleRepository = articleRepository;
    this.categorieRepository = categorieRepository;
  }

  @GetMapping
  public List<ArticleResponse> listerPublies() {
    return articleRepository.findPublies().stream().map(ArticleMapper::toResponse).toList();
  }

  @GetMapping("/recents")
  public List<ArticleResponse> recents() {
    return articleRepository.findRecents(LIMITE_RECENTS).stream()
        .map(ArticleMapper::toResponse)
        .toList();
  }

  @GetMapping("/recents/count")
  public Integer countPublies() {
    return articleRepository.countPublies();
  }

  @GetMapping("/{id}")
  public ArticleResponse un(@PathVariable int id) {
    return articleRepository
        .findPublishedById(id)
        .map(ArticleMapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable"));
  }

  @GetMapping("/{id}/categories")
  public List<CategorieResponse> categories(@PathVariable int id) {
    if (articleRepository.findPublishedById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
    }
    return categorieRepository.findByArticleId(id).stream()
        .map(CategorieMapper::toResponse)
        .toList();
  }
}
