# ECA Backend

Backend du projet **ECA (Espace Collaborateur Alltech)**, une plateforme interne destinée à faciliter la collaboration
entre les collaborateurs Alltech et à centraliser leurs informations professionnelles.

Ce projet constitue le socle technique du futur Espace Collaborateur. Il se concentre sur la modélisation des données,
leur persistance dans MongoDB et la mise en place des premières fonctionnalités backend.

Le projet comprend actuellement :

- Une structure de données organisée autour des principales entités métier d'ECA.
- Des modèles Java et des repositories utilisant Spring Data MongoDB.
- Des scripts Liquibase pour initialiser la base de données, insérer les données de référence et créer les index.
- Les premières règles de validation et de cohérence des données.
- Une API REST de gestion des référentiels, dont les premières fonctionnalités concernent les métiers (*professions*).
- Des tests automatisés couvrant les différentes couches déjà développées.

## Technologies utilisées

Le backend ECA s'appuie sur les technologies suivantes :

| Technologie                           | Utilisation                                                 |
|---------------------------------------|-------------------------------------------------------------|
| **Java 21**                           | Langage de développement backend                            |
| **Spring Boot 4.2.0**                 | Framework applicatif                                        |
| **Maven Wrapper**                     | Gestion des dépendances et automatisation de la compilation |
| **MongoDB Community Server 9.0.8**    | Base de données NoSQL                                       |
| **Spring Data MongoDB**               | Accès aux données et persistance des documents              |
| **Liquibase MongoDB Extension 5.0.4** | Initialisation et évolution de la base de données           |
| **JUnit / Spring Boot Test**          | Tests automatisés                                           |

### Outils de développement

- **IntelliJ IDEA** — Développement Java.
- **MongoDB Compass** — Consultation et administration de la base MongoDB.
- **Docker Desktop** — Environnement de développement et conteneurisation.
- **Git et GitHub** — Gestion du code source et du versionnement.

## Architecture du projet

Le backend ECA adopte une architecture en couches afin de séparer les responsabilités techniques et de faciliter la
maintenance et les évolutions de l'application.

### Principales couches

| Couche       | Responsabilité                                                           |
|--------------|--------------------------------------------------------------------------|
| `config`     | Contient la configuration technique de l'application.                    |
| `model`      | Définit les modèles métier et les documents persistés dans MongoDB.      |
| `repository` | Assure l'accès aux collections MongoDB via Spring Data MongoDB.          |
| `service`    | Centralise la logique métier et coordonne les opérations applicatives.   |
| `controller` | Expose les fonctionnalités de l'application au travers d'endpoints REST. |

Ces différentes couches sont mises en place progressivement. Elles ne sont pas encore toutes implémentées pour chaque
domaine fonctionnel.

### Organisation des packages

Le projet utilise `fr.alltech.eca` comme package racine. Les classes sont regroupées selon leur responsabilité et leur
domaine fonctionnel.

L'organisation principale comprend notamment :

- `config` — Configuration de l'application et composants techniques.
- `model` — Modèles de données et objets embarqués.
    - `collaborator` — Profils collaborateurs et déclarations de compétences.
    - `reference` — Référentiels.
    - `association` — Relations entre documents indépendants.
    - `mission` — Clients et missions.
    - `tool` — Outils internes et catégories.
    - `audit` — Journalisation des modifications.
    - `notification` — Notifications.
- `repository` — Repositories Spring Data MongoDB.

Les packages `service` et `controller` accueillent progressivement la logique applicative et les endpoints REST.

### Flux de traitement des requêtes

Le fonctionnement général suit le circuit suivant :

```text
Client HTTP
    |
    v
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
MongoDB
```

Chaque couche possède un rôle distinct :

1. **Controller :** reçoit les requêtes HTTP et prépare les réponses.
2. **Service :** applique les règles métier et coordonne les opérations.
3. **Repository :** effectue les opérations de lecture et d'écriture en base de données.
4. **MongoDB :** stocke les documents et fournit les données demandées.

