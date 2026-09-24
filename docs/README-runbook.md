# Runbook — dépannage et incidents

Guide **how-to** au format **symptôme → cause probable → action**.

---

## Incidents courants

| Symptôme | Cause probable | Action |
|----------|----------------|--------|
| `Could not resolve placeholder 'JWT_SECRET'` au démarrage | `.env` absent ou `JWT_SECRET` vide | `make env`, éditer `.env`, redémarrer l'API |
| `make: command not found` | Make non installé (Windows) | Installer Make (Chocolatey ou Scoop), redémarrer Git Bash — voir [README.md](../README.md) |
| Login **401** pour Alice (`alice@example.com`) | Mot de passe BCrypt non appliqué en base | `make db-init` |
| Login **400** au lieu de **401** | Mot de passe trop court (< 8 car.) ou mail invalide | Envoyer un JSON conforme à la validation Bean Validation |
| Login **429** « Trop de tentatives » | Rate limit login (10 POST / 5 min par IP) | Attendre 5 minutes ou `SECURITY_LOGIN_RATE_LIMIT_ENABLED=false` en dev |
| `./mvnw test` échoue sur la BDD | Base `java_blog_test` absente ou PostgreSQL arrêté | Démarrer PostgreSQL, `make db-test` |
| `psql: command not found` | PostgreSQL pas dans le `PATH` | Ajouter le dossier `bin` de PostgreSQL au `PATH` |
| Erreur **CORS** dans le navigateur | Origine front absente de `.env` | Vérifier `CORS_ALLOWED_ORIGINS` (`5173`, `5174`), redémarrer l'API |
| Front ne charge pas les articles | API arrêtée ou mauvais port | Vérifier `curl http://localhost:8080/ping` |
| `401` sur routes admin | Token absent, expiré ou invalide | Se reconnecter via `/auth/login`, vérifier header `Authorization: Bearer …` |
| `403` sur PATCH/DELETE commentaire | IDOR — user tente de modifier le commentaire d'un autre | Comportement attendu (A01) |
| `400 Contenu refusé` sur commentaire | Motif SQL suspect détecté par `InputSanitizer` | Reformuler le commentaire |
| Headers sécurité absents | Ancienne version de l'API en mémoire | Redémarrer `make backend`, vérifier avec `curl -I http://localhost:8080/ping` |
| Tests MockMvc → **429** | Rate limit actif en profil test | `security.login-rate-limit.enabled: false` dans `application-test.yaml` |
| CI échoue sur **Spotless** | Code Java non formaté | `make format-java` puis recommiter |
| CI échoue sur **Prettier** | Code TS/CSS non formaté | `make format-ts` puis recommiter |
| CI échoue sur **SpotBugs** | Alerte d'analyse statique | `./mvnw spotbugs:spotbugs` → ouvrir `target/spotbugs.html` |

---

## Vérifications rapides

```bash
# API vivante
curl http://localhost:8080/ping

# Connexion PostgreSQL
curl http://localhost:8080/db/ping

# Login Alice
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"mail":"alice@example.com","mdp":"demo1234"}'

# Headers de sécurité (étape OWASP — pas de corps, headers seulement)
curl -I http://localhost:8080/ping
```

---

## Réinitialisation complète (dev — destructif)

> ⚠️ Supprime et recrée les données locales. **Ne pas utiliser en production.**

```bash
# 1. Arrêter backend, admin, site (Ctrl+C dans chaque terminal)

# 2. Recréer la base et les données de démo
make db-init
make db-test

# 3. Réinstaller les dépendances front si besoin
make setup

# 4. Relancer
make backend    # terminal 1
make admin      # terminal 2
make site       # terminal 3
```

Si le rate limit bloque encore les tests après redémarrage : attendre 5 minutes ou redémarrer l'API (compteur en mémoire).

---

## Escalade

| Niveau | Qui | Action |
|--------|-----|--------|
| Dev local | Développeur | Runbook ci-dessus |
| CI GitHub | Workflow `.github/workflows/ci.yml` | Consulter les logs Actions, reproduire avec `make ci` |
| Jury / démo | — | Vérifier prérequis (Java 21, PostgreSQL, `.env`) avant la soutenance |

---

## Liens

- [README-exploitation.md](README-exploitation.md) — installation et lancement
- [README-api.md](README-api.md) — codes HTTP et routes
- [README-architecture.md](README-architecture.md) — sécurité OWASP
