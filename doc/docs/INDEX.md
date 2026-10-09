# Index des supports — Blog Java / Spring Boot

> Ordre de lecture et d'exécution. Suis les numéros : chaque étape s'appuie sur la précédente.

## Fil rouge — parties 01 à 07

| Partie | Métaphore          | Ce que tu fais                                                          |
| ------ | ------------------ | ----------------------------------------------------------------------- |
| **01** | Post-it en mémoire | Classe `Article` + getters, routes `/ping` et `/articles`               |
| **02** | Poser le câble     | PostgreSQL + JDBC + `/db/ping` + `/articles/recents` + carte des routes |
| **03** | Chantier propre    | Couches (DTO, mapper, repository) + **CRUD articles complet**           |
| **04** | Back-office        | React **composants + props** — admin articles (très commenté)           |
| **05** | Verrou             | Authentification JWT + login React + `/admin` protégé                   |
| **06** | Usine              | DevOps : tests auto + CI + documentation                                |
| **07** | TP libre           | Routes restantes + front-office React `site/`                           |
| **08** | Exploitation       | `.env`, lint, Makefile, logs, OWASP, doc Diátaxis (à toi de coder)      |

> 💡 **Syntaxe partie 03 :** model `Article` = classe + `getTitre()` / `setId()` ; DTO = **records** + `body.titre()`.

## Convention de nommage

Chaque **partie** regroupe ses supports ainsi :

| Fichier                                    | Rôle                     |
| ------------------------------------------ | ------------------------ |
| `partie-01-01-installation-spring-boot.md` | Support principal (code) |
| `partie-01-02-corriges-exercices.md`       | Corrigé des exercices    |
| `annexe-01-glossaire.md`                   | Glossaire (en parallèle) |

**Schéma :** `partie-NN-SS-titre.md` — `NN` = partie, `SS` = ordre dans la partie (`01`, `02`…).

**Branche Git associée :** `partie-01` (partie 01), `partie-02` (partie 02), etc.

---

## Vue rapide

### Partie 01 — `partie-01-…`

| Fichier                                                                              | Document                                 | Qui                   | Code ? |
| ------------------------------------------------------------------------------------ | ---------------------------------------- | --------------------- | ------ |
| [partie-01-01-installation-spring-boot.md](partie-01-01-installation-spring-boot.md) | Installation et premiers pas Spring Boot | Élève                 | Oui    |
| [annexe-01-glossaire.md](annexe-01-glossaire.md)                                     | Glossaire                                | Élève                 | Non    |
| [partie-01-02-corriges-exercices.md](partie-01-02-corriges-exercices.md)             | Corrigé exercices                        | Élève _(après essai)_ | Oui    |

### Partie 02 — `partie-02-…`

| Fichier                                                                                  | Document                                        | Qui               | Code ?  |
| ---------------------------------------------------------------------------------------- | ----------------------------------------------- | ----------------- | ------- |
| [partie-02-01-install-postgresql-pgadmin.md](partie-02-01-install-postgresql-pgadmin.md) | Installation PostgreSQL + pgAdmin + branche Git | Élève             | Non     |
| [partie-02-02-vue-ensemble-architecture.md](partie-02-02-vue-ensemble-architecture.md)   | Vue d'ensemble architecture                     | Élève / formateur | Non     |
| [partie-02-03-connexion-bdd.md](partie-02-03-connexion-bdd.md)                           | pgAdmin + connexion BDD + `/db/ping`            | Élève             | Oui     |
| [partie-02-04-premiere-route-bdd.md](partie-02-04-premiere-route-bdd.md)                 | Première route `/articles/recents`              | Élève             | Oui     |
| [partie-02-05-concevoir-les-routes.md](partie-02-05-concevoir-les-routes.md)             | Concevoir les routes du blog                    | Élève             | **Non** |
| [partie-02-06-corrige-routes-blog.md](partie-02-06-corrige-routes-blog.md)               | Corrigé routes blog                             | **Formateur**     | Non     |

### Partie 03 — `partie-03-…`

| Fichier                                                                        | Document                                      | Qui   | Code ? |
| ------------------------------------------------------------------------------ | --------------------------------------------- | ----- | ------ |
| [partie-03-01-cadrage-couches.md](partie-03-01-cadrage-couches.md)             | Cadrage : couches et 2 mappings + branche Git | Élève | Non    |
| [partie-03-02-model-et-dtos.md](partie-03-02-model-et-dtos.md)                 | `Article` + 3 DTO (4 fichiers)                | Élève | Oui    |
| [partie-03-03-mapper-et-repository.md](partie-03-03-mapper-et-repository.md)   | `ArticleMapper` + `ArticleRepository`         | Élève | Oui    |
| [partie-03-04-controller-get-public.md](partie-03-04-controller-get-public.md) | `ArticleController` (GET)                     | Élève | Oui    |
| [partie-03-05-controller-admin-crud.md](partie-03-05-controller-admin-crud.md) | `AdminArticleController` (CRUD)               | Élève | Oui    |

