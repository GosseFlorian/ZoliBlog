# Déploiement — GitHub Pages + API Render

Guide **how-to** pour publier le ZoliBlog en **production** (coût nul, branche `main`).

> **Décision d’architecture :** [ADR-0005 — Pages + Render](adr/adr-0005-deploiement-pages-render.md)  
> **Dev local :** [README-exploitation.md](README-exploitation.md) (Makefile, `.env` racine pour Spring uniquement)

---

## 1. Vue d’ensemble

```
Navigateur
    │
    ├─► https://<user>.github.io/ZoliBlog/          (site public — GitHub Pages)
    ├─► https://<user>.github.io/ZoliBlog/admin/    (back-office — GitHub Pages)
    │
    └─► fetch HTTPS ──► https://<api>.onrender.com  (Spring Boot — Render)
                              │
                              └─► PostgreSQL (Render Postgres)
```

| Composant             | Hébergeur          | Branche / déclencheur                     |
| --------------------- | ------------------ | ----------------------------------------- |
| Site + admin (static) | GitHub Pages       | Push `main` → workflow `deploy-pages.yml` |
| API JAR               | Render Web Service | Push `main` (auto-deploy GitHub)          |
| Base de données       | Render Postgres    | Manuelle (création + seed SQL)            |

Remplace `<user>` par ton identifiant GitHub (ex. `GosseFlorian`). Le dépôt doit s’appeler **`ZoliBlog`** (project page → chemin `/ZoliBlog/`).

---

## 2. Prérequis

