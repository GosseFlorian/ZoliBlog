# API — référence des routes HTTP

> Base URL : `http://localhost:8080`  
> Auth : header `Authorization: Bearer <token>` (obtenu via `POST /auth/login` ou `/auth/register`)

Document **reference** — contrat HTTP du backend.

---

## Santé

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/ping` | Non | API vivante → `pong` |
| GET | `/db/ping` | Non | Test connexion PostgreSQL |

---

## Authentification

| Méthode | Route | Auth | Corps JSON | Réponse |
|---------|-------|------|------------|---------|
| POST | `/auth/login` | Non | `{ "mail", "mdp" }` | `{ "token", "pseudo", "userId" }` |
| POST | `/auth/register` | Non | `{ "pseudo", "mail", "mdp" }` | **201** `{ "token", "pseudo", "userId" }` |

Validation (Bean Validation) : mail format email, mot de passe 8–128 caractères.  
Rate limit : 10 tentatives POST `/auth/login` / 5 min / IP → **429**.

---

## Articles (public)

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/articles` | Non | Articles **publiés** |
| GET | `/articles/{id}` | Non | Détail article publié |
| GET | `/articles/recents` | Non | 5 derniers articles (tous statuts) |
| GET | `/articles/recents/count` | Non | Nombre d'articles récents |
| GET | `/articles/{id}/categories` | Non | Catégories d'un article |

---

## Catégories (public)

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/categories` | Non | Liste des catégories |
| GET | `/categories/{id}` | Non | Détail catégorie |
| GET | `/categories/{id}/articles` | Non | Articles publiés d'une catégorie |

---

## Commentaires (site)

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/articles/{articleId}/commentaires` | Non | Liste des commentaires d'un article |
| GET | `/commentaires/{id}` | Non | Détail d'un commentaire |
| POST | `/articles/{articleId}/commentaires` | **JWT** | Créer — `{ "contenu", "userId" }` |
| PATCH | `/commentaires/{id}` | **JWT** | Modifier — `{ "contenu" }` (auteur uniquement) |
| DELETE | `/commentaires/{id}` | **JWT** | Supprimer (auteur uniquement) |

Contrôle IDOR : le `userId` du token doit correspondre à l'auteur (create/update/delete).

---

## Médias (public)

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/articles/{articleId}/medias` | Non | Médias d'un article |
| GET | `/medias/{id}` | Non | Détail média |

---

## Utilisateurs (public)

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| GET | `/users/{id}` | Non | Profil public (pseudo, etc.) |

---

## Admin — articles

Préfixe : `/admin/articles` — **JWT obligatoire**

| Méthode | Route | Description |
|---------|-------|-------------|
| GET | `/admin/articles` | Liste (tous statuts) |
| GET | `/admin/articles/{id}` | Détail |
| POST | `/admin/articles` | Créer brouillon — `{ "titre", "contenu", "userId" }` |
| PUT | `/admin/articles/{id}` | Modifier — `{ "titre", "contenu", "publie" }` |
| PATCH | `/admin/articles/{id}/publier` | Publier |
| PATCH | `/admin/articles/{id}/depublier` | Dépublier |
| DELETE | `/admin/articles/{id}` | Supprimer |
| GET | `/admin/articles/{id}/categories` | Catégories liées |
| PUT | `/admin/articles/{id}/categories` | Remplacer catégories — `{ "categorieIds": [...] }` |
| POST | `/admin/articles/{id}/medias` | Ajouter média |

---

## Admin — catégories, users, commentaires, médias

| Préfixe | Méthodes | Auth |
|---------|----------|------|
| `/admin/categories` | POST, PUT `/{id}`, DELETE `/{id}` | JWT |
| `/admin/users` | GET, GET `/{id}`, POST, PUT `/{id}`, DELETE `/{id}` | JWT |
| `/admin/commentaires` | GET, DELETE `/{id}` | JWT |
| `/admin/medias` | POST, DELETE `/{id}` | JWT |

---

## Codes HTTP usuels

| Code | Situation |
|------|-----------|
| 200 | OK (GET, PUT, PATCH, login réussi) |
| 201 | Ressource créée (POST article, commentaire, register) |
| 204 | Suppression réussie (DELETE) |
| 400 | Validation échouée ou contenu refusé (`GlobalExceptionHandler`) |
| 401 | Non authentifié ou identifiants invalides |
| 403 | IDOR — action interdite (pas le bon user) |
| 404 | Ressource introuvable |
| 409 | Conflit (ex. mail déjà utilisé à l'inscription) |
| 429 | Rate limit login |
| 500 | Erreur interne (pas de stack trace côté client) |

Format erreur JSON :

```json
{ "message": "...", "champs": { "mail": "...", "mdp": "..." } }
```

---

## Exemples curl

```bash
# Santé
curl http://localhost:8080/ping
curl http://localhost:8080/db/ping

# Login
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"mail":"alice@example.com","mdp":"demo1234"}'

# Articles publiés
curl -s http://localhost:8080/articles

# Commentaires d'un article (public)
curl -s http://localhost:8080/articles/1/commentaires

# Poster un commentaire (remplacer TOKEN)
curl -s -X POST http://localhost:8080/articles/1/commentaires \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"contenu":"Mon commentaire","userId":1}'
```

---

## Liens

- [README-exploitation.md](README-exploitation.md) — lancer l'API
- [README-architecture.md](README-architecture.md) — sécurité et couches
