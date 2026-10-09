# Partie 08 — Étape 03
# ESLint, Prettier, scripts npm et Makefile

> 📘 **Objectif :** homogénéiser le code front et lancer le projet en **une commande**.  
> 🗣️ **On vulgarise :** **ESLint** = le correcteur ; **Prettier** = le ruleur ; **scripts npm** = raccourcis **dans chaque app React** ; **Makefile** = télécommande **du projet entier** (Java + 2 React + BDD).

## Ce que tu auras à la fin

- Prettier + `eslint-config-prettier` dans `admin/` et `site/`
- Bloc `"scripts"` enrichi dans chaque `package.json` front
- **Makefile** racine (remplace les scripts npm globaux **et** les fichiers `.sh`)
- `.prettierrc` partagé à la racine (optionnel mais pratique)
- `.github/dependabot.yml` (optionnel mais recommandé CDA)

> ⏱️ **Durée estimée :** 1 h à 1 h 30.

---

## Todo

- [ ] Vérifier que **`make --version`** fonctionne (sinon installer — voir [08-01](partie-08-01-cadrage-exploitation-securite.md))
- [ ] Installer Prettier + `eslint-config-prettier` dans `admin/` et `site/`
- [ ] Enrichir les `"scripts"` npm des deux fronts
- [ ] Créer `.prettierrc` à la racine
- [ ] Étendre le **Makefile** (db, lint, lancement)
- [ ] Mettre à jour le **README.md** racine
- [ ] (Optionnel) Créer `.github/dependabot.yml`
- [ ] Commit `08-03 — eslint prettier makefile`

---

## 1. Deux niveaux de scripts — bien séparer

### Niveau 1 — `package.json` (chaque front)

Comme dans tes cours Node : les commandes **propres au front** restent dans `admin/package.json` et `site/package.json`.

**Modèle à adapter** (dans `admin/package.json` **et** `site/package.json`) :

```json
"scripts": {
  "dev":           "vite",
  "build":         "tsc -b && vite build",
  "preview":       "vite preview",

  "test":          "vitest run",
  "test:watch":    "vitest",

  "lint":          "eslint .",
  "lint:fix":      "eslint . --fix",

  "format":        "prettier --write \"src/**/*.{ts,tsx,css,json}\"",
  "format:check":  "prettier --check \"src/**/*.{ts,tsx,css,json}\""
}
```

> 💡 `admin/` a déjà `"lint": "eslint ."` — tu **complètes**, tu ne remplaces pas `dev` / `build`.

**Installation des devDependencies** (dans chaque dossier front) :

```bash
cd admin
npm install -D prettier eslint-config-prettier
cd ../site
npm install -D prettier eslint-config-prettier
```

**ESLint** — ajoute `eslint-config-prettier` en dernier dans `eslint.config.js` :

```javascript
import eslintConfigPrettier from 'eslint-config-prettier'

// dans extends: [ ..., eslintConfigPrettier ]
```

### Niveau 2 — `Makefile` (racine)

Le Makefile **orchestre** ce qui touche plusieurs parties du projet. Il remplace :

