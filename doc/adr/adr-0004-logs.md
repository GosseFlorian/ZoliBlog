# ADR-0004 — Journalisation sans données sensibles

Date : 2026-08-15  
Statut : Accepté

## Contexte

Les logs serveur (login, audit HTTP, erreurs 401) sont indispensables au dépannage. Ils peuvent aussi devenir une fuite de données (A09 — Security Logging and Monitoring Failures) si on y écrit :

- mots de passe en clair
- tokens JWT complets
- adresses mail ou IP non masquées

## Décision

Mettre en place une journalisation **structurée et filtrée** :

| Composant | Rôle |
|-----------|------|
| `LogSanitizer` | Masque emails (`a***@example.com`), IP partielles, chemins |
| `RequestAuditFilter` | Log une ligne par requête : méthode, route, statut, durée |
| `AuthService` | Logs login réussi/échoué avec mail masqué uniquement |
| `SecurityConfig` | Log warn sur accès 401 non authentifié |
| `LOG_LEVEL` | Configurable via `.env` / `application.yaml` |

Interdiction explicite : ne jamais logger `body.mdp()`, le token JWT entier ou le contenu brut des secrets.

## Conséquences

**Positif :**

- Traçabilité pour le runbook et la conformité aux bonnes pratiques OWASP A09
- Réduction du risque en cas d'exposition des fichiers log

**Négatif :**

- Debugging parfois plus difficile (données masquées)
- Pas de centralisation type ELK / Grafana : stack volontairement légère pour ce projet

**Complément :** les réponses d'erreur client passent par `GlobalExceptionHandler` — pas de stack trace Java exposée (A05).
