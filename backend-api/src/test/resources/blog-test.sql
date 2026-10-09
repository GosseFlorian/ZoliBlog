-- Schéma minimal + seed pour java_blog_test (JUnit, profil test, CI).
-- Exécuté via application-test.yaml (spring.sql.init) et le job CI backend.

DROP TABLE IF EXISTS articles_categories CASCADE;
DROP TABLE IF EXISTS articles_medias CASCADE;
DROP TABLE IF EXISTS commentaires CASCADE;
DROP TABLE IF EXISTS medias CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS articles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TYPE IF EXISTS user_role CASCADE;
DROP TYPE IF EXISTS "type" CASCADE;

CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE "type" AS ENUM ('image', 'video', 'gif', 'musique');

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    pseudo VARCHAR(255) UNIQUE NOT NULL,
    mail VARCHAR(255) UNIQUE NOT NULL,
    mdp VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'USER'
);

CREATE TABLE articles (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    contenu TEXT NOT NULL,
    statut BOOLEAN NOT NULL,
    date TIMESTAMP NOT NULL,
    "update" TIMESTAMP NOT NULL,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE commentaires (
    id SERIAL PRIMARY KEY,
    contenu TEXT NOT NULL,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    article_id INT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    date TIMESTAMP NOT NULL
);

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE medias (
    id SERIAL PRIMARY KEY,
    "type" "type" NOT NULL,
    url VARCHAR(255) NOT NULL
);

CREATE TABLE articles_categories (
    id SERIAL PRIMARY KEY,
    article_id INT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    categorie_id INT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT articles_categories_unique UNIQUE (article_id, categorie_id)
);

CREATE TABLE articles_medias (
    id SERIAL PRIMARY KEY,
    media_id INT NOT NULL REFERENCES medias(id) ON DELETE CASCADE,
    article_id INT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    CONSTRAINT articles_medias_unique UNIQUE (article_id, media_id)
);

-- Hash BCrypt de "demo1234" (même qu'en partie 05)
INSERT INTO users (id, pseudo, mail, mdp, role) VALUES
(1, 'alice_dev', 'alice@example.com', '$2y$10$dogkYyhsfVKlpjKpyhRUkecSPVCJA3D5yUSvj4L050OGVolNJUuG6', 'ADMIN'),
(2, 'bob_martin', 'bob@example.com', '$2y$10$dogkYyhsfVKlpjKpyhRUkecSPVCJA3D5yUSvj4L050OGVolNJUuG6', 'USER');

INSERT INTO articles (id, titre, contenu, statut, date, "update", user_id) VALUES
(1, 'Article test CI', 'Contenu pour JUnit', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(2, 'Brouillon test', 'Non publié', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(3, 'Article de Bob', 'Contenu Bob', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2);

INSERT INTO commentaires (contenu, user_id, article_id, date) VALUES
('Commentaire test', 1, 1, CURRENT_TIMESTAMP),
('Commentaire de Bob', 2, 1, CURRENT_TIMESTAMP),
('Commentaire sur article Bob', 1, 3, CURRENT_TIMESTAMP);

INSERT INTO categories (id, nom, description) VALUES
(1, 'Java', 'Articles sur Java');

INSERT INTO articles_categories (article_id, categorie_id) VALUES
(1, 1),
(3, 1);

SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users));
SELECT setval(pg_get_serial_sequence('articles', 'id'), (SELECT MAX(id) FROM articles));
SELECT setval(pg_get_serial_sequence('categories', 'id'), (SELECT MAX(id) FROM categories));
