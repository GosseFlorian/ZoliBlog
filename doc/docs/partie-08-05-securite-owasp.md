# Partie 08 — Étape 05
# Sécurité OWASP Top 10 (extrait appliqué)

> 📘 **Objectif :** renforcer le blog sur les risques les plus fréquents — **par la pratique**.  
> 🗣️ **On vulgarise :** OWASP Top 10 = la **hit parade** des failles web ; tu n'as pas besoin de tout connaître par cœur, mais de **montrer des mesures concrètes** dans ton projet.

## Ce que tu auras à la fin

| Mesure OWASP | Fichier(s) concerné(s) |
|--------------|------------------------|
| **A01** IDOR | `CommentaireController` *(déjà en place — à vérifier)* |
| **A03** Injection SQL / XSS | Repositories + `InputSanitizer` + `CommentaireController` |
| **A05** Mauvaise config | `SecurityConfig` (headers), `WebConfig` (CORS), `.env` |
| **A07** Auth | DTOs + `@Valid`, `LoginRateLimitFilter`, BCrypt *(déjà)* |
| **A09** Logs | Partie 08-04 *(déjà si tu l'as faite)* |
| Erreurs propres | `GlobalExceptionHandler` |

> ⏱️ **Durée estimée :** 2 h à 3 h.

---

## Todo — checklist globale

Coche au fur et à mesure :

- [ ] **Étape 1** — Dépendance validation dans `pom.xml`
- [ ] **Étape 2** — Annotations sur les DTOs + `@Valid` dans les controllers
- [ ] **Étape 3** — `GlobalExceptionHandler`
- [ ] **Étape 4** — Headers sécurité dans `SecurityConfig`
- [ ] **Étape 5** — CORS restrictif dans `WebConfig`
- [ ] **Étape 6** — `InputSanitizer` + branchement dans `CommentaireController`
- [ ] **Étape 7** — `LoginRateLimitFilter` + config test
- [ ] **Étape 8** — Vérifier A01 (IDOR) avec curl
- [ ] **Étape 9** — `./mvnw test` vert
- [ ] Commit `08-05 — sécurité OWASP`

---

## Où en es-tu déjà ?

| Élément | Statut probable | Action |
|---------|-----------------|--------|
| JWT + BCrypt | ✅ Partie 05 | Rien |
| IDOR commentaires (`verifierUserIdCorrespondAuToken`) | ✅ Partie 07 | **Vérifier** avec curl (étape 8) |
| Secrets dans `.env` | ✅ Partie 08-02 | Rien |
| CORS depuis `.env` | ✅ Partie 08-02 | Renforcer headers (étape 5) |
| Logs A09 | ✅ Partie 08-04 | Rien |
| Bean Validation | ❌ À faire | Étapes 1–2 |
| Headers CSP / X-Frame | ❌ À faire | Étape 4 |
| `InputSanitizer` | ❌ À faire | Étape 6 |
| Rate limit login | ❌ À faire | Étape 7 |
| `GlobalExceptionHandler` | ❌ À faire | Étape 3 |

---

## Étape 1 — Dépendance Maven

**Fichier :** `pom.xml`

Ajoute **une fois** (évite les doublons security/jjwt déjà présents) :

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Vérifie : le projet compile `./mvnw compile`.

---

## Étape 2 — Valider les entrées (Bean Validation)

### Principe

Spring vérifie le JSON **avant** d'entrer dans ton controller si tu mets `@Valid` sur le `@RequestBody`.

### 2a. DTOs à modifier

**`LoginRequest.java`** — remplace tout le fichier :

```java
package fr.ada.java_blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "L'adresse mail est obligatoire")
        @Email(message = "Format de mail invalide")
        String mail,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, max = 128, message = "Le mot de passe doit contenir entre 8 et 128 caractères")
        String mdp) {
}
```

**`RegisterRequest.java`** :

```java
package fr.ada.java_blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Le pseudo est obligatoire")
        @Size(min = 2, max = 50, message = "Le pseudo doit contenir entre 2 et 50 caractères")
        String pseudo,

        @NotBlank(message = "L'adresse mail est obligatoire")
        @Email(message = "Format de mail invalide")
        String mail,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, max = 128, message = "Le mot de passe doit contenir entre 8 et 128 caractères")
        String mdp) {
}
```

**`CommentaireCreateRequest.java`** :

```java
package fr.ada.java_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentaireCreateRequest(
        @NotBlank(message = "Le contenu est obligatoire")
        @Size(min = 1, max = 2000, message = "Le commentaire doit contenir entre 1 et 2000 caractères")
        String contenu,

        @NotNull(message = "Le userId est obligatoire")
        Integer userId) {
}
```

**`CommentaireUpdateRequest.java`** :

```java
package fr.ada.java_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentaireUpdateRequest(
        @NotBlank(message = "Le contenu est obligatoire")
        @Size(min = 1, max = 2000, message = "Le commentaire doit contenir entre 1 et 2000 caractères")
        String contenu) {
}
```

### 2b. Controllers — ajouter `@Valid`

**`AuthController.java`** — import + paramètres :

```java
import jakarta.validation.Valid;

@PostMapping("/login")
public LoginResponse login(@Valid @RequestBody LoginRequest body) { ... }

@PostMapping("/register")
public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest body) { ... }
```

**`CommentaireController.java`** :

```java
import jakarta.validation.Valid;

// create :
@Valid @RequestBody CommentaireCreateRequest body

// update :
@Valid @RequestBody CommentaireUpdateRequest body
```

### ✅ Contrôle étape 2

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"mail":"pas-un-mail","mdp":"court"}'
```

| Attendu | Signification |
|---------|---------------|
| **400** + JSON `champs` | Validation OK *(nécessite étape 3)* |
| **401** ou **500** | Validation pas encore branchée |

---

## Étape 3 — `GlobalExceptionHandler`

**Fichier à créer :** `src/main/java/fr/ada/java_blog/config/GlobalExceptionHandler.java`

```java
package fr.ada.java_blog.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Réponses d'erreur homogènes — pas de stack trace vers le client (A05).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> champs = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            champs.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> body = Map.of(
                "message", "Données invalides",
                "champs", champs);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleStatus(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : "Erreur";
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("message", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur interne du serveur"));
    }
}
```

### ✅ Contrôle étape 3

Même curl qu'étape 2 → **400** :

```json
{
  "message": "Données invalides",
  "champs": {
    "mail": "Format de mail invalide",
    "mdp": "Le mot de passe doit contenir entre 8 et 128 caractères"
  }
}
```

Pas de stack trace Java dans la réponse.

---

## Étape 4 — Headers de sécurité (A05)

**Fichier :** `SecurityConfig.java`

### Imports à ajouter

```java
import org.springframework.security.config.Customizer;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
```

### Chaîne `.headers(...)` — juste après `.csrf(...)`

```java
.headers(headers -> headers
        .contentSecurityPolicy(csp -> csp.policyDirectives(
                "default-src 'self'; frame-ancestors 'none'; form-action 'self'; base-uri 'self'"))
        .frameOptions(frame -> frame.deny())
        .contentTypeOptions(Customizer.withDefaults())
        .referrerPolicy(referrer -> referrer
                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))