### Références

| Fichier                                                                  | Usage                                                    |
| ------------------------------------------------------------------------ | -------------------------------------------------------- |
| [blog.sql](../../backend-api/src/main/resources/blog.sql)                | Schéma SQL + données de test (install partie 02)         |
| [sql/README.md](sql/README.md)                                           | Upgrades SQL par partie (`upgrade-05-…`, `upgrade-06-…`) |
| [annexe-reinstall-propre.md](annexe-reinstall-propre.md)                 | **Réinstall propre** — macOS / Linux                     |
| [annexe-reinstall-propre-windows.md](annexe-reinstall-propre-windows.md) | **Réinstall propre** — Windows                           |
| [questions.csv](questions.csv)                                           | QCM formateur                                            |

---

## Parcours détaillé

### Partie 01

```
partie-01-01-installation-spring-boot.md
        │
        ├── Java 21, Spring Boot, /ping, /articles
        │
        ├── annexe-01-glossaire.md              (en parallèle)
        └── partie-01-02-corriges-exercices.md  (après essai)
```

**Branche Git :** `partie-01` _(contient uniquement cette étape ; `main` = projet Initializr seul)_

---

### Partie 02

```
partie-02-01-install-postgresql-pgadmin.md   (install PostgreSQL + branche Git)
        │
        ▼
partie-02-02-vue-ensemble-architecture.md     (cadrage, optionnel)
        │
        ▼
partie-02-03-connexion-bdd.md                (connexion BDD + /db/ping + commit Git)
        │
        ▼
partie-02-04-premiere-route-bdd.md          (/articles/recents + commit Git)
        │
        ▼
partie-02-05-concevoir-les-routes.md        (routes sur papier)
        │
        ▼
partie-02-06-corrige-routes-blog.md         (formateur)
```

**Branche Git :** `partie-02` _(créée dans `partie-02-01` ; premier commit après le code de connexion)_

---

### Partie 03

```
partie-03-01-cadrage-couches.md        (concepts + branche Git)
        │
        ▼
partie-03-02-model-et-dtos.md          (Article + DTOs + nettoyage partie 01 + commit)
        │
        ▼
partie-03-03-mapper-et-repository.md   (mapper + SQL + commit)
        │
        ▼
partie-03-04-controller-get-public.md  (ArticleController GET + commit)
        │
        ▼
partie-03-05-controller-admin-crud.md   (AdminArticleController + commit)
```

**Branche Git :** `partie-03` _(API articles complète — socle pour le React admin)_

---

### Partie 04 — Back-office React

> 🎯 **Pédagogie :** composants + **props** avant tout ; state/`useEffect` surtout dans le parent ; **très commenté** (public ~2 jours de React) ; **bonnes pratiques** obligatoires (1 composant = 1 fichier, props down / events up, pas de Redux).

> 💡 **Ancienne copie du projet ?** [macOS / Linux](annexe-reinstall-propre.md) · [Windows](annexe-reinstall-propre-windows.md)

| Fichier                                                                                | Document                                  | Code ? |
| -------------------------------------------------------------------------------------- | ----------------------------------------- | ------ |
| [partie-04-01-cadrage-react-composants.md](partie-04-01-cadrage-react-composants.md)   | Cadrage : composants, props, arborescence | Non    |
| [partie-04-02-setup-react-vite.md](partie-04-02-setup-react-vite.md)                   | Vite, dossier `admin/`, CORS Spring       | Oui    |
| [partie-04-03-premiers-composants-props.md](partie-04-03-premiers-composants-props.md) | Composants présentation (données en dur)  | Oui    |
| [partie-04-04-appels-api.md](partie-04-04-appels-api.md)                               | Module `api/` + chargement liste          | Oui    |
| [partie-04-05-formulaire-crud.md](partie-04-05-formulaire-crud.md)                     | `ArticleForm` + POST / PUT                | Oui    |
| [partie-04-06-suppression-et-recap.md](partie-04-06-suppression-et-recap.md)           | DELETE, erreurs HTTP, récap               | Oui    |

