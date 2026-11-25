# Documentation technique — ECA Backend

**Projet :** ECA — Espace Collaborateur Alltech  
**Document :** Documentation technique de l'implémentation  
**Version :** 1.0 — Document de travail  
**Date :** 24 novembre 2026  
**Statut :** Implémentation initiale — en cours de développement

---

## 1. Présentation générale

### 1.1 Objectif

Le projet ECA Backend constitue la fondation technique et la structure de données du futur espace collaborateur Alltech.

L'objectif actuel est de mettre en place un socle de données commun, cohérent et réutilisable, destiné à encadrer les
développements futurs de l'application ECA.

Le périmètre actuel porte principalement sur :

- La conception et la structuration des modèles de données.
- La persistance des documents dans MongoDB.
- La mise en place de la couche d'accès aux données.
- La gestion des relations entre les différentes entités.
- Les mécanismes d'initialisation et de validation de la base de données.
- La documentation des choix techniques.

La réalisation de l'application complète, de son interface utilisateur et de ses intégrations externes ne fait pas
partie du périmètre actuel.

### 1.2 Périmètre technique

Le projet se concentre actuellement sur la couche de données et sa persistance.

Les fonctionnalités suivantes sont hors périmètre de cette première phase :

- Interface utilisateur et application frontend.
- Authentification et intégration Active Directory.
- Synchronisation des informations provenant d'Active Directory.
- Services métier et API applicatives complètes.
- Intégration avec Microsoft Teams.
- Gestion des données personnelles provenant des outils RH.

---

## 2. Environnement technique

### 2.1 Technologies utilisées

| Technologie              | Version / Configuration       | Utilisation                            |
|--------------------------|-------------------------------|----------------------------------------|
| Java                     | 21                            | Langage principal                      |
| Spring Boot              | 4.2.0                         | Framework backend                      |
| Maven                    | Configuration du projet       | Gestion des dépendances et compilation |
| Spring Data MongoDB      | Version gérée par Spring Boot | Accès aux données MongoDB              |
| MongoDB Community Server | 9.0                           | Base de données documentaire           |
| IntelliJ IDEA            | 2026.3                        | Environnement de développement         |
| Git                      | —                             | Gestion du code source                 |

### 2.2 Structure générale du projet

Package principal :

`fr.alltech.eca`

Les principaux packages actuellement utilisés sont :

- `model` : représentation des documents MongoDB et des objets embarqués.
- `repository` : interfaces d'accès aux données.

Les tests sont organisés sous :

`src/test/java/fr/alltech/eca/repository`

Les classes de test sont séparées des classes de production afin de ne pas mélanger le code applicatif et le code de
validation.

---

## 3. Architecture de la base de données

### 3.1 Base MongoDB

Nom de la base de données :

`eca`

La base de données comporte actuellement sept collections correspondant aux modèles indépendants implémentés :

| Collection              | Modèle Java        | Type                 |
|-------------------------|--------------------|----------------------|
| `collaborators`         | Collaborator       | Document indépendant |
| `agencies`              | Agency             | Document indépendant |
| `competences`           | Competence         | Document indépendant |
| `professions`           | Profession         | Document indépendant |
| `profession_categories` | ProfessionCategory | Document indépendant |
| `experience_levels`     | ExperienceLevel    | Document indépendant |
| `domains`               | Domain             | Document indépendant |

`CompetenceDeclaration` est un objet embarqué dans le document `Collaborator`. Il ne possède donc pas de collection
indépendante.

La relation `CompetenceDomain` entre les compétences et les domaines n'est pas encore implémentée.

### 3.2 Architecture conceptuelle actuelle

```text
MongoDB — eca
    │
    ├── collaborators
    │   └── Collaborator
    │       ├── agencyId
    │       ├── professionId
    │       ├── experienceLevelId
    │       └── competences[]
    │           └── CompetenceDeclaration
    │               └── competenceId
    │
    ├── agencies
    │   └── Agency
    │
    ├── competences
    │   └── Competence
    │
    ├── professions
    │   └── Profession
    │       └── categoryId
    │
    ├── profession_categories
    │   └── ProfessionCategory
    │
    ├── experience_levels
    │   └── ExperienceLevel
    │
    └── domains
        └── Domain
```

**Principes de structuration :**

- Les modèles `Collaborator`, `Agency`, `Competence`, `Profession`, `ProfessionCategory`, `ExperienceLevel` et `Domain`
  correspondent chacun à une collection MongoDB indépendante.