```

> 💡 Garde ton `exceptionHandling`, tes filtres et le reste **inchangés** — tu **ajoutes** seulement `.headers(...)`.

### ✅ Contrôle étape 4

```bash
curl -I http://localhost:8080/ping
```

Headers attendus (parmi d'autres) :

| Header | Valeur indicative |
|--------|-------------------|
| `X-Frame-Options` | `DENY` |
| `Content-Security-Policy` | `default-src 'self'; ...` |
| `X-Content-Type-Options` | `nosniff` |

---

## Étape 5 — CORS restrictif (A05)

**Fichier :** `WebConfig.java`

Remplace `.allowedHeaders("*")` par une liste explicite :

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins(allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("Authorization", "Content-Type")
            .exposedHeaders("Content-Type")
            .maxAge(3600);
}
```

### ✅ Contrôle étape 5

- Front admin (`5173`) et site (`5174`) fonctionnent toujours avec l'API
- `CORS_ALLOWED_ORIGINS` dans `.env` contient bien les deux URLs

---

## Étape 6 — XSS : `InputSanitizer` + commentaires

### Principe — pourquoi cette étape ?

L'étape 2 (`@Valid`) vérifie la **forme** du texte : pas vide, longueur max, etc.  
Elle ne vérifie **pas** si quelqu'un envoie du contenu **malveillant**.

