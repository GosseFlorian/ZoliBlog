# Déploiement — Railway

Guide **how-to** pour publier le ZoliBlog en production (branche `main`, **CI verte** obligatoire avant merge).

> **Dev local :** [README-exploitation.md](README-exploitation.md)  
> **Décision :** [ADR-0005 — Railway](adr/adr-0005-deploiement-railway.md)

---

## 1. Architecture prod

```
Navigateur
    │
    ├─► https://<fronts>.up.railway.app/           site (Vite, base /)
    ├─► https://<fronts>.up.railway.app/admin/     admin (Vite, base /admin/)
    │
    └─► fetch HTTPS ──► https://<api>.up.railway.app   API Spring Boot
                              │
                              └─► PostgreSQL (Railway)
```

| Service Railway | Root Directory | Rôle |
| ----------------- | -------------- | ---- |
| **API** | `backend-api` | JAR Spring Boot, profil `prod` |
| **Fronts** | *(racine repo)* | `npm run build` + Express [`server.js`](../server.js) |
| **Postgres** | plugin DB | Schéma via `blog.sql` |

---

## 2. Prérequis

- Repo GitHub + [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) vert sur `main`
- Projet Railway (3 ressources : Postgres, API, Fronts)
- Compte démo après seed : `alice@example.com` / `demo1234`

---

## 3. Variables d’environnement

### Où les mettre ?

| Type | Exemple | Où |
| ---- | ------- | --- |
| **Secrets** | `JWT_SECRET`, mots de passe Postgres | **Railway uniquement** (service API) — jamais dans Git ([ADR-0003](adr/adr-0003-env.md)) |
| **Config Spring prod** | JDBC, `CORS_ALLOWED_ORIGINS` | Railway (service API) |
| **`VITE_API_URL`** | URL publique de l’API | Service **Fronts** sur Railway (build Vite) |

### `VITE_API_URL` (build des fronts)

Valeur **publique** (visible dans le JS). À définir sur le service **Fronts** :

```text
VITE_API_URL=https://<api>.up.railway.app
```

Sans slash final. Après modification → **redeploy** du service Fronts (rebuild).

Le **`.env`** racine sert au **dev Spring** (`make backend`), pas au deploy Railway.

### API (service `backend-api`)

| Variable | Rôle |
| -------- | ---- |
| `JWT_SECRET` | ≥ 32 caractères |
| `DATABASE_URL` | JDBC PostgreSQL |
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | Connexion BDD |
| `CORS_ALLOWED_ORIGINS` | URL du service **fronts** (ex. `https://xxx-web.up.railway.app`), sans `/` final |

Profil : `SPRING_PROFILES_ACTIVE=prod` (start command ci-dessous). Détails : `backend-api/src/main/resources/application-prod.yaml`.

---

## 4. PostgreSQL

1. Créer Postgres sur Railway.
2. Initialiser une fois depuis ta machine :

```bash
psql "<DATABASE_URL_EXTERNE>" -f backend-api/src/main/resources/blog.sql
```

Pas de Flyway : `blog.sql` (prod/dev), `blog-test.sql` (tests).

---

## 5. Service API

| Réglage | Valeur |
| -------- | ------ |
| **Root Directory** | `backend-api` |
| **Build** | `./mvnw -B -DskipTests package` |
| **Start** | `java -Dspring.profiles.active=prod -jar target/java_blog-0.0.1-SNAPSHOT.jar` |

Smoke : `curl -s https://<api>/ping` → `pong`.

---

## 6. Service Fronts

| Réglage | Valeur |
| -------- | ------ |
| **Root Directory** | *(vide — racine du monorepo)* |
| **Build** | `npm ci && npm run build` |
| **Start** | `npm start` |

[`package.json`](../package.json) enchaîne `site/` + `admin/` ; [`server.js`](../server.js) sert `site/dist` et `admin/dist`.

**Networking :** le port public doit correspondre au **`PORT`** injecté (souvent **8080**). Si logs `listening on …:8080` mais Networking = 3000 → **502**.

---

## 7. Ordre de mise en prod

1. CI verte sur `main`.
2. Postgres + `blog.sql`.
3. Déployer **API** (variables secrets + JDBC).
4. **`VITE_API_URL`** sur le service Fronts = URL API.
5. Déployer **Fronts**.
6. Mettre **`CORS_ALLOWED_ORIGINS`** = URL fronts → redéployer **API**.
7. Tests : `/`, `/admin/`, login Alice, F12 → appels vers l’API prod.

---

## 8. CI et parité deploy

La CI exécute les mêmes builds que Railway :

| Job | Équivalent prod |
| --- | ---------------- |
| `backend` | Spotless, SpotBugs, tests, package JAR |
| `frontend-admin` / `frontend-site` | Lint, format, tests (sans build Vite) |
| `fronts-railway` | `npm ci && npm run build` + smoke Express |

Parité locale : `make ci` (inclut le build racine).

---

## 9. Dépannage

| Symptôme | Piste |
| -------- | ----- |
| **502** fronts | Networking port = `PORT` des logs (`8080` vs `3000`) |
| `Failed to fetch` / CORS | `CORS_ALLOWED_ORIGINS` + `VITE_API_URL` au **build** |
| API `localhost` dans le navigateur | Rebuild fronts après correction `VITE_API_URL` |
| Assets admin 404 | `admin` prod : `base: '/admin/'` dans `vite.config.ts` |
| Login 401 Alice | `blog.sql` non appliqué sur Postgres prod |

→ [README-runbook.md](README-runbook.md)

---

## 10. Liens

| Document | Contenu |
| -------- | ------- |
| [README-exploitation.md](README-exploitation.md) | Dev local |
| [README-architecture.md](README-architecture.md) | CORS, couches |
| [ADR-0005](adr/adr-0005-deploiement-railway.md) | Pourquoi Railway |
