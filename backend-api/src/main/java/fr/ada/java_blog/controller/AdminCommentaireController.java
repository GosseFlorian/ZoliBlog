package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.CommentaireResponse;
import fr.ada.java_blog.service.CommentaireService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/commentaires")
public class AdminCommentaireController {

  private final CommentaireService commentaireService;

  public AdminCommentaireController(CommentaireService commentaireService) {
    this.commentaireService = commentaireService;
  }

  @GetMapping
  public List<CommentaireResponse> all() {
    return commentaireService.findAllAdmin();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable int id) {
    commentaireService.supprimerAdmin(id);
  }
}