- `CompetenceDeclaration` est embarqué dans `Collaborator`, car une déclaration appartient au profil d'un collaborateur.
- `Collaborator.agencyId` représente une référence vers une agence.
- `Collaborator.professionId` représente une référence vers un métier.
- `Collaborator.experienceLevelId` représente une référence vers un niveau d'expérience, défini par le modèle
  `ExperienceLevel`.
- `CompetenceDeclaration.competenceId` permet de référencer une entrée du catalogue central des compétences.
- `Profession.categoryId` permet de référencer une catégorie de métier.
- `Domain` représente un domaine permettant de regrouper des compétences. La relation entre les compétences et les
  domaines reste à implémenter.

Les références entre collections sont actuellement représentées par des identifiants simples. Leur résolution nécessite
une interrogation explicite des repositories concernés.

Cette architecture décrit l'état actuel de l'implémentation et pourra évoluer en fonction des besoins fonctionnels et
des décisions techniques à venir.

### 3.3 Identifiants

Les modèles indépendants utilisent un champ Java `id`, annoté avec `@Id`.

Spring Data MongoDB assure la correspondance entre ce champ et le champ `_id` des documents MongoDB.

Les identifiants des documents sont générés par MongoDB lors de leur insertion lorsque aucun identifiant n'est fourni.

Les références entre documents sont actuellement représentées par des identifiants de type `String`.

---

## 4. Modèles de données implémentés

### 4.1 Collaborator

**Collection :** `collaborators`  
**Annotation :** `@Document(collection = "collaborators")`

Le modèle `Collaborator` représente un collaborateur Alltech.

Il regroupe les informations d'identification, les informations professionnelles et les déclarations de compétences
associées à son profil.

| Attribut            | Type Java                   | Description                            |
|---------------------|-----------------------------|----------------------------------------|
| id                  | String                      | Identifiant MongoDB du document        |
| adIdentifier        | String                      | Identifiant Active Directory           |
| firstName           | String                      | Prénom                                 |
| lastName            | String                      | Nom                                    |
| email               | String                      | Adresse électronique                   |
| agencyId            | String                      | Référence vers une agence              |
| professionId        | String                      | Référence vers un métier               |
| experienceLevelId   | String                      | Référence vers un niveau d'expérience  |
| hireDate            | LocalDate                   | Date d'arrivée chez Alltech            |
| presentation        | String                      | Présentation professionnelle           |
| photoUrl            | String                      | Adresse de la photographie             |
| profileStatus       | String                      | Statut du profil                       |
| onboardingCompleted | boolean                     | Indique si l'onboarding est terminé    |
| searchable          | boolean                     | Indique si le profil est consultable   |
| competences         | List<CompetenceDeclaration> | Déclarations de compétences embarquées |

**Constructeurs**

Le modèle dispose :

- D'un constructeur sans argument.
- D'un constructeur permettant d'initialiser les principales informations du collaborateur : identifiant AD, prénom,
  nom, adresse électronique et identifiant d'agence.

Le constructeur ne réalise aucune communication avec Active Directory. Il permet uniquement d'initialiser un objet Java.

**Initialisation des compétences**

La liste `competences` est initialisée avec une `ArrayList` vide.

Le setter de cette propriété garantit également qu'une valeur `null` est remplacée par une liste vide.

Cette organisation permet de manipuler les déclarations de compétences sans devoir vérifier systématiquement si la liste
est initialisée.

**Validation des déclarations**

La méthode `validate()` parcourt les déclarations de compétences embarquées et appelle leur propre méthode de
validation.

Elle permet ainsi de vérifier la cohérence des déclarations avant leur persistance dans MongoDB.

La validation est déclenchée automatiquement par le mécanisme `CollaboratorValidationCallback`, exécuté avant la
conversion du collaborateur en document MongoDB.

**Statut : Implémenté et testé.**

### 4.2 Agency

**Collection :** `agencies`  
**Annotation :** `@Document(collection = "agencies")`

Le modèle `Agency` représente une agence Alltech.

| Attribut      | Type Java | Description         |
|---------------|-----------|---------------------|
| id            | String    | Identifiant MongoDB |
| name          | String    | Nom de l'agence     |
| streetAddress | String    | Adresse postale     |
| postalCode    | String    | Code postal         |
| city          | String    | Ville               |
| country       | String    | Pays                |
| phoneNumber   | String    | Numéro de téléphone |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser les informations
principales de l'agence.

**Gestion de l'identifiant d'agence**

Le champ `code`, initialement envisagé, a été retiré provisoirement.

La nécessité d'un identifiant métier distinct de l'identifiant MongoDB reste à déterminer, notamment en raison de la
gestion externe des affectations d'agence.

### 4.3 Competence

