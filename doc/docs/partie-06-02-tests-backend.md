# Partie 06 — Étape 02
# Tests backend — unitaires, PostgreSQL et repository

> 📘 **Tu crées des tests JUnit** + la config **PostgreSQL** (profil `test`) sur la base dédiée **`java_blog_test`**.  
> 🗣️ **On vulgarise :** un test auto = un **robot** qui vérifie le code ; **`java_blog_test`** = bac à sable séparé de ta vraie base **`java_blog`**.  
> 📋 **Ordre de tous les supports :** `INDEX.md`  
> ⚙️ **Prérequis :** [partie-06-01-cadrage-devops.md](partie-06-01-cadrage-devops.md) ; PostgreSQL **lancé** (partie 02).

## Ce que tu auras à la fin de cette étape

- **`ArticleMapperTest`** — test **unitaire** pur (sans Spring).
- **`JwtServiceTest`** — génération + validation JWT.
- **`application-test.yaml`** + **`blog-test.sql`** — base **`java_blog_test`** PostgreSQL.
- **`ArticleRepositoryTest`** — `@JdbcTest` sur le **vrai** SQL PostgreSQL.

> ⏱️ **Durée estimée :** 75 à 90 minutes.

---

## Todo

- [ ] Créer la base **`java_blog_test`** ([upgrade-06-01](sql/upgrade-06-01-create-java-blog-test.sql) — annoncé en [partie-02-03](partie-02-03-connexion-bdd.md))
- [ ] Créer les fichiers `src/test/resources/`
- [ ] Créer `ArticleMapperTest` et `JwtServiceTest`
- [ ] Créer `ArticleRepositoryTest`
- [ ] Lancer `./mvnw test` → **BUILD SUCCESS** (PostgreSQL allumé)
- [ ] Committer `06-02`

---

## Branche Git

Branche active : **`partie-06`** (créée en [partie-06-01-cadrage-devops.md](partie-06-01-cadrage-devops.md)).

```bash
git branch   # * partie-06
```

Si besoin : `git checkout partie-06`

---

## 0. Base de test PostgreSQL — `java_blog_test`

On **ne mélange pas** les données de cours et les données de test.

Dans **pgAdmin** (ou `psql`), connecté en tant qu'utilisateur **`postgres`** (comme partie 02) :

**Option A — script d'upgrade (recommandé) :**

```bash
psql -U postgres -d postgres -f doc/sql/upgrade-06-01-create-java-blog-test.sql
```

**Option B — SQL manuel :**

```sql
CREATE DATABASE java_blog_test;
```

> 💡 Voir [doc/sql/README.md](sql/README.md) pour l'ordre des scripts SQL du projet.

| Base | Usage |
|---|---|
| **`java_blog`** | Dev quotidien (`spring-boot:run`, React admin) |
| **`java_blog_test`** | `./mvnw test` uniquement |

> 💡 Même utilisateur / mot de passe que la partie 02 (`postgres` / voir `POSTGRES_PASSWORD`). Seul le **nom de base** change.

> ⚠️ **Jamais `./mvnw test` sur `java_blog`** — tes données de dev seraient modifiées (INSERT/DELETE des tests).

---

## 1. Profil `test` — `application-test.yaml`

**Chemin :** `src/test/resources/application-test.yaml`

```yaml
# Profil test — base dédiée java_blog_test (jamais java_blog de dev).
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/java_blog_test
    username: postgres
    password: ${POSTGRES_PASSWORD:postgres}
  sql:
    init:
      mode: always
      schema-locations: classpath:blog-test.sql

jwt:
  secret: "test-jwt-secret-minimum-32-characters"
  expiration-ms: 3600000
```

**Explication :**

| Clé | Rôle |
|---|---|
| `java_blog_test` | Base **dédiée** aux tests — pas ta base de dev |
| `spring.sql.init.mode: always` | Recrée schéma + seed à chaque contexte test |
| `jwt.secret` | Secret fixe pour tests (pas de fuite prod) |

> 💡 En local, PostgreSQL doit **tourner** avant `./mvnw test`.

---

## 2. Schéma + seed — `blog-test.sql`

**Chemin :** `backend-api/src/test/resources/blog-test.sql`

Un seul fichier : DROP/CREATE des tables (dont `commentaires`, catégories, …) + INSERT de seed (Alice/Bob, articles, commentaires). Hash BCrypt **`demo1234`** comme en partie 05.

> 💡 Le repo ZoliBlog fournit ce fichier complet ; en cours tu peux le construire progressivement (parties 06–07).

---

# Test 1 — `ArticleMapperTest.java`

## Objectif

Vérifier le mapping **model → DTO** sans Spring ni base.

**Chemin :** `src/test/java/fr/ada/java_blog/mapper/ArticleMapperTest.java`

