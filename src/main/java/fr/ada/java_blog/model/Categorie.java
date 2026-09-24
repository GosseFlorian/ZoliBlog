package fr.ada.java_blog.model;

public class Categorie {
  private Integer id;
  private String nom;
  private String description;

  public Categorie(Integer id, String nom, String description) {
    this.id = id;
    this.nom = nom;
    this.description = description;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
