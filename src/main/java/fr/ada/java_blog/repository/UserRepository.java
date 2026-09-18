package fr.ada.java_blog.repository;

import fr.ada.java_blog.model.User;
import fr.ada.java_blog.model.UserRole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ArticleRepository articleRepository;

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("pseudo"),
            rs.getString("mail"),
            rs.getString("mdp"),
            UserRole.valueOf(rs.getString("role")));

    public UserRepository(JdbcTemplate jdbcTemplate, ArticleRepository articleRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.articleRepository = articleRepository;
    }

    public Optional<User> findById(int id) {
        List<User> user = jdbcTemplate.query(
                """
                        SELECT id, pseudo, mail, mdp, role
                        FROM users
                        WHERE id = ?
                        """,
                USER_ROW_MAPPER,
                id);
        return user.stream().findFirst();
    }

    public List<User> findAll() {
        return jdbcTemplate.query(
                """
                        SELECT id, pseudo, mail, mdp, role
                        FROM users
                        ORDER BY pseudo ASC
                        """,
                USER_ROW_MAPPER);
    }

    public User save(User user) {
        Integer id = jdbcTemplate.queryForObject(
                """
                        INSERT INTO users (pseudo, mail, mdp, role)
                        VALUES (?, ?, ?, ?::user_role)
                        RETURNING id
                        """,
                Integer.class,
                user.getPseudo(),
                user.getMail(),
                user.getMdp(),
                user.getRole().name());
        user.setId(id);
        return user;
    }

    public boolean updateById(int id, User user) {
        int rows = jdbcTemplate.update(
                """
                        UPDATE users
                        SET pseudo = ?, mail = ?, mdp = ?
                        WHERE id = ?
                        """,
                user.getPseudo(),
                user.getMail(),
                user.getMdp(),
                id);
        return rows > 0;
    }

    @Transactional
    public boolean deleteById(int id) {
        List<Integer> articleIds = jdbcTemplate.queryForList(
                "SELECT id FROM articles WHERE user_id = ?",
                Integer.class,
                id);
        for (Integer articleId : articleIds) {
            articleRepository.deleteById(articleId);
        }
        jdbcTemplate.update("DELETE FROM commentaires WHERE user_id = ?", id);

        int rows = jdbcTemplate.update(
                """
                        DELETE FROM users
                        WHERE id = ?
                        """,
                id);
        return rows > 0;
    }

    /**
     * Trouve un user par mail (login).
     * 
     * @return Optional vide si aucun compte avec ce mail
     */
    public Optional<User> findByMail(String mail) {
        String sql = """
                SELECT id, pseudo, mail, mdp, role
                FROM "users"
                WHERE mail = ?
                """;

        return jdbcTemplate.query(sql, USER_ROW_MAPPER, mail).stream().findFirst();
    }

    public Optional<User> findByPseudo(String pseudo) {
        return jdbcTemplate.query(
                """
                        SELECT id, pseudo, mail, mdp, role
                        FROM users
                        WHERE pseudo = ?
                        """,
                USER_ROW_MAPPER,
                pseudo).stream().findFirst();
    }

    public boolean existsByMailForOtherUser(String mail, int excludeId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM users
                        WHERE mail = ? AND id <> ?
                        """,
                Integer.class,
                mail,
                excludeId);
        return count != null && count > 0;
    }

    public boolean existsByPseudoForOtherUser(String pseudo, int excludeId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM users
                        WHERE pseudo = ? AND id <> ?
                        """,
                Integer.class,
                pseudo,
                excludeId);
        return count != null && count > 0;
    }
}