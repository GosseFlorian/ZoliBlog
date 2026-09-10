# ZoliBlog — Spring Boot + React

API REST, back-office admin et site public pour un blog (formation ADA).

![CI](https://github.com/GosseFlorian/java_blog/actions/workflows/ci.yml/badge.svg)

---

## Prérequis

| Outil          | Version | Vérification                     |
| -------------- | ------- | -------------------------------- |
| **Java**       | 21      | `java -version`                  |
| **Node.js**    | 20+     | `node -version`                  |
| **PostgreSQL** | 14+     | `psql --version`                 |
| **Make**       | —       | `make --version`                 |
| **Git Bash**   | —       | terminal recommandé sous Windows |

> Maven : `./mvnw` (wrapper inclus).  
> Le **`Makefile`** à la racine nécessite l'outil **`make`** — il n'est pas inclus dans Git Bash par défaut.

---

## Installer Make (Windows)

**Étape 0** avant toute commande `make …` :

### Option A — Chocolatey _(administrateur)_

PowerShell ou CMD **en administrateur** :

```powershell
choco install make -y
```

Ferme puis rouvre **Git Bash**, vérifie :

```bash
make --version
```

### Option B — Scoop _(sans admin)_

```powershell
scoop install make
```

Puis dans Git Bash : `make --version`

### Si `make` reste introuvable dans Git Bash

Ajoute le dossier des binaires à ton `PATH` (ex. Chocolatey) dans `~/.bashrc` :

```bash
export PATH="/c/ProgramData/chocolatey/bin:$PATH"
```

→ `source ~/.bashrc` puis `make --version`

---

## Installation

À la racine du projet, dans **Git Bash** :

```bash
make setup      # .env + npm install (admin + site)
make db-init    # schéma blog.sql + mot de passe BCrypt Alice
make db-test    # base java_blog_test (pour ./mvnw test)
```

**Compte de test** : `alice@example.com` / `demo1234`

---

## Lancement

Ouvre **3 terminaux** Git Bash à la racine :

```bash
make backend    # API — http://localhost:8080
make admin      # Back-office — http://localhost:5173
make site       # Site public — http://localhost:5174
```

| Service    | URL                           |
| ---------- | ----------------------------- |
| API        | http://localhost:8080/ping    |
| PostgreSQL | http://localhost:8080/db/ping |
| Admin      | http://localhost:5173         |
| Site       | http://localhost:5174         |

---

## Commandes utiles

```bash
make help       # liste toutes les cibles du Makefile
make lint       # ESLint (admin + site)
make format     # Prettier (admin + site)
make test       # tests backend (JUnit)
make ci         # pipeline locale (tests + build des 2 fronts)
```

Scripts npm dans chaque front (`admin/`, `site/`) : `dev`, `build`, `lint`, `lint:fix`, `format`, `test`.

---

## Variables d'environnement

```bash
make env        # cp .env.example → .env (si absent)
```

Modèle : [`.env.example`](.env.example)

| Variable                              | Rôle                                                 |
| ------------------------------------- | ---------------------------------------------------- |
| `JWT_SECRET`                          | Signature JWT (**min. 32 caractères**) — obligatoire |
| `DATABASE_URL`                        | URL JDBC PostgreSQL                                  |
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | Connexion BDD                                        |
| `CORS_ALLOWED_ORIGINS`                | Fronts autorisés (`5173`, `5174`)                    |
| `LOG_LEVEL`                           | Verbosité des logs (`INFO` par défaut)               |

> Ne commite jamais le fichier `.env`.

---

## Documentation

| Document                                                   | Contenu                               |
| ---------------------------------------------------------- | ------------------------------------- |
| [docs/README-diataxis.md](docs/README-diataxis.md)         | Hub — les 4 types de doc              |
| [docs/README-exploitation.md](docs/README-exploitation.md) | Installer, lancer, maintenir          |
| [docs/README-runbook.md](docs/README-runbook.md)           | Dépannage et incidents                |
| [docs/README-api.md](docs/README-api.md)                   | Référence des routes HTTP             |
| [docs/README-architecture.md](docs/README-architecture.md) | Couches, schéma, sécurité             |
| [docs/adr/README-adr.md](docs//adr/README-adr.md)          | Décisions techniques (ADR)            |
| [doc/INDEX.md](doc/INDEX.md)                               | Supports de formation (parties 01–08) |

---

## Dépannage rapide

| Problème                                     | Action                                                        |
| -------------------------------------------- | ------------------------------------------------------------- |
| `make: command not found`                    | Installer Make (section ci-dessus), redémarrer Git Bash       |
| `Could not resolve placeholder 'JWT_SECRET'` | `make env`, puis éditer `.env`                                |
| Login 401 pour Alice                         | `make db-init`                                                |
| `./mvnw test` échoue sur la BDD              | `make db-test` + PostgreSQL démarré                           |
| `psql: command not found`                    | Ajouter le `bin` PostgreSQL au `PATH`                         |
| Erreur CORS                                  | Vérifier `CORS_ALLOWED_ORIGINS` dans `.env`, redémarrer l'API |

→ Détail : [docs/README-runbook.md](docs/README-runbook.md)

---

## Structure

```
java_blog/
├── src/main/java/     # API Spring Boot
├── admin/             # Back-office React (5173)
├── site/              # Site public React (5174)
├── doc/               # Cours formation
├── docs/              # Doc exploitation (Diátaxis)
├── Makefile           # Raccourcis projet (nécessite make)
└── .env.example       # Modèle de configuration
```