**Collection :** `competences`  
**Annotation :** `@Document(collection = "competences")`

Le modèle `Competence` représente une entrée du catalogue central des compétences.

| Attribut    | Type Java | Description                         |
|-------------|-----------|-------------------------------------|
| id          | String    | Identifiant MongoDB                 |
| name        | String    | Nom de la compétence                |
| description | String    | Description de la compétence        |
| active      | boolean   | Indique si la compétence est active |

Ce catalogue permet de centraliser les compétences proposées aux collaborateurs.

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser les informations
principales.

### 4.4 CompetenceDeclaration

**Type :** Objet embarqué dans `Collaborator`  
**Collection indépendante :** Aucune

`CompetenceDeclaration` représente la manière dont un collaborateur déclare une compétence ou un sujet sur lequel il
peut être sollicité.

| Attribut     | Type Java | Description                                                    |
|--------------|-----------|----------------------------------------------------------------|
| competenceId | String    | Référence vers une compétence du catalogue                     |
| customName   | String    | Nom personnalisé lorsqu'il n'existe pas de référence catalogue |
| expertise    | boolean   | Indique si le sujet est déclaré comme expertise                |
| solicitable  | boolean   | Indique si le collaborateur accepte d'être sollicité           |
| source       | String    | Origine de la déclaration                                      |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser l'ensemble de ses
attributs.

**Relation avec Competence**

Le champ `competenceId` contient l'identifiant du document `Competence` correspondant.

Il s'agit d'une référence logique entre deux documents MongoDB. Ce mécanisme ne constitue pas une relation de clé
étrangère et n'est pas résolu automatiquement par MongoDB.

La résolution de cette référence nécessite une interrogation explicite du catalogue des compétences à partir de
l'identifiant enregistré.

**Déclaration personnalisée**

Le champ `customName` permet de représenter une compétence ou un sujet qui ne correspond pas encore à une entrée du
catalogue central.

Cette possibilité permet de distinguer les déclarations associées au catalogue central des déclarations personnalisées.

**Validation**

La méthode `validate()` applique une règle d'exclusivité entre `competenceId` et `customName`.

Une déclaration doit obligatoirement :

- Référencer une compétence existante dans le catalogue à travers `competenceId`, ou
- Fournir un nom personnalisé à travers `customName`.

Les deux champs ne peuvent pas être renseignés simultanément. Ils ne peuvent pas non plus être tous les deux absents ou
contenir uniquement des espaces.

Cette validation est déclenchée lors de la persistance d'un `Collaborator`, par l'intermédiaire de
`CollaboratorValidationCallback`.

Six tests unitaires couvrent les différents scénarios de validation.

**Statut : Implémenté et testé.**

`CompetenceDeclaration` ne possède pas de repository indépendant. Sa persistance est assurée par le document
`Collaborator` qui la contient.

### 4.5 Profession

La classe `Profession` représente une entrée du catalogue des métiers d'Alltech.

**Collection MongoDB :** `professions`

| Attribut      | Type      | Description                            |
|---------------|-----------|----------------------------------------|
| `id`          | `String`  | Identifiant unique du document         |
| `name`        | `String`  | Nom du métier                          |
| `description` | `String`  | Description du métier                  |
| `categoryId`  | `String`  | Référence vers une catégorie de métier |
| `active`      | `boolean` | Indique si le métier est actif         |

Le champ `categoryId` représente une référence logique vers un document `ProfessionCategory`.

La classe est associée à `ProfessionRepository`, qui étend `MongoRepository<Profession, String>` et fournit les
opérations standard de persistance.

**Tests :**

La persistance du modèle est couverte par `ProfessionRepositoryTest`.

Le fonctionnement de la relation avec les catégories est également vérifié par `ProfessionCategoryIntegrationTest`.

**Statut : Implémenté et testé.**

### 4.6 ProfessionCategory

La classe `ProfessionCategory` représente une catégorie permettant de regrouper plusieurs métiers au sein du catalogue
des professions.

**Collection MongoDB :** `profession_categories`

| Attribut      | Type     | Description                    |
|---------------|----------|--------------------------------|
| `id`          | `String` | Identifiant unique du document |
| `name`        | `String` | Nom de la catégorie            |
| `description` | `String` | Description de la catégorie    |

La classe dispose d'un constructeur sans argument, d'un constructeur paramétré ainsi que des accesseurs nécessaires à la
manipulation de ses attributs.

**Accès aux données :**

La classe est associée à `ProfessionCategoryRepository`, qui étend `MongoRepository<ProfessionCategory, String>` et
fournit les opérations standard de persistance.