L'étape 6 traite le risque **A03 — Injection** sur les **commentaires** : texte libre posté par les utilisateurs, puis affiché sur le site. Deux menaces :

| Menace | Idée | Exemple |
|--------|------|---------|
| **XSS** (Cross-Site Scripting) | Injecter du JavaScript dans une page | `<script>alert(1)</script>` |
| **SQL injection** | Tenter de manipuler une requête SQL | `'; DROP TABLE commentaires--` |

> 💡 **Pourquoi les commentaires ?** C'est du contenu **généré par l'utilisateur**. Un visiteur mal intentionné peut essayer d'y cacher du code. On nettoie **côté serveur**, avant de sauver en base.

### Schéma — parcours d'un commentaire

```
JSON reçu
    ↓
@Valid (longueur, champs obligatoires)
    ↓
sanitizeContenu()
    ├─ looksLikeSqlInjection ? → 400 « Contenu refusé »
    └─ stripDangerousHtml → texte propre
    ↓
repository.save(...)  avec  ?
    ↓
affiché sur le site (sans script)
```

### Ce qu'il ne faut pas confondre

| Mécanisme | Rôle |
|-----------|------|
| `@Valid` + `@Size` | « Le commentaire fait entre 1 et 2000 caractères » |
| `JdbcTemplate` + `?` | Protection **réelle** contre l'injection SQL |
| `InputSanitizer` | Filtrage du contenu dangereux (XSS + signaux SQL pour la démo CDA) |

---

### 6a. Créer la classe

**Fichier :** `src/main/java/fr/ada/java_blog/util/InputSanitizer.java`

`InputSanitizer` est une **classe utilitaire** : on n'instancie pas d'objet, on appelle directement `InputSanitizer.nomDeLaMethode(...)`.

```java
package fr.ada.java_blog.util;

import java.util.regex.Pattern;

public final class InputSanitizer {

    private static final Pattern SCRIPT_TAG =
            Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern EVENT_HANDLER =
            Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE);

    private InputSanitizer() {
    }

    public static String stripDangerousHtml(String input) {
        if (input == null) {
            return null;
        }
        String cleaned = SCRIPT_TAG.matcher(input).replaceAll("");
        cleaned = EVENT_HANDLER.matcher(cleaned).replaceAll("");
        return cleaned.trim();
    }

    public static boolean looksLikeSqlInjection(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }
        String lower = input.toLowerCase();
        return lower.contains("--")
                || lower.contains("';")
                || lower.contains("drop table")
                || lower.contains("union select");
    }
}
```

#### `stripDangerousHtml(String input)` — contre le XSS

**Rôle :** nettoyer le texte **avant** de le sauver en base.

```
Entrée  : "Super article ! <script>alert(1)</script>"
Sortie  : "Super article !"
```