Les réponses suivent le chemin inverse.

Cette séparation permet notamment de ne pas disperser la logique métier dans les contrôleurs ou les repositories.

### Organisation des tests

Les tests sont regroupés dans `src/test/java/fr/alltech/eca` et répartis en trois grandes catégories :

- **Tests des modèles :** vérification du comportement des modèles et de leurs règles de validation.
- **Tests des repositories :** vérification de la persistance et de la récupération des données.
- **Tests d'intégration :** vérification des interactions entre plusieurs composants et entités de la base de données.

## Structure de la base de données

Le backend ECA utilise **MongoDB** comme système de gestion de base de données. La structure initiale repose sur 17
collections indépendantes, organisées par domaine fonctionnel.

### Collections

#### Collaborateurs et organisation

| Collection              | Modèle               | Description                                                  |
|-------------------------|----------------------|--------------------------------------------------------------|
| `collaborators`         | `Collaborator`       | Profils et informations professionnelles des collaborateurs. |
| `agencies`              | `Agency`             | Informations relatives aux agences Alltech.                  |
| `professions`           | `Profession`         | Référentiel des métiers.                                     |
| `profession_categories` | `ProfessionCategory` | Catégories permettant de regrouper les métiers.              |
| `experience_levels`     | `ExperienceLevel`    | Niveaux d'expérience professionnelle.                        |
| `roles`                 | `Role`               | Rôles pouvant être attribués aux collaborateurs.             |

#### Compétences et domaines d'expertise

| Collection               | Modèle                 | Description                                 |
|--------------------------|------------------------|---------------------------------------------|
| `competences`            | `Competence`           | Catalogue central des compétences.          |
| `domains`                | `Domain`               | Référentiel des domaines de compétences.    |
| `competence_domains`     | `CompetenceDomain`     | Associations entre compétences et domaines. |
| `profession_competences` | `ProfessionCompetence` | Associations entre métiers et compétences.  |

Les déclarations individuelles de compétences sont intégrées directement aux documents `Collaborator` sous forme
d'objets `CompetenceDeclaration`. Elles ne disposent pas de collection indépendante.

#### Clients et missions

| Collection              | Modèle                | Description                                   |
|-------------------------|-----------------------|-----------------------------------------------|
| `clients`               | `Client`              | Informations relatives aux clients.           |
| `missions`              | `Mission`             | Missions associées aux clients.               |
| `collaborator_missions` | `CollaboratorMission` | Affectations des collaborateurs aux missions. |

#### Outils internes

| Collection        | Modèle         | Description                                    |
|-------------------|----------------|------------------------------------------------|
| `tools`           | `Tool`         | Catalogue des outils et applications internes. |
| `tool_categories` | `ToolCategory` | Catégories permettant de regrouper les outils. |

#### Fonctionnalités transversales

| Collection      | Modèle         | Description                                                  |
|-----------------|----------------|--------------------------------------------------------------|
| `audit_logs`    | `AuditLog`     | Historique des modifications et informations de traçabilité. |
| `notifications` | `Notification` | Notifications destinées aux collaborateurs.                  |

### Identifiants et relations

Chaque modèle indépendant possède un identifiant de type `String`, correspondant au champ `_id` de MongoDB. Si aucun
identifiant n'est fourni lors de la création d'un document, MongoDB en génère automatiquement un.

Les relations entre documents sont représentées par des identifiants stockés dans des attributs tels que `agencyId`,
`professionId` ou `clientId`.

Ces références sont **logiques** : contrairement aux clés étrangères d'une base relationnelle, MongoDB ne garantit pas
automatiquement l'existence des documents référencés. La cohérence de ces relations doit être assurée par la logique
applicative.

Certaines relations sont représentées dans des collections d'association dédiées :

- `CompetenceDomain` relie les compétences aux domaines d'expertise.
- `ProfessionCompetence` relie les métiers aux compétences correspondantes.
- `CollaboratorMission` relie les collaborateurs aux missions.

