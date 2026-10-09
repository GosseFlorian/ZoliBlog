# Partie 08 — Étape 06
# Documentation Diátaxis

> 📘 **Objectif :** structurer la doc **exploitation** pour le CDA — sans mélanger avec les cours `doc/partie-*`.  
> 🗣️ **On vulgarise :** **[Diátaxis](https://diataxis.fr/)** = 4 types de docs qui ne se marchent pas dessus : tutoriel, guide, référence, explication.

## Ce que tu auras à la fin

Un dossier `docs/` avec des fichiers **nommés par rôle** (pas un sous-dossier par rôle) :

```
docs/
├── README-diataxis.md       # hub — les 4 types (optionnel mais clair)
├── README-exploitation.md   # how-to : installer, lancer, maintenir
├── README-runbook.md        # how-to : dépanner / incidents
├── README-api.md            # reference : routes HTTP
├── README-architecture.md   # explanation : couches, schéma, OWASP
└── README-adr.md            # explanation : index des décisions
    ├── adr-0001-jdbc.md     # (exemples dans le même dossier)
    └── adr-0002-jwt.md
```

> 📌 **Convention :** `README-<rôle>.md` — **pas** `docs/exploitation/README.md`.

> ⏱️ **Durée estimée :** 1 h 30 à 2 h.

---

## Todo

- [ ] Créer le dossier `docs/`
- [ ] Rédiger les 5 README par rôle (+ hub Diátaxis)
- [ ] Rédiger 2 ADR minimum dans `docs/`
- [ ] Mettre à jour le **README.md racine** (liens vers `docs/`)
- [ ] Relire : un inconnu peut-il installer sans oral ?
- [ ] Commit `08-06 — documentation diataxis`

---

## 1. Les 4 types Diátaxis — appliqués au blog

| Type Diátaxis | Fichier | Question à laquelle ça répond |
|---------------|---------|-------------------------------|
| **Tutorial** | `doc/partie-*` (existant) | « Comment j'apprends étape par étape ? » |
| **How-to** | `README-exploitation.md`, `README-runbook.md` | « Comment je lance / je répare ? » |
| **Reference** | `README-api.md` (+ `doc/API.md` historique) | « Quelle route pour POST commentaire ? » |
| **Explanation** | `README-architecture.md`, `README-adr.md` | « Pourquoi JDBC et pas JPA ? » |

---

## 2. `docs/README-diataxis.md` — hub

Contenu suggéré :

- Une phrase sur Diátaxis
- Tableau avec liens vers les 5 README
- Rappel : `doc/` = formation, `docs/` = exploitation

---

## 3. `docs/README-exploitation.md`

Sections minimales :

1. Composants (API 8080, admin 5173, site 5174, PostgreSQL)
2. Prérequis
3. Première install (`make setup`, `make db-init`)
4. Lancement quotidien (`make backend`, `make admin`, `make site`)
5. Qualité (`make lint`, `make test`)
6. Variables d'environnement (renvoi `.env.example`)
7. Logs (`LOG_LEVEL`, règle « pas de secrets »)
8. Liens vers runbook, api, architecture

---

## 4. `docs/README-runbook.md`

Format **symptôme → cause → action** :

| Symptôme | Action |
|----------|--------|
| `JWT_SECRET` manquant | `make env`, éditer `.env` |
| Login 401 Alice | `make db-init` |
| Tests Maven BDD | `make db-test` |
| CORS | vérifier `CORS_ALLOWED_ORIGINS` |
| 429 login | rate limit — attendre 5 min |

Inclure une section « réinitialisation complète dev » (destructif).

---

## 5. `docs/README-api.md`

Référence des routes — tu peux **synthétiser** `doc/API.md` et compléter avec les routes partie 07.

Tableaux : méthode, route, auth, description.

Exemples `curl` pour login et GET articles.

---

## 6. `docs/README-architecture.md`

- Schéma ASCII (navigateur → Spring → PostgreSQL)
- Table des couches (controller, DTO, mapper, repository)
- Table OWASP → mesure (renvoi partie 08-05)
- Lien vers ADR

---

## 7. `docs/README-adr.md` + fichiers `adr-*.md`

**ADR** = Architecture Decision Record — une décision datée par fichier.

Modèle :

```markdown
# ADR-0003 — Secrets via .env

Date : 2026-…
Statut : Accepté

## Contexte
## Décision
## Conséquences
```

Sujets suggérés :

- `adr-0001-jdbc.md` — JDBC vs JPA
- `adr-0002-jwt.md` — JWT stateless
- `adr-0003-env.md` — variables d'environnement
- `adr-0004-logs.md` — logs sans données sensibles

---

## 8. README racine — checklist CDA

Ton `README.md` à la racine doit :

- [ ] Tenir en ~80–120 lignes
- [ ] Commencer par prérequis + install en 4 commandes max
- [ ] Mentionner les **3 terminaux** (backend, admin, site)
- [ ] Pointer vers `docs/README-exploitation.md`
- [ ] Avoir une mini table de dépannage

> 💡 **Test jury :** donne le repo à un camarade **sans oral** — peut-il lancer l'API en 15 min ?

---

## 9. Différence `doc/` vs `docs/`

| Dossier | Public | Contenu |
|---------|--------|---------|
| `doc/partie-*` | Élève ADA | Cours pas à pas |
| `docs/README-*.md` | Exploitant, jury | Exploitation, runbook, archi |
| `README.md` | Premier contact | Install express |
| `doc/API.md` | Dev front | Contrat routes (peut fusionner avec `README-api.md`) |

---

## Commit Git

```bash
git add docs/ README.md
git commit -m "08-06 — documentation Diátaxis (README par rôle)"
```

---

## ✅ Récapitulatif partie 08

| Étape | Livrable |
|-------|----------|
| 08-02 | `.env`, dotenv, config Spring |
| 08-03 | scripts npm front + Makefile racine |
| 08-04 | Logback, filtres, sanitizer |
| 08-05 | OWASP : validation, headers, rate limit |
| 08-06 | `docs/README-exploitation.md`, … |

👉 Retour à [INDEX.md](INDEX.md) — tu as bouclé le fil rouge formation + exploitation.