```java
package fr.ada.java_blog.mapper;

import fr.ada.java_blog.dto.ArticleResponse;
import fr.ada.java_blog.model.Article;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ArticleMapperTest {

    @Test
    void toResponse_copieLesChampsPublics() {
        LocalDateTime date = LocalDateTime.of(2026, 3, 1, 10, 0);

        Article article = new Article(
                42,
                "Titre test",
                "Contenu test",
                true,
                date,
                date,
                99
        );

        ArticleResponse response = ArticleMapper.toResponse(article);

        assertEquals(42, response.id());
        assertEquals("Titre test", response.titre());
        assertEquals("Contenu test", response.contenu());
        assertEquals(true, response.publie());
        assertEquals(date, response.date());
    }

    @Test
    void toResponse_neExposePasUserIdNiUpdate() {
        Article article = new Article(
                1, "A", "B", false,
                LocalDateTime.now(), LocalDateTime.now(), 5
        );

        ArticleResponse response = ArticleMapper.toResponse(article);

        assertFalse(response.publie());
    }
}
```

**Explication :**

- Pas d'annotation Spring → test **ultra rapide**, **sans** PostgreSQL.

---

# Test 2 — `JwtServiceTest.java`

**Chemin :** `src/test/java/fr/ada/java_blog/service/JwtServiceTest.java`

```java
package fr.ada.java_blog.service;

import fr.ada.java_blog.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "test-jwt-secret-minimum-32-characters",
                3_600_000L
        );
    }

    @Test
    void generateTokenPuisParseToken_retourneUserId() {
        User user = new User(1, "alice_dev", "alice@example.com", "hash");

        String token = jwtService.generateToken(user);
        Claims claims = jwtService.parseToken(token);

        assertEquals("1", claims.getSubject());
        assertEquals("alice_dev", claims.get("pseudo", String.class));
        assertEquals(1, jwtService.extractUserId(claims));
    }

    @Test
    void parseToken_invalide_lanceException() {
        assertThrows(Exception.class, () -> jwtService.parseToken("token.bidon"));
    }
}
```

---

# Test 3 — `ArticleRepositoryTest.java`

## Objectif

Tester le **SQL** contre **PostgreSQL** — le même dialecte qu'en production.

**Chemin :** `src/test/java/fr/ada/java_blog/repository/ArticleRepositoryTest.java`

```java
package fr.ada.java_blog.repository;

import fr.ada.java_blog.model.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(ArticleRepository.class)
@Transactional
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void findRecents_retourneLesArticlesDuSeed() {
        List<Article> recents = articleRepository.findRecents(5);

        assertTrue(recents.size() >= 2);
    }

    @Test
    void findById_existant_retourneArticle() {
        Optional<Article> opt = articleRepository.findById(1);

        assertTrue(opt.isPresent());
        assertEquals("Article test CI", opt.get().getTitre());
    }

    @Test
    void save_assigneUnId() {
        Article nouveau = new Article(
                null,
                "Nouveau",
                "Corps",
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1
        );

        Article sauve = articleRepository.save(nouveau);

        assertTrue(sauve.getId() != null && sauve.getId() > 0);
    }

    @Test
    void deleteById_supprimeLaLigne() {
        Article nouveau = articleRepository.save(new Article(
                null, "A supprimer", "x", false,
                LocalDateTime.now(), LocalDateTime.now(), 1
        ));

        boolean supprime = articleRepository.deleteById(nouveau.getId());

        assertTrue(supprime);
        assertTrue(articleRepository.findById(nouveau.getId()).isEmpty());
    }
}
```

**Explication :**

| Annotation | Rôle |
|---|---|
| `@JdbcTest` | Charge **uniquement** la couche JDBC |
| `@ActiveProfiles("test")` | `application-test.yaml` → **`java_blog_test`** |
| `@Import(ArticleRepository.class)` | Enregistre le repository |
| `@Transactional` | **Rollback** après chaque test — les INSERT/DELETE ne polluent pas `java_blog_test` |

> 💡 **`deleteById`** : en dev (`java_blog` + `blog.sql`), le repository supprime d'abord les lignes liées (commentaires, liaisons N-N). En test, `schema-test.sql` n'a **pas** la table `commentaires` — `executeIfTableExists` (partie 04-06) évite une erreur SQL.

---

## 5. Lancer les tests

**Prérequis :** PostgreSQL allumé, base **`java_blog_test`** créée.

```bash
./mvnw test
```

Résultat attendu :

```
Tests run: …, Failures: 0, Errors: 0
BUILD SUCCESS
```

> ✅ **Todo :** tests verts sur **`java_blog_test`** uniquement.

---

## 6. Commit

```bash
git add pom.xml src/test/
git commit -m "06-02 — tests backend : mapper, JWT, repository PostgreSQL"
```

---

## 🆘 En cas de problème

| Symptôme | Cause | Solution |
|---|---|---|
| `Connection refused` | PostgreSQL arrêté | Lance PostgreSQL (partie 02) |
| `database "java_blog_test" does not exist` | Base non créée | `CREATE DATABASE java_blog_test;` |
| `Table articles does not exist` | SQL init absent | `schema-test.sql` + `mode: always` |
| `@JdbcTest` ne trouve pas le repo | Import manquant | `@Import(ArticleRepository.class)` |
| JwtServiceTest échoue | Secret trop court | Min. 32 caractères |
| Tests modifient `java_blog` | Mauvaise URL datasource | Vérifier `java_blog_test` dans `application-test.yaml` |

---

## Suite

👉 **[partie-06-03-tests-api-mockmvc.md](partie-06-03-tests-api-mockmvc.md)** — tests HTTP complets avec **MockMvc** + JWT.
