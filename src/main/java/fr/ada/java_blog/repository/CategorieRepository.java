package fr.ada.java_blog.repository;

import fr.ada.java_blog.model.Article;
import fr.ada.java_blog.model.Categorie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CategorieRepository {

    private static final RowMapper<Categorie> RowMapper = (rs, rowNum) -> new Categorie(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("description"));

    // Même RowMapper qu'ArticleRepository : on lit une ligne "articles" complète,
    // même quand elle vient d'un JOIN avec articles_categories.
    private static final RowMapper<Article> ArticleRowMapper = (rs, rowNum) -> {
        Timestamp dateTs = rs.getTimestamp("date");
        Timestamp updateTs = rs.getTimestamp("update");
        return new Article(
                rs.getInt("id"),
                rs.getString("titre"),
                rs.getString("contenu"),
                rs.getBoolean("statut"),
                dateTs != null ? dateTs.toLocalDateTime() : null,
                updateTs != null ? updateTs.toLocalDateTime() : null,
                rs.getObject("user_id", Integer.class));
    };

    private final JdbcTemplate jdbcTemplate;

    public CategorieRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Categorie> findAll() {
        return jdbcTemplate.query(
                """
                        SELECT id, nom, description
                        FROM categories
                        ORDER BY nom ASC
                        """,
                RowMapper);
    }

    public Optional<Categorie> findById(int id) {
        List<Categorie> categories = jdbcTemplate.query(
                """
                        SELECT id, nom, description
                            FROM categories
                            WHERE id = ?
                            """,
                RowMapper,
                id);
        return categories.stream().findFirst();
    }

    public List<Categorie> findByArticleId(int articleId) {
        return jdbcTemplate.query(
                """
                        SELECT c.id, c.nom, c.description
                        FROM categories c
                        JOIN articles_categories ac ON ac.categorie_id = c.id
                        WHERE ac.article_id = ?
                        ORDER BY c.nom ASC
                        """,
                RowMapper,
                articleId);
    }

    public List<Article> findArticlesPubliesByCategorieId(int categorieId) {
        return jdbcTemplate.query(
                """
                        SELECT a.id, a.titre, a.contenu, a.statut, a.date, a."update", a.user_id
                        FROM articles a
                        JOIN articles_categories ac ON ac.article_id = a.id
                        WHERE ac.categorie_id = ? AND a.statut = TRUE
                        ORDER BY a.date DESC
                        """,
                ArticleRowMapper,
                categorieId);
    }

    public Categorie save(Categorie categorie) {
        Integer id = jdbcTemplate.queryForObject(
                """
                        INSERT INTO categories (nom, description)
                        VALUES (?, ?)
                        RETURNING id
                        """,
                Integer.class,
                categorie.getNom(),
                categorie.getDescription());
        categorie.setId(id);
        return categorie;
    }

    public boolean updateById(int id, Categorie categorie) {
        int rows = jdbcTemplate.update(
                """
                        UPDATE categories
                        SET nom = ?, description = ?
                        WHERE id = ?
                        """,
                categorie.getNom(),
                categorie.getDescription(),
                id);
        return rows > 0;
    }

    @Transactional
    public boolean deleteById(int id) {
        jdbcTemplate.update(
                """
                        DELETE FROM articles_categories
                        WHERE categorie_id = ?
                        """,
                id);
        int rows = jdbcTemplate.update(
                """
                        DELETE FROM categories
                        WHERE id = ?
                        """,
                id);
        return rows > 0;
    }

    @Transactional
    public void replaceCategoriesArticle(int articleId, List<Integer> categorieIds) {
        jdbcTemplate.update(
                """
                        DELETE FROM articles_categories
                        WHERE article_id = ?
                        """,
                articleId);
        for (Integer categorieId : categorieIds) {
            jdbcTemplate.update(
                    """
                            INSERT INTO articles_categories (article_id, categorie_id)
                            VALUES (?, ?)
                            """,
                    articleId, categorieId);
        }
    }
}