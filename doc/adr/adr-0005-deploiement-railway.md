# ADR-0005 — Déploiement production sur Railway

Date : 2026-10-09  
Statut : Accepté

## Contexte

Le ZoliBlog est un monorepo : API Spring Boot (`backend-api/`), site public et back-office React (`site/`, `admin/`). Il doit être déployable en production avec un coût maîtrisé, HTTPS, et une CI qui valide les builds avant merge sur `main`.

Contraintes :

- Monorepo : un seul dépôt GitHub, CI GitHub Actions.
- Deux fronts Vite + une API stateful (PostgreSQL).
- Pas de Dockerfile requis dans le dépôt : build via builder PaaS (Railpack).
- Secrets (JWT, BDD) hors Git ; URL API front visible dans le bundle JS.

## Décision

Héberger la production sur **[Railway](https://railway.com)** :

1. **PostgreSQL** — plugin Railway, schéma initial `backend-api/src/main/resources/blog.sql`.
2. **API** — service dédié, Root Directory `backend-api`, build Maven → JAR, profil `prod`, variables JDBC + JWT sur Railway.
3. **Fronts** — un service Node à la **racine** du repo :
   - [`package.json`](../../package.json) : `npm run build` (site + admin), `npm start` → [`server.js`](../../server.js) (Express).
   - Site servi sur `/`, admin sur `/admin/` (`base: '/admin/'` en build prod admin).
4. **Configuration fronts** :
   - `VITE_API_URL` = variable Railway sur le service **Fronts** (build Vite).
   - CORS API : origine = URL publique du service fronts.
5. **Qualité** — CI inclut package JAR API et build monorepo fronts identique à Railway (`fronts-railway`).

Alternatives écartées pour ce projet :

| Option | Raison |
| ------ | ------ |
| Un service Railway par front | Plus de services à configurer ; CORS et URLs multiples sans gain fonctionnel pour un blog à faible trafic. |
| Static only (Caddy SPA) sans Express | Deux SPAs (`/` + `/admin/`) : rewrites explicites plus simples avec Express. |
| VPS + nginx | Coût récurrent et charge ops (SSL, sauvegardes, systemd) disproportionnée pour ce périmètre. |

## Conséquences

**Positif :**

- Deux services applicatifs (+ Postgres) : modèle clair API / fronts.
- `package.json` racine : Railpack détecte Node, `npm start` au runtime.
- `VITE_API_URL` centralisée sur Railway (service Fronts).
- CI alignée sur les commandes de deploy.

**Négatif :**

- Build fronts long (double `npm ci` site + admin).
- Port public Railway doit correspondre à `PORT` (sinon 502).
- Toute change d’URL API impose rebuild des fronts.

## Liens

- [README-deploiement.md](../README-deploiement.md)
- [ADR-0003 — `.env`](adr-0003-env.md)