### Documents embarqués

Le modèle `CompetenceDeclaration` est intégré directement dans le document `Collaborator`, sous forme de tableau.

Il ne possède ni collection ni identifiant MongoDB indépendant. Son cycle de vie dépend du document collaborateur qui le
contient.

### Conventions de modélisation

La conception de la base repose sur les conventions suivantes :

- Les noms techniques des modèles, attributs et collections sont en anglais.
- Les entités indépendantes disposent de leur propre collection.
- Les relations entre entités utilisent des identifiants de type `String`.
- Les relations nécessitant une association dédiée utilisent des collections spécifiques.
- Les objets appartenant directement à une entité peuvent être intégrés sous forme de documents embarqués.

La modélisation initiale est implémentée dans le projet, mais certains choix et règles métier restent à confirmer et
pourront évoluer selon les besoins fonctionnels.

## Initialisation de la base de données

L'initialisation et l'évolution de la base de données sont gérées par **Liquibase**, à l'aide de l'extension MongoDB
`liquibase-mongodb`.

Les changements sont définis dans des fichiers XML appelés *changelogs*. Ils permettent de versionner les opérations de
création des collections, d'insertion des données initiales et de création des index.

### Organisation des fichiers

Les fichiers Liquibase sont regroupés dans `src/main/resources/db/changelog/` :

```text
src/main/resources/
└── db/
    └── changelog/
        ├── db.changelog-master.xml
        ├── changes/
        │   ├── 001-create-collections.xml
        │   └── 002-create-indexes.xml
        └── data/
            ├── 001-insert-profession-categories.xml
            ├── 002-insert-professions.xml
            ├── 003-insert-agencies.xml
            ├── 004-insert-experience-levels.xml
            ├── 005-insert-roles.xml
            └── ...
```

Le fichier `db.changelog-master.xml` constitue le point d'entrée de Liquibase. Il référence les différents changelogs
dans leur ordre d'exécution :

1. Création des collections.
2. Insertion des données initiales.
3. Création des index.

### Organisation des changesets

Les changesets suivent une numérotation séquentielle :

| Plage     | Type      | Contenu                                     |
|-----------|-----------|---------------------------------------------|
| `001–017` | Structure | Création des 17 collections MongoDB.        |
| `018–075` | Données   | Insertion des données initiales et de test. |
| `076–106` | Index     | Création de 31 index.                       |

Les données initiales alimentent notamment les référentiels des métiers, catégories, agences, niveaux d'expérience,
rôles, domaines et compétences.

Les données de démonstration et de test sont fictives et ne représentent pas des collaborateurs ou clients réels
d'Alltech.

### Configuration et activation

L'exécution de Liquibase est contrôlée par la propriété `eca.liquibase.enabled`.

La configuration documentée distingue deux environnements :

| Environnement            | Base MongoDB | Liquibase |
|--------------------------|--------------|-----------|
| Configuration par défaut | `eca`        | Désactivé |
| Profil de test           | `eca_test`   | Activé    |

L'initialisation est assurée par une configuration Java personnalisée, située dans `LiquibaseConfiguration`.

Au démarrage de l'application, un `ApplicationRunner` vérifie si Liquibase est activé. Si c'est le cas, il charge le
changelog principal et exécute les changements non encore appliqués.

### Suivi des migrations

Liquibase conserve l'historique des changements exécutés dans deux collections MongoDB :

- `DATABASECHANGELOG` — Historique des changesets appliqués.
- `DATABASECHANGELOGLOCK` — Gestion du verrouillage pendant l'exécution des migrations.

Ce mécanisme permet de suivre les migrations et d'éviter de réexécuter les changesets déjà appliqués.

La structure des changelogs permet également de faire évoluer progressivement la base en ajoutant de nouveaux
changements sans modifier ceux qui ont déjà été exécutés.

## Installation et configuration locale

### Prérequis

