# ADR-0005 — Déploiement : GitHub Pages + API Render

Date : 2026-10-07  
Statut : Accepté

## Contexte

Le ZoliBlog doit être "deploiement ready", avec un coût **nul** ou minimal.

Contraintes retenues :

- **Coût nul** (ou quasi) pour une démo jury durablement accessible.
- Trois composants en développement : API Spring Boot, back-office React (`admin/`), site public React (`site/`).
- Le dépôt GitHub s’appelle **`ZoliBlog`** ; la branche **`main`** est la branche de **production** (merge après CI verte).

Alternatives envisagées :

| Option                                 | Écartée parce que…                                                                                                                                                                                                  |
| -------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| VPS + nginx                            | Coût récurrent, temps sysadmin (SSL, sauvegardes, systemd), peu prioritaire pour un projet diplôme solo.                                                                                                            |
| Tout sur Render (API + 2 static sites) | Plus de services à configurer / surveiller ; free tier limité sur plusieurs apps.                                                                                                                                   |
| Docker Compose en prod                 | Pages = statique seulement : l’API et Postgres restent sur un autre hébergeur. Gratuit → plutôt PaaS que VPS + Compose, évite un second mode d’exploitation (images, volumes) en parallèle du Makefile et de la CI. |

## Décision

Adopter un modèle **hybride gratuit** :

1. **Frontends (statiques)** — **GitHub Pages** (project site), un seul site Pages par dépôt :
   - Site public à la racine du site publié : `https://<user>.github.io/ZoliBlog/`
   - Back-office sous-chemin : `https://<user>.github.io/ZoliBlog/admin/`
   - Publication via **GitHub Actions** (`deploy-pages.yml`), déclenchée sur `main` après qualité CI.

2. **API Spring Boot** — **Render** (Web Service, build Maven → JAR ; déploiement géré par le PaaS, sans stack Compose à opérer côté diplôme) :
   - URL publique dédiée (ex. `https://zoliblog-api.onrender.com`).
   - Variables d’environnement sur Render (`JWT_SECRET`, JDBC, CORS…) — pas de `.env` commité (cf. ADR-0003).

3. **PostgreSQL** — **Render Postgres** (free tier) ou équivalent PaaS :
   - Schéma initial via scripts SQL versionnés (`doc/sql/`), procédure documentée (pas de Flyway à ce stade).

4. **Intégration front ↔ API** :
   - Build Vite : **`VITE_API_URL`** en variable de dépôt GitHub Actions ; `.env.production.example` = modèle local uniquement.
   - `base` Vite (constant en prod dans `vite.config.ts`) et `basename` React Router : `/ZoliBlog/` et `/ZoliBlog/admin/`.
   - CORS API : origine `https://<user>.github.io` (sans chemin — une seule origine pour site et admin).

5. **CI/CD** :
   - **CI** existante (`.github/workflows/ci.yml`) : tests et qualité sur PR / push.
   - **CD fronts** : workflow Pages sur `main`.
   - **CD API** : déploiement Render lié au repo (webhook GitHub sur `main`), documenté dans le guide de déploiement.

## Conséquences

**Positif :**

- Coût **zéro** adapté à un projet de diplôme.
- HTTPS et intégration **GitHub Actions** natives pour les fronts.
- Séparation claire **statique (Pages)** vs **stateful (API + BDD)** — argumentation DevOps lisible au jury.
- Secrets prod uniquement chez le PaaS ; le repo reste public sans fuite JWT/DB.

**Négatif :**

- **Deux hébergeurs** à documenter (GitHub + Render).
- **Sous-chemin GitHub Pages** : configuration `base` / `basename` et fichiers `404.html` pour le routing SPA au rafraîchissement.
- **Free tier Render** : cold start après inactivité, limites de durée/ressources Postgres — acceptable pour démo, pas pour prod réelle.
- **CORS cross-origin** : erreurs fréquentes si l’origine ou `VITE_API_URL` est mal configurée au build.

## Liens

- [README-deploiement.md](../README-deploiement.md) — procédure pas à pas
- [README-exploitation.md](../README-exploitation.md) — dev local
- [ADR-0003 — `.env`](adr-0003-env.md) — secrets et environnements (même dossier `adr/`)