| Outil / compte                      | Usage                                                       |
| ----------------------------------- | ----------------------------------------------------------- |
| Repo GitHub **ZoliBlog**            | Code + Actions + Pages                                      |
| Compte [Render](https://render.com) | API + PostgreSQL                                            |
| CI verte sur `main`                 | Tests avant merge ([`ci.yml`](../.github/workflows/ci.yml)) |

Secrets **jamais** dans Git : JWT, mot de passe Postgres → Render (cf. [ADR-0003](adr/adr-0003-env.md)).

---

## 3. PostgreSQL sur Render

1. **Dashboard Render → New → PostgreSQL** (free tier si disponible).
2. Noter : host, port, database, user, password, **Internal Database URL** (pour l’API sur Render).
3. **Initialiser le schéma** (une fois), depuis ta machine avec `psql` et l’URL **externe** :

```bash
psql "<EXTERNAL_DATABASE_URL>" -f doc/sql/blog.sql
```

4. Vérifier le compte démo : `alice@example.com` / `demo1234` (après `blog.sql`).

> Pas de Flyway : toute évolution de schéma passe par les scripts `doc/sql/` documentés ici.

---

## 4. API Spring Boot sur Render

1. **New → Web Service** → connecter le repo **ZoliBlog**, branche **`main`**.
2. **Root directory** : `/` (racine Maven).
3. **Runtime** : Java (ou Native).
4. **Build command** :

```bash
./mvnw -B -DskipTests package
```

5. **Start command** :

```bash
java -jar target/java_blog-0.0.1-SNAPSHOT.jar
```

6. **Variables d’environnement** (exemple) :

| Variable                            | Exemple / remarque                                |
| ----------------------------------- | ------------------------------------------------- |
| `JWT_SECRET`                        | Chaîne aléatoire **≥ 32 caractères**              |
| `DATABASE_URL`                      | `jdbc:postgresql://…` (URL JDBC Render)           |
| `POSTGRES_USER`                     | Utilisateur Render                                |
| `POSTGRES_PASSWORD`                 | Mot de passe Render                               |
| `CORS_ALLOWED_ORIGINS`              | `https://<user>.github.io` (**sans** `/ZoliBlog`) |
| `LOG_LEVEL`                         | `INFO` ou `WARN`                                  |
| `SECURITY_LOGIN_RATE_LIMIT_ENABLED` | `true`                                            |

7. **Health check path** (si proposé) : `/ping`.

8. Noter l’URL publique de l’API, ex. `https://zoliblog-api.onrender.com` → sert de **`VITE_API_URL`** pour les builds front.

**Smoke test :**

```bash
curl -s https://<api>.onrender.com/ping
curl -s https://<api>.onrender.com/db/ping
```

**Free tier :** l’API peut **s’endormir** ; le premier appel après inactivité est lent (cold start) — normal pour une démo diplôme.

---

## 5. Frontends — variables de build

Les fronts ne lisent **pas** le `.env` racine (réservé à Spring). En production, les variables sont passées **au moment du build** (GitHub Actions).

| Variable       | Site (`site/`)              | Admin (`admin/`)   |
| -------------- | --------------------------- | ------------------ |
| `VITE_API_URL` | URL Render (sans `/` final) | idem               |
| `VITE_BASE`    | `/ZoliBlog/`                | `/ZoliBlog/admin/` |

**Exemple en local (simulation Pages, Git Bash) :**

```bash
cd site
VITE_API_URL=https://<api>.onrender.com VITE_BASE=/ZoliBlog/ npm run build

cd ../admin
VITE_API_URL=https://<api>.onrender.com VITE_BASE=/ZoliBlog/admin/ npm run build
```

En **dev** (`npm run dev`), ne pas définir `VITE_BASE` : défaut `/` → `http://localhost:5173` et `5174`.

Détails code : `import.meta.env.VITE_API_URL`, `vite.config.ts` (`base`), `routerBasename()` dans `main.tsx`.

---

## 6. GitHub Pages

1. Repo **ZoliBlog → Settings → Pages**.
2. **Source** : **GitHub Actions** (pas « Deploy from branch »).
3. Le workflow **`deploy-pages.yml`** (à la racine `.github/workflows/`) :
   - build site + admin avec les `VITE_*` ci-dessus ;
   - fusionne les `dist/` en un artefact unique ;
   - copie `404.html` pour le routing SPA (rafraîchissement d’URL).

4. **Variables du repo** (Settings → Secrets and variables → Actions) :
   - **`VITE_API_URL`** : variable (URL publique Render) — utilisée par le workflow de déploiement.

**URLs attendues après déploiement :**

| App   | URL                                        |
| ----- | ------------------------------------------ |
| Site  | `https://<user>.github.io/ZoliBlog/`       |
| Admin | `https://<user>.github.io/ZoliBlog/admin/` |

---

## 7. Ordre du premier déploiement

1. Merge des changements de déploiement sur **`main`** (CI verte).
2. Créer Postgres Render + exécuter `doc/sql/blog.sql`.
3. Créer le Web Service Render + variables + vérifier `/ping`.
4. Configurer `VITE_API_URL` dans GitHub Actions.
5. Activer Pages (Actions) et lancer / vérifier le workflow deploy.
6. Tester dans le navigateur (site, login, admin Alice).
7. En cas d’erreur CORS : vérifier `CORS_ALLOWED_ORIGINS` et l’origine exacte dans la console réseau.

---

## 8. CI / CD (résumé)

| Pipeline      | Fichier                              | Rôle                                                                 |
| ------------- | ------------------------------------ | -------------------------------------------------------------------- |
| **CI**        | `.github/workflows/ci.yml`           | Qualité + tests sur PR / push                                        |
| **CI**        | job `backend-package`                | Artefact **zoliblog-api-jar** (`target/java_blog-*.jar`, 14 jours)   |
| **CD fronts** | `.github/workflows/deploy-pages.yml` | Build + publication Pages                                            |
| **CD API**    | Render (lien GitHub)                 | Rebuild JAR sur push `main`                                          |

Parité locale : `make ci` (sans déploiement). JAR local : `./mvnw -B -DskipTests package` → `target/java_blog-0.0.1-SNAPSHOT.jar`.

Téléchargement du JAR CI : **Actions** → run vert → artefact **zoliblog-api-jar**.

---

## 9. Dépannage déploiement

| Symptôme                         | Piste                                                          |
| -------------------------------- | -------------------------------------------------------------- |
| Assets 404 (JS/CSS)              | `VITE_BASE` incorrect ou oublié au build                       |
| F5 sur `/connexion` → 404 GitHub | Fichiers `404.html` manquants (workflow deploy)                |
| `Failed to fetch` / CORS         | `CORS_ALLOWED_ORIGINS` ou mauvaise `VITE_API_URL` **au build** |
| API lente au 1er clic            | Cold start Render free tier                                    |
| Login 401 Alice                  | Seed SQL non appliqué sur Postgres Render                      |
| Admin 403                        | Compte sans rôle ADMIN en base                                 |

→ Runbook général : [README-runbook.md](README-runbook.md)

---

## 10. Liens

| Document                                             | Contenu                 |
| ---------------------------------------------------- | ----------------------- |
| [README-exploitation.md](README-exploitation.md)     | Installation dev        |
| [README-runbook.md](README-runbook.md)               | Incidents               |
| [README-architecture.md](README-architecture.md)     | Couches, CORS, sécurité |
| [ADR-0005](adr/adr-0005-deploiement-pages-render.md) | Pourquoi Pages + Render |