- un hypothétique `package.json` racine avec `"db:seed"`, `"backend"`, etc.
- les fichiers `scripts/*.sh` (on n'en crée **pas**)

---

## 2. `.prettierrc` (racine)

```json
{
  "semi": true,
  "singleQuote": true,
  "trailingComma": "all",
  "printWidth": 100,
  "tabWidth": 2
}
```

`.prettierignore` :

```
node_modules
dist
target
```

---

## 3. Makefile — modèle complet

> ⚠️ **Prérequis :** l'outil `make` doit être installé (`make --version`). Sous Windows + Git Bash : `choco install make` ou `scoop install make` — détail dans [README.md](../README.md#installer-make-windows).

**Chemin :** `Makefile` (remplace ou étends celui de la partie 06)

```makefile
# Raccourcis — Blog Java
.PHONY: help setup env db-init db-test lint format test ci backend admin site

help:
	@echo "Commandes :"
	@echo "  make setup     — .env + npm install (admin + site)"
	@echo "  make env       — cp .env.example .env"
	@echo "  make db-init   — blog.sql + upgrade BCrypt Alice"
	@echo "  make db-test   — crée java_blog_test"
	@echo "  make lint      — ESLint admin + site"
	@echo "  make format    — Prettier admin + site"
	@echo "  make test      — ./mvnw test"
	@echo "  make ci        — tests + build admin + build site"
	@echo "  make backend   — API port 8080"
	@echo "  make admin     — Vite port 5173"
	@echo "  make site      — Vite port 5174"

setup: env
	cd admin && npm install
	cd site && npm install

env:
	cp .env.example .env

db-init:
	PGPASSWORD=$${POSTGRES_PASSWORD:-postgres} psql -h localhost -U $${POSTGRES_USER:-postgres} -d java_blog -f backend-api/src/main/resources/blog.sql
	PGPASSWORD=$${POSTGRES_PASSWORD:-postgres} psql -h localhost -U $${POSTGRES_USER:-postgres} -d java_blog -f doc/sql/upgrade-05-01-bcrypt-alice.sql

db-test:
	PGPASSWORD=$${POSTGRES_PASSWORD:-postgres} psql -h localhost -U $${POSTGRES_USER:-postgres} -d postgres -f doc/sql/upgrade-06-01-create-java-blog-test.sql

lint:
	cd admin && npm run lint
	cd site && npm run lint

format:
	cd admin && npm run format
	cd site && npm run format

test:
	./mvnw test

ci:
	./mvnw -B test
	cd admin && npm ci && npm run build
	cd site && npm ci && npm run build

backend:
	./mvnw spring-boot:run

admin:
	cd admin && npm run dev

site:
	cd site && npm run dev
```

> 🪟 **Windows :** Git Bash ne fournit pas `make`. Installe-le **avant** d'utiliser ce Makefile — voir [README](../README.md#installer-make-windows).

> 💡 `db-init` appelle **`psql` directement** — pas de script `.sh` intermédiaire.

---

## 4. README.md racine — critères CDA

Le README d'installation est le **premier test de livrabilité**. Il doit être :

| Critère | Concret |
|---------|---------|
| **Précis** | versions Java / Node / PostgreSQL, commandes copiables |
| **Court** | lisible en ~5 minutes |
| **Robuste** | table de dépannage, lien vers le runbook |

**Structure minimale à rédiger :**

1. Titre + badge CI
2. Prérequis (tableau)
3. Installation (`make setup`, `make db-init`, `make db-test`)
4. Lancement (`make backend`, `make admin`, `make site`)
5. Commandes utiles (`make lint`, `make test`, `make help`)
6. Variables d'environnement (renvoi vers `.env.example`)
7. Liens vers `docs/README-exploitation.md`, etc.
8. Dépannage rapide (3–5 lignes)

---

## 5. Dependabot (optionnel)

**Chemin :** `.github/dependabot.yml`

```yaml
version: 2
updates:
  - package-ecosystem: maven
    directory: /
    schedule:
      interval: weekly
  - package-ecosystem: npm
    directory: /admin
    schedule:
      interval: weekly
  - package-ecosystem: npm
    directory: /site
    schedule:
      interval: weekly
  - package-ecosystem: github-actions
    directory: /
    schedule:
      interval: weekly
```

---

## 6. Vérifications

```bash
make help
make lint          # doit passer (corrige avec lint:fix si besoin)
make format
make test
make db-init       # si base vide
make backend       # terminal 1
make admin         # terminal 2
```

---

## Commit Git

```bash
git add Makefile README.md .prettierrc .prettierignore \
  admin/package.json admin/eslint.config.js \
  site/package.json site/eslint.config.js
# + dependabot si créé
git commit -m "08-03 — eslint prettier scripts npm + makefile"
```

👉 **[partie-08-04-journalisation-securisee.md](partie-08-04-journalisation-securisee.md)**
