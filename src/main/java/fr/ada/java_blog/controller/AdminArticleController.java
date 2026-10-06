package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.ArticleCategoriesRequest;
import fr.ada.java_blog.dto.ArticleCreateRequest;
import fr.ada.java_blog.dto.ArticleMediaLinkRequest;
import fr.ada.java_blog.dto.ArticleResponse;
import fr.ada.java_blog.dto.ArticleUpdateRequest;
import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.dto.CommentaireResponse;
import fr.ada.java_blog.service.ArticleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/articles")
public class AdminArticleController {

  private final ArticleService articleService;

  public AdminArticleController(ArticleService articleService) {
    this.articleService = articleService;
  }

  @GetMapping
  public List<ArticleResponse> all() {
    return articleService.findAllAdmin();
  }

  @GetMapping("/{id}")
  public ArticleResponse byId(@PathVariable int id) {
    return articleService.findByIdAdmin(id);
  }

  @PostMapping
  public ResponseEntity<ArticleResponse> creer(@Valid @RequestBody ArticleCreateRequest body) {
    ArticleResponse created = articleService.creer(body);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  public ArticleResponse modifier(
      @PathVariable int id, @Valid @RequestBody ArticleUpdateRequest body) {
    return articleService.modifier(id, body);
  }

  @PatchMapping("/{id}/publier")
  public ResponseEntity<Void> publier(@PathVariable int id) {
    articleService.publier(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/depublier")
  public ResponseEntity<Void> depublier(@PathVariable int id) {
    articleService.depublier(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> supprimer(@PathVariable int id) {
    articleService.supprimer(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/categories")
  public List<CategorieResponse> categories(@PathVariable int id) {
    return articleService.categoriesAdmin(id);
  }

  @GetMapping("/{id}/commentaires")
  public List<CommentaireResponse> commentaires(@PathVariable int id) {
    return articleService.commentairesAdmin(id);
  }

  @PutMapping("/{id}/categories")
  public ResponseEntity<Void> remplacerCategories(
      @PathVariable int id, @RequestBody ArticleCategoriesRequest body) {
    articleService.remplacerCategories(id, body);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/medias")
  public ResponseEntity<Void> lierMedia(
      @PathVariable int id, @Valid @RequestBody ArticleMediaLinkRequest body) {
    articleService.lierMedia(id, body);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