Avant de lancer le backend, les outils suivants doivent être disponibles :

- **Java 21**
- **MongoDB Community Server 9.0.8**
- **Git**

Le projet utilise Maven Wrapper (`mvnw`), il n'est donc pas nécessaire d'installer Maven globalement.

### Configuration MongoDB

Le développement repose sur une instance MongoDB locale accessible avec les paramètres suivants :

| Paramètre               | Valeur      |
|-------------------------|-------------|
| Hôte                    | `localhost` |
| Port                    | `27017`     |
| Base principale         | `eca`       |
| Base de test            | `eca_test`  |
| Authentification locale | Désactivée  |

Deux bases distinctes permettent de séparer les données de l'application des données utilisées pour les tests et les
initialisations expérimentales.

### Configuration Spring Boot

La connexion MongoDB et l'activation de Liquibase sont contrôlées par deux propriétés :

| Propriété               | Rôle                                                           |
|-------------------------|----------------------------------------------------------------|
| `spring.mongodb.uri`    | Définit l'URI de connexion et la base de données cible.        |
| `eca.liquibase.enabled` | Active ou désactive l'initialisation de la base par Liquibase. |

La configuration par défaut cible la base `eca`, tandis que la configuration de test utilise `eca_test`.

L'initialisation Liquibase doit être activée explicitement avec `eca.liquibase.enabled`.

### Remarque sur les environnements

La configuration locale est destinée au développement et aux tests. Elle ne constitue pas une configuration de
production.

La sécurisation d'une instance de production — authentification, gestion des droits, chiffrement des communications et
sauvegardes — relève de l'infrastructure gérée par Alltech.

## API REST

Le backend expose progressivement des endpoints REST destinés aux applications clientes.

Les routes utilisent le préfixe `/api`. Les données de référence sont regroupées sous `/api/references`.

Les échanges sont réalisés au format JSON. Les données exposées sont structurées à l'aide de DTO (*Data Transfer
Objects*) : les modèles de persistance MongoDB ne sont pas directement retournés par l'API.

### Référentiel des professions

La première API de référence implémentée permet de gérer les professions stockées dans la collection `professions`.

Chaque profession est associée à une catégorie (`categoryId`) et possède un champ `active` permettant de contrôler sa
disponibilité.

#### Endpoints disponibles

| Méthode  | Endpoint                             | Description                                             | Réponse nominale |
|----------|--------------------------------------|---------------------------------------------------------|------------------|
| `GET`    | `/api/references/professions/active` | Récupérer les professions actives.                      | `200 OK`         |
| `GET`    | `/api/references/professions/all`    | Récupérer toutes les professions, actives et inactives. | `200 OK`         |
| `GET`    | `/api/references/professions/{id}`   | Récupérer une profession par son identifiant.           | `200 OK`         |
| `POST`   | `/api/references/professions`        | Créer une profession.                                   | `201 Created`    |
| `PUT`    | `/api/references/professions/{id}`   | Modifier une profession existante.                      | `200 OK`         |
| `DELETE` | `/api/references/professions/{id}`   | Désactiver une profession.                              | `204 No Content` |

Les endpoints de consultation retournent une liste vide lorsqu'aucun résultat ne correspond à la recherche. Une
profession ou une catégorie introuvable entraîne une réponse `404 Not Found` pour les opérations concernées.

#### Création et modification

Les opérations de création et de modification utilisent le DTO `ProfessionRequest` :

```json
{
  "name": "Backend Developer",
  "description": "Develops backend services and APIs.",
  "categoryId": "CAT-001"
}
```

Lors de la création :

- L'existence de la catégorie est vérifiée.
- Un identifiant est généré à l'aide de `SequenceGenerator`.
- La profession est créée avec `active` initialisé à `true`.
- Les modifications sont enregistrées dans `audit_logs`.

Lors d'une modification, seuls les champs ayant effectivement changé sont mis à jour. Le champ `active` n'est pas
modifié par cet endpoint.

