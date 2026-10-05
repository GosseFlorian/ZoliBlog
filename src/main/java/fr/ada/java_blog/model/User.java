package fr.ada.java_blog.model;

public class User {
  private Integer id;
  private String pseudo;
  private String mail;
  private String mdp;
  private UserRole role;

  public User(Integer id, String pseudo, String mail, String mdp, UserRole role) {
    this.id = id;
    this.pseudo = pseudo;
    this.mail = mail;
    this.mdp = mdp;
    this.role = role != null ? role : UserRole.USER;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getPseudo() {
    return pseudo;
  }

  public void setPseudo(String pseudo) {
    this.pseudo = pseudo;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getMdp() {
    return mdp;
  }

  public void setMdp(String mdp) {
    this.mdp = mdp;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }
}
