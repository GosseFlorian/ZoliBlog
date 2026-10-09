# Documentation — hub Diátaxis

Ce projet suit le framework **[Diátaxis](https://diataxis.fr/)** : quatre types de documentation qui ne se mélangent pas.

| Type            | Question                                 | Fichier(s)                                                                               |
| --------------- | ---------------------------------------- | ---------------------------------------------------------------------------------------- |
| **Tutorial**    | « Comment j'apprends étape par étape ? » | Tutoriels pas à pas dans `doc/docs/` *(hors doc exploitation)* |
| **How-to**      | « Comment je lance / je déploie / je répare ? » | [README-exploitation.md](README-exploitation.md), [README-deploiement.md](README-deploiement.md), [README-runbook.md](README-runbook.md) |
| **Reference**   | « Quelle route pour POST commentaire ? » | [README-api.md](README-api.md)                                                           |
| **Explanation** | « Pourquoi JDBC et pas JPA ? »           | [README-architecture.md](README-architecture.md), [README-adr.md](./adr/README-adr.md)   |

## Où chercher quoi ?

| Besoin                             | Document                                         |
| ---------------------------------- | ------------------------------------------------ |
| Installer et lancer le projet      | [README-exploitation.md](README-exploitation.md) |
| Déployer (Railway — API + web)     | [README-deploiement.md](README-deploiement.md)   |
| Problème au démarrage ou en prod   | [README-runbook.md](README-runbook.md)           |
| Contrat HTTP (routes, auth, codes) | [README-api.md](README-api.md)                   |
| Architecture, couches, OWASP       | [README-architecture.md](README-architecture.md) |
| Décisions techniques datées        | [README-adr.md](./adr/README-adr.md)             |
| Première visite du repo            | [README.md](../README.md) (racine)               |

## Rappel

| Emplacement | Public | Contenu |
| ----------- | ------ | ------- |
| `doc/README-*.md`, `doc/adr/` | Exploitant, contributeur | Cette doc (exploitation, déploiement, …) |
| `README.md` (racine) | Premier contact | Installation express |
