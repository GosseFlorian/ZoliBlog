# ADR — Architecture Decision Records

Index des **décisions techniques** datées. Format : une décision = un fichier `adr-NNNN-sujet.md`.

> **Explanation** Diátaxis — répond à « pourquoi avons-nous choisi X ? »

---

## Liste des ADR

| ID   | Sujet                                   | Statut  | Fichier                              |
| ---- | --------------------------------------- | ------- | ------------------------------------ |
| 0001 | JDBC + JdbcTemplate (pas JPA/Hibernate) | Accepté | [adr-0001-jdbc.md](adr-0001-jdbc.md) |
| 0002 | Authentification JWT stateless          | Accepté | [adr-0002-jwt.md](adr-0002-jwt.md)   |
| 0003 | Secrets et config via `.env`            | Accepté | [adr-0003-env.md](adr-0003-env.md)   |
| 0004 | Journalisation sans données sensibles   | Accepté | [adr-0004-logs.md](adr-0004-logs.md) |
| 0005 | Hébergement diplôme Pages + Render      | Accepté | [adr-0005-deploiement-pages-render.md](adr-0005-deploiement-pages-render.md) |

---

## Modèle pour un nouvel ADR

```markdown
# ADR-NNNN — Titre court

Date : AAAA-MM-JJ
Statut : Proposé | Accepté | Déprécié | Remplacé par ADR-XXXX

## Contexte

Pourquoi une décision était nécessaire.

## Décision

Ce qui a été choisi.

## Conséquences

Points positifs, négatifs, ce qu'il faudra revoir plus tard.
```

---

## Liens

- [README-architecture.md](../README-architecture.md) — vue d'ensemble technique
- [README-exploitation.md](../README-exploitation.md) — variables `.env`
