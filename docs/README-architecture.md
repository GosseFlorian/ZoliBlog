# Architecture — couches, schéma et sécurité

Document **explanation** — pourquoi le projet est structuré ainsi et quelles mesures OWASP sont en place.

---

## Schéma global

```
┌─────────────┐     HTTP      ┌──────────────────────────────────┐
│  Navigateur │ ────────────► │  Spring Boot (port 8080)         │
│  admin 5173 │               │  ┌────────────┐  ┌─────────────┐ │
│  site  5174 │ ◄──────────── │  │ Controllers│→ │ Repositories│ │
└─────────────┘   JSON + JWT  │  │ + DTOs     │  │ (JdbcTemplate)│
                              │  │ + Mappers  │  └──────┬──────┘ │
                              │  └────────────┘         │ JDBC   │
                              │  SecurityConfig + filtres         │
                              └─────────────────────────┼────────┘
                                                        ▼
                                              ┌─────────────────┐
                                              │  PostgreSQL     │
                                              │  java_blog      │
                                              └─────────────────┘
```

---

## Couches applicatives

| Couche         | Package / rôle | Responsabilité                                                  |
| -------------- | -------------- | --------------------------------------------------------------- |
| **Controller** | `controller/`  | Routes HTTP, codes de statut, `@Valid`, contrôles métier (IDOR) |
| **DTO**        | `dto/`         | Contrat JSON entrant/sortant, validation Bean Validation        |
| **Mapper**     | `mapper/`      | Conversion Model ↔ DTO (pas de SQL ici)                         |
| **Model**      | `model/`       | Entités métier (Article, User, Commentaire…)                    |
| **Repository** | `repository/`  | SQL via `JdbcTemplate`, paramètres `?`                          |
| **Config**     | `config/`      | Sécurité, CORS, filtres, gestion d'erreurs                      |
| **Util**       | `util/`        | `LogSanitizer`, `InputSanitizer`                                |

Flux typique (lecture article) :

```
GET /articles/{id}
  → ArticleController
  → ArticleRepository.findPublishedById(?)
  → ArticleMapper.toResponse(model)
  → JSON ArticleResponse
```

---

## Sécurité — filtres HTTP

Ordre d'exécution sur chaque requête :

```
LoginRateLimitFilter  →  RequestAuditFilter  →  JwtAuthFilter  →  Controller
```

| Filtre                 | Rôle                                           |
| ---------------------- | ---------------------------------------------- |
| `LoginRateLimitFilter` | Limite les POST `/auth/login` (A07)            |
| `RequestAuditFilter`   | Journalise méthode, route, statut, durée (A09) |
| `JwtAuthFilter`        | Valide le JWT sur les routes protégées         |

---

## OWASP Top 10 — mesures implémentées

| Risque                               | Mesure                                               | Fichier(s)                                     |
| ------------------------------------ | ---------------------------------------------------- | ---------------------------------------------- |
| **A01** Broken Access Control (IDOR) | `verifierUserIdCorrespondAuToken` sur commentaires   | `CommentaireController`                        |
| **A03** Injection SQL                | `JdbcTemplate` + `?`, jamais de concat SQL           | `*Repository.java`                             |
| **A03** XSS                          | `InputSanitizer` + `sanitizeContenu`                 | `CommentaireController`, `util/InputSanitizer` |
| **A05** Security Misconfiguration    | Headers CSP, X-Frame-Options, CORS restrictif        | `SecurityConfig`, `WebConfig`                  |
| **A05** Erreurs exposées             | `GlobalExceptionHandler` — pas de stack trace client | `GlobalExceptionHandler`                       |
| **A07** Auth failures                | BCrypt, `@Valid`, rate limit login                   | `AuthController`, `LoginRateLimitFilter`       |
| **A09** Logging                      | `LogSanitizer`, logs sans secrets                    | `LogSanitizer`, `AuthController`, filtres      |

Exemple requête paramétrée (preuve SQL injection) :

```java
WHERE id = ? AND statut = true
```

---

## Authentification JWT

- **Stateless** : pas de session serveur (`SessionCreationPolicy.STATELESS`)
- Login / register publics → token JWT signé avec `JWT_SECRET`
- Routes `/admin/**` et certaines routes commentaires → header `Authorization: Bearer …`
- Mots de passe hashés en **BCrypt** en base

Voir [adr-0002-jwt.md](adr-0002-jwt.md).

---

## Configuration externalisée

Secrets et paramètres sensibles via `.env` (partie 08-02) :

- `JWT_SECRET`, `DATABASE_URL`, `CORS_ALLOWED_ORIGINS`, `LOG_LEVEL`
- Chargement Spring : `${VAR:default}` dans `application.yaml`

Voir [adr-0003-env.md](adr-0003-env.md).

---

## Frontends

| App   | Dossier  | Port | Consomme                                     |
| ----- | -------- | ---- | -------------------------------------------- |
| Admin | `admin/` | 5173 | Routes `/admin/*` + login                    |
| Site  | `site/`  | 5174 | Routes publiques + commentaires authentifiés |

Build : Vite + React. Qualité front : ESLint + Prettier (`make lint-ts`, `make format-ts`). Raccourcis globaux : `make lint-all`, `make format-all` (alias `make lint`, `make format`).

---

## Qualité backend (Maven)

| Outil | Rôle | Commande |
| ----- | ---- | -------- |
| **Spotless** | Formatage Java (Google Java Format) | `./mvnw spotless:apply` / `spotless:check` |
| **SpotBugs** | Analyse statique (NPE, sécurité, logique) | `./mvnw spotbugs:check` |
| **JaCoCo** | Couverture de tests | rapport après `./mvnw test` → `target/site/jacoco/index.html` |

Faux positifs SpotBugs documentés dans `spotbugs-exclude.xml` (DTOs, injection Spring).

---

## Décisions techniques

Les choix structurants (JDBC vs JPA, JWT, .env, logs) sont documentés dans :

👉 [README-adr.md](./adr/README-adr.md)

---

## Liens

- [README-api.md](README-api.md) — routes HTTP
- [README-exploitation.md](README-exploitation.md) — exploitation
- [doc/partie-08-05-securite-owasp.md](../doc/partie-08-05-securite-owasp.md) — guide implémentation OWASP
