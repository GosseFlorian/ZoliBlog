# ADR-0002 — Authentification JWT stateless

Date : 2026-03-15  
Statut : Accepté

## Contexte

L'API REST sert deux frontends React (admin + site) sur des ports différents. Il fallait authentifier les routes `/admin/**` et certaines actions commentaires sans sessions serveur classiques (cookies de session).

Options envisagées :

- **Session cookie** — simple côté Spring Security, mais état serveur et CORS plus délicats entre deux origins
- **JWT stateless** — token signé côté client, API sans session

## Décision

Adopter **JWT (HS256)** via la lib `jjwt` :

- `POST /auth/login` et `POST /auth/register` émettent `{ token, pseudo, userId }`
- Les clients envoient `Authorization: Bearer <token>`
- `JwtAuthFilter` valide la signature et pose le `userId` comme principal Spring Security
- `SessionCreationPolicy.STATELESS` — pas de session HTTP

Mots de passe hashés en **BCrypt** avant stockage PostgreSQL.

## Conséquences

**Positif :**

- API stateless, scalable horizontalement en théorie
- Fronts découplés (admin / site) avec le même mécanisme
- Compatible CORS (header Authorization)

**Négatif :**

- Révocation de token non implémentée (expiration seule via `JWT_EXPIRATION_MS`)
- Secret partagé (`JWT_SECRET`) — rotation manuelle
- XSS sur le front pourrait voler le token localStorage — mitigé par CSP headers côté API

**Mesures associées (partie 08-05) :** rate limit login, validation des entrées, `JWT_SECRET` externalisé dans `.env`.