| Élément du code | Utilité |
|-----------------|---------|
| `SCRIPT_TAG` | Regex qui repère `<script>...</script>` et le **supprime** |
| `EVENT_HANDLER` | Repère des attributs du type `onclick=`, `onerror=` (autre façon d'exécuter du JS) et les **supprime** |
| `trim()` | Enlève les espaces inutiles en début/fin |

> 🗣️ **On vulgarise :** si le front affiche le commentaire tel quel, un `<script>` pourrait s'exécuter chez les visiteurs. On enlève les morceaux les plus dangereux **avant** stockage. Ce n'est pas une protection parfaite (un vrai projet utiliserait une lib dédiée type OWASP Java HTML Sanitizer), mais c'est suffisant pour **montrer au jury** que tu as pensé au XSS.

#### `looksLikeSqlInjection(String input)` — détection de motifs SQL suspects

**Rôle :** renvoyer `true` si le texte **ressemble** à une tentative d'injection SQL.

```
"Bon commentaire"              → false
"'; DROP TABLE commentaires"  → true
```

On passe le texte en minuscules et on cherche des motifs connus :

| Motif | Signification |
|-------|---------------|
| `--` | Commentaire SQL (ignore le reste de la ligne) |
| `';` | Fermer une chaîne SQL + lancer une nouvelle commande |
| `drop table` | Commande de suppression de table |
| `union select` | Technique classique d'injection |

> 🗣️ **On vulgarise :** en théorie, ton code JDBC avec `?` protège déjà — la valeur utilisateur n'est **jamais concaténée** dans le SQL. Cette fonction est une **couche de défense en plus** + une **preuve pédagogique** pour l'examen : « je filtre aussi les entrées suspectes, pas seulement les paramètres SQL ».

---

### 6b. Utiliser dans `CommentaireController`

**Import :**

```java
import fr.ada.java_blog.util.InputSanitizer;
```

**Méthode privée** (en bas de la classe, avant la dernière `}`) — c'est le **pont** entre l'utilitaire et ton API :

```java
private static String sanitizeContenu(String contenu) {
    if (InputSanitizer.looksLikeSqlInjection(contenu)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contenu refusé");
    }
    return InputSanitizer.stripDangerousHtml(contenu);
}
```

**Flux de `sanitizeContenu` :**

1. Le client envoie un commentaire (JSON).
2. `@Valid` a déjà vérifié longueur / champs obligatoires.
3. `sanitizeContenu` :
   - SQL suspect → **400** « Contenu refusé »
   - sinon → texte nettoyé (sans `<script>`, etc.)
4. On sauve le texte **nettoyé** en base.

**Dans `create`** — remplace `body.contenu()` par :

```java
String contenu = sanitizeContenu(body.contenu());
var saved = commentaireRepository.save(articleId, contenu, body.userId());
```

**Dans `update`** — remplace `body.contenu()` par :

```java
String contenu = sanitizeContenu(body.contenu());
if (!commentaireRepository.updateById(id, contenu)) {
```

> ❓ **Quiz :** pourquoi appelle-t-on `sanitizeContenu` **après** `@Valid` et **avant** `repository.save()` ?  
> → `@Valid` ne détecte pas le HTML dangereux ; le repository ne doit recevoir que du texte déjà nettoyé.

### ✅ Contrôle étape 6

Avec un token valide, POST commentaire contenant `<script>alert(1)</script>` :

| Attendu | Signification |
|---------|---------------|
| **201** + contenu sans balise `<script>` | Sanitizer OK |
| **400** `Contenu refusé` | si tu envoies `'; DROP TABLE` |

### A03 SQL — rien à coder de plus

Tes repositories utilisent déjà `JdbcTemplate` avec `?`. **Ne concatène jamais** de strings utilisateur dans le SQL.

**Preuve à montrer au jury :** une requête dans `ArticleRepository.java` :

```java
WHERE id = ? AND statut = true
```

> 💡 Les `?` sont remplacés par JDBC de façon **sécurisée** : la valeur utilisateur ne peut pas « casser » la requête SQL. C'est la vraie protection ; `looksLikeSqlInjection` est un filet supplémentaire pour la démo.

---

## Étape 7 — Rate limit login (A07)

### Principe — pourquoi cette étape ?

L'étape 2 valide le **format** des identifiants (mail, longueur du mot de passe).  
BCrypt *(partie 05)* rend les mots de passe **difficiles à deviner** en base.  
Mais un attaquant peut quand même envoyer **des milliers de combinaisons** à `/auth/login` pour tenter de trouver le bon mot de passe — c'est une **attaque par force brute**.

| Mesure déjà en place | Ce qu'elle fait | Ce qu'elle ne fait pas |
|----------------------|-----------------|------------------------|
| `@Valid` | Refuse mail invalide / mdp trop court | N'empêche pas 10 000 essais avec des mdp valides |
| BCrypt | Hash lent à craquer **si** tu as le hash | Ne bloque pas les tentatives sur l'API |
| JWT | Protège les routes **après** login | Ne protège pas la route `/auth/login` elle-même |

> 🗣️ **On vulgarise :** le **rate limit** = un vigile devant la porte de login. Après **10 tentatives en 5 minutes** depuis la même IP, il répond **429 Too Many Requests** (« trop de tentatives, revenez plus tard ») **sans même** appeler le controller.

C'est le risque **A07 — Identification and Authentication Failures** : montrer qu'on limite les abus sur l'authentification.

### Schéma — où intervient le filtre ?

```
POST /auth/login
    ↓
LoginRateLimitFilter     ← compte les tentatives par IP
    ├─ ≥ 10 en 5 min ? → 429 (stop ici)
    └─ sinon → continue
    ↓
RequestAuditFilter       ← log de la requête
    ↓
JwtAuthFilter            ← ignore (pas de token sur /login)
    ↓
AuthController.login()   ← vérifie mail + mdp en base
```

> 💡 Le rate limit est placé **en premier** dans `SecurityConfig` : on bloque l'abus **avant** toute autre logique (audit, JWT, controller).

### Ce qu'il ne faut pas confoudre

| Code HTTP | Signification | Exemple |
|-----------|---------------|---------|
| **400** | Données invalides (`@Valid`) | mail mal formé, mdp < 8 caractères |
| **401** | Identifiants incorrects | bon format, mauvais mail ou mdp |
| **429** | Trop de requêtes (rate limit) | 11ᵉ login raté en 5 min depuis la même IP |

---

### 7a. Config dans `application.yaml`

```yaml
security:
  login-rate-limit:
    enabled: ${SECURITY_LOGIN_RATE_LIMIT_ENABLED:true}
```

| Élément | Utilité |
|---------|---------|
| `security.login-rate-limit.enabled` | Active ou désactive le filtre |
| `${SECURITY_LOGIN_RATE_LIMIT_ENABLED:true}` | Lit la variable d'environnement ; **`true` par défaut** en dev/prod |
| Désactivation en test | Voir étape 7d — sinon les tests MockMvc enchaînent des logins et reçoivent **429** |

> ❓ **Quiz :** pourquoi met-on `enabled: false` dans `application-test.yaml` et pas dans le `.env` de prod ?  
> → Les tests automatisés envoient plusieurs POST `/auth/login` d'affilée ; sans désactivation, ils échoueraient pour la mauvaise raison (429 au lieu de 401).

---

### 7b. Créer le filtre

**Fichier :** `src/main/java/fr/ada/java_blog/config/LoginRateLimitFilter.java`

Un **filtre servlet** s'exécute **à chaque requête HTTP**, avant le controller.  
`OncePerRequestFilter` (Spring) garantit qu'il ne tourne **qu'une fois** par requête, même si la chaîne de filtres est complexe.

**Constantes :**

| Constante | Valeur | Signification |
|-----------|--------|---------------|
| `MAX_ATTEMPTS` | `10` | Nombre max de POST `/auth/login` par fenêtre |
| `WINDOW_MS` | `5 * 60 * 1000` (5 min) | Durée de la fenêtre glissante |

**Structure en mémoire :**

```
attemptsByIp (ConcurrentHashMap)
    │
    ├── "192.168.1.42" → AttemptWindow { count: 3, windowStart: ... }
    ├── "127.0.0.1"    → AttemptWindow { count: 11, windowStart: ... }  → bloqué
    └── ...
```

```java
package fr.ada.java_blog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 10;
    private static final long WINDOW_MS = 5 * 60 * 1000L;

    private final boolean enabled;
    private final Map<String, AttemptWindow> attemptsByIp = new ConcurrentHashMap<>();

    public LoginRateLimitFilter(
            @Value("${security.login-rate-limit.enabled:true}") boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!enabled
                || !"POST".equalsIgnoreCase(request.getMethod())
                || !"/auth/login".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        AttemptWindow window = attemptsByIp.computeIfAbsent(ip, key -> new AttemptWindow());

        synchronized (window) {
            window.resetIfExpired();
            if (window.count.get() >= MAX_ATTEMPTS) {
                response.sendError(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        "Trop de tentatives de connexion. Réessayez dans quelques minutes.");
                return;
            }
            window.count.incrementAndGet();
        }

        filterChain.doFilter(request, response);
    }

    private static final class AttemptWindow {
        private final AtomicInteger count = new AtomicInteger(0);
        private long windowStart = Instant.now().toEpochMilli();

        void resetIfExpired() {
            long now = Instant.now().toEpochMilli();
            if (now - windowStart > WINDOW_MS) {
                count.set(0);
                windowStart = now;
            }
        }
    }
}
```

#### `doFilterInternal` — ligne par ligne

| Bloc | Rôle |
|------|------|
| `if (!enabled \|\| !POST \|\| !/auth/login)` | **Filtre ciblé** : on ne compte que les tentatives de login. Toutes les autres routes passent sans compter. |
| `request.getRemoteAddr()` | Identifie le client par **adresse IP** (clé du compteur). |
| `computeIfAbsent(ip, ...)` | Crée un compteur **par IP** la première fois qu'elle tente un login. |
| `synchronized (window)` | Évite deux requêtes simultanées de la même IP qui fausseraient le compteur. |
| `resetIfExpired()` | Si 5 minutes sont passées, remet le compteur à **0** (nouvelle fenêtre). |
| `count >= MAX_ATTEMPTS` | **11ᵉ tentative** (ou plus) → **429**, la requête **n'atteint pas** `AuthController`. |
| `count.incrementAndGet()` | Compte la tentative **avant** de laisser passer (même si le login échouera en 401 ensuite). |
| `filterChain.doFilter(...)` | Laisse continuer vers le controller si la limite n'est pas atteinte. |

> 🗣️ **On vulgarise :** on compte **chaque** POST `/auth/login`, qu'il réussisse (200) ou échoue (401). Un attaquant qui teste des mots de passe remplit vite le quota.

#### Classe interne `AttemptWindow`

| Champ / méthode | Rôle |
|-----------------|------|
| `count` | Nombre de tentatives dans la fenêtre en cours |
| `windowStart` | Horodatage du début de la fenêtre (millisecondes) |
| `resetIfExpired()` | Fenêtre expirée (> 5 min) → compteur remis à zéro, nouvelle fenêtre |

#### Limites volontaires (à connaître pour le jury)

| Limite | Explication |
|--------|-------------|
| **Mémoire locale** | Le compteur est en RAM : un redémarrage du serveur remet tout à zéro. |
| **Par IP** | Un attaquant avec plusieurs IP contourne la limite ; en prod on utiliserait Redis + proxy. |
| **Pas de distinction succès/échec** | Chaque POST compte, y compris un login réussi. Suffisant pour la démo CDA. |

> 💡 Pour l'examen, l'important est de **nommer le risque** (force brute sur A07) et de **montrer une mesure concrète**, pas d'avoir une solution enterprise-grade.

---

### 7c. Brancher dans `SecurityConfig`

Spring Security exécute les filtres dans l'ordre où tu les enregistres avec `addFilterBefore`.

**Constructeur** — ajoute le filtre :

```java
private final LoginRateLimitFilter loginRateLimitFilter;

public SecurityConfig(
        JwtAuthFilter jwtAuthFilter,
        RequestAuditFilter requestAuditFilter,
        LoginRateLimitFilter loginRateLimitFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.requestAuditFilter = requestAuditFilter;
    this.loginRateLimitFilter = loginRateLimitFilter;
}
```

**Chaîne de filtres** — login rate limit **en premier** :

```java
.addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
.addFilterBefore(requestAuditFilter, UsernamePasswordAuthenticationFilter.class)
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```

| Ordre | Filtre | Pourquoi cet ordre |
|-------|--------|-------------------|
| 1 | `LoginRateLimitFilter` | Bloquer l'abus **le plus tôt possible** |
| 2 | `RequestAuditFilter` | Logger la requête (y compris les 429) |
| 3 | `JwtAuthFilter` | Vérifier le token JWT sur les routes protégées |

> ❓ **Quiz :** que se passe-t-il si tu mets `LoginRateLimitFilter` **après** `JwtAuthFilter` ?  
> → Le rate limit s'appliquerait quand même sur `/auth/login` (JWT ne bloque pas cette route), mais les tentatives abusives passeraient déjà par l'audit et le JWT inutilement. Moins efficace.

---

### 7d. Désactiver en tests

**Fichier :** `src/test/resources/application-test.yaml` — ajoute :

```yaml
security:
  login-rate-limit:
    enabled: false
```

Sans ça, `./mvnw test` peut échouer : plusieurs tests appellent `/auth/login` depuis la même IP (`127.0.0.1`) et déclenchent **429** au lieu du **401** ou **200** attendu.

---

### 7e. Adapter un test

**`AuthControllerMockMvcTest.java`** — mot de passe incorrect **≥ 8 caractères** :

```java
{"mail":"alice@example.com","mdp":"wrongpass"}   // pas "wrong" (trop court → 400)
```

| Mot de passe test | Résultat | Pourquoi |
|-------------------|----------|----------|
| `"wrong"` (5 car.) | **400** | `@Size(min=8)` — validation, pas login |
| `"wrongpass"` (9 car.) | **401** | Format OK, identifiants invalides — teste le vrai échec login |

---

### ✅ Contrôle étape 7

En dev, envoie **11** POST `/auth/login` ratés d'affilée (même IP) :

```bash
for i in $(seq 1 11); do
  echo "Tentative $i :"
  curl -s -o /dev/null -w "HTTP %{http_code}\n" \
    -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d '{"mail":"alice@example.com","mdp":"wrongpass"}'
done
```

| Tentative | Code attendu |
|-----------|--------------|
| 1 à 10 | **401** (identifiants invalides) |
| 11 | **429** (rate limit) |

Après **5 minutes**, le compteur se réinitialise : un nouveau login doit repasser en **401** (ou **200** si bons identifiants).

---

## Étape 8 — Vérifier A01 (IDOR) — déjà codé, à tester

### Pourquoi Spring Security ne suffit pas

`SecurityConfig` dit « il faut être connecté » pour PATCH `/commentaires/*`, mais **n'importe quel** user connecté passerait sans `verifierUserIdCorrespondAuToken`.

### Test manuel IDOR

```bash
# 1. Login Alice → TOKEN_A
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"mail":"alice@example.com","mdp":"demo1234"}'

# 2. Login autre user (ou crée-en un via /auth/register) → TOKEN_B

# 3. Alice tente de PATCH un commentaire dont elle n'est pas l'auteur,
#    en mettant TOKEN_A dans Authorization :
curl -X PATCH http://localhost:8080/commentaires/1 \
  -H "Authorization: Bearer TOKEN_A" \
  -H "Content-Type: application/json" \
  -d '{"contenu":"hack"}'
```

| Situation | Code attendu |
|-----------|--------------|
| Commentaire d'un **autre** user | **403** |
| Commentaire **d'Alice** | **200** |
| Sans token | **401** |
| `/admin/articles` sans token | **401** |

---

## Étape 9 — Tests automatisés

```bash
./mvnw test
```

| Si ça casse | Cause fréquente |
|-------------|-----------------|
| Login test → 429 | Rate limit pas désactivé en `application-test.yaml` |
| Login mauvais mdp → 400 au lieu de 401 | Mot de passe test trop court (`wrong` → utiliser `wrongpass`) |

---

## Tableau récap — « As-tu tout fait ? »

| # | Fichier / action | OWASP | Comment vérifier |
|---|------------------|-------|------------------|
| 1 | `pom.xml` + validation | A07 | compile OK |
| 2 | DTOs + `@Valid` | A07 | curl mail invalide → 400 |
| 3 | `GlobalExceptionHandler` | A05 | pas de stack trace client |
| 4 | Headers `SecurityConfig` | A05 | `curl -I /ping` |
| 5 | CORS `WebConfig` | A05 | fronts OK |
| 6 | `InputSanitizer` | A03 XSS | POST commentaire `<script>` |
| 7 | SQL paramétré | A03 SQL | code `ArticleRepository` |
| 8 | `LoginRateLimitFilter` | A07 | 11ᵉ login → 429 |
| 9 | IDOR commentaires | A01 | PATCH autre user → 403 |
| 10 | Logs accès refusé | A09 | WARN en console (08-04) |
| 11 | Secrets `.env` | A05 | pas de JWT en dur | 
| 12 | `./mvnw test` | — | BUILD SUCCESS |

---

## A09 — Journalisation

Déjà traité en [partie-08-04-journalisation-securisee.md](partie-08-04-journalisation-securisee.md). Pas de code supplémentaire ici — vérifie juste que les WARN apparaissent sur 401/403.

---

## Phrase pour l'oral CDA (A01)

> « Spring Security garantit qu'il faut un JWT pour modifier un commentaire, mais pas **que c'est le bon** utilisateur. C'est pour ça que je compare le `userId` du token avec l'auteur en base dans `CommentaireController`. »

---

## Commit Git

```bash
git add pom.xml \
  src/main/java/fr/ada/java_blog/dto/ \
  src/main/java/fr/ada/java_blog/config/ \
  src/main/java/fr/ada/java_blog/controller/AuthController.java \
  src/main/java/fr/ada/java_blog/controller/CommentaireController.java \
  src/main/java/fr/ada/java_blog/util/InputSanitizer.java \
  src/main/resources/application.yaml \
  src/test/resources/application-test.yaml \
  src/test/java/fr/ada/java_blog/controller/AuthControllerMockMvcTest.java
git commit -m "08-05 — sécurité OWASP (validation, headers, rate limit, XSS)"
```

👉 **[partie-08-06-documentation-diataxis.md](partie-08-06-documentation-diataxis.md)** — documente ces mesures dans `docs/README-architecture.md`.
