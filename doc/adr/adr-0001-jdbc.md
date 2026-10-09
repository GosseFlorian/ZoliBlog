# ADR-0001 — JDBC + JdbcTemplate (pas JPA/Hibernate)

Date : 2026-02-01  
Statut : Accepté

## Contexte

Le projet blog est une formation ADA centrée sur la compréhension du SQL et des couches applicatives. Il fallait choisir entre :

- **JPA / Hibernate** — ORM, entités annotées, requêtes générées
- **JDBC pur + `JdbcTemplate`** — SQL explicite, mapping manuel vers les models

Le référentiel CDA exige la maîtrise de la persistance et de l'accès aux données ; le parcours pédagogique (parties 02–04) construit les repositories requête par requête.

## Décision

Utiliser **Spring `JdbcTemplate`** avec des classes `*Repository` contenant le SQL en clair et des `RowMapper` pour le mapping ligne → model.

Pas de dépendance `spring-boot-starter-data-jpa`.

## Conséquences

**Positif :**

- SQL visible et auditable (preuve anti-injection avec `?`)
- Aligné avec les objectifs pédagogiques du cursus
- Contrôle fin des requêtes (articles publiés, jointures N-N, etc.)

**Négatif :**

- Plus de code boilerplate (mappers, requêtes dupliquées possibles)
- Pas de migrations automatiques type Flyway/Liquibase intégrées au choix ORM
- Évolution vers un ORM demanderait un refactor des repositories

**À revoir plus tard :** si le projet grossit, une couche query builder ou JPA pourrait réduire la duplication — documenter dans un futur ADR.
