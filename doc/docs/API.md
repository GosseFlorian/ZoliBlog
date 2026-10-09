# API Blog Java — routes implémentées

> Base URL : `http://localhost:8080`  
> Auth admin : header `Authorization: Bearer <token>` (obtenu via login)  
> Corrigé complet des routes prévues : [partie-02-06-corrige-routes-blog.md](partie-02-06-corrige-routes-blog.md)

## Santé

| Méthode | Route      | Auth | Description          |
| ------- | ---------- | ---- | -------------------- |
| GET     | `/ping`    | Non  | API vivante          |
| GET     | `/db/ping` | Non  | Connexion PostgreSQL |

## Articles (public)

| Méthode | Route                     | Auth | Description               |
| ------- | ------------------------- | ---- | ------------------------- |
| GET     | `/articles`               | Non  | Articles **publiés**      |
| GET     | `/articles/{id}`          | Non  | Détail par id             |
| GET     | `/articles/recents`       | Non  | 5 derniers (tous statuts) |
| GET     | `/articles/recents/count` | Non  | Nombre d'articles récents |

## Authentification

| Méthode | Route         | Auth | Corps JSON          | Réponse                           |
| ------- | ------------- | ---- | ------------------- | --------------------------------- |
| POST    | `/auth/login` | Non  | `{ "mail", "mdp" }` | `{ "token", "pseudo", "userId" }` |

## Admin — articles

| Méthode | Route                  | Auth    | Description                                                                          |
| ------- | ---------------------- | ------- | ------------------------------------------------------------------------------------ |
| POST    | `/admin/articles`      | **JWT** | Créer (brouillon) — `{ "titre", "contenu", "userId" }`                               |
| PUT     | `/admin/articles/{id}` | **JWT** | Modifier — `{ "titre", "contenu", "publie" }`                                        |
| DELETE  | `/admin/articles/{id}` | **JWT** | Supprimer — 204 (commentaires et liaisons N-N supprimés ou détachés avant l'article) |

## Codes HTTP usuels

| Code | Situation                                                                                                      |
| ---- | -------------------------------------------------------------------------------------------------------------- |
| 200  | OK (GET, PUT, login)                                                                                           |
| 201  | Article créé (POST)                                                                                            |
| 204  | Supprimé (DELETE)                                                                                              |
| 401  | Non authentifié                                                                                                |
| 404  | Ressource introuvable                                                                                          |
| 500  | Erreur serveur (ex. FK non gérée à la suppression — voir [partie-04-06](partie-04-06-suppression-et-recap.md)) |

## Exemples curl

```bash
# Login (après upgrade BCrypt Alice — doc/sql/upgrade-05-01-bcrypt-alice.sql)
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"mail":"alice@example.com","mdp":"demo1234"}'

# Articles récents (public)
curl -s http://localhost:8080/articles/recents
```
