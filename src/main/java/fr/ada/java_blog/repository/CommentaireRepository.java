package fr.ada.java_blog.repository;

import fr.ada.java_blog.model.Commentaire;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CommentaireRepository {

  private final JdbcTemplate jdbcTemplate;

  private static final RowMapper<Commentaire> ROW_MAPPER =
      (rs, rowNum) -> {
        Commentaire commentaire =
            new Commentaire(
                rs.getInt("id"),
                rs.getString("contenu"),
                rs.getObject("user_id", Integer.class),
                rs.getObject("article_id", Integer.class),
                rs.getTimestamp("date").toLocalDateTime());
        commentaire.setPseudo(rs.getString("pseudo"));
        return commentaire;
      };

  private static final String SELECT_WITH_USER =
      """
            SELECT c.id, c.contenu, c.user_id, c.article_id, c.date, u.pseudo
            FROM commentaires c
            JOIN users u ON u.id = c.user_id
            """;

  public CommentaireRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Commentaire> findByArticleId(int articleId) {
    String sql =
        SELECT_WITH_USER
            + """
                WHERE c.article_id = ?
                ORDER BY c.date DESC
                """;
    return jdbcTemplate.query(sql, ROW_MAPPER, articleId);
  }

  public Optional<Commentaire> findById(int id) {
    String sql = SELECT_WITH_USER + " WHERE c.id = ?";
    return jdbcTemplate.query(sql, ROW_MAPPER, id).stream().findFirst();
  }

  public List<Commentaire> findAll() {
    String sql = SELECT_WITH_USER + " ORDER BY c.date ASC";
    return jdbcTemplate.query(sql, ROW_MAPPER);
  }

  public Commentaire save(int articleId, String contenu, int userId) {
    String sql =
        """
                INSERT INTO commentaires (contenu, user_id, article_id, date)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;
    LocalDateTime now = LocalDateTime.now();
    Integer generatedId =
        jdbcTemplate.queryForObject(sql, Integer.class, contenu, userId, articleId, now);
    if (generatedId == null) {
      throw new IllegalStateException("INSERT commentaire n'a pas retourné d'id");
    }
    int id = generatedId;
    return findById(id).orElseThrow();
  }

  public boolean updateById(int id, String contenu) {
    String sql =
        """
                UPDATE commentaires
                SET contenu = ?
                WHERE id = ?
                """;
    return jdbcTemplate.update(sql, contenu, id) > 0;
  }

  public boolean deleteById(int id) {
    return jdbcTemplate.update("DELETE FROM commentaires WHERE id = ?", id) > 0;
  }
}
