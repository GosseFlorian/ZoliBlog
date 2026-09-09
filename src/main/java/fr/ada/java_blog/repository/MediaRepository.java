package fr.ada.java_blog.repository;

import fr.ada.java_blog.model.Media;
import fr.ada.java_blog.model.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MediaRepository {

    private static final RowMapper<Media> RowMapper = (rs, rowNum) -> new Media(
            rs.getInt("id"),
            MediaType.valueOf(rs.getString("type")),
            rs.getString("url"));

    private final JdbcTemplate jdbcTemplate;

    public MediaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Media> findByArticleId(int articleId) {
        return jdbcTemplate.query(
                """
                        SELECT m.id, m.type, m.url
                        FROM médias m
                        JOIN articles_medias am ON am.media_id = m.id
                        WHERE am.article_id = ?
                        """,
                RowMapper,
                articleId);
    }

    public Optional<Media> findById(int id) {
        List<Media> medias = jdbcTemplate.query(
                """
                        SELECT id, type, url
                        FROM médias
                        WHERE id = ?
                        """,
                RowMapper,
                id);
        return medias.stream().findFirst();
    }

    public Media save(Media media) {
        Integer id = jdbcTemplate.queryForObject(
                """
                        INSERT INTO médias (type, url)
                        VALUES (?::type, ?)
                        RETURNING id
                        """,
                Integer.class,
                media.getType().name(),
                media.getUrl());
        media.setId(id);
        return media;
    }

    public boolean deleteById(int id) {
        int rows = jdbcTemplate.update(
                """
                        DELETE FROM médias
                        WHERE id = ?
                        """, id);
        return rows > 0;
    }

    public void lierArticle(int articleId, int mediaId) {
        jdbcTemplate.update(
                """
                        INSERT INTO articles_medias (article_id, media_id)
                        VALUES (?, ?)
                        """,
                articleId, mediaId);
    }
}