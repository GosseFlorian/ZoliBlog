package fr.ada.java_blog.service;

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
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArticleService {

  private static final int LIMITE_RECENTS = 5;

  private final ArticleRepository articleRepository;
  private final CategorieRepository categorieRepository;
  private final MediaRepository mediaRepository;
  private final CommentaireRepository commentaireRepository;

  public ArticleService(
      ArticleRepository articleRepository,
      CategorieRepository categorieRepository,
      MediaRepository mediaRepository,
      CommentaireRepository commentaireRepository) {
    this.articleRepository = articleRepository;
    this.categorieRepository = categorieRepository;
    this.mediaRepository = mediaRepository;
    this.commentaireRepository = commentaireRepository;
  }

  public List<ArticleResponse> listerPublies() {
    return articleRepository.findPublies().stream().map(ArticleMapper::toResponse).toList();
  }

  public List<ArticleResponse> recents() {
    return articleRepository.findRecents(LIMITE_RECENTS).stream()
        .map(ArticleMapper::toResponse)
        .toList();
  }

  public Integer countPublies() {
    return articleRepository.countPublies();
  }

  public ArticleResponse findPublishedById(int id) {
    return articleRepository
        .findPublishedById(id)
        .map(ArticleMapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable"));
  }

  public List<CategorieResponse> categoriesArticlePublie(int id) {
    if (articleRepository.findPublishedById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
    }
    return categorieRepository.findByArticleId(id).stream()
        .map(CategorieMapper::toResponse)
        .toList();
  }

  public List<ArticleResponse> findAllAdmin() {
    return articleRepository.findAllAdmin().stream().map(ArticleMapper::toResponse).toList();
  }

  public ArticleResponse findByIdAdmin(int id) {
    return articleRepository
        .findByIdAdmin(id)
        .map(ArticleMapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable"));
  }

  public ArticleResponse creer(ArticleCreateRequest body) {
    LocalDateTime maintenant = LocalDateTime.now();
    Article article =
        new Article(
            null, body.titre(), body.contenu(), false, maintenant, maintenant, body.userId());
    Article sauve = articleRepository.save(article);
    return ArticleMapper.toResponse(sauve);
  }

  public ArticleResponse modifier(int id, ArticleUpdateRequest body) {
    Article article =
        articleRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable"));

    article.setTitre(body.titre());
    article.setContenu(body.contenu());
    article.setPublie(body.publie());
    article.setUpdate(LocalDateTime.now());

    if (!articleRepository.update(id, article)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
    }
    return ArticleMapper.toResponse(article);
  }

  public void publier(int id) {
    if (!articleRepository.updateStatut(id, true)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
    }
  }

  public void depublier(int id) {
    if (!articleRepository.updateStatut(id, false)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
    }
  }

  public void supprimer(int id) {
    if (!articleRepository.deleteById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
    }
  }

  public List<CategorieResponse> categoriesAdmin(int id) {
    if (articleRepository.findById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
    }
    return categorieRepository.findByArticleId(id).stream()
        .map(CategorieMapper::toResponse)
        .toList();
  }

  public List<CommentaireResponse> commentairesAdmin(int id) {
    if (articleRepository.findByIdAdmin(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
    }
    return commentaireRepository.findByArticleId(id).stream()
        .map(CommentaireMapper::toResponse)
        .toList();
  }

  public void remplacerCategories(int id, ArticleCategoriesRequest body) {
    categorieRepository.replaceCategoriesArticle(id, body.categorieIds());
  }

  public void lierMedia(int id, ArticleMediaLinkRequest body) {
    if (articleRepository.findByIdAdmin(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable");
    }
    if (mediaRepository.findById(body.mediaId()).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Média introuvable");
    }
    if (mediaRepository.existsArticleMediaLink(id, body.mediaId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce media est deja lie a cet article");
    }
    mediaRepository.lierArticle(id, body.mediaId());
  }
}