**Test de persistance :**

`ProfessionCategoryRepositoryTest` vérifie :

- La création et l'enregistrement d'une catégorie.
- La génération de son identifiant MongoDB.
- La récupération du document à partir de cet identifiant.
- La cohérence des attributs enregistrés.
- La suppression du document après vérification.

**Statut : Implémenté et testé.**

### 4.6.1 Relation entre Profession et ProfessionCategory

Le modèle `Profession` contient un attribut `categoryId` de type `String`, permettant de référencer une catégorie du
catalogue `ProfessionCategory`.

Cette relation est représentée par un identifiant stocké dans le document `Profession`. Elle ne constitue pas une
relation de clé étrangère et n'est pas gérée automatiquement par MongoDB.

La résolution de cette référence est réalisée explicitement à l'aide des repositories concernés.

La vérification de l'existence de la catégorie référencée n'est pas actuellement intégrée à la couche de persistance.

### 4.6.2 Test d'intégration entre Profession et ProfessionCategory

La classe `ProfessionCategoryIntegrationTest` vérifie le fonctionnement de la référence entre un métier et sa catégorie.

Le scénario testé comprend les opérations suivantes :

- Création et enregistrement d'une catégorie de métier.
- Récupération de l'identifiant MongoDB généré.
- Création d'un métier utilisant cet identifiant dans son attribut `categoryId`.
- Enregistrement et récupération du métier.
- Récupération de la catégorie à partir de l'identifiant référencé.
- Vérification de la cohérence entre `categoryId` et l'identifiant de la catégorie.
- Vérification des informations de la catégorie récupérée.
- Nettoyage des documents de test.

Ce test confirme que les deux documents peuvent être persistés indépendamment et que leur association logique peut être
exploitée à travers leurs identifiants respectifs.

**Statut : Test d'intégration implémenté et réussi.**

### 4.6.3. Relation entre Profession et ProfessionCategory

Le modèle `Profession` contient un attribut `categoryId` de type `String`, permettant de référencer une catégorie du
catalogue `ProfessionCategory`.

Cette relation est représentée par un identifiant stocké dans le document `Profession`. Elle ne constitue pas une
relation objet directement gérée par MongoDB.

La résolution de cette référence et la vérification de l'existence de la catégorie associée ne sont pas encore
implémentées.

Cette organisation permet de conserver deux collections indépendantes tout en établissant un lien entre les métiers et
leurs catégories.

### 4.6.4. Test d'intégration entre Profession et ProfessionCategory

La classe `ProfessionCategoryIntegrationTest` vérifie le fonctionnement de la référence entre un métier et sa catégorie.

Le scénario testé comprend les opérations suivantes :

- Création et enregistrement d'une catégorie de métier.
- Récupération de l'identifiant MongoDB généré.
- Création d'un métier utilisant cet identifiant dans son attribut `categoryId`.
- Enregistrement et récupération du métier.
- Récupération de la catégorie à partir de l'identifiant référencé.
- Vérification de la cohérence entre `categoryId` et l'identifiant de la catégorie.
- Vérification des informations de la catégorie récupérée.
- Suppression des deux documents après vérification.

Ce test confirme que les deux documents peuvent être persistés indépendamment et que leur association logique peut être
exploitée à travers leurs identifiants respectifs.

La résolution de la référence est réalisée explicitement à l'aide des repositories. Elle n'est pas effectuée
automatiquement par MongoDB.

**Statut : Test d'intégration implémenté et réussi.**

### 4.7 ExperienceLevel

**Collection :** `experience_levels`  
**Annotation :** `@Document(collection = "experience_levels")`

Le modèle `ExperienceLevel` représente un niveau d'expérience pouvant être associé au profil d'un collaborateur.

| Attribut | Type Java | Description                     |
|----------|-----------|---------------------------------|
| id       | String    | Identifiant MongoDB du document |
| name     | String    | Nom du niveau d'expérience      |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser le nom du niveau.

Le champ `Collaborator.experienceLevelId` permet de référencer un document de cette collection.

**Accès aux données :**

La classe est associée à `ExperienceLevelRepository`, qui étend `MongoRepository<ExperienceLevel, String>`.

**Tests :**

La persistance du modèle est couverte par `ExperienceLevelRepositoryTest`.

**Statut : Implémenté et testé.**

### 4.8 Domain

**Collection :** `domains`  
**Annotation :** `@Document(collection = "domains")`

Le modèle `Domain` représente un domaine permettant de regrouper des compétences liées à un même sujet ou secteur.