```
partie-04-01-cadrage-react-composants.md
        │
        ▼
partie-04-02-setup-react-vite.md           (Vite + admin/ + CORS + branche Git)
        │
        ▼
partie-04-03-premiers-composants-props.md  (PageHeader, ArticleCard, ArticleList — props)
        │
        ▼
partie-04-04-appels-api.md                 (fetch + useEffect dans App)
        │
        ▼
partie-04-05-formulaire-crud.md             (ArticleForm réutilisable create/edit)
        │
        ▼
partie-04-06-suppression-et-recap.md       (DELETE + commits Git)
```

**Branche Git :** `partie-04`

---

### Partie 05 — Authentification

> 🎯 **Pédagogie :** JWT (adapté React + CORS) ; BCrypt pour les mots de passe ; Spring Security en **3 morceaux** (login → filtre → React).

| Fichier                                                                          | Document                                       | Code ? |
| -------------------------------------------------------------------------------- | ---------------------------------------------- | ------ |
| [partie-05-01-cadrage-auth.md](partie-05-01-cadrage-auth.md)                     | Cadrage : authN/authZ, JWT, plan + branche Git | Non    |
| [partie-05-02-login-api-jwt.md](partie-05-02-login-api-jwt.md)                   | `User`, repository, `POST /auth/login`, JWT    | Oui    |
| [partie-05-03-securiser-admin-spring.md](partie-05-03-securiser-admin-spring.md) | Spring Security + filtre JWT, `/admin/**`      | Oui    |
| [partie-05-04-login-react.md](partie-05-04-login-react.md)                       | `LoginForm` + token dans les `fetch`           | Oui    |

```
partie-05-01-cadrage-auth.md
        │
        ▼
partie-05-02-login-api-jwt.md            (login + génération token)
        │
        ▼
partie-05-03-securiser-admin-spring.md   (filtre + routes protégées)
        │
        ▼
partie-05-04-login-react.md              (écran login admin)
```

**Branche Git :** `partie-05`

---

### Partie 06 — DevOps (tests & CI)

> 🎯 **Pédagogie :** pyramide des tests ; **PostgreSQL** (`java_blog` + `java_blog_test`) ; pipeline GitHub Actions ; **documentation** (`README.md`, `doc/API.md`). Docker = cours séparé.

| Fichier                                                                            | Document                                    | Code ? |
| ---------------------------------------------------------------------------------- | ------------------------------------------- | ------ |
| [partie-06-01-cadrage-devops.md](partie-06-01-cadrage-devops.md)                   | Cadrage DevOps, pyramide tests, branche Git | Non    |
| [partie-06-02-tests-backend.md](partie-06-02-tests-backend.md)                     | JUnit unitaires + `@JdbcTest` PostgreSQL    | Oui    |
| [partie-06-03-tests-api-mockmvc.md](partie-06-03-tests-api-mockmvc.md)             | MockMvc public + admin JWT                  | Oui    |
| [partie-06-04-pipeline-github-actions.md](partie-06-04-pipeline-github-actions.md) | GitHub Actions CI (+ Vitest optionnel)      | Oui    |
| [partie-06-05-recap-devops.md](partie-06-05-recap-devops.md)                       | Makefile + README + `doc/API.md` + récap    | Oui    |

```
partie-06-01-cadrage-devops.md
        │
        ▼
partie-06-02-tests-backend.md           (mapper, JWT, repository PostgreSQL)
        │
        ▼
partie-06-03-tests-api-mockmvc.md       (HTTP + sécurité admin)
        │
        ▼
partie-06-04-pipeline-github-actions.md (CI automatique)
        │
        ▼
partie-06-05-recap-devops.md            (Makefile + README + doc/API.md)
```

**Branche Git :** `partie-06`

**Fichiers DevOps créés :** `.github/workflows/ci.yml`, `Makefile`, `README.md`, `doc/API.md`, `src/test/resources/*`

---

### Partie 07 — TP

> 🎯 **Pédagogie :** autonomie sur les **couches** (partie 03) ; routes de [partie-02-06](partie-02-06-corrige-routes-blog.md) ; front **public** distinct de `admin/`.

| Fichier                                                                        | Document                           | Qui           | Code ? |
| ------------------------------------------------------------------------------ | ---------------------------------- | ------------- | ------ |
| [partie-07-01-enonce-tp-routes.md](partie-07-01-enonce-tp-routes.md)           | Énoncé TP — routes + `site/`       | Élève         | Non    |
| [partie-07-02-corrige-formateur.md](partie-07-02-corrige-formateur.md)         | Corrigé backend + front public     | **Formateur** | Oui    |
| [partie-07-03-tests-tp-commentaires.md](partie-07-03-tests-tp-commentaires.md) | Tests JUnit commentaires _(bonus)_ | Élève         | Oui    |

