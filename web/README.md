# zoliblog-web — service Railway (statique)

Deux applications React (Vite) servies par **un seul** service Node, **sans Docker** :

| Chemin URL | App | Sources repo |
| ---------- | --- | ------------ |
| `/` | Site public | `../site/` |
| `/admin/` | Back-office | `../admin/` |

L’**API Spring Boot** tourne sur un **autre** service Railway (racine du repo).

---

## Comment ça marche

```
┌─────────────────────────────────────────────────────────────┐
│  Projet Railway                                              │
│                                                              │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐     │
│  │  Postgres    │   │  API (Java)  │   │  web (Node)  │     │
│  │  zoliblog-db │◄──│  backend-api/│   │  build cd web│     │
│  └──────────────┘   └──────┬───────┘   └──────┬───────┘     │
│                            │                   │             │
│                     /ping, /articles…     /, /admin/…        │
└────────────────────────────┼───────────────────┼─────────────┘
                             │                   │
                             │    fetch HTTPS     │
                             └◄───────────────────┘
                                  VITE_API_URL
                                  (inscrite au build)
```

### Phase **build** (`npm run build` → `scripts/build-static.js`)

1. Lit **`VITE_API_URL`** (variable Railway sur ce service).
2. Lance `npm ci` + `npm run build` dans **`site/`** → copie `site/dist/` vers **`web/deploy/`**.
3. Idem pour **`admin/`** → copie vers **`web/deploy/admin/`**.

Vite grave l’URL API dans le JavaScript (`import.meta.env.VITE_API_URL`).

### Phase **run** (`npm start` → `server.js`)

- Sert le dossier **`deploy/`** avec **`serve-handler`**.
- Fichiers réels (`.js`, `.css`, …) servis tels quels.
- **Rewrites SPA** (F5 / URL directe) :
  - `/admin/**` → `/admin/index.html`
  - tout le reste → `/index.html`

---

## Fichiers utiles

| Fichier | Rôle |
| ------- | ---- |
| `scripts/build-static.js` | Build site + admin → `deploy/`. |
| `server.js` | Serveur HTTP sur **`PORT`**. |
| `package.json` | Scripts `build` et `start`. |

Build / start Railway : **uniquement dans l’UI** (pas de `railway.toml` dans le repo pour l’instant).

---

## Mise en place Railway

### Service **API** (existant)

- **Root Directory** : `backend-api`
- **Build** : `./mvnw -B -DskipTests package`
- **Start** : `java -Dspring.profiles.active=prod -jar target/java_blog-0.0.1-SNAPSHOT.jar`
- **Variables** : `JWT_SECRET`, JDBC, `POSTGRES_*`, etc.
- **`CORS_ALLOWED_ORIGINS`** : URL **publique du service web** (ex. `https://zoliblog-web.up.railway.app`), **sans** `/` final.
- Domaine public → URL de l’API (ex. `https://zoliblog-production.up.railway.app`).

### Service **web** (nouveau)

> **Important — Root Directory**  
> Ne mets **pas** `web` seul : Railway n’envoie alors **pas** les dossiers `site/` et `admin/`, le build échoue.  
> Laisse **Root Directory vide** (racine du repo, comme l’API).

1. **+ New** → **GitHub Repo** → même repo.
2. **Settings → Source → Root Directory** : **vide** (`.` / racine).
3. **Build Command** :
   ```bash
   cd web && npm ci && npm run build
   ```
4. **Start Command** : laisser Railway / [`railpack.json`](../railpack.json) (`cd web && node server.js`), ou la même ligne sans `npm`.
5. **Variables** (service web) :
   - **`VITE_API_URL`** = URL HTTPS de l’**API** (sans `/` final).
6. **Networking → Generate Domain** → port = valeur de **`PORT`** (ou logs `listening on …`).

### Railpack (service **web**, racine repo)

Root Directory **vide**. `RAILPACK_PACKAGES` installe Node pour le **build** ; sans **provider Node** au deploy, le run échoue (`node: not found`).

**[`railpack.json`](../railpack.json)** à la racine : `"provider": "node"` + start `cd web && node server.js`. Lu seulement quand le contexte de build = racine (service **web**). L’API (`Root Directory = backend-api`) ne l’utilise pas.

| Variable (service web) | Exemple | Rôle |
| ---------------------- | ------- | ---- |
| `RAILPACK_NO_SPA` | `true` | Garde `server.js` (rewrites `/admin`) |
| `VITE_API_URL` | `https://…` | URL API au build Vite |

**Build Command** (UI) : `cd web && npm ci && npm run build`  
**Start Command** (UI) : vide ou `cd web && node server.js` — **pas** `npm start`, **pas** le hack `sh -c PATH=…` si `railpack.json` est en place.

- **Ne pas** mettre de `railway.toml` à la racine (conflit entre services).

Logs de build web attendus : `=== Build site ===`, `npm run build`, **pas** `./mvnw package`.

Après le premier deploy web, **mettre à jour `CORS_ALLOWED_ORIGINS`** sur l’API si l’URL du front vient d’être créée, puis **redéployer l’API**.

---

## Vérifications

| Test | Attendu |
| ---- | ------- |
| `https://<api>/ping` | `pong` |
| `https://<web>/` | Accueil site |
| `https://<web>/admin/` | Login admin |
| F5 sur `https://<web>/admin/articles` | Toujours l’admin (pas une 404 JSON) |
| F12 → Network | Requêtes vers **`<api>`**, pas `localhost` |

---

## Local

```bash
cd web
npm ci
```

PowerShell :

```powershell
$env:VITE_API_URL="http://localhost:8080"
npm run build
npm start
```

- Site : http://localhost:3000/  
- Admin : http://localhost:3000/admin/  

(Spring doit tourner en local sur le port 8080 pour les appels API.)

---

## Dépannage

| Symptôme | Piste |
| -------- | ----- |
| `Failed to fetch` / CORS | `CORS_ALLOWED_ORIGINS` sur l’API = URL exacte du **web** (schéma + domaine, sans slash). |
| API appelle `localhost` en prod | Rebuild **web** après avoir défini **`VITE_API_URL`**. |
| Build échoue (npm dans site/admin) | Logs Railway : Node 20+ ; chemins `../site` et `../admin` depuis `web/`. |
| `spawnSync /bin/sh ENOENT` ou build `(/site)` | Root Directory = **`web`** → repasser à **vide** + build `cd web && …`. Pousser la dernière version de `build-static.js`. |
| Page blanche | Console navigateur : erreur de chargement d’asset → revérifier un build complet (`npm run build`). |
| **502** / `npm: command not found` au run | Node présent au **build** seulement. Start = `sh -c 'export PATH="/mise/shims:$PATH" && cd web && node server.js'` (pas `npm start`). Logs deploy : `zoliblog-web listening on http://0.0.0.0:…` |
