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
