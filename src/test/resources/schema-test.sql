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
    pseudo VARCHAR(255),
    mail VARCHAR(255),
    mdp VARCHAR(255),
    role user_role NOT NULL DEFAULT 'USER'
);

CREATE TABLE articles (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255),
    contenu TEXT,
    statut BOOLEAN,
    date TIMESTAMP,
    "update" TIMESTAMP,
    user_id INT
);

CREATE TABLE commentaires (
    id SERIAL PRIMARY KEY,
    contenu TEXT,
    user_id INT,
    article_id INT,
    date TIMESTAMP
);

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255),
    description TEXT
);

CREATE TABLE medias (
    id SERIAL PRIMARY KEY,
    "type" "type",
    url VARCHAR(255)
);

CREATE TABLE articles_categories (
    id SERIAL PRIMARY KEY,
    article_id INT,
    categorie_id INT
);
