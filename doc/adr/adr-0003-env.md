# ADR-0003 — Secrets et configuration via `.env`

Date : 2026-08-01  
Statut : Accepté

## Contexte

Au démarrage, des valeurs sensibles étaient en dur ou absentes du dépôt Git :

- Secret JWT (signature des tokens)
- URL et identifiants PostgreSQL
- Origines CORS des frontends

Commiter ces valeurs exposerait le projet (A05 — Security Misconfiguration). Il fallait un mécanisme standard pour le dev local, la CI et les environnements déployés (variables d’environnement, pas de secrets dans Git).

## Décision

- Fichier **`.env`** à la racine (gitignoré) — jamais commité
- Fichier **`.env.example`** — modèle sans secrets réels, versionné
- **`dotenv-java`** + placeholders Spring `${VAR:default}` dans `application.yaml`
- Cible Makefile `make env` pour copier `.env.example` → `.env`
- **Prod API (Railway)** : mêmes clés sensibles via **Variables** du service (pas de `.env` sur le serveur)
- **Prod front (Railway)** : **`VITE_API_URL`** sur le service Fronts (build Vite)

Variables clés : `JWT_SECRET`, `DATABASE_URL`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `CORS_ALLOWED_ORIGINS`, `LOG_LEVEL`, `SECURITY_LOGIN_RATE_LIMIT_ENABLED`.

## Conséquences

**Positif :**

- Séparation claire config / code
- Onboarding : `make setup` + édition `.env`
- Aligné OWASP A05 (pas de secrets dans le repo)

**Négatif :**

- Chaque environnement (dev, CI, prod) doit maintenir son propre `.env` ou variables système
- Oubli de `JWT_SECRET` → crash au démarrage (message explicite)

**Règle d'exploitation :** après modification de `.env`, redémarrer l'API. Voir [README-exploitation.md](../README-exploitation.md).