#### Désactivation

La suppression d'une profession est logique (*soft delete*). Le document est conservé dans MongoDB et son champ `active`
passe à `false`.

Cette opération est également enregistrée dans `audit_logs`. Une profession déjà inactive ne génère pas de nouvelle
entrée d'audit lors d'une nouvelle demande de désactivation.

### DTO de réponse

Les endpoints utilisent `ProfessionResponse` pour exposer les informations d'une profession :

```json
{
  "id": "PROF-063",
  "name": "Backend Developer",
  "description": "Develops backend services and APIs.",
  "categoryId": "CAT-001",
  "active": true
}
```

La conversion entre le modèle `Profession` et le DTO `ProfessionResponse` est assurée par `ProfessionMapper`.

### Journalisation des modifications

Les opérations de création, de modification et de désactivation sont tracées dans la collection `audit_logs`.

Les entrées permettent notamment de conserver :

- Le type et l'identifiant de l'entité concernée.
- Le nom du champ modifié.
- L'ancienne et la nouvelle valeur.
- L'auteur et la date de la modification.

### Évolution de l'API

L'API est encore en développement. Le référentiel des professions constitue la première fonctionnalité exposée. Les
autres domaines fonctionnels pourront progressivement disposer de leurs propres endpoints.

## Tests automatisés

Le projet dispose de plusieurs niveaux de tests afin de vérifier le comportement des modèles, la persistance des données
dans MongoDB et le fonctionnement des fonctionnalités API déjà développées.

Les tests sont regroupés dans `src/test/java/fr/alltech/eca`.

### Tests des modèles

Les tests unitaires vérifient les méthodes de validation des modèles et les règles actuellement implémentées.

Deux modèles disposent notamment de tests dédiés :

- **`CompetenceDeclaration`** — Vérification de la cohérence des déclarations de compétences, notamment l'utilisation de
  `competenceId` et `customName`, ainsi que les limites associées aux déclarations.
- **`Collaborator`** — Vérification des déclarations de compétences embarquées et des limites suivantes :
    - Trois déclarations maximum avec `expertise = true`.
    - Deux déclarations maximum avec `solicitable = true`.

### Tests des repositories

Les tests de persistance vérifient les opérations de création, d'enregistrement et de récupération des documents à
travers Spring Data MongoDB.

Les dix-sept modèles indépendants disposent de classes de tests dédiées, couvrant notamment :

- Les entités métier et les référentiels.
- Les collections d'association.
- Les outils et leurs catégories.
- Les données de journalisation et les notifications.

### Tests d'intégration

Les tests d'intégration vérifient les relations logiques entre plusieurs documents MongoDB.

Les associations couvertes comprennent notamment :

| Classe de test                          | Relation vérifiée                     |
|-----------------------------------------|---------------------------------------|
| `CollaboratorCompetenceIntegrationTest` | Collaborateur et compétence           |
| `ProfessionCategoryIntegrationTest`     | Profession et catégorie de profession |
| `CompetenceDomainIntegrationTest`       | Compétence et domaine                 |
| `ProfessionCompetenceIntegrationTest`   | Profession et compétence              |
| `CollaboratorMissionIntegrationTest`    | Collaborateur et mission              |
| `ToolCategoryIntegrationTest`           | Outil et catégorie d'outil            |

Ces tests vérifient que les identifiants stockés dans les documents permettent de retrouver les entités associées. Ils
ne garantissent pas automatiquement l'existence de toutes les références lors de chaque opération de persistance.

### Tests de l'API des professions

L'API des professions dispose de tests répartis sur trois couches :

| Classe de test             | Vérifications                                                                                     |
|----------------------------|---------------------------------------------------------------------------------------------------|
| `ProfessionRepositoryTest` | Persistance et récupération des professions.                                                      |
| `ProfessionServiceTest`    | Consultation, création, modification, désactivation, validation des catégories et journalisation. |
| `ProfessionControllerTest` | Endpoints HTTP, réponses JSON, codes HTTP et gestion des erreurs.                                 |

