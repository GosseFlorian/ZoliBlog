# Documentation exploitation (Diátaxis)

| Fichier | Contenu |
| ------- | ------- |
| [README-diataxis.md](README-diataxis.md) | Hub — types de doc |
| [README-exploitation.md](README-exploitation.md) | Installer, lancer |
| [README-deploiement.md](README-deploiement.md) | Déploiement |
| [README-runbook.md](README-runbook.md) | Dépannage |
| [README-api.md](README-api.md) | Routes HTTP |
| [README-architecture.md](README-architecture.md) | Architecture |
| [adr/README-adr.md](adr/README-adr.md) | ADR |

**SQL (module `backend-api`)**

| Fichier | Usage |
| ------- | ----- |
| [blog.sql](../backend-api/src/main/resources/blog.sql) | Seed dev `java_blog` (schéma + BCrypt Alice) — `make db-init` |
| [blog-test.sql](../backend-api/src/test/resources/blog-test.sql) | Schéma + seed `java_blog_test` — `make test` ou `cd backend-api && ./mvnw test` |

`make db-test` crée la base vide **`java_blog_test`** si besoin ; le contenu est appliqué par les tests via **`blog-test.sql`**.
