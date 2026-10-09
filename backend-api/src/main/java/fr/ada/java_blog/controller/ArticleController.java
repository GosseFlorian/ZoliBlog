package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.ArticleResponse;
import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.service.ArticleService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/articles")
public class ArticleController {

  private final ArticleService articleService;

  public ArticleController(ArticleService articleService) {
    this.articleService = articleService;
  }

  @GetMapping
  public List<ArticleResponse> listerPublies() {
    return articleService.listerPublies();
  }

  @GetMapping("/recents")
  public List<ArticleResponse> recents() {
    return articleService.recents();
  }

  @GetMapping("/recents/count")
  public Integer countPublies() {
    return articleService.countPublies();
  }

  @GetMapping("/{id}")
  public ArticleResponse un(@PathVariable int id) {
    return articleService.findPublishedById(id);
  }

  @GetMapping("/{id}/categories")
  public List<CategorieResponse> categories(@PathVariable int id) {
    return articleService.categoriesArticlePublie(id);
  }
}
