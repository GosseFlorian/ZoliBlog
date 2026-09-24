package fr.ada.java_blog.model;

import java.time.LocalDateTime;

public class Commentaire {

  private Integer id;
  private String contenu;
  private Integer userId;
  private Integer articleId;
  private LocalDateTime date;
  private String pseudo;

  public Commentaire(
      Integer id, String contenu, Integer userId, Integer articleId, LocalDateTime date) {
    this.id = id;
    this.contenu = contenu;
    this.userId = userId;
    this.articleId = articleId;
    this.date = date;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getContenu() {
    return contenu;
  }

  public void setContenu(String contenu) {
    this.contenu = contenu;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getArticleId() {
    return articleId;
  }

  public void setArticleId(Integer articleId) {
    this.articleId = articleId;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public String getPseudo() {
    return pseudo;
  }

  public void setPseudo(String pseudo) {
    this.pseudo = pseudo;
  }
}
