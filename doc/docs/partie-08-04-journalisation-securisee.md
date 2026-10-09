# Partie 08 — Étape 04

# Journalisation sécurisée (SLF4J / Logback)

> 📘 **Objectif :** savoir **ce qui se passe** en prod, **sans** exposer mots de passe, tokens ou corps JSON complets.  
> 🗣️ **On vulgarise :** en Node on cite souvent **Pino** ; en Java / Spring Boot, l'équivalent standard c'est **SLF4J** + **Logback** (déjà inclus).

## Ce que tu auras à la fin

- Configuration Logback (`logback-spring.xml`)
- Niveau de log piloté par `LOG_LEVEL` dans `.env`
- `LogSanitizer` — masquer email, IP, path avant d'écrire dans les logs
- `RequestAuditFilter` — WARN à chaque réponse HTTP ≥ 400 (accès refusé, 404, 500…)
- Des logs auth avec mail **masqué** dans `AuthController` et `JwtAuthFilter`

> ⏱️ **Durée estimée :** 1 h.

---

## Todo

- [ ] Créer `logback-spring.xml`
- [ ] Créer `LogSanitizer` (classe utilitaire)
- [ ] Créer `RequestAuditFilter` (filtre HTTP)
- [ ] Enregistrer le filtre dans `SecurityConfig`
- [ ] Ajouter des logs dans `AuthController` et `JwtAuthFilter`
- [ ] Tester avec `LOG_LEVEL=INFO` puis `DEBUG` dans `.env`
- [ ] Commit `08-04 — journalisation sécurisée`

---

## 1. Règles du projet (OWASP A09)

Exigence clé : **ajouter un log de niveau WARN à chaque accès refusé**, sans donnée sensible.

| ✅ On logue                      | ❌ On ne logue jamais       |
| -------------------------------- | --------------------------- |
| Méthode HTTP, path, status code  | `req.body` complet          |
| Durée en ms                      | Token JWT (`Authorization`) |
| userId après login OK            | Mot de passe ou hash BCrypt |
| Mail masqué (`a***@example.com`) | Secret JWT                  |

### État actuel du projet (avant cette étape)

| Situation                         | Comportement                           | Log aujourd'hui ? |
| --------------------------------- | -------------------------------------- | ----------------- |
| **401** — route protégée sans JWT | `SecurityConfig` → « Non authentifié » | ❌ Non            |
| **401** — login raté              | `AuthController` → exception           | ❌ Non            |
| **403** — IDOR commentaire        | `CommentaireController` → `FORBIDDEN`  | ❌ Non            |
| JWT invalide                      | `JwtAuthFilter` → contexte effacé      | ❌ Non            |

C'est **toi** qui ajoutes la journalisation dans cette étape.

---

## 2. Deux classes, deux rôles

| Classe               | Rôle                                                 | Métaphore                                    |
| -------------------- | ---------------------------------------------------- | -------------------------------------------- |
| `LogSanitizer`       | **Nettoyer / masquer** avant d'écrire dans les logs  | Le correcteur qui floute les infos sensibles |
| `RequestAuditFilter` | **Observer** chaque requête HTTP et loguer un résumé | La caméra à l'entrée du bâtiment             |

```
Requête HTTP
    │
    ▼
RequestAuditFilter  ──► chronomètre, puis log WARN si status ≥ 400
    │                      utilise LogSanitizer pour path + IP
    ▼
JwtAuthFilter / Controllers / Security
    │
    ▼
AuthController    ──► log.warn("Échec login (mail={})", LogSanitizer.maskEmail(...))
JwtAuthFilter     ──► log.warn("JWT invalide (path={})", LogSanitizer.sanitizePath(...))
```

| Composant              | Question à laquelle il répond                            |
| ---------------------- | -------------------------------------------------------- |
| **LogSanitizer**       | « Comment écrire ce champ **sans** exposer de secret ? » |
| **RequestAuditFilter** | « Quelle requête a échoué, avec **quel code HTTP** ? »   |

