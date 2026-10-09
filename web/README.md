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
│  │  zoliblog-db │◄──│  racine /    │   │  dossier web/│     │
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
| `railway.toml` | Build / start pour **ce** service (si Railway le lit ; sinon recopier dans l’UI). |
| `scripts/build-static.js` | Build site + admin → `deploy/`. |
| `server.js` | Serveur HTTP sur **`PORT`**. |
| `package.json` | Scripts `build` et `start`. |

`railway.toml` **à la racine du repo** concerne uniquement le service **API**, pas celui-ci.

---

## Mise en place Railway

### Service **API** (existant)

- **Root Directory** : *(vide — racine du repo)*
- **Build** : `./mvnw -B -DskipTests package`
- **Start** : `java -Dspring.profiles.active=prod -jar target/java_blog-0.0.1-SNAPSHOT.jar`
- **Variables** : `JWT_SECRET`, JDBC, `POSTGRES_*`, etc.
- **`CORS_ALLOWED_ORIGINS`** : URL **publique du service web** (ex. `https://zoliblog-web.up.railway.app`), **sans** `/` final.
- Domaine public → URL de l’API (ex. `https://zoliblog-production.up.railway.app`).

### Service **web** (nouveau)

1. **+ New** → **GitHub Repo** → même repo.
2. **Settings → Root Directory** : **`web`**
3. **Variables** :
   - **`VITE_API_URL`** = URL HTTPS de l’**API** (sans `/` final).
4. **Networking → Generate Domain** → URL du front.
5. **Build** : `npm ci && npm run build`  
   **Start** : `npm start`  
   (ou laisser `web/railway.toml` faire foi.)

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
| Page blanche | Console navigateur : erreur de chargement d’asset → revérifier un build complet (`npm run build`). |