| Attribut    | Type Java | Description                     |
|-------------|-----------|---------------------------------|
| id          | String    | Identifiant MongoDB du document |
| name        | String    | Nom du domaine                  |
| description | String    | Description du domaine          |
| active      | boolean   | Indique si le domaine est actif |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser le nom, la description
et le statut actif.

**Accès aux données :**

La classe est associée à `DomainRepository`, qui étend `MongoRepository<Domain, String>`.

**Tests :**

La persistance du modèle est couverte par `DomainRepositoryTest`.

Le test vérifie la création du document, la génération de son identifiant MongoDB, sa récupération et la cohérence de
ses attributs.

**Statut : Implémenté et testé.**

---

## 5. Relations entre les modèles

### 5.1 Collaborator et Agency

Le modèle `Collaborator` possède un champ `agencyId`.

Ce champ représente une référence vers un document de la collection `agencies`.

La relation est actuellement représentée par un identifiant de type `String`.

La gestion de l'affectation d'agence est externe et ne doit pas être confondue avec une responsabilité de
synchronisation Active Directory du présent projet.

### 5.2 Collaborator et Profession

Le modèle `Collaborator` possède un champ `professionId`.

Ce champ représente une référence vers un document de la collection `professions`.

La relation est représentée par un identifiant de type `String`.

La résolution de cette référence nécessite une interrogation explicite du repository correspondant.

### 5.3 Collaborator et Competence

Un collaborateur peut déclarer plusieurs compétences.

Ces déclarations sont stockées directement dans le document `Collaborator`, sous la forme d'une liste de
`CompetenceDeclaration`.

Chaque déclaration peut contenir une référence vers une entrée du catalogue central à travers `competenceId`, ou un nom
personnalisé à travers `customName`.

### 5.4 Profession et ProfessionCategory

Le modèle `Profession` contient un champ `categoryId` permettant de référencer une catégorie de la collection
`profession_categories`.

La relation est représentée par un identifiant de type `String`.

Les deux documents sont persistés indépendamment et leur association est résolue explicitement à l'aide des
repositories.

### 5.5 Choix de l'embarquement des déclarations de compétences

Le choix d'embarquer `CompetenceDeclaration` dans `Collaborator` repose sur plusieurs considérations :

- Une déclaration appartient à un collaborateur.
- Elle n'a pas actuellement de cycle de vie indépendant.
- Elle est généralement consultée avec les informations du collaborateur.
- Son embarquement permet de récupérer les déclarations avec le document parent sans requête supplémentaire.

Le catalogue `Competence` reste indépendant afin de centraliser les définitions des compétences.

Ce choix est une proposition technique retenue pour l'implémentation initiale. Il pourra être réévalué selon les besoins
de recherche, de mise à jour et de gestion des déclarations.

### 5.6 Gestion des références

Les relations entre les documents MongoDB sont actuellement représentées par des identifiants simples de type `String`.

MongoDB ne vérifie pas automatiquement l'existence des documents référencés et ne fournit pas de contraintes de clés
étrangères.

La résolution des références est réalisée explicitement à l'aide des repositories concernés.

La validation de l'existence des documents référencés est différée à une future couche de services applicatifs.

### 5.7 Relation envisagée entre Competence et Domain

Le modèle conceptuel prévoit une relation entre les compétences du catalogue central et les domaines permettant de les
regrouper.

Cette relation est envisagée sous la forme d'un modèle indépendant `CompetenceDomain`, contenant les identifiants des
deux documents associés :

- `competenceId` : référence vers une compétence.
- `domainId` : référence vers un domaine.

Cette structure permettrait de représenter une relation plusieurs-à-plusieurs entre les compétences et les domaines.

Le modèle `CompetenceDomain` et sa collection MongoDB ne sont pas encore implémentés.

La règle selon laquelle chaque compétence du catalogue central devrait appartenir à au moins un domaine reste à valider
avec les interlocuteurs Alltech.

## 6. Couche d'accès aux données

### 6.1 Principe général

L'accès aux collections MongoDB est assuré par Spring Data MongoDB.

Les interfaces de repository héritent de `MongoRepository`, qui fournit les opérations standards de persistance.

Spring Data génère automatiquement les implémentations nécessaires au démarrage de l'application.

Aucune implémentation manuelle des opérations CRUD standards n'est nécessaire à ce stade.

### 6.2 Repositories implémentés

| Repository                   | Modèle géré        | Type d'identifiant |
|------------------------------|--------------------|--------------------|
| CollaboratorRepository       | Collaborator       | String             |
| AgencyRepository             | Agency             | String             |
| CompetenceRepository         | Competence         | String             |
| ProfessionRepository         | Profession         | String             |
| ProfessionCategoryRepository | ProfessionCategory | String             |
| ExperienceLevelRepository    | ExperienceLevel    | String             |
| DomainRepository             | Domain             | String             |