---

## 3. `logback-spring.xml`

**Chemin :** `src/main/resources/logback-spring.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <springProperty name="LOG_LEVEL" source="logging.level.root" defaultValue="INFO"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="fr.ada.java_blog" level="${LOG_LEVEL}"/>
    <root level="${LOG_LEVEL}">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

Dans `application.yaml` (déjà prévu en 08-02) :

```yaml
logging:
  level:
    root: ${LOG_LEVEL:INFO}
```

---

## 4. `LogSanitizer` — masquer avant de loguer

### À quoi ça sert ?

Classe **utilitaire** (pas un filtre). Elle centralise les règles « pas de données sensibles dans les logs ».

**Sans elle :** tu risques d'écrire un mail complet, une IP exacte ou un path manipulé → fuite RGPD + OWASP A09.

**Avec elle :** tu appelles `LogSanitizer.maskEmail(...)` partout où tu logues un mail — une seule règle, un seul endroit à maintenir.

### Ce que tu écris

**Chemin :** `src/main/java/fr/ada/java_blog/util/LogSanitizer.java`

```java
package fr.ada.java_blog.util;

public final class LogSanitizer {

    private LogSanitizer() {
        // classe utilitaire — pas d'instanciation
    }

    /** Retire les retours ligne du path (évite log injection). */
    public static String sanitizePath(String path) {
        if (path == null) {
            return "/";
        }
        return path.replaceAll("[\\r\\n]", "");
    }

    /** a***@example.com — le domaine reste lisible pour le debug. */
    public static String maskEmail(String mail) {
        if (mail == null || !mail.contains("@")) {
            return "***";
        }
        int at = mail.indexOf('@');
        if (at <= 1) {
            return "***@" + mail.substring(at + 1);
        }
        return mail.charAt(0) + "***@" + mail.substring(at + 1);
    }

    /** 192.168.1.xxx — repère une IP sans la stocker en clair. */
    public static String maskIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return "unknown";
        }
        int lastDot = ip.lastIndexOf('.');
        if (lastDot > 0) {
            return ip.substring(0, lastDot) + ".xxx";
        }
        return "xxx";
    }
}
```

### Quand l'appeler ?

Dès qu'une info **utilisateur** ou **réseau** entre dans un log : `AuthController`, `JwtAuthFilter`, `RequestAuditFilter`, etc.

---

## 5. `RequestAuditFilter` — WARN à chaque accès refusé

### À quoi ça sert ?

Filtre servlet qui s'exécute sur **chaque requête HTTP**. Il entoure toute la chaîne (Security, controllers…) et lit le **status final** de la réponse.

**Rôle A09 :** une trace d'audit par requête :

- method, path, status, durée
- **WARN** si status ≥ 400 (= 401, 403, 404, 500…)
- **DEBUG** pour le reste (si `LOG_LEVEL=DEBUG`)
- **jamais** le body, le token, le mot de passe

### Ce que tu écris

**Chemin :** `src/main/java/fr/ada/java_blog/config/RequestAuditFilter.java`

```java
package fr.ada.java_blog.config;

