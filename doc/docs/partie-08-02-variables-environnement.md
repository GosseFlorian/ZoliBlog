# Partie 08 — Étape 02
# Variables d'environnement (`.env`)

> 📘 **Objectif :** sortir le secret JWT (et le reste de la config sensible) du code versionné.  
> 🗣️ **On vulgarise :** `.env` = ton carnet de config **privé** ; `.env.example` = le modèle **public** sur Git.

## Ce que tu auras à la fin

- `.env.example` versionné
- `.env` local (gitignoré)
- `application.yaml` qui lit `${JWT_SECRET}`, `${POSTGRES_PASSWORD}`, etc.
- Chargement du `.env` au démarrage Spring Boot

> ⏱️ **Durée estimée :** 45 à 60 minutes.

---

## Todo

- [ ] Créer `.env.example`
- [ ] Mettre à jour `.gitignore` (`.env` oui, `.env.example` non)
- [ ] Modifier `application.yaml`
- [ ] Ajouter la lib **dotenv-java** dans `pom.xml`
- [ ] Charger le `.env` dans `JavaBlogApplication.java`
- [ ] Tester : `./mvnw spring-boot:run` avec un `.env` valide
- [ ] Commit `08-02 — variables d'environnement`

---

## 1. Pourquoi ?

Aujourd'hui, le secret JWT est en dur :

```yaml
jwt:
  secret: "dev-secret-java-blog-changez-moi-32chars"   # ❌ dans Git
```

Si le repo est public, n'importe qui peut **forger des tokens**. En prod, c'est une faille **A05 OWASP** (mauvaise configuration).

---

## 2. Fichier `.env.example`

**Chemin :** racine du projet (à côté de `pom.xml`)

```bash
# Blog Java — copier vers .env : cp .env.example .env

DATABASE_URL=jdbc:postgresql://localhost:5432/java_blog
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

JWT_SECRET=changez-moi-minimum-32-caracteres-pour-hs256
JWT_EXPIRATION_MS=86400000

CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174
LOG_LEVEL=INFO
```

> 💡 **Minimum 32 caractères** pour `JWT_SECRET` (algorithme HS256).

---

## 3. `.gitignore`

Vérifie que `.env` est ignoré **mais pas** `.env.example` :

```gitignore
.env
!.env.example
```

---

## 4. `application.yaml`

Remplace les valeurs en dur par des placeholders :

```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/java_blog}
    username: ${POSTGRES_USER:postgres}
    password: ${POSTGRES_PASSWORD:postgres}

jwt:
  secret: ${JWT_SECRET}
  expiration-ms: ${JWT_EXPIRATION_MS:86400000}

cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:5173,http://localhost:5174}

logging:
  level:
    root: ${LOG_LEVEL:INFO}
```

> ⚠️ Pas de valeur par défaut pour `JWT_SECRET` : si absent, l'API **doit** refuser de démarrer.

**Profil test** : garde un secret fixe dans `src/test/resources/application-test.yaml` (les tests ne lisent pas ton `.env` local).

---

## 5. Dépendance Maven

Dans `pom.xml`, ajoute **dotenv-java** :

```xml
<dependency>
  <groupId>io.github.cdimascio</groupId>
  <artifactId>dotenv-java</artifactId>
  <version>3.2.0</version>
</dependency>
```

---

## 6. Chargement au démarrage

Dans `JavaBlogApplication.java`, **avant** `SpringApplication.run` :

```java
import io.github.cdimascio.dotenv.Dotenv;

public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure()
            .filename(".env")
            .ignoreIfMissing()
            .load();

    dotenv.entries().forEach(entry -> {
        if (System.getenv(entry.getKey()) == null) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    });

    SpringApplication.run(JavaBlogApplication.class, args);
}
```

---

## 7. CORS depuis l'environnement

Adapte `WebConfig.java` pour lire `${cors.allowed-origins}` et faire un `split(",")` au lieu d'origines en dur.

---

## 8. Vérifications

```bash
cp .env.example .env
./mvnw spring-boot:run
curl http://localhost:8080/ping
```

| Test | Résultat attendu |
|------|------------------|
| Sans `.env` | Erreur `JWT_SECRET` manquant |
| Avec `.env` | API démarre |
| `./mvnw test` | Toujours vert (profil `test`) |

---

## 9. Makefile (aperçu — détail en 08-03)

Tu ajouteras plus tard une cible :

```makefile
env:
	cp .env.example .env
```

---

## Commit Git

```bash
git add .env.example .gitignore pom.xml src/main/resources/application.yaml \
  src/main/java/fr/ada/java_blog/JavaBlogApplication.java \
  src/main/java/fr/ada/java_blog/config/WebConfig.java
git commit -m "08-02 — variables d'environnement (.env + dotenv)"
```

👉 **[partie-08-03-eslint-prettier-scripts.md](partie-08-03-eslint-prettier-scripts.md)**