Les sept interfaces héritent de `MongoRepository`.

### 6.3 Opérations disponibles

Les repositories héritent notamment des méthodes suivantes :

| Méthode        | Fonction                               |
|----------------|----------------------------------------|
| `save()`       | Insérer ou mettre à jour un document   |
| `findById()`   | Rechercher un document par identifiant |
| `findAll()`    | Récupérer tous les documents           |
| `existsById()` | Vérifier l'existence d'un document     |
| `deleteById()` | Supprimer un document                  |
| `count()`      | Compter les documents                  |

Les méthodes personnalisées ne sont pas encore nécessaires pour le périmètre actuellement implémenté.

### 6.4 Objets embarqués

`CompetenceDeclaration` ne possède pas de repository indépendant.

Son enregistrement est effectué dans le cadre de la sauvegarde du document `Collaborator` qui la contient.

La persistance du document parent entraîne l'enregistrement de ses objets embarqués.

## 7. Configuration MongoDB

### 7.1 Fichier de configuration

La configuration de connexion est actuellement définie dans `application.properties`.

```properties
spring.application.name=eca-backend
# MongoDB configuration
spring.mongodb.uri=mongodb://localhost:27017/eca
```

### 7.2 Paramètres de connexion

| Paramètre | Valeur    | Description               |
|-----------|-----------|---------------------------|
| Protocole | mongodb   | Protocole de connexion    |
| Hôte      | localhost | Serveur MongoDB local     |
| Port      | 27017     | Port MongoDB par défaut   |
| Base      | eca       | Nom de la base de données |

### 7.3 Configuration des tests

Les tests utilisent une base de données MongoDB distincte de celle utilisée par l'application :

`eca_test`

La configuration de cette base est centralisée dans le fichier :

`src/test/resources/application.properties`

La propriété `spring.mongodb.uri` y définit l'URI de connexion à la base de données de test.

Cette configuration est automatiquement chargée par Spring Boot lors de l'exécution des tests utilisant l'annotation
`@DataMongoTest`.

Il n'est donc pas nécessaire de répéter l'URI de connexion dans chaque classe de test.

Cette séparation permet d'isoler les données de test des données utilisées par l'application et de simplifier la
configuration des tests.

---

## 8. Tests

### 8.1 Objectif

Les tests actuels vérifient le fonctionnement des modèles, de la validation et de la persistance MongoDB à travers
Spring Data.

Ils utilisent :

- JUnit 5 pour l'exécution des tests.
- Spring Boot Test pour la configuration de l'environnement.
- `@DataMongoTest` pour charger le contexte de test dédié à MongoDB.
- AssertJ pour les assertions.

Les tests de persistance se connectent à MongoDB sur la base `eca_test`.

### 8.2 Tests de persistance des repositories

Les tests de repositories vérifient les opérations fondamentales de persistance des documents.

Ils couvrent notamment :

- `CollaboratorRepositoryTest` : création, enregistrement et récupération d'un collaborateur avec ses déclarations de
  compétences embarquées.
- `CompetenceRepositoryTest` : persistance et récupération d'une entrée du catalogue des compétences.
- `ProfessionRepositoryTest` : persistance et récupération d'un métier.
- `ProfessionCategoryRepositoryTest` : persistance et récupération d'une catégorie de métier.
- `ExperienceLevelRepositoryTest` : persistance et récupération d'un niveau d'expérience.
- `DomainRepositoryTest` : persistance et récupération d'un domaine.

Ces tests vérifient notamment la génération des identifiants MongoDB et la cohérence des informations enregistrées.

### 8.3 Tests unitaires de CompetenceDeclaration

Six tests unitaires vérifient la méthode `validate()` de `CompetenceDeclaration`.

Les scénarios couverts sont :

- Déclaration valide utilisant `competenceId`.
- Déclaration valide utilisant `customName`.
- Rejet d'une déclaration sans `competenceId` ni `customName`.
- Rejet d'une déclaration contenant simultanément les deux champs.
- Rejet d'un `competenceId` vide ou composé uniquement d'espaces.
- Rejet d'un `customName` vide ou composé uniquement d'espaces.

Ces tests vérifient l'application de la règle d'exclusivité entre les deux modes de déclaration.

### 8.4 Validation lors de la persistance : CollaboratorValidationCallback

La classe `CollaboratorValidationCallback` implémente `BeforeConvertCallback<Collaborator>`.