Les tests utilisent notamment :

- **JUnit 5** pour l'exécution des tests.
- **Mockito** pour isoler les composants et simuler leurs dépendances.
- **AssertJ** pour les assertions.
- **Spring Boot Test**, `@DataMongoTest`, `@WebMvcTest` et `MockMvc` pour tester les différentes couches.

### Isolation des tests

Les tests de persistance utilisent une base MongoDB distincte :

`eca_test`

Les classes concernées nettoient les données après chaque test, notamment avec `@AfterEach`. Pour les tests impliquant
plusieurs repositories, chaque repository concerné fait l'objet d'un nettoyage explicite.

Cette organisation limite les interactions entre les tests et évite de modifier accidentellement les données de la base
principale `eca`.

### Limites de la couverture actuelle

La couverture actuelle ne vérifie pas encore tous les aspects du système. Restent notamment à approfondir :

- La vérification systématique de l'existence des documents référencés.
- Les règles de suppression ou de désactivation des éléments encore référencés.
- Les règles métier définitives concernant les déclarations personnalisées.
- Les recherches multicritères.
- Les performances sur des volumes représentatifs de données.
- L'efficacité des index sur les requêtes réelles de l'application.

## Périmètre actuel et évolutions

### État actuel du projet

La première phase du backend ECA a permis de mettre en place un socle technique comprenant :

- Les 17 collections MongoDB et leurs modèles Java.
- Les 17 repositories Spring Data MongoDB.
- Les relations entre les principales entités métier.
- Les scripts Liquibase pour la création des collections, l'insertion des données initiales et la création des index.
- Les premières règles de validation des collaborateurs et de leurs déclarations de compétences.
- Les tests unitaires, de persistance et d'intégration.
- Une première API REST dédiée au référentiel des professions.

Ce socle constitue la base des développements applicatifs à venir.

### Fonctionnalités restant à développer

Les éléments suivants ne font pas encore partie de l'implémentation actuelle :

- Le développement de l'interface utilisateur et du frontend.
- L'intégration avec Active Directory et la synchronisation automatique des collaborateurs.
- L'authentification et la gestion des accès en environnement de production.
- L'intégration avec Microsoft Teams et les autres outils externes.
- Le déploiement et l'administration de l'infrastructure de production.
- La connexion aux systèmes RH et la synchronisation de leurs données.
- L'extension des services métier et des endpoints REST aux autres domaines fonctionnels.

Ces fonctionnalités pourront être développées dans les phases ultérieures, selon les besoins et les décisions
d'architecture définis avec Alltech.

### Points restant à confirmer

Certains choix fonctionnels et techniques nécessitent encore une validation auprès des interlocuteurs Alltech,
notamment :

- Les règles de gestion des collaborateurs et de leurs affectations aux agences.
- La nomenclature des niveaux d'expérience.
- Les règles de gestion des compétences personnalisées et des domaines.
- Les règles d'attribution des rôles.
- La gestion des outils internes et de leurs catégories.
- Les événements devant être journalisés ou déclencher des notifications.
- Les règles de gestion des clients, des missions et des affectations.
- Les règles de cohérence applicables aux références entre documents.

La modélisation et les règles métier pourront évoluer en fonction des réponses apportées et des besoins fonctionnels
précisés.

### Documentation complémentaire

Les documents suivants détaillent les différents aspects du projet :

| Document                     | Contenu                                                         |
|------------------------------|-----------------------------------------------------------------|
| `documentation-technique.md` | Architecture, environnement technique, implémentation et tests. |
| `conception-bdd.md`          | Modèles de données, collections et relations.                   |
| `initialisation-bdd.md`      | Configuration et fonctionnement de Liquibase.                   |
| `api-reference.md`           | Endpoints REST, DTO et comportements de l'API.                  |

Ces documents constituent les références détaillées pour comprendre et faire évoluer le backend ECA.

