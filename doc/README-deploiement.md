# Déploiement — Railway (API + web + Postgres)

Guide **how-to** pour publier le ZoliBlog en **production** (branche `main`, CI verte).

> **Procédure cible :** [§ 6 Railway](#6-railway--2-services-api--web) + détail service web [`web/README.md`](../web/README.md).  
> **Historique Pages + Render :** [ADR-0005](adr/adr-0005-deploiement-pages-render.md) et § 3–4 ci-dessous (stack jury précédente).  
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
| Site + admin (static) | Railway (à venir)  | Même service ou JAR Spring — doc en cours   |
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
psql "<EXTERNAL_DATABASE_URL>" -f backend-api/src/main/resources/blog.sql
```

4. Vérifier le compte démo : `alice@example.com` / `demo1234` (après `blog.sql`).

> Pas de Flyway : `backend-api/src/main/resources/blog.sql` (prod/dev) et `backend-api/src/test/resources/blog-test.sql` (tests).

---

## 4. API Spring Boot sur Render

Prérequis : PostgreSQL Render initialisé (section 3).

### 4.1 Créer le Web Service

1. **Dashboard → New + → Web Service**.
2. **Connect a repository** : autoriser GitHub si besoin, choisir **`ZoliBlog`**.
3. **Branch** : `main` (après merge de la branche déploiement).
4. **Name** : ex. `zoliblog-api` → URL du type `https://zoliblog-api.onrender.com`.
5. **Region** : même région que la base (ex. Frankfurt).
6. **Root Directory** : `backend-api` (module Maven du monorepo).
7. **Runtime** : **Java** (ou **Native** selon l’interface).
8. **Instance type** : **Free** si disponible.

**Build command :**

```bash
./mvnw -B -DskipTests package
```

**Start command :**

```bash
java -Dspring.profiles.active=prod -jar target/java_blog-0.0.1-SNAPSHOT.jar
```

**Advanced → Health Check Path** (si proposé) : `/ping`.

Clique **Create Web Service** (premier build long, normal).

### 4.2 Variables d’environnement (Render)

**Dans le repo** : `application-prod.yaml` (CORS, logs, rate limit, port). **Front Pages** : variable GitHub **`VITE_API_URL`** (§ 5).

**Sur Render — Web Service → Environment** (secrets, jamais dans Git) :

| Variable | Rôle |
| -------- | ---- |
| `JWT_SECRET` | ≥ 32 caractères |
| `DATABASE_URL` | JDBC — § 4.3 |
| `POSTGRES_USER` | Utilisateur Postgres Render |
| `POSTGRES_PASSWORD` | Mot de passe Postgres Render |

Start command :

```bash
java -Dspring.profiles.active=prod -jar target/java_blog-0.0.1-SNAPSHOT.jar
```

**Save Changes** → redeploy. Cf. [ADR-0003](adr/adr-0003-env.md).

### 4.3 JDBC depuis l’URL Postgres Render

Sur la base **PostgreSQL** → **Connect** :

- **Internal Database URL** (recommandé si l’API est sur Render) :  
  `postgresql://USER:PASSWORD@HOST/NOM_BASE`
- Convertir en JDBC pour `DATABASE_URL` :

```text
jdbc:postgresql://HOST:5432/NOM_BASE?sslmode=require
```

Exemple :

```text
postgresql://zoliblog_db_user:****@dpg-xxxx-a/zoliblog_db
```

→

```text
jdbc:postgresql://dpg-xxxx-a:5432/zoliblog_db?sslmode=require
```

(`USER` / `PASSWORD` restent dans `POSTGRES_USER` et `POSTGRES_PASSWORD`.)

### 4.4 Front — URL API

URL publique du service (ex. **`https://zoliblog-production.up.railway.app`**) → variable de dépôt **`VITE_API_URL`** (section 5), puis rebuild Pages (section 6).

### 4.5 Smoke tests

```bash
curl -s https://zoliblog.onrender.com/ping
curl -s https://zoliblog.onrender.com/db/ping
```

Login (optionnel) :

```bash
curl -s -X POST https://zoliblog.onrender.com/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"mail\":\"alice@example.com\",\"mdp\":\"demo1234\"}"
```

**Free tier :** cold start après inactivité — le premier appel peut prendre 30–60 s.

### 4.6 Profil Spring `prod`

Fichier `backend-api/src/main/resources/application-prod.yaml` :

- écoute sur **`PORT`** (injecté par Render) ;
- **CORS** : `https://GosseFlorian.github.io` par défaut ;
- logs **`WARN`**, rate limit login **activé** ;
- secrets JDBC + JWT : variables Render (§ 4.2).

---

## 5. Frontends — variables de build

Les fronts ne lisent **pas** le `.env` dev (Spring). **`VITE_API_URL`** n’est pas un secret (visible dans le JS) mais **dépend de l’environnement** :

| Contexte | Où configurer `VITE_API_URL` |
| -------- | ------------------------------ |
| **Prod (Railway, à venir)** | `VITE_API_URL` = URL publique de l’API au build (ex. même domaine Railway que l’API). |
| **Build prod local** | `cp .env.production.example .env.production` puis éditer (souvent `http://localhost:8080` ou l’URL Railway pour un test). **`.env.production`** reste gitignoré. |
| **Modèle local** | **`.env.production.example`** (versionné) — exemple uniquement, pas utilisé par la CI. |

**Chemin Pages (`base`)** : constante dans chaque `vite.config.ts` (`/ZoliBlog/` et `/ZoliBlog/admin/` en mode `production`, `/` en dev).

`envDir: '..'` : les builds `npm run build` dans `site/` et `admin/` lisent **`.env.production`** à la racine pour `VITE_API_URL`.

**Build prod local (simulation Pages) :**

```bash
cd site && npm run build
cd ../admin && npm run build
```

En **dev** (`npm run dev`), le mode n’est pas `production` → `base: '/'` → `http://localhost:5173` et `5174`.

Détails code : `import.meta.env.VITE_API_URL`, `vite.config.ts` (`base`), `routerBasename()` dans `main.tsx`.

---

## 6. Railway — 2 services (API + web)

| Service | Root Directory (UI) | Build / start (UI) |
| ------- | --------------------- | -------------------- |
| **API** | `backend-api` | `./mvnw -B -DskipTests package` → `java -Dspring.profiles.active=prod -jar target/java_blog-0.0.1-SNAPSHOT.jar` |
| **Web** | *(vide — racine)* | `cd web && npm ci && npm run build` → `cd web && npm start` — détails [`web/README.md`](../web/README.md) |

Pas de `railway.toml` dans le repo pour l’instant : tout se configure dans l’UI Railway.

| URL (exemple) | Service |
| ------------- | ------- |
| `https://zoliblog-production.up.railway.app/ping` | API |
| `https://<web>.up.railway.app/` | Site |
| `https://<web>.up.railway.app/admin/` | Admin |

**Web** : variable **`VITE_API_URL`** = URL de l’API (build).  
**API** : **`CORS_ALLOWED_ORIGINS`** = URL publique du service web.

Pas de Docker : Node + `serve-handler` (`web/server.js`), rewrites SPA pour `/` et `/admin/**`.

---

## 7. Ordre du premier déploiement (Railway)

1. Merge sur **`main`** (CI verte).
2. Créer **Postgres** (Railway ou autre) + exécuter `backend-api/src/main/resources/blog.sql`.
3. Service **API** : Root `backend-api`, build/start Maven, variables JDBC + `JWT_SECRET`, `CORS_ALLOWED_ORIGINS` (URL du web, § 6).
4. Service **web** : racine repo vide, `cd web && npm ci && npm run build` / `cd web && npm start`, **`VITE_API_URL`** = URL API.
5. Smoke : `/ping`, site `/`, admin `/admin/`, login Alice.
6. CORS : origine exacte du web dans `CORS_ALLOWED_ORIGINS`, redéployer l’API si besoin.

*(Stack Render / Pages : § 3–4 si tu restes sur l’ancienne procédure ADR-0005.)*

---

## 8. CI / CD (résumé)

| Pipeline      | Fichier                              | Rôle                                                               |
| ------------- | ------------------------------------ | ------------------------------------------------------------------ |
| **CI**        | `.github/workflows/ci.yml`           | Qualité + tests sur PR / push                                      |
| **CI**        | job `backend-package`                | Artefact **zoliblog-api-jar** (`target/java_blog-*.jar`, 14 jours) |
| **CD fronts** | Railway (à documenter)               | Site + admin avec l’API                                            |
| **CD API**    | Render (lien GitHub)                 | Rebuild JAR sur push `main`                                        |

Parité locale : `make ci` (sans déploiement). JAR local : `cd backend-api && ./mvnw -B -DskipTests package` → `backend-api/target/java_blog-0.0.1-SNAPSHOT.jar`.

Téléchargement du JAR CI : **Actions** → run vert → artefact **zoliblog-api-jar**.

---

## 9. Dépannage déploiement

| Symptôme                         | Piste                                                          |
| -------------------------------- | -------------------------------------------------------------- |
| Assets 404 (JS/CSS)              | `base` dans `vite.config.ts` (repo renommé ≠ `ZoliBlog` ?)     |
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