import fr.ada.java_blog.util.LogSanitizer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Journalise chaque requête HTTP sans données sensibles (OWASP A09).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestAuditFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestAuditFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            String method = request.getMethod();
            String path = LogSanitizer.sanitizePath(request.getRequestURI());
            int status = response.getStatus();
            String ip = LogSanitizer.maskIp(request.getRemoteAddr());

            if (status >= 400) {
                log.warn("HTTP {} {} → {} ({} ms, ip={})", method, path, status, durationMs, ip);
            } else if (log.isDebugEnabled()) {
                log.debug("HTTP {} {} → {} ({} ms, ip={})", method, path, status, durationMs, ip);
            }
        }
    }
}
```

> 💡 Le bloc `finally` garantit le log **même** si une exception est levée dans un controller.

### Enregistrer le filtre dans `SecurityConfig`

Injecte `RequestAuditFilter` dans le constructeur de `SecurityConfig`, puis :

```java
.addFilterBefore(requestAuditFilter, UsernamePasswordAuthenticationFilter.class)
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```

`RequestAuditFilter` en **premier** → il voit le status **final** après toute la chaîne (401, 403, etc.).

---

## 6. Ce que ça couvre pour « WARN à chaque accès refusé »

| Cas                                      | Qui logue ?                                 |
| ---------------------------------------- | ------------------------------------------- |
| `GET /admin/...` sans token → **401**    | `RequestAuditFilter` (WARN `HTTP … → 401`)  |
| Login raté → **401**                     | `AuthController` + filtre (double trace OK) |
| IDOR commentaire → **403**               | `RequestAuditFilter` (WARN `HTTP … → 403`)  |
| JWT invalide puis 401 sur route protégée | `JwtAuthFilter` (contexte métier) + filtre  |

Le filtre couvre **tous** les 4xx/5xx automatiquement. Les logs dans `AuthController` / `JwtAuthFilter` apportent le **contexte métier** (mail masqué, JWT invalide).

### Bonus (optionnel)

Pour un 401 encore plus explicite dans `SecurityConfig` :

```java
.authenticationEntryPoint((request, response, authException) -> {
    log.warn("Accès refusé — non authentifié ({} {})",
            request.getMethod(),
            LogSanitizer.sanitizePath(request.getRequestURI()));
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Non authentifié");
})
```

(Nécessite un `Logger` statique ou une classe dédiée — ne logue **pas** le header `Authorization`.)

---

## 7. Logs dans `AuthController`

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.ada.java_blog.util.LogSanitizer;

private static final Logger log = LoggerFactory.getLogger(AuthController.class);

.orElseThrow(() -> {
    log.warn("Échec login — utilisateur inconnu (mail={})", LogSanitizer.maskEmail(body.mail()));
    return unauthorized();
});

// Succès :
log.info("Login réussi (userId={}, mail={})", user.getId(), LogSanitizer.maskEmail(body.mail()));
```

> ⚠️ **Jamais** `body.mdp()` dans un log.

---

## 8. Logs dans `JwtAuthFilter`

En cas de token invalide ou expiré (bloc `catch`) :

```java
log.warn("JWT invalide (path={})", LogSanitizer.sanitizePath(request.getRequestURI()));
// pas de log du token !
```

---

## 9. Vérifications

```bash
./mvnw spring-boot:run
```

| Test           | Commande                                                                        | Log attendu                                |
| -------------- | ------------------------------------------------------------------------------- | ------------------------------------------ |
| 401 sans token | `curl -s http://localhost:8080/admin/articles`                                  | WARN `HTTP GET /admin/articles → 401`      |
| Login raté     | `curl -X POST …/auth/login -d '{"mail":"alice@example.com","mdp":"wrongpass"}'` | WARN `Échec login (mail=a***@example.com)` |
| Login OK       | mêmes identifiants valides                                                      | INFO `Login réussi (userId=…)`             |
| 404            | `curl -s http://localhost:8080/articles/99999`                                  | WARN `HTTP GET … → 404`                    |

```bash
./mvnw test    # toujours vert
```

---

## Commit Git

```bash
git add src/main/resources/logback-spring.xml \
  src/main/java/fr/ada/java_blog/util/LogSanitizer.java \
  src/main/java/fr/ada/java_blog/config/RequestAuditFilter.java \
  src/main/java/fr/ada/java_blog/config/SecurityConfig.java \
  src/main/java/fr/ada/java_blog/controller/AuthController.java \
  src/main/java/fr/ada/java_blog/config/JwtAuthFilter.java
git commit -m "08-04 — journalisation sécurisée SLF4J"
```

👉 **[partie-08-05-securite-owasp.md](partie-08-05-securite-owasp.md)**
