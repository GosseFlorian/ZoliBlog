package fr.ada.java_blog.mapper;

import fr.ada.java_blog.dto.UserResponse;
import fr.ada.java_blog.model.User;

public final class UserMapper {

  private UserMapper() {}

  public static UserResponse toResponse(User user) {
    return new UserResponse(user.getId(), user.getPseudo(), user.getMail());
    // Pas de utilisateur.getMdp() ici : c'est volontaire et définitif.
  }
}
