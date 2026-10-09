package fr.ada.java_blog.mapper;

import fr.ada.java_blog.dto.ArticleResponse;
import fr.ada.java_blog.model.Article;

public final class ArticleMapper {

  private ArticleMapper() {}

  public static ArticleResponse toResponse(Article article) {
    return new ArticleResponse(
        article.getId(),
        article.getTitre(),
        article.getContenu(),
        article.isPublie(),
        article.getDate());
  }
}