```
partie-07-01-enonce-tp-routes.md     (cadrage TP + branche Git)
        │
        ├── partie-07-03-tests-tp-commentaires.md   (bonus DevOps — après commentaires)
        │
        ▼
partie-07-02-corrige-formateur.md    (formateur — après remise)
```

**Branche Git :** `partie-07` _(travail élève — commits libres par thème)_

---

## Branches Git

| Branche     | Contenu                                                                          |
| ----------- | -------------------------------------------------------------------------------- |
| `main`      | Projet Spring Boot vierge (Initializr uniquement)                                |
| `partie-01` | Étape 01 — API en mémoire (`Article`, `/ping`, `/articles`)                      |
| `partie-02` | Partie 02 — PostgreSQL, JDBC, `/db/ping`, `/articles/recents`, conception routes |
| `partie-03` | API articles — couches + CRUD complet (socle backend)                            |
| `partie-04` | Frontend React back-office (CRUD articles admin)                                 |
| `partie-05` | Auth JWT + sécurisation `/admin` + login React                                   |
| `partie-06` | DevOps : tests + CI + documentation projet                                       |
| `partie-07` | TP — routes complètes + front-office React `site/`                               |

```bash
git checkout main
git checkout partie-01
git checkout partie-02
git checkout partie-03
git checkout partie-04
git checkout partie-05
git checkout partie-06
git checkout partie-07
```

---

## Commits attendus par branche

Chaque **partie** = **une branche** ; chaque **étape avec code** = **un commit** (message `NN-SS — …`).

| Branche     | Création                                                   | Commits (exemples)        |
| ----------- | ---------------------------------------------------------- | ------------------------- |
| `partie-01` | [partie-01-01](partie-01-01-installation-spring-boot.md)   | fin partie 01             |
| `partie-02` | [partie-02-01](partie-02-01-install-postgresql-pgadmin.md) | `02-03`, `02-04`          |
| `partie-03` | [partie-03-01](partie-03-01-cadrage-couches.md)            | `03-02` … `03-05`         |
| `partie-04` | [partie-04-01](partie-04-01-cadrage-react-composants.md)   | `04-02` … `04-06`         |
| `partie-05` | [partie-05-01](partie-05-01-cadrage-auth.md)               | `05-02` … `05-04`         |
| `partie-06` | [partie-06-01](partie-06-01-cadrage-devops.md)             | `06-02` … `06-05`         |
| `partie-07` | [partie-07-01](partie-07-01-enonce-tp-routes.md)           | commits libres (`07 — …`) |

> 💡 Les **cadrages `-01`** créent la branche **sans commit obligatoire**. Chaque support **code** rappelle la branche active et se termine par un commit. **Partie 07** : commits au fil du TP (voir énoncé).

### Partie 08 — `partie-08-…` _(exploitation & sécurité — tu codes)_

| Fichier                                                                                        | Document                                 | Code ?        |
| ---------------------------------------------------------------------------------------------- | ---------------------------------------- | ------------- |
| [partie-08-01-cadrage-exploitation-securite.md](partie-08-01-cadrage-exploitation-securite.md) | Cadrage : npm vs Makefile, plan RNCP     | Non           |
| [partie-08-02-variables-environnement.md](partie-08-02-variables-environnement.md)             | `.env`, dotenv, Spring config            | **Oui (toi)** |
| [partie-08-03-eslint-prettier-scripts.md](partie-08-03-eslint-prettier-scripts.md)             | ESLint, Prettier, scripts npm + Makefile | **Oui (toi)** |
| [partie-08-04-journalisation-securisee.md](partie-08-04-journalisation-securisee.md)           | SLF4J / Logback sans secrets             | **Oui (toi)** |
| [partie-08-05-securite-owasp.md](partie-08-05-securite-owasp.md)                               | OWASP Top 10 appliqué                    | **Oui (toi)** |
| [partie-08-06-documentation-diataxis.md](partie-08-06-documentation-diataxis.md)               | `docs/README-exploitation.md`, etc.      | **Oui (toi)** |

Branche Git : **`partie-08`**. Doc exploitation cible : `docs/README-<rôle>.md` (pas de sous-dossiers par rôle).

---

## Prolongements (hors parcours)

| Sujet           | Contenu              |
| --------------- | -------------------- |
| **Docker**      | Cours dédié          |
| **Déploiement** | Render, Fly.io, VPS… |