Elle déclenche automatiquement la méthode `validate()` du collaborateur avant que Spring Data MongoDB ne convertisse
celui-ci en document MongoDB.

Le test `CollaboratorRepositoryTest` couvre également deux scénarios :

1. Un collaborateur contenant une déclaration de compétence valide peut être enregistré et récupéré.
2. Un collaborateur contenant une déclaration invalide provoque une `IllegalArgumentException` lors de sa sauvegarde.

Le second scénario vérifie également qu'aucun document invalide ne reste enregistré dans la collection.

Le callback est explicitement importé dans le contexte du test à l'aide de
`@Import(CollaboratorValidationCallback.class)`.

Cette validation s'applique aux opérations passant par le mécanisme de conversion Spring Data MongoDB. Elle ne protège
pas contre les écritures directes effectuées en dehors de l'application.

### 8.5 Tests d'intégration

#### CollaboratorCompetenceIntegrationTest

**Objectif :** vérifier le fonctionnement de la relation logique entre un document `Collaborator` et une entrée du
catalogue `Competence`.

Le test couvre :

1. La création et l'enregistrement d'une compétence.
2. La récupération de son identifiant MongoDB.
3. La création d'une `CompetenceDeclaration` utilisant cet identifiant.
4. L'enregistrement et la récupération du collaborateur.
5. La récupération explicite de la compétence référencée à travers `CompetenceRepository`.
6. La vérification de la cohérence entre la déclaration et le document du catalogue.

#### ProfessionCategoryIntegrationTest

**Objectif :** vérifier le fonctionnement de la relation logique entre `Profession` et `ProfessionCategory`.

Le test couvre :

1. La création et l'enregistrement d'une catégorie.
2. La récupération de son identifiant MongoDB.
3. La création d'un métier utilisant cet identifiant dans `categoryId`.
4. L'enregistrement et la récupération du métier.
5. La récupération explicite de la catégorie référencée.
6. La vérification de la cohérence entre les deux documents.

Ces deux tests démontrent que les relations logiques entre les documents peuvent être exploitées à travers les
identifiants et les repositories concernés.

### 8.6 Nettoyage et isolation des tests

Les classes de test concernées utilisent `@AfterEach` afin de nettoyer les données après chaque test.

Pour les tests portant sur plusieurs repositories, le nettoyage est effectué explicitement pour chacun des repositories
concernés.

Cette organisation permet de limiter les interactions entre les tests et de conserver un environnement de test
reproductible.

### 8.7 Résultat et limites

L'ensemble des tests actuels a été exécuté avec succès.

Les tests vérifient le fonctionnement des modèles, de la validation des déclarations embarquées, de la persistance et
des références logiques entre documents.

Ils ne valident pas encore :

- L'existence obligatoire des documents référencés dans tous les modèles.
- La suppression ou la désactivation d'une entrée de catalogue encore référencée.
- Les règles métier définitives concernant les déclarations personnalisées.
- Les recherches multicritères.
- Les performances sur des volumes représentatifs de données.
- Les index métier.

Ces éléments restent à traiter dans les prochaines étapes du développement.

## 9. État actuel de l'implémentation

### 9.1 Éléments implémentés

| Élément                                      | État        |
|----------------------------------------------|-------------|
| Projet Spring Boot                           | Implémenté  |
| Configuration MongoDB                        | Implémentée |
| Séparation des bases `eca` et `eca_test`     | Implémentée |
| Modèle Collaborator                          | Implémenté  |
| Modèle Agency                                | Implémenté  |
| Modèle Competence                            | Implémenté  |
| Modèle CompetenceDeclaration                 | Implémenté  |
| Modèle Profession                            | Implémenté  |
| Modèle ProfessionCategory                    | Implémenté  |
| CollaboratorRepository                       | Implémenté  |
| AgencyRepository                             | Implémenté  |
| CompetenceRepository                         | Implémenté  |
| ProfessionRepository                         | Implémenté  |
| ProfessionCategoryRepository                 | Implémenté  |
| Embarquement des déclarations de compétences | Implémenté  |
| Validation des déclarations de compétences   | Implémentée |
| CollaboratorValidationCallback               | Implémenté  |
| Tests unitaires et de persistance            | Réussis     |
| Tests d'intégration des références           | Réussis     |
| Nettoyage des données après les tests        | Implémenté  |
| Modèle ExperienceLevel                       | Implémenté  |
| Modèle Domain                                | Implémenté  |
| ExperienceLevelRepository                    | Implémenté  |
| DomainRepository                             | Implémenté  |
| Tests de persistance ExperienceLevel         | Réussis     |
| Tests de persistance Domain                  | Réussis     |

