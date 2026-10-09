# ADR-0001 — JDBC + JdbcTemplate (pas JPA/Hibernate)

Date : 2026-02-01  
Statut : Accepté

## Contexte

Le ZoliBlog persiste articles, utilisateurs et commentaires dans PostgreSQL. Il fallait choisir entre :

- **JPA / Hibernate** — ORM, entités annotées, requêtes générées
- **JDBC pur + `JdbcTemplate`** — SQL explicite, mapping manuel vers les models

Les repositories du dépôt portent des requêtes SQL visibles (filtres publication, jointures, CRUD admin). L’équipe privilégie la lisibilité du SQL et le contrôle des requêtes plutôt qu’un mapping ORM implicite.

## Décision

Utiliser **Spring `JdbcTemplate`** avec des classes `*Repository` contenant le SQL en clair et des `RowMapper` pour le mapping ligne → model.

Pas de dépendance `spring-boot-starter-data-jpa`.

## Conséquences

**Positif :**

- SQL visible et auditable (preuve anti-injection avec `?`)
- Requêtes et schéma auditable dans le code source (revue, dépannage, tests)
- Contrôle fin des requêtes (articles publiés, jointures N-N, etc.)

**Négatif :**

- Plus de code boilerplate (mappers, requêtes dupliquées possibles)
- Pas de migrations automatiques type Flyway/Liquibase intégrées au choix ORM
- Évolution vers un ORM demanderait un refactor des repositories

**À revoir plus tard :** si le projet grossit, une couche query builder ou JPA pourrait réduire la duplication — documenter dans un futur ADR.
