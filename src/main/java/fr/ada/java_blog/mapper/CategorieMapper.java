package fr.ada.java_blog.mapper;

import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.model.Categorie;

public final class CategorieMapper {

  private CategorieMapper() {}

  public static CategorieResponse toResponse(Categorie categorie) {
    return new CategorieResponse(categorie.getId(), categorie.getNom(), categorie.getDescription());
  }
}