### 9.2 Éléments restant à développer ou à étudier

Les éléments suivants restent envisagés pour les prochaines étapes :

- Relation entre `Competence` et `Domain` à travers le modèle envisagé `CompetenceDomain`.
- Éventuels modèles complémentaires selon le périmètre fonctionnel retenu.
- Initialisation contrôlée de la base de données.
- Éventuels modèles complémentaires selon le périmètre fonctionnel retenu.
- Initialisation contrôlée de la base de données.
- Étude et mise en place éventuelle de Liquibase.
- Gestion des index métier.
- Validation de l'existence des références dans une future couche de services.
- Définition des règles de gestion des catalogues et de leurs éléments désactivés.
- Évolution de la couverture des besoins de recherche.

Les intégrations externes, notamment Active Directory et les systèmes RH, restent hors périmètre de cette phase.

La priorité reste la construction d'une structure de données cohérente, documentée et réutilisable par les futurs
développements ECA.

## 10. Principes de développement retenus

Les principes suivants guident l'implémentation actuelle :

1. Séparer les modèles de données des interfaces d'accès aux données.
2. Utiliser les mécanismes standards de Spring Data MongoDB lorsque cela est pertinent.
3. Privilégier une structure documentaire adaptée aux usages attendus.
4. Éviter de créer des collections indépendantes sans besoin fonctionnel identifié.
5. Séparer les données de test des données de l'application.
6. Tester les opérations de persistance avant d'étendre le modèle.
7. Documenter les choix techniques et les hypothèses non validées.
8. Ne pas implémenter les intégrations externes qui ne font pas partie du périmètre.

---

## 11. Points à valider

Les points suivants nécessitent encore une validation fonctionnelle ou technique.

Ils ne constituent pas nécessairement des blocages pour l'implémentation actuelle. Ils doivent cependant être clarifiés
avant que les choix concernés deviennent des règles définitives du projet ECA.

| Sujet                         | Question                                                                                     |
|-------------------------------|----------------------------------------------------------------------------------------------|
| Périmètre du socle de données | Le modèle doit-il constituer une fondation réutilisable pour d'autres projets Alltech ?      |
| Référence agence              | Quel identifiant métier doit être utilisé pour représenter une agence gérée extérieurement ? |
| Affectation agence            | Quelle est la source officielle des affectations d'agence et qui en assure la gestion ?      |
| Expérience                    | Quelle nomenclature officielle doit être utilisée pour les niveaux d'expérience ?            |
| Compétences personnalisées    | Quelles règles doivent encadrer la gestion et la validation des compétences personnalisées ? |
| Catalogue compétences         | Comment gérer la désactivation ou la suppression d'une compétence encore référencée ?        |
| Domaines                      | Une compétence doit-elle obligatoirement appartenir à au moins un domaine ?                  |
| Initialisation                | Quel mécanisme d'initialisation et de migration doit être retenu pour MongoDB ?              |
| Indexation                    | Quels critères de recherche nécessitent des index spécifiques ?                              |
| Références                    | Quelles règles de cohérence doivent être appliquées aux références entre documents ?         |

Les décisions concernant l'identité des collaborateurs, les affectations, les catalogues et les règles métier devront
être confirmées avec les interlocuteurs Alltech concernés.

Les choix d'implémentation technique pourront être approfondis progressivement au cours du développement.

## 12. Conclusion

La première phase du backend ECA a permis de mettre en place un socle initial de données persistantes reposant sur
MongoDB et Spring Data MongoDB.

Sept modèles indépendants sont actuellement implémentés : `Collaborator`, `Agency`, `Competence`, `Profession`,
`ProfessionCategory`, `ExperienceLevel` et `Domain`.

Les déclarations de compétences sont intégrées directement dans les documents `Collaborator` à travers le modèle
embarqué `CompetenceDeclaration`.

La couche d'accès aux données repose sur sept repositories Spring Data MongoDB.

Un mécanisme de validation automatique des déclarations de compétences a également été mis en place à travers
`CollaboratorValidationCallback`.

Les opérations de persistance, les relations logiques entre documents et les règles de validation actuellement
implémentées sont couvertes par des tests unitaires, de persistance et d'intégration.

Le socle technique est ainsi établi pour poursuivre progressivement la modélisation des données, la gestion de
l'initialisation de la base et les évolutions nécessaires à la future application ECA.

**Ce document décrit l'état réel de l'implémentation au 24 novembre 2026. Il ne constitue pas une spécification
fonctionnelle définitive ni une validation officielle des choix métier par Alltech.**
