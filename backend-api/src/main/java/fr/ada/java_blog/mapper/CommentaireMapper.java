package fr.ada.java_blog.mapper;

import fr.ada.java_blog.dto.CommentaireResponse;
import fr.ada.java_blog.model.Commentaire;

public final class CommentaireMapper {

  private CommentaireMapper() {}

  public static CommentaireResponse toResponse(Commentaire commentaire) {
    return new CommentaireResponse(
        commentaire.getId(),
        commentaire.getContenu(),
        commentaire.getUserId(),
        commentaire.getPseudo(),
        commentaire.getDate());
  }
}
