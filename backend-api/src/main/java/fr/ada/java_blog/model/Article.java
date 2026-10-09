package fr.ada.java_blog.model;

import java.time.LocalDateTime;

public class Article {

  private Integer id;
  private String titre;
  private String contenu;
  private boolean publie;
  private LocalDateTime date;
  private LocalDateTime update;
  private Integer userId;

  public Article(
      Integer id,
      String titre,
      String contenu,
      boolean publie,
      LocalDateTime date,
      LocalDateTime update,
      Integer userId) {
    this.id = id;
    this.titre = titre;
    this.contenu = contenu;
    this.publie = publie;
    this.date = date;
    this.update = update;
    this.userId = userId;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTitre() {
    return titre;
  }

  public void setTitre(String titre) {
    this.titre = titre;
  }

  public String getContenu() {
    return contenu;
  }

  public void setContenu(String contenu) {
    this.contenu = contenu;
  }

  public boolean isPublie() {
    return publie;
  }

  public void setPublie(boolean publie) {
    this.publie = publie;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public LocalDateTime getUpdate() {
    return update;
  }

  public void setUpdate(LocalDateTime update) {
    this.update = update;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }
}
