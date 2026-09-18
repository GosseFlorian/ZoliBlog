package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.ArticleCategoriesRequest;
import fr.ada.java_blog.dto.ArticleCreateRequest;
import fr.ada.java_blog.dto.ArticleMediaLinkRequest;
import fr.ada.java_blog.dto.ArticleResponse;
import fr.ada.java_blog.dto.ArticleUpdateRequest;
import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.dto.CommentaireResponse;
import fr.ada.java_blog.mapper.ArticleMapper;
import fr.ada.java_blog.mapper.CategorieMapper;
import fr.ada.java_blog.mapper.CommentaireMapper;
import fr.ada.java_blog.model.Article;
import fr.ada.java_blog.repository.ArticleRepository;
import fr.ada.java_blog.repository.CategorieRepository;
import fr.ada.java_blog.repository.CommentaireRepository;
import fr.ada.java_blog.repository.MediaRepository;

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
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/articles")
public class AdminArticleController {
    private final ArticleRepository articleRepository;
    private final CategorieRepository categorieRepository;
    private final MediaRepository mediaRepository;
    private final CommentaireRepository commentaireRepository;

    public AdminArticleController(
            ArticleRepository articleRepository,
            CategorieRepository categorieRepository,
            MediaRepository mediaRepository,
            CommentaireRepository commentaireRepository) {
        this.articleRepository = articleRepository;
        this.categorieRepository = categorieRepository;
        this.mediaRepository = mediaRepository;
        this.commentaireRepository = commentaireRepository;
    }

    @GetMapping
    public List<ArticleResponse> all() {
        return articleRepository.findAllAdmin().stream()
                .map(ArticleMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ArticleResponse byId(@PathVariable int id) {
        return articleRepository.findByIdAdmin(id)
                .map(ArticleMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article introuvable"));
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> creer(@Valid @RequestBody ArticleCreateRequest body) {
        LocalDateTime maintenant = LocalDateTime.now();
        Article article = new Article(
                null, body.titre(), body.contenu(),
                false, maintenant, maintenant, body.userId());
        Article sauve = articleRepository.save(article);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ArticleMapper.toResponse(sauve));
    }

    @PutMapping("/{id}")
    public ArticleResponse modifier(@PathVariable int id, @Valid @RequestBody ArticleUpdateRequest body) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article introuvable"));

        article.setTitre(body.titre());
        article.setContenu(body.contenu());
        article.setPublie(body.publie());
        article.setUpdate(LocalDateTime.now());

        if (!articleRepository.update(id, article)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
        }
        return ArticleMapper.toResponse(article);
    }

    @PatchMapping("/{id}/publier")
    public ResponseEntity<Void> publier(@PathVariable int id) {
        if (!articleRepository.updateStatut(id, true)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/depublier")
    public ResponseEntity<Void> depublier(@PathVariable int id) {
        if (!articleRepository.updateStatut(id, false)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable int id) {
        if (!articleRepository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/categories")
    public List<CategorieResponse> categories(@PathVariable int id) {
        if (articleRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
        }
        return categorieRepository.findByArticleId(id).stream()
                .map(CategorieMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}/commentaires")
    public List<CommentaireResponse> commentaires(@PathVariable int id) {
        if (articleRepository.findByIdAdmin(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
        }
        return commentaireRepository.findByArticleId(id).stream()
                .map(CommentaireMapper::toResponse)
                .toList();
    }

    @PutMapping("/{id}/categories")
    public ResponseEntity<Void> remplacerCategories(
            @PathVariable int id,
            @RequestBody ArticleCategoriesRequest body) {
        categorieRepository.replaceCategoriesArticle(id, body.categorieIds());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/medias")
    public ResponseEntity<Void> lierMedia(
            @PathVariable int id,
            @Valid @RequestBody ArticleMediaLinkRequest body) {
        if (articleRepository.findByIdAdmin(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
        }
        if (mediaRepository.findById(body.mediaId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Média introuvable");
        }
        if (mediaRepository.existsArticleMediaLink(id, body.mediaId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce media est deja lie a cet article");
        }

        mediaRepository.lierArticle(id, body.mediaId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
