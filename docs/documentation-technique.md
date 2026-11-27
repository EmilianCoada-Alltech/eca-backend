## 1. Présentation et périmètre

### 1.1 Contexte et objectif

Le projet ECA Backend constitue le socle technique du futur **Espace Collaborateur Alltech**.

Il a pour objectif de fournir une structure de données cohérente et évolutive permettant de gérer les informations
relatives aux collaborateurs, à leurs compétences, aux agences, aux métiers, aux missions et aux autres référentiels
nécessaires au fonctionnement de l'application.

Le travail réalisé vise à établir une base technique réutilisable pour les développements futurs, en s'appuyant sur les
besoins fonctionnels identifiés dans les spécifications ECA et sur les choix techniques définis avec Alltech.

### 1.2 Objectifs de cette phase

Cette phase de développement porte principalement sur :

- La conception et la modélisation des données.
- La création des modèles Java et des objets associés.
- La mise en place de la couche de persistance avec Spring Data MongoDB.
- La définition des relations et des références entre les collections.
- L'initialisation de la base de données avec Liquibase.
- La préparation des données de référence et des données de test.
- La création des index nécessaires aux principales opérations de recherche et de gestion.
- La mise en place des premières règles de validation et d'intégrité des données.
- Le développement progressif d'une API de consultation des données de référence.
- La documentation des choix techniques et des décisions d'architecture.

### 1.3 Périmètre actuel

Le périmètre couvre la construction du backend et de ses fondations techniques.

Il comprend notamment :

- Les 17 collections définies pour la structure initiale de la base de données.
- Les modèles Java correspondant aux principales entités du domaine ECA.
- Les repositories permettant l'accès aux données MongoDB.
- Les scripts Liquibase de création des collections, d'initialisation des données et de création des index.
- Les premières validations portant sur les données des collaborateurs et leurs déclarations de compétences.
- Le début de la couche API, avec notamment un premier endpoint de consultation des métiers (*professions*).

Le périmètre est évolutif : les services métier et les endpoints complémentaires seront développés progressivement à
partir des fondations mises en place.

### 1.4 Éléments hors périmètre de cette phase

Les éléments suivants ne sont pas implémentés dans le cadre de cette phase :

- Le développement de l'interface utilisateur et de l'application frontend.
- L'intégration technique avec Active Directory et la synchronisation automatique des collaborateurs.
- La mise en place de l'authentification et de la gestion des accès en environnement de production.
- L'intégration avec Microsoft Teams et les autres outils externes.
- Le déploiement et l'administration de l'infrastructure de production.
- La connexion aux systèmes RH et la synchronisation de leurs données.

Ces éléments pourront être intégrés dans les phases ultérieures du projet. Leur mise en œuvre dépend notamment des choix
d'architecture et des responsabilités définis par Alltech.

## 2. Environnement technique

### 2.1 Technologies principales

Le backend ECA repose sur l'écosystème Java et Spring Boot. MongoDB est utilisé comme système de gestion de base de
données documentaire.

| Technologie                 | Version / configuration       | Rôle                                                              |
|-----------------------------|-------------------------------|-------------------------------------------------------------------|
| Java                        | 21                            | Langage principal du backend                                      |
| Spring Boot                 | 4.2.0                         | Framework principal de l'application                              |
| Maven                       | Maven Wrapper                 | Gestion des dépendances, compilation et exécution du projet       |
| Spring Data MongoDB         | Version gérée par Spring Boot | Couche d'accès aux données MongoDB                                |
| MongoDB Community Server    | 9.0.8                         | Système de gestion de base de données                             |
| Liquibase MongoDB Extension | 5.0.4                         | Initialisation et évolution de la structure de la base de données |
| JUnit / Spring Boot Test    | Dépendances du projet         | Tests automatisés                                                 |

Les versions des dépendances Spring gérées par Spring Boot ne sont pas nécessairement définies individuellement dans le
projet. Elles sont déterminées par la gestion des dépendances de Spring Boot.

### 2.2 Outils de développement

| Outil           | Utilisation                                                               |
|-----------------|---------------------------------------------------------------------------|
| IntelliJ IDEA   | Environnement de développement Java                                       |
| MongoDB Compass | Consultation et administration visuelle de MongoDB en environnement local |
| Docker Desktop  | Outil disponible pour les besoins de conteneurisation et de développement |
| Git             | Gestion du code source et de son historique                               |
| GitHub          | Hébergement du dépôt et gestion des branches                              |

Le projet utilise Maven Wrapper (`mvnw`), ce qui permet d'exécuter les commandes Maven sans dépendre d'une installation
Maven globale sur chaque poste.

### 2.3 Base de données et environnement local

Le développement s'appuie sur une instance locale de MongoDB Community Server.

La configuration actuelle utilise les paramètres suivants :

| Paramètre                     | Valeur      |
|-------------------------------|-------------|
| Serveur                       | `localhost` |
| Port                          | `27017`     |
| Base principale               | `eca`       |
| Base de test                  | `eca_test`  |
| Mode d'authentification local | Désactivé   |

Deux bases distinctes sont utilisées afin de séparer les données de développement des données utilisées lors des tests
et des initialisations expérimentales.

La configuration locale ne constitue pas une configuration de production. La sécurisation de l'instance MongoDB de
production, notamment l'authentification, les droits d'accès, le chiffrement des communications et les sauvegardes,
relève de l'infrastructure gérée par Alltech.

### 2.4 Configuration Spring

Les paramètres de connexion à MongoDB et le comportement de Liquibase sont définis dans la configuration Spring Boot.

Le projet distingue notamment :

- La configuration par défaut, destinée à l'exécution normale de l'application et connectée à `eca`.
- La configuration de test, destinée à utiliser `eca_test` et à permettre l'initialisation de la base par Liquibase.

L'activation de Liquibase est contrôlée par la propriété :

`eca.liquibase.enabled`

Cette propriété permet de décider explicitement si l'initialisation de la base doit être exécutée au démarrage de
l'application.

La configuration détaillée des environnements et du mécanisme d'initialisation est présentée dans la section consacrée à
Liquibase.

## 3. Architecture du projet

### 3.1 Organisation générale

Le backend ECA est organisé selon une séparation des responsabilités entre plusieurs composants :

- **Configuration** : paramètres et composants techniques nécessaires au fonctionnement de l'application.
- **Modèles (`model`)** : représentation des données métier et des documents persistés dans MongoDB.
- **Repositories (`repository`)** : accès aux données à travers Spring Data MongoDB.
- **Services (`service`)** : centralisation des règles métier et orchestration des opérations.
- **Contrôleurs (`controller`)** : exposition des fonctionnalités à travers une API REST.

Cette organisation vise à séparer les responsabilités techniques et métier afin de faciliter la maintenance et
l'évolution du backend.

Les couches de services et de contrôleurs sont introduites progressivement avec le développement de l'API.

### 3.2 Organisation des packages

Le projet adopte une organisation par domaine fonctionnel. Les classes sont regroupées selon leur rôle et les entités
auxquelles elles se rapportent.

La structure principale est organisée autour du package racine `fr.alltech.eca` :

- `config` : configuration technique de l'application.
- `model` : modèles de données et objets associés.
- `repository` : interfaces d'accès aux collections MongoDB.

Les packages `model` et `repository` sont eux-mêmes subdivisés par domaine fonctionnel afin de faciliter la navigation
dans le code et de maintenir une séparation claire entre les modèles et leur persistance.

Cette organisation constitue la base de la structure du backend. Les autres couches applicatives sont ajoutées
progressivement selon les besoins de développement.

### 3.3 Responsabilités des packages

Les principaux packages du projet ont les responsabilités suivantes :

- `config` : contient les classes de configuration technique de l'application.
- `model` : contient les modèles persistés dans MongoDB ainsi que les objets embarqués.
- `model.collaborator` : regroupe les modèles liés aux profils des collaborateurs et à leurs compétences.
- `model.reference` : regroupe les catalogues et données de référence utilisés par les autres modèles.
- `model.association` : regroupe les modèles représentant les relations entre plusieurs documents.
- `model.mission` : regroupe les modèles relatifs aux clients et aux missions.
- `model.tool` : regroupe les modèles relatifs aux outils partagés.
- `model.audit` : regroupe les modèles liés à la journalisation des modifications.
- `model.notification` : regroupe les modèles de notifications.
- `repository` : contient les interfaces Spring Data MongoDB permettant d'accéder aux collections.

Cette organisation permet de retrouver facilement les composants associés à un domaine fonctionnel et de limiter le
mélange des responsabilités entre les différentes parties du backend.

### 3.4 Fonctionnement général d'une requête

Dans l'architecture cible du backend, une requête suit généralement le chemin suivant :

Client HTTP / Frontend -> Controller (API REST) -> Service (règles métier) -> Repository (Spring Data MongoDB) ->
MongoDB

Chaque couche possède une responsabilité distincte :

- **Controller** : reçoit la requête HTTP et prépare la réponse destinée au client.
- **Service** : applique les règles métier et coordonne les opérations nécessaires.
- **Repository** : exécute les opérations d'accès aux données.
- **MongoDB** : stocke les documents et retourne les résultats demandés.

La réponse remonte ensuite vers le client à travers les mêmes couches.

Cette séparation permet notamment de centraliser les règles métier dans les services, plutôt que de les disperser dans
les contrôleurs ou les repositories.

Ce fonctionnement décrit l'organisation cible des fonctionnalités API. Toutes ces couches ne sont pas encore
implémentées pour l'ensemble des domaines fonctionnels.

### 3.5 Organisation des tests

Les tests du projet sont regroupés sous `src/test/java/fr/alltech/eca`.

Ils sont organisés selon trois catégories principales :

- **Tests de modèles** : vérifient le comportement des modèles et l'application de leurs règles de validation.
- **Tests de repositories** : vérifient les opérations de persistance et les méthodes de recherche utilisant Spring Data
  MongoDB.
- **Tests d'intégration** : vérifient le fonctionnement conjoint de plusieurs composants et les interactions entre les
  différentes parties du backend.

Cette organisation permet de tester séparément les responsabilités de chaque couche et de détecter les erreurs au fur et
à mesure du développement.

La présence d'une catégorie de tests dans l'organisation du projet ne signifie pas que tous les tests correspondants
sont déjà implémentés.

### 3.6 Convention linguistique et nomenclature technique

Les noms utilisés dans le code et dans la base de données sont définis en anglais. Cette convention s'applique
notamment :

- Aux classes, attributs, méthodes et variables Java.
- Aux noms des collections et des champs MongoDB.
- Aux packages et aux composants techniques.
- Aux identifiants et aux références entre documents.

La documentation fonctionnelle et technique destinée aux équipes Alltech reste rédigée en français. Les libellés et les
contenus présentés aux utilisateurs sont également prévus en français, sauf besoin particulier.

Cette séparation permet de conserver une nomenclature technique homogène, conforme aux conventions courantes de
l'écosystème Java et Spring, tout en gardant une documentation accessible aux interlocuteurs du projet. Elle évite
également le mélange de termes français et anglais dans les noms techniques.

### 3.7 Glossaire de nomenclature

Le tableau suivant établit les principales correspondances entre les termes fonctionnels français et les noms techniques
utilisés dans le projet. Il sert de référence pour la rédaction du code et de la documentation.

| Terme fonctionnel                                     | Nom technique           |
|-------------------------------------------------------|-------------------------|
| Collaborateur                                         | `Collaborator`          |
| Identifiant Active Directory                          | `adIdentifier`          |
| Prénom                                                | `firstName`             |
| Nom                                                   | `lastName`              |
| Agence                                                | `Agency`                |
| Métier                                                | `Profession`            |
| Niveau d'expérience                                   | `ExperienceLevel`       |
| Date d'arrivée chez Alltech                           | `hireDate`              |
| Présentation                                          | `presentation`          |
| Compétence                                            | `Competence`            |
| Déclaration de compétence                             | `CompetenceDeclaration` |
| Identifiant de compétence                             | `competenceId`          |
| Nom personnalisé de compétence                        | `customName`            |
| Expertise                                             | `expertise`             |
| Sujet sur lequel le collaborateur peut être sollicité | `solicitable`           |
| Origine de la déclaration                             | `source`                |
| Mission                                               | `Mission`               |
| Affectation à une mission                             | `CollaboratorMission`   |
| Client                                                | `Client`                |
| Rôle                                                  | `Role`                  |
| Journal d'audit                                       | `AuditLog`              |

Ce glossaire pourra être enrichi lorsque de nouveaux modèles, attributs ou concepts fonctionnels seront ajoutés.

## 4. Architecture de la base de données

### 4.1 Base de données MongoDB

Le backend ECA utilise MongoDB comme système de gestion de base de données documentaire.

La base principale porte le nom `eca`. Elle est structurée autour de 17 collections correspondant aux principales
entités métier et aux relations entre ces entités.

Les collections sont réparties en deux catégories :

- **Documents indépendants** : représentent des entités disposant de leur propre collection et de leur propre cycle de
  vie.
- **Documents d'association** : représentent les relations entre plusieurs entités et permettent de conserver ces
  relations dans des collections dédiées.

La structure initiale comprend les collections suivantes :

| Collection               | Modèle Java          | Type                   |
|--------------------------|----------------------|------------------------|
| `collaborators`          | Collaborator         | Document indépendant   |
| `agencies`               | Agency               | Document indépendant   |
| `competences`            | Competence           | Document indépendant   |
| `professions`            | Profession           | Document indépendant   |
| `profession_categories`  | ProfessionCategory   | Document indépendant   |
| `experience_levels`      | ExperienceLevel      | Document indépendant   |
| `domains`                | Domain               | Document indépendant   |
| `competence_domains`     | CompetenceDomain     | Document d'association |
| `profession_competences` | ProfessionCompetence | Document d'association |
| `clients`                | Client               | Document indépendant   |
| `missions`               | Mission              | Document indépendant   |
| `collaborator_missions`  | CollaboratorMission  | Document d'association |
| `roles`                  | Role                 | Document indépendant   |
| `tools`                  | Tool                 | Document indépendant   |
| `tool_categories`        | ToolCategory         | Document indépendant   |
| `audit_logs`             | AuditLog             | Document indépendant   |
| `notifications`          | Notification         | Document indépendant   |

Une particularité concerne `CompetenceDeclaration` : il s'agit d'un objet embarqué directement dans le document
`Collaborator`. Il ne possède donc pas de collection MongoDB indépendante.

### 4.2 Architecture conceptuelle et relations entre les collections

Les relations entre les principales entités sont représentées par des identifiants faisant référence aux documents des
collections concernées.

Cette approche permet de conserver les entités indépendantes tout en établissant les associations nécessaires à leur
exploitation.

Les principales relations sont les suivantes :

- Un collaborateur référence son agence, son métier et son niveau d'expérience à travers `agencyId`, `professionId` et
  `experienceLevelId`.
- Un collaborateur possède une liste de rôles référencés par `roleIds`.
- Les déclarations de compétences d'un collaborateur sont embarquées dans son document. Une déclaration peut référencer
  une compétence du catalogue à travers `competenceId`.
- Un métier référence sa catégorie à travers `categoryId`.
- Les relations entre compétences et domaines sont conservées dans `competence_domains`.
- Les relations entre métiers et compétences sont conservées dans `profession_competences`.
- Une mission référence son client à travers `clientId`.
- Les affectations aux missions sont conservées dans `collaborator_missions`, qui référence le collaborateur et la
  mission concernés.
- Un outil référence sa catégorie à travers `categoryId`.

Les collections `audit_logs` et `notifications` disposent de leurs propres documents et sont destinées respectivement à
conserver les événements d'audit et les notifications des collaborateurs.

Ces références constituent des liens logiques entre documents MongoDB. Elles ne créent pas automatiquement de
contraintes d'intégrité référentielle : leur cohérence doit être contrôlée par la logique applicative.

### 4.3 Identifiants et références

Les modèles représentant des documents indépendants utilisent un attribut Java `id`, annoté avec `@Id`.

Spring Data MongoDB assure la correspondance entre cet attribut et le champ `_id` du document MongoDB.

Lorsqu'aucun identifiant n'est fourni lors de l'insertion d'un nouveau document, MongoDB génère automatiquement un
identifiant.

Les références entre documents sont actuellement représentées par des identifiants de type `String`.

Dans le modèle `Collaborator`, par exemple :

- `agencyId` référence une agence.
- `professionId` référence un métier.
- `experienceLevelId` référence un niveau d'expérience.
- `roleIds` contient les identifiants des rôles attribués au collaborateur.

Les références ne sont pas automatiquement résolues par MongoDB ou Spring Data MongoDB. Pour récupérer les documents
associés, l'application doit interroger explicitement les repositories concernés.

### 4.4 Principes de structuration

La structure de la base de données repose sur plusieurs principes :

- **Séparation des entités** : les entités disposant de leur propre cycle de vie sont représentées par des collections
  indépendantes.
- **Relations explicites** : les associations entre documents sont représentées par des identifiants de référence.
- **Données embarquées lorsque cela est pertinent** : les déclarations de compétences (`CompetenceDeclaration`) sont
  intégrées directement au document `Collaborator`, car elles appartiennent au profil du collaborateur.
- **Centralisation des référentiels** : les catalogues tels que les compétences, les métiers, les agences et les niveaux
  d'expérience sont conservés dans des collections dédiées.
- **Séparation des associations** : les relations entre compétences et domaines, métiers et compétences, ou
  collaborateurs et missions disposent de collections spécifiques.

Ces choix visent à maintenir une structure cohérente, à limiter la duplication des données et à faciliter leur
consultation et leur évolution.

**Justification de l'intégration de CompetenceDeclaration**

Deux approches ont été envisagées pour stocker les déclarations de compétences : créer une collection indépendante ou
les intégrer directement dans le document `Collaborator`.

L'approche retenue pour l'implémentation initiale consiste à embarquer les déclarations dans le profil du collaborateur.
Elles constituent une partie de ce profil et sont normalement consultées avec les autres informations du collaborateur.
Cette organisation évite de multiplier les collections et les opérations de lecture nécessaires pour reconstituer un
profil.

L'estimation de dimensionnement utilisée pour cette décision est d'environ 2 000 collaborateurs avec une dizaine de
déclarations chacun, soit un ordre de grandeur de 20 000 déclarations. Ce volume est considéré comme modeste pour le
modèle documentaire envisagé ; il s'agit d'une estimation de conception et non d'une mesure de données réelles.

Ce choix devra être réévalué si le nombre de déclarations par collaborateur augmente fortement, si les déclarations
acquièrent un cycle de vie ou un historique indépendant, si elles nécessitent des traitements analytiques séparés, ou si
les performances observées rendent leur stockage embarqué inadapté.

## 5. Modèles de données

### 5.1 Collaborator

**Collection :** `collaborators`  
**Annotation :** `@Document(collection = "collaborators")`

Le modèle `Collaborator` représente un collaborateur Alltech.

Il regroupe les informations d'identification, les informations professionnelles, les rôles attribués et les
déclarations de compétences associées à son profil.

| Attribut              | Type Java                   | Description                                              |
|-----------------------|-----------------------------|----------------------------------------------------------|
| `id`                  | String                      | Identifiant MongoDB du document                          |
| `adIdentifier`        | String                      | Identifiant Active Directory                             |
| `firstName`           | String                      | Prénom                                                   |
| `lastName`            | String                      | Nom                                                      |
| `email`               | String                      | Adresse électronique                                     |
| `agencyId`            | String                      | Référence vers une agence                                |
| `professionId`        | String                      | Référence vers un métier                                 |
| `experienceLevelId`   | String                      | Référence vers un niveau d'expérience                    |
| `roleIds`             | List<String>                | Identifiants des rôles attribués                         |
| `hireDate`            | LocalDate                   | Date d'arrivée chez Alltech                              |
| `presentation`        | String                      | Présentation professionnelle                             |
| `photoUrl`            | String                      | Adresse de la photographie                               |
| `profileStatus`       | String                      | Statut du profil                                         |
| `onboardingCompleted` | boolean                     | Indique si l'onboarding est terminé                      |
| `searchable`          | boolean                     | Indique si le profil peut apparaître dans les recherches |
| `competences`         | List<CompetenceDeclaration> | Déclarations de compétences embarquées                   |

**Références et rôles**

Les propriétés `agencyId`, `professionId` et `experienceLevelId` référencent les collections correspondantes.

La propriété `roleIds` contient les identifiants des rôles attribués au collaborateur. Les rôles sont stockés dans une
collection indépendante (`roles`), ce qui permet d'associer plusieurs rôles à un même collaborateur.

**Initialisation des compétences**

La liste `competences` est initialisée avec une `ArrayList` vide. Le setter remplace également une valeur `null` par une
liste vide afin de simplifier sa manipulation.

**Validation**

Le modèle dispose d'une méthode `validate()` qui vérifie les déclarations de compétences embarquées et applique les
limites définies pour les expertises et les sujets sur lesquels le collaborateur peut être sollicité.

Cette méthode doit être appelée par la couche applicative avant la persistance. Elle n'est pas déclenchée
automatiquement par Spring Data MongoDB.

**Statut :** Modèle implémenté. Les règles de validation des compétences sont implémentées et testées.

### 5.2 Agency

**Collection :** `agencies`  
**Annotation :** `@Document(collection = "agencies")`

Le modèle `Agency` représente une agence Alltech.

| Attribut        | Type Java | Description         |
|-----------------|-----------|---------------------|
| `id`            | String    | Identifiant MongoDB |
| `name`          | String    | Nom de l'agence     |
| `streetAddress` | String    | Adresse postale     |
| `postalCode`    | String    | Code postal         |
| `city`          | String    | Ville               |
| `country`       | String    | Pays                |
| `phoneNumber`   | String    | Numéro de téléphone |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser les principales
informations de l'agence.

**Identifiant de l'agence**

Un champ `code`, initialement envisagé, a été retiré provisoirement.

La nécessité d'un identifiant métier distinct de l'identifiant MongoDB reste à déterminer, notamment en raison de la
gestion externe des affectations d'agence.

**Statut :** Modèle implémenté.

### 5.3 Competence

**Collection :** `competences`  
**Annotation :** `@Document(collection = "competences")`

Le modèle `Competence` représente une compétence disponible dans le catalogue central d'Alltech.

| Attribut      | Type Java | Description                         |
|---------------|-----------|-------------------------------------|
| `id`          | String    | Identifiant MongoDB                 |
| `name`        | String    | Nom de la compétence                |
| `description` | String    | Description de la compétence        |
| `active`      | boolean   | Indique si la compétence est active |

Ce catalogue permet de centraliser les compétences proposées aux collaborateurs et d'éviter la duplication des
définitions.

Les déclarations de compétences des collaborateurs peuvent référencer une entrée de ce catalogue à travers le champ
`competenceId` de `CompetenceDeclaration`.

Le champ `active` permet de distinguer les compétences disponibles des compétences qui ne sont plus proposées dans le
catalogue.

**Statut :** Modèle implémenté.

### 5.4 CompetenceDeclaration

**Type :** Objet embarqué dans `Collaborator`  
**Collection indépendante :** Aucune

`CompetenceDeclaration` représente une compétence ou un sujet sur lequel un collaborateur peut être sollicité.

| Attribut       | Type Java | Description                                                       |
|----------------|-----------|-------------------------------------------------------------------|
| `competenceId` | String    | Référence vers une compétence du catalogue                        |
| `customName`   | String    | Nom personnalisé d'une compétence ou d'un sujet                   |
| `expertise`    | boolean   | Indique si le sujet est déclaré comme expertise                   |
| `solicitable`  | boolean   | Indique si le collaborateur accepte d'être sollicité sur ce sujet |
| `source`       | String    | Origine de la déclaration                                         |

**Référence au catalogue**

Le champ `competenceId` permet de référencer une entrée de la collection `competences`.

**Déclaration personnalisée**

Le champ `customName` permet au collaborateur de déclarer une compétence ou un sujet qui ne figure pas dans le catalogue
central.

**Règles de validation**

La méthode `validate()` applique une règle d'exclusivité entre `competenceId` et `customName`.

Une déclaration doit obligatoirement :

- Référencer une compétence du catalogue à travers `competenceId`, ou
- Fournir un nom personnalisé à travers `customName`.

Les deux champs ne peuvent pas être renseignés simultanément. Ils ne peuvent pas non plus être tous les deux absents ou
contenir uniquement des espaces.

Six tests unitaires couvrent les différents scénarios de validation.

Les limites du nombre d'expertises et de sujets sollicitables sont contrôlées au niveau du modèle `Collaborator`, et non
individuellement dans `CompetenceDeclaration` :

- Maximum de 3 déclarations avec `expertise = true`.
- Maximum de 2 déclarations avec `solicitable = true`.

**Persistance**

Les déclarations sont enregistrées directement dans le document `Collaborator` qui les contient. Elles ne disposent donc
pas d'un repository indépendant.

**Séparation entre validation structurelle et vérification des références**

La validation actuellement implémentée porte sur la structure et la cohérence interne des objets :
`CompetenceDeclaration`
valide ses propres attributs, puis `Collaborator.validate()` parcourt les déclarations embarquées et déclenche leur
validation individuelle. Cette méthode contrôle également les limites globales du nombre d'expertises et de sujets
sollicitables.

L'appel à `Collaborator.validate()` doit être effectué explicitement par la couche applicative avant la persistance. Il
n'existe pas de mécanisme automatique `BeforeConvertCallback` dans l'implémentation actuelle. La validation ne s'exécute
donc pas automatiquement lors de chaque appel à `save()`.

Cette validation structurelle ne vérifie pas que les identifiants de référence correspondent effectivement à des
documents existants. Par exemple, la présence d'un `competenceId` non vide ne garantit pas que la compétence référencée
existe dans la collection `competences`. Ces vérifications inter-documents doivent être réalisées par les futurs
services
applicatifs, en s'appuyant sur les repositories concernés.

Cette séparation permet de conserver les modèles indépendants de l'accès aux repositories et de distinguer la cohérence
interne d'un objet de la cohérence de ses références avec les autres collections.

**Limites de l'approche actuelle**

L'appel explicite à `validate()` implique que chaque parcours applicatif qui persiste un collaborateur doit penser à
l'exécuter. Les écritures réalisées directement dans MongoDB, en dehors de l'application, ne bénéficient pas de ces
contrôles. La centralisation de ces appels et les vérifications d'existence des références devront être assurées par les
services applicatifs à mesure de leur mise en place.

**Statut :** Modèle implémenté et règles de validation testées.

### 5.5 Profession

**Collection :** `professions`

La classe `Profession` représente une entrée du catalogue des métiers d'Alltech.

| Attribut      | Type Java | Description                            |
|---------------|-----------|----------------------------------------|
| `id`          | String    | Identifiant unique du document         |
| `name`        | String    | Nom du métier                          |
| `description` | String    | Description du métier                  |
| `categoryId`  | String    | Référence vers une catégorie de métier |
| `active`      | boolean   | Indique si le métier est actif         |

Le champ `categoryId` représente une référence logique vers un document `ProfessionCategory`.

**Accès aux données**

La classe est associée à `ProfessionRepository`, qui étend `MongoRepository<Profession, String>` et fournit les
opérations standard de persistance.

**Tests**

La persistance du modèle est couverte par `ProfessionRepositoryTest`. Le fonctionnement de la relation avec les
catégories est également vérifié par `ProfessionCategoryIntegrationTest`.

**Statut :** Modèle implémenté et testé.

### 5.6 ProfessionCategory

**Collection :** `profession_categories`

La classe `ProfessionCategory` représente une catégorie permettant de regrouper plusieurs métiers au sein du catalogue
des professions.

| Attribut      | Type Java | Description                    |
|---------------|-----------|--------------------------------|
| `id`          | String    | Identifiant unique du document |
| `name`        | String    | Nom de la catégorie            |
| `description` | String    | Description de la catégorie    |

**Accès aux données**

La classe est associée à `ProfessionCategoryRepository`, qui étend `MongoRepository<ProfessionCategory, String>` et
fournit les opérations standard de persistance.

**Tests**

`ProfessionCategoryRepositoryTest` vérifie notamment :

- La création et l'enregistrement d'une catégorie.
- La génération de son identifiant MongoDB.
- La récupération du document à partir de cet identifiant.
- La cohérence des attributs enregistrés.
- La suppression du document.

**Relation avec Profession**

Le modèle `Profession` contient un attribut `categoryId` permettant de référencer une catégorie.

Cette relation repose sur un identifiant MongoDB. La récupération de la catégorie nécessite une interrogation explicite
du repository correspondant.

La cohérence de cette association est vérifiée par `ProfessionCategoryIntegrationTest`.

**Statut :** Modèle implémenté et testé.

### 5.7 ExperienceLevel

**Collection :** `experience_levels`  
**Annotation :** `@Document(collection = "experience_levels")`

Le modèle `ExperienceLevel` représente un niveau d'expérience pouvant être associé au profil d'un collaborateur.

| Attribut | Type Java | Description                     |
|----------|-----------|---------------------------------|
| `id`     | String    | Identifiant MongoDB du document |
| `name`   | String    | Nom du niveau d'expérience      |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser le nom du niveau.

**Relation avec Collaborator**

Le champ `Collaborator.experienceLevelId` permet de référencer un document de cette collection.

**Accès aux données**

La classe est associée à `ExperienceLevelRepository`, qui étend `MongoRepository<ExperienceLevel, String>` et fournit
les opérations standard de persistance.

**Tests**

La persistance du modèle est couverte par `ExperienceLevelRepositoryTest`.

**Statut :** Modèle implémenté et testé.

### 5.8 Domain

**Collection :** `domains`  
**Annotation :** `@Document(collection = "domains")`

Le modèle `Domain` représente un domaine permettant de regrouper des compétences liées à un même sujet ou secteur.

| Attribut      | Type Java | Description                     |
|---------------|-----------|---------------------------------|
| `id`          | String    | Identifiant MongoDB du document |
| `name`        | String    | Nom du domaine                  |
| `description` | String    | Description du domaine          |
| `active`      | boolean   | Indique si le domaine est actif |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser le nom, la description
et le statut actif.

**Accès aux données**

La classe est associée à `DomainRepository`, qui étend `MongoRepository<Domain, String>` et fournit les opérations
standard de persistance.

**Tests**

La persistance du modèle est couverte par `DomainRepositoryTest`. Le test vérifie notamment la création du document, la
génération de son identifiant MongoDB, sa récupération et la cohérence de ses attributs.

**Statut :** Modèle implémenté et testé.

### 5.9 CompetenceDomain

**Collection :** `competence_domains`  
**Annotation :** `@Document(collection = "competence_domains")`

Le modèle `CompetenceDomain` représente l'association entre une compétence du catalogue central et un domaine.

| Attribut       | Type Java | Description                     |
|----------------|-----------|---------------------------------|
| `id`           | String    | Identifiant MongoDB du document |
| `competenceId` | String    | Référence vers une compétence   |
| `domainId`     | String    | Référence vers un domaine       |

Cette structure permet de représenter une relation plusieurs-à-plusieurs entre les compétences et les domaines, sans
embarquer les informations d'un document dans l'autre.

**Accès aux données**

La classe est associée à `CompetenceDomainRepository`, qui étend `MongoRepository<CompetenceDomain, String>` et fournit
les opérations standard de persistance.

**Tests**

La persistance du modèle est couverte par `CompetenceDomainRepositoryTest`. Le fonctionnement de l'association est
également vérifié par `CompetenceDomainIntegrationTest`.

**Statut :** Modèle implémenté et testé.

### 5.10 ProfessionCompetence

**Collection :** `profession_competences`  
**Annotation :** `@Document(collection = "profession_competences")`

Le modèle `ProfessionCompetence` représente l'association entre un métier et une compétence du catalogue central.

| Attribut       | Type Java | Description                     |
|----------------|-----------|---------------------------------|
| `id`           | String    | Identifiant MongoDB du document |
| `professionId` | String    | Référence vers un métier        |
| `competenceId` | String    | Référence vers une compétence   |

Cette association permet de définir les compétences suggérées pour un métier.

Elle ne signifie pas que ces compétences sont automatiquement déclarées dans le profil des collaborateurs exerçant ce
métier. Les déclarations individuelles restent gérées dans le document `Collaborator`.

**Accès aux données**

La classe est associée à `ProfessionCompetenceRepository`, qui étend `MongoRepository<ProfessionCompetence, String>` et
fournit les opérations standard de persistance.

**Tests**

La persistance du modèle est couverte par `ProfessionCompetenceRepositoryTest`. Le fonctionnement de l'association est
également vérifié par `ProfessionCompetenceIntegrationTest`.

**Statut :** Modèle implémenté et testé.

### 5.11 Client

**Collection :** `clients`  
**Annotation :** `@Document(collection = "clients")`

Le modèle `Client` représente un client associé aux missions enregistrées dans la base de données.

| Attribut        | Type Java | Description                      |
|-----------------|-----------|----------------------------------|
| `id`            | String    | Identifiant MongoDB du document  |
| `name`          | String    | Nom du client                    |
| `streetAddress` | String    | Adresse postale                  |
| `postalCode`    | String    | Code postal                      |
| `city`          | String    | Ville                            |
| `country`       | String    | Pays                             |
| `phoneNumber`   | String    | Numéro de téléphone              |
| `email`         | String    | Adresse électronique             |
| `isVisible`     | boolean   | Indique si le client est visible |

**Accès aux données**

La classe est associée à `ClientRepository`, qui étend `MongoRepository<Client, String>` et fournit les opérations
standard de persistance.

**Tests**

La persistance du modèle est couverte par `ClientRepositoryTest`.

**Statut :** Modèle implémenté et testé.

### 5.12 Mission

**Collection :** `missions`  
**Annotation :** `@Document(collection = "missions")`

Le modèle `Mission` représente une mission associée à un client.

| Attribut      | Type Java | Description                       |
|---------------|-----------|-----------------------------------|
| `id`          | String    | Identifiant MongoDB du document   |
| `name`        | String    | Nom de la mission                 |
| `clientId`    | String    | Référence vers un client          |
| `startDate`   | LocalDate | Date de début de la mission       |
| `endDate`     | LocalDate | Date de fin de la mission         |
| `description` | String    | Description de la mission         |
| `isVisible`   | boolean   | Indique si la mission est visible |

Le champ `clientId` représente une référence logique vers un document de la collection `clients`.

**Accès aux données**

La classe est associée à `MissionRepository`, qui étend `MongoRepository<Mission, String>` et fournit les opérations
standard de persistance.

**Tests**

La persistance du modèle est couverte par `MissionRepositoryTest`.

**Statut :** Modèle implémenté et testé.

### 5.13 CollaboratorMission

**Collection :** `collaborator_missions`  
**Annotation :** `@Document(collection = "collaborator_missions")`

Le modèle `CollaboratorMission` représente l'affectation d'un collaborateur à une mission.

| Attribut         | Type Java | Description                     |
|------------------|-----------|---------------------------------|
| `id`             | String    | Identifiant MongoDB du document |
| `collaboratorId` | String    | Référence vers un collaborateur |
| `missionId`      | String    | Référence vers une mission      |
| `startDate`      | LocalDate | Date de début de l'affectation  |
| `endDate`        | LocalDate | Date de fin de l'affectation    |

Les champs `collaboratorId` et `missionId` représentent des références logiques vers les documents correspondants.

Une date de fin `endDate` non renseignée permet de représenter une affectation toujours en cours.

**Accès aux données**

La classe est associée à `CollaboratorMissionRepository`, qui étend `MongoRepository<CollaboratorMission, String>`.

**Tests**

La persistance du modèle est couverte par `CollaboratorMissionRepositoryTest`. Le fonctionnement de l'association entre
collaborateur et mission est également vérifié par `CollaboratorMissionIntegrationTest`.

**Statut :** Modèle implémenté et testé.

### 5.14 Role

**Collection :** `roles`  
**Annotation :** `@Document(collection = "roles")`

Le modèle `Role` représente un rôle pouvant être attribué à un collaborateur. Les rôles sont stockés dans une collection
MongoDB indépendante afin de permettre leur gestion sous forme de données de référence.

| Attribut      | Type Java | Description                     |
|---------------|-----------|---------------------------------|
| `id`          | String    | Identifiant MongoDB du document |
| `name`        | String    | Nom du rôle                     |
| `description` | String    | Description du rôle             |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser l'identifiant, le nom et
la description du rôle.

Contrairement à une énumération Java (`enum`), `Role` est un document MongoDB indépendant. Cette approche permet de
gérer les rôles comme des données persistées dans la base.

Dans le modèle `Collaborator`, le champ `roleIds` contient une liste d'identifiants faisant référence aux documents de
cette collection. Un collaborateur peut ainsi se voir attribuer plusieurs rôles.

**Accès aux données**

La classe est associée à `RoleRepository`, qui étend `MongoRepository<Role, String>`.

**Tests**

La persistance du modèle et le fonctionnement de la couche d'accès aux données ont été vérifiés au travers de la suite
de tests du projet.

**Statut :** Modèle implémenté et testé.

### 5.15 Tool

**Collection :** `tools`  
**Annotation :** `@Document(collection = "tools")`

Le modèle `Tool` représente un outil ou une application interne référencé dans le catalogue des outils mis à disposition
des collaborateurs.

| Attribut      | Type Java | Description                                |
|---------------|-----------|--------------------------------------------|
| `id`          | String    | Identifiant MongoDB du document            |
| `name`        | String    | Nom de l'outil                             |
| `description` | String    | Description de l'outil                     |
| `url`         | String    | Adresse URL permettant d'accéder à l'outil |
| `categoryId`  | String    | Référence vers une catégorie d'outils      |
| `active`      | boolean   | Indique si l'outil est actif               |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser l'ensemble de ses
attributs.

Le champ `categoryId` représente une référence logique vers un document de la collection `tool_categories`. Il permet de
regrouper les outils selon leur catégorie sans embarquer les informations de celle-ci dans le document `Tool`.

Le champ `active` permet de distinguer les outils actifs des outils qui ne sont plus proposés aux collaborateurs, sans
nécessairement supprimer leur document de la base de données.

**Accès aux données**

La classe est associée à `ToolRepository`, qui étend `MongoRepository<Tool, String>`.

**Tests**

La persistance du modèle est couverte par `ToolRepositoryTest`.

**Statut :** Modèle implémenté et testé.

### 5.16 ToolCategory

**Collection :** `tool_categories`  
**Annotation :** `@Document(collection = "tool_categories")`

Le modèle `ToolCategory` représente une catégorie permettant de regrouper les outils du catalogue selon leur nature ou
leur usage.

| Attribut      | Type Java | Description                     |
|---------------|-----------|---------------------------------|
| `id`          | String    | Identifiant MongoDB du document |
| `name`        | String    | Nom de la catégorie             |
| `description` | String    | Description de la catégorie     |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser l'identifiant, le nom et
la description de la catégorie.

Le champ `Tool.categoryId` permet de référencer une catégorie de cette collection. Cette association permet de classer
les outils sans embarquer les informations de leur catégorie dans chaque document `Tool`.

**Accès aux données**

La classe est associée à `ToolCategoryRepository`, qui étend `MongoRepository<ToolCategory, String>`.

**Tests**

La persistance du modèle est couverte par `ToolCategoryRepositoryTest`. Le fonctionnement de l'association entre les
outils et leurs catégories est également vérifié par `ToolCategoryIntegrationTest`.

**Statut :** Modèle implémenté et testé.

### 5.17 AuditLog

**Collection :** `audit_logs`  
**Annotation :** `@Document(collection = "audit_logs")`

Le modèle `AuditLog` représente une entrée d'historique permettant de conserver la trace d'une modification effectuée
sur une entité de la base de données.

| Attribut     | Type Java     | Description                                       |
|--------------|---------------|---------------------------------------------------|
| `id`         | String        | Identifiant MongoDB du document                   |
| `entityType` | String        | Type de l'entité concernée                        |
| `entityId`   | String        | Identifiant du document concerné                  |
| `fieldName`  | String        | Nom du champ modifié                              |
| `oldValue`   | String        | Ancienne valeur du champ                          |
| `newValue`   | String        | Nouvelle valeur du champ                          |
| `changedBy`  | String        | Identifiant ou nom de l'auteur de la modification |
| `changedAt`  | LocalDateTime | Date et heure de la modification                  |

Les champs `entityType` et `entityId` permettent d'identifier l'entité et le document concernés sans établir de
référence directe vers un modèle particulier. Les champs `fieldName`, `oldValue` et `newValue` décrivent la modification
enregistrée.

Le champ `changedBy` identifie l'auteur de la modification, tandis que `changedAt` indique à quel moment elle a eu lieu.

Ce modèle constitue une base pour la traçabilité des modifications. La présence de cette classe ne signifie pas, à elle
seule, qu'un mécanisme automatique de journalisation est déjà intégré aux opérations métier.

**Accès aux données**

La classe est associée à `AuditLogRepository`, qui étend `MongoRepository<AuditLog, String>`.

**Tests**

La persistance du modèle est couverte par `AuditLogRepositoryTest`.

**Statut :** Modèle implémenté et testé.

### 5.18 Notification

**Collection :** `notifications`  
**Annotation :** `@Document(collection = "notifications")`

Le modèle `Notification` représente une notification destinée à un collaborateur. Il permet notamment de conserver des
messages d'information ou de rappel associés à un profil.

| Attribut         | Type Java     | Description                                  |
|------------------|---------------|----------------------------------------------|
| `id`             | String        | Identifiant MongoDB du document              |
| `collaboratorId` | String        | Référence vers le collaborateur destinataire |
| `type`           | String        | Type de notification                         |
| `message`        | String        | Contenu du message de notification           |
| `link`           | String        | Lien associé à la notification               |
| `read`           | boolean       | Indique si la notification a été lue         |
| `createdAt`      | LocalDateTime | Date et heure de création de la notification |

Le modèle dispose d'un constructeur sans argument et d'un constructeur permettant d'initialiser l'ensemble de ses
attributs.

Le champ `collaboratorId` représente une référence logique vers un document de la collection `collaborators`. Il permet
d'associer chaque notification à son destinataire.

Le champ `type` permet de distinguer les différentes catégories de notifications. Le champ `message` contient le texte
présenté au collaborateur et le champ `link` peut fournir un lien vers une ressource ou une page associée.

Le champ `read` permet de suivre l'état de lecture de la notification. Le champ `createdAt` conserve sa date et son
heure de création.

Ce modèle constitue une base de données pour la gestion des notifications. Les règles de déclenchement, les modalités de
distribution et les mécanismes de notification restent à définir dans les couches applicatives.

**Accès aux données**

La classe est associée à `NotificationRepository`, qui étend `MongoRepository<Notification, String>`.

**Tests**

La persistance du modèle est couverte par `NotificationRepositoryTest`.

**Statut :** Modèle implémenté et testé.

## 6. Couche d'accès aux données

### 6.1 Principe général

L'accès aux collections MongoDB est assuré par Spring Data MongoDB.

Les interfaces de repository héritent de `MongoRepository`, qui fournit les opérations standards de persistance.

Spring Data génère automatiquement les implémentations nécessaires au démarrage de l'application.

Aucune implémentation manuelle des opérations CRUD standards n'est donc nécessaire à ce stade.

### 6.2 Repositories implémentés

Chaque modèle persistant dans une collection MongoDB possède une interface repository dédiée, héritant de
`MongoRepository`.

| Repository                       | Modèle                 | Type d'identifiant |
|----------------------------------|------------------------|--------------------|
| `CollaboratorRepository`         | `Collaborator`         | `String`           |
| `AgencyRepository`               | `Agency`               | `String`           |
| `CompetenceRepository`           | `Competence`           | `String`           |
| `ProfessionRepository`           | `Profession`           | `String`           |
| `ProfessionCategoryRepository`   | `ProfessionCategory`   | `String`           |
| `ExperienceLevelRepository`      | `ExperienceLevel`      | `String`           |
| `DomainRepository`               | `Domain`               | `String`           |
| `CompetenceDomainRepository`     | `CompetenceDomain`     | `String`           |
| `ProfessionCompetenceRepository` | `ProfessionCompetence` | `String`           |
| `ClientRepository`               | `Client`               | `String`           |
| `MissionRepository`              | `Mission`              | `String`           |
| `CollaboratorMissionRepository`  | `CollaboratorMission`  | `String`           |
| `RoleRepository`                 | `Role`                 | `String`           |
| `ToolRepository`                 | `Tool`                 | `String`           |
| `ToolCategoryRepository`         | `ToolCategory`         | `String`           |
| `AuditLogRepository`             | `AuditLog`             | `String`           |
| `NotificationRepository`         | `Notification`         | `String`           |

Les dix-sept interfaces héritent de `MongoRepository` et fournissent les opérations standards de persistance pour leurs
modèles respectifs.

`CompetenceDeclaration` ne possède pas de repository indépendant, car il s'agit d'un objet embarqué dans `Collaborator`.
Sa persistance est assurée par celle du document parent.

### 6.3 Opérations disponibles

Les repositories héritent notamment des méthodes standards suivantes, fournies par `MongoRepository` :

| Méthode        | Fonction                               |
|----------------|----------------------------------------|
| `save()`       | Insérer ou mettre à jour un document   |
| `findById()`   | Rechercher un document par identifiant |
| `findAll()`    | Récupérer l'ensemble des documents     |
| `existsById()` | Vérifier l'existence d'un document     |
| `deleteById()` | Supprimer un document par identifiant  |
| `count()`      | Compter les documents de la collection |

Des méthodes spécifiques peuvent également être déclarées dans les interfaces repository. Spring Data MongoDB génère
leur implémentation à partir de leur nom et de la convention de requête utilisée.

Deux méthodes personnalisées sont actuellement implémentées :

| Repository               | Méthode                  | Fonction                                                                       |
|--------------------------|--------------------------|--------------------------------------------------------------------------------|
| `ProfessionRepository`   | `findByActiveTrue()`     | Récupérer uniquement les professions actives                                   |
| `CollaboratorRepository` | `findBySearchableTrue()` | Récupérer uniquement les collaborateurs autorisés à apparaître dans l'annuaire |

La méthode `findBySearchableTrue()` permet de filtrer les collaborateurs selon leur éligibilité à l'affichage dans
l'annuaire. Ce critère ne constitue pas un mécanisme de contrôle des accès et ne remplace pas les règles de sécurité
applicatives.

Les méthodes personnalisées sont ajoutées aux repositories lorsque les besoins de l'application nécessitent des critères
de recherche ou de filtrage spécifiques.

## 7. API de référence

### 7.1 API des professions

La première API de référence implémentée permet de récupérer la liste des professions actives enregistrées dans la base
de données.

**Endpoint :** `GET /api/references/professions`

**URL locale :** `http://localhost:8080/api/references/professions`

L'endpoint retourne une liste de `ProfessionResponse`, contenant les informations suivantes :

| Champ         | Description                          |
|---------------|--------------------------------------|
| `id`          | Identifiant de la profession         |
| `name`        | Nom de la profession                 |
| `description` | Description de la profession         |
| `categoryId`  | Identifiant de la catégorie associée |
| `active`      | Indique si la profession est active  |

L'implémentation repose sur quatre composants :

- `ProfessionController` : expose l'endpoint HTTP et retourne la réponse au client.
- `ProfessionService` : récupère les professions actives et coordonne leur conversion en DTO.
- `ProfessionRepository` : accède aux documents de la collection `professions`, notamment à travers la méthode
  `findByActiveTrue()`.
- `ProfessionMapper` : convertit les objets `Profession` en `ProfessionResponse`.

Le DTO `ProfessionResponse` définit explicitement les données exposées par l'API, sans retourner directement le modèle
MongoDB.

L'endpoint a été testé manuellement avec Postman. Il a retourné une réponse HTTP `200` et 62 professions actives lors de
ce test.

**Statut : Implémenté et testé manuellement.**

### 7.2 Tests de l'API des professions

La couverture automatisée de l'API des professions comprend sept tests répartis entre trois composants.

| Classe de test             | Nombre de tests | Vérifications                                                                             |
|----------------------------|----------------:|-------------------------------------------------------------------------------------------|
| `ProfessionRepositoryTest` |               3 | Persistance, récupération des professions actives et gestion d'un résultat vide           |
| `ProfessionServiceTest`    |               2 | Récupération des professions et conversion en DTO, y compris lorsque le résultat est vide |
| `ProfessionControllerTest` |               2 | Réponse HTTP `200`, structure JSON attendue et retour d'une liste vide                    |

Les tests utilisent les outils suivants :

- `@DataMongoTest` pour les tests du repository, avec la base `eca_test`.
- Mockito pour isoler et tester la couche service.
- `@WebMvcTest` et `MockMvc` pour tester le contrôleur HTTP.
- `@MockitoBean` pour remplacer les dépendances du contrôleur pendant les tests.

Cette organisation permet de vérifier séparément l'accès aux données, le traitement applicatif et l'exposition HTTP de
l'API.

**Statut : Sept tests automatisés implémentés.**

### 7.3 Documentation de l'API

La documentation de l'API est maintenue dans un fichier Markdown distinct :

`api-reference.md`

Ce document est rédigé en français et décrit les conventions de l'API ainsi que les endpoints de référence implémentés.

La version actuelle comprend notamment :

- Les conventions générales de l'API.
- La description de l'endpoint des professions.
- La structure du DTO `ProfessionResponse`.
- Les réponses HTTP.
- Les composants impliqués dans l'implémentation.
- La collection MongoDB utilisée.
- Le filtrage des professions actives.
- La couverture des tests automatisés.

Cette documentation doit être mise à jour au fur et à mesure de l'ajout de nouveaux endpoints de référence afin de
conserver une description cohérente avec l'implémentation réelle.

## 8. Initialisation de la base de données

### 8.1 Objectif et principe de Liquibase

Le backend ECA utilise Liquibase pour gérer l'initialisation et les évolutions de la base de données MongoDB.

L'objectif est de disposer d'un mécanisme versionné, reproductible et contrôlé permettant de créer les collections, de
charger les données initiales et de définir les index nécessaires au fonctionnement de l'application.

Plutôt que de gérer manuellement ces opérations depuis MongoDB Compass ou directement dans le code applicatif, les
modifications sont décrites dans des fichiers de changelog.

Chaque modification est représentée par un *changeset* identifié individuellement et suivi par Liquibase.

Au démarrage de l'application, Liquibase compare les changesets déclarés dans le changelog principal avec ceux déjà
enregistrés dans son historique. Seuls les changements qui n'ont pas encore été appliqués sont exécutés.

Cette organisation permet notamment :

- De conserver un historique des évolutions de la base de données.
- D'éviter de réexécuter les changements déjà appliqués.
- De reproduire l'initialisation dans différents environnements.
- De faire évoluer progressivement la structure et les données de la base.
- De séparer les opérations d'initialisation de la logique métier de l'application.

L'implémentation actuelle couvre la création des 17 collections, le chargement des données de référence et la création
des index définis pour le projet.

### 8.2 Dépendance et configuration de Liquibase

Le projet utilise l'extension Liquibase dédiée à MongoDB :

- **Dépendance Maven :** `org.liquibase.ext:liquibase-mongodb`
- **Version :** `5.0.4`

Cette extension permet à Liquibase d'exécuter des opérations adaptées à MongoDB. Liquibase Core est fourni de manière
transitive par cette dépendance.

Le starter Spring Boot Liquibase classique n'est pas utilisé. L'initialisation repose sur une configuration Java dédiée,
permettant de contrôler explicitement la connexion MongoDB et l'exécution des migrations.

**Configuration Spring**

Deux propriétés principales contrôlent le mécanisme :

| Propriété               | Fonction                                                 |
|-------------------------|----------------------------------------------------------|
| `spring.mongodb.uri`    | Définit la connexion MongoDB et la base de données cible |
| `eca.liquibase.enabled` | Active ou désactive l'initialisation Liquibase           |

La propriété `eca.liquibase.enabled` permet de contrôler explicitement l'exécution de Liquibase au démarrage de
l'application.

**Configuration Java**

La classe `LiquibaseConfiguration` assure l'initialisation de Liquibase au démarrage.

Elle utilise un composant Spring `ApplicationRunner`, exécuté lors du démarrage du contexte applicatif.

Le mécanisme suit les étapes suivantes :

1. Vérification de la propriété `eca.liquibase.enabled`.
2. Récupération de l'URI MongoDB définie dans `spring.mongodb.uri`.
3. Création d'une connexion Liquibase à l'aide de `DatabaseFactory`.
4. Chargement du changelog principal avec `ClassLoaderResourceAccessor`.
5. Création de l'instance `Liquibase`.
6. Exécution de `liquibase.update("")` afin d'appliquer les changesets non encore exécutés.

Le changelog principal utilisé est `db/changelog/db.changelog-master.xml`.

Lorsque `eca.liquibase.enabled` est désactivée, le mécanisme d'initialisation n'est pas exécuté.

### 8.3 Organisation des fichiers de changelog

Les fichiers Liquibase sont regroupés dans le répertoire `src/main/resources/db/changelog/`.

Le fichier `db.changelog-master.xml` constitue le point d'entrée de l'initialisation. Il référence les différents
changelogs à exécuter.

L'organisation repose sur une séparation entre les changements structurels et le chargement des données :

- `db.changelog-master.xml` : fichier principal chargé par Liquibase.
- `changes/` : répertoire contenant les changements structurels de la base, notamment la création des collections et des
  index.
- `data/` : répertoire contenant les changelogs dédiés à l'insertion des données de référence.

Les changesets sont organisés selon une numérotation séquentielle :

| Plage d'identifiants | Opération                          |
|----------------------|------------------------------------|
| `001–017`            | Création des 17 collections métier |
| `018–075`            | Insertion des données de référence |
| `076–106`            | Création des index MongoDB         |

Cette organisation permet de distinguer les différentes étapes d'initialisation tout en conservant un historique unique
et ordonné des changements appliqués.

Le changelog principal centralise leur exécution. Les évolutions futures doivent être ajoutées à cet historique sans
modifier les changesets déjà exécutés.

### 8.4 Création des collections

Le fichier `changes/001-create-collections.xml` contient les 17 changesets nécessaires à la création des collections
métier de la base `eca`.

Chaque collection est créée au moyen de l'opération MongoDB `mongodb:createCollection`, fournie par l'extension
Liquibase MongoDB.

Les changesets utilisent les identifiants suivants :

| Identifiant                         | Collection               |
|-------------------------------------|--------------------------|
| `001-create-collaborators`          | `collaborators`          |
| `002-create-agencies`               | `agencies`               |
| `003-create-competences`            | `competences`            |
| `004-create-professions`            | `professions`            |
| `005-create-profession-categories`  | `profession_categories`  |
| `006-create-experience-levels`      | `experience_levels`      |
| `007-create-domains`                | `domains`                |
| `008-create-competence-domains`     | `competence_domains`     |
| `009-create-profession-competences` | `profession_competences` |
| `010-create-clients`                | `clients`                |
| `011-create-missions`               | `missions`               |
| `012-create-collaborator-missions`  | `collaborator_missions`  |
| `013-create-roles`                  | `roles`                  |
| `014-create-tools`                  | `tools`                  |
| `015-create-tool-categories`        | `tool_categories`        |
| `016-create-audit-logs`             | `audit_logs`             |
| `017-create-notifications`          | `notifications`          |

Cette migration établit la structure initiale de la base de données en créant les collections correspondant aux modèles
persistants du projet.

`CompetenceDeclaration` ne possède pas de collection indépendante : cet objet est directement intégré aux documents
`Collaborator`.

### 8.5 Suivi des changements et historique des migrations

Liquibase conserve l'historique des changesets exécutés directement dans la base MongoDB, au moyen de deux collections
techniques.

| Collection              | Fonction                                                                                               |
|-------------------------|--------------------------------------------------------------------------------------------------------|
| `DATABASECHANGELOG`     | Enregistre les changesets appliqués, avec leurs identifiants et leurs informations d'exécution         |
| `DATABASECHANGELOGLOCK` | Gère le verrouillage pendant l'exécution des migrations afin d'éviter plusieurs exécutions simultanées |

Lorsqu'un changeset est exécuté avec succès, Liquibase enregistre son exécution dans `DATABASECHANGELOG`.

Lors d'un démarrage ultérieur, Liquibase consulte cet historique pour déterminer les changements qui restent à
appliquer.

La collection `DATABASECHANGELOGLOCK` permet quant à elle de contrôler l'accès au mécanisme de migration. Elle évite que
plusieurs processus tentent d'exécuter simultanément des changements sur la même base.

Ce mécanisme permet notamment :

- De conserver une trace des migrations exécutées.
- D'éviter de réexécuter les changesets déjà appliqués.
- De reprendre progressivement les évolutions de la base.
- De limiter les risques liés à des exécutions concurrentes.

Ces deux collections sont des éléments techniques gérés par Liquibase. Elles ne font pas partie des 17 collections
métier du projet.

### 8.6 Vérification de l'initialisation

L'initialisation des collections a été vérifiée sur MongoDB.

Un premier démarrage de l'application a confirmé la création des 17 collections métier définies dans le changelog
initial.

Un second démarrage a permis de vérifier que Liquibase reconnaissait les changesets déjà exécutés et ne les appliquait
pas une nouvelle fois.

Le résultat observé était :

| Indicateur       | Premier démarrage | Second démarrage |
|------------------|------------------:|-----------------:|
| `Run`            |                17 |                0 |
| `Previously run` |                 0 |               17 |
| `Total`          |                17 |               17 |

Une vérification complémentaire de l'idempotence a également été réalisée sur une base MongoDB de test dédiée, nommée
`liquibase_test`.

Cette vérification utilisait un changeset créant une collection temporaire `liquibase_probe`. Après sa première
exécution, le second lancement a retourné :

- `Run: 0`
- `Previously run: 1`
- `Total: 1`

Ces résultats confirment le fonctionnement du suivi des changesets et le comportement idempotent de la création des
collections testée avec MongoDB 9.0.8 et Liquibase MongoDB 5.0.4.

Ils ne constituent toutefois pas une validation exhaustive de l'ensemble des opérations de l'extension ni de l'exécution
complète des changelogs de données et d'index.

### 8.7 Conventions pour les évolutions futures

Afin de conserver un historique fiable et cohérent des évolutions de la base de données, toute modification doit
respecter les conventions suivantes.

**Ne pas modifier les changesets déjà exécutés**

Une fois qu'un changeset a été appliqué sur une base de données, il doit être considéré comme immuable.

Liquibase conserve une empreinte des changesets exécutés dans son historique. Modifier un changeset existant peut
entraîner une erreur de validation lors d'une exécution ultérieure.

Toute correction ou évolution doit donc être décrite dans un nouveau changeset. Il ne faut pas réinitialiser la base de
données pour contourner un problème lié à une migration déjà appliquée.

**Ajouter de nouveaux changesets**

Les évolutions doivent être ajoutées dans de nouveaux changesets, avec des identifiants uniques et une convention de
nommage cohérente.

Cela concerne notamment :

- L'ajout ou la modification de collections.
- L'ajout ou la modification d'index.
- L'insertion de nouvelles données de référence.
- Les modifications de structure des documents existants.

**Séparer les changements structurels et les données**

Les opérations portant sur la structure de la base doivent rester distinctes des opérations de chargement des données.

Les fichiers de création des collections et des index sont conservés dans `changes/`, tandis que les opérations
d'insertion des données de référence sont regroupées dans `data/`.

Cette séparation facilite la maintenance et permet de faire évoluer indépendamment la structure et les référentiels.

**Respecter les capacités de l'extension MongoDB**

L'extension Liquibase MongoDB 5.0.4 présente certaines limitations sur les types de changements disponibles.

Une tentative d'utilisation de `mongodb:updateMany` a notamment échoué avec une erreur `Unknown change type`.

Les insertions de données de référence utilisent actuellement `mongodb:insertMany`. Tout nouveau type d'opération doit
être vérifié avec la version de l'extension utilisée avant son intégration.

### 8.8 Journalisation et diagnostic

**Lecture des logs Liquibase**

Lors de l'exécution d'une migration, Liquibase affiche un résumé permettant de suivre les changements appliqués.

Les principales informations sont :

| Indicateur          | Signification                                          |
|---------------------|--------------------------------------------------------|
| `Run`               | Nombre de changesets exécutés pendant cette opération  |
| `Previously run`    | Nombre de changesets déjà exécutés                     |
| `Filtered out`      | Nombre de changesets ignorés par les filtres éventuels |
| `Total change sets` | Nombre total de changesets pris en compte              |

Ces informations permettent de vérifier rapidement si Liquibase a exécuté de nouvelles migrations ou si la base était
déjà à jour.

**Diagnostic en cas d'erreur**

En cas d'échec de l'initialisation, les premières vérifications à effectuer sont :

- Vérifier que le serveur MongoDB est démarré et accessible.
- Vérifier que l'URI MongoDB configurée dans `spring.mongodb.uri` est correcte.
- Vérifier que l'application cible bien la base de données attendue.
- Vérifier que le changelog principal et les fichiers qu'il référence sont présents dans les ressources du projet.
- Consulter les logs pour identifier le changeset à l'origine de l'erreur.
- Vérifier l'état des collections techniques `DATABASECHANGELOG` et `DATABASECHANGELOGLOCK`.

Une erreur liée à un changeset déjà exécuté peut notamment provenir d'une modification apportée à ce changeset après son
application.

Dans ce cas, il convient de vérifier l'historique Liquibase et de privilégier la création d'un nouveau changeset plutôt
que la modification d'une migration existante.

**Gestion du verrouillage**

Liquibase utilise `DATABASECHANGELOGLOCK` pour éviter que plusieurs processus exécutent simultanément des migrations sur
la même base.

Si une exécution est interrompue de manière inattendue, il peut être nécessaire de vérifier l'état du verrou.

La suppression ou la modification manuelle du verrou ne doit jamais être effectuée sans avoir vérifié qu'aucune
migration n'est encore en cours. Toute intervention doit être réalisée avec précaution afin de ne pas compromettre
l'historique des migrations.

### 8.9 Environnement de test

Les tests de persistance MongoDB utilisent une base de données distincte de celle de l'application :

`eca_test`

La connexion à cette base est définie dans le fichier :

`src/test/resources/application.properties`

La propriété `spring.mongodb.uri` permet de configurer l'URI de connexion utilisée pendant les tests.

Les tests reposant sur l'annotation `@DataMongoTest` chargent automatiquement cette configuration Spring Boot. Il n'est
donc pas nécessaire de répéter l'URI de connexion dans chaque classe de test.

Cette séparation permet :

- D'isoler les données utilisées par les tests de celles de l'environnement principal.
- D'éviter les modifications accidentelles de la base `eca`.
- De centraliser la configuration de connexion des tests.
- De faciliter l'exécution des tests de persistance et d'intégration.

**Relation avec Liquibase**

Le comportement de Liquibase dans l'environnement de test est contrôlé par la propriété `eca.liquibase.enabled`.

La valeur effective de cette propriété doit être vérifiée dans la configuration actuelle avant de considérer
l'initialisation automatique de `eca_test` comme activée ou désactivée.

Les tests nécessitant des collections ou des données particulières doivent disposer d'un environnement de test
explicitement préparé. La séparation des bases ne garantit pas, à elle seule, la présence des collections et des données
nécessaires.

## 9. Tests

### 9.1 Objectif et outils de test

Les tests actuels vérifient le fonctionnement des modèles, des règles de validation et de la persistance MongoDB à
travers Spring Data.

Ils permettent de contrôler que les documents sont correctement construits, que les règles de validation sont respectées
et que les opérations de persistance fonctionnent comme prévu.

Les principaux outils utilisés sont :

| Outil            | Utilisation                                                       |
|------------------|-------------------------------------------------------------------|
| JUnit 5          | Exécution des tests automatisés                                   |
| Spring Boot Test | Configuration et chargement de l'environnement de test            |
| `@DataMongoTest` | Chargement d'un contexte de test dédié à MongoDB                  |
| AssertJ          | Vérification des résultats à l'aide d'assertions                  |
| Mockito          | Isolation des dépendances dans les tests unitaires et applicatifs |

Les tests de persistance utilisent la base MongoDB dédiée `eca_test`, afin de séparer les données de test de celles de
l'environnement principal.

La couverture actuelle comprend des tests unitaires des modèles et de leur validation, ainsi que des tests de
persistance des repositories.

### 9.2 Tests de persistance des repositories

Les tests de repositories vérifient les opérations fondamentales de persistance des documents dans MongoDB à travers
Spring Data.

Les classes de test couvrent les dix-sept modèles indépendants :

| Classe de test                       | Vérification                                                                                   |
|--------------------------------------|------------------------------------------------------------------------------------------------|
| `CollaboratorRepositoryTest`         | Persistance et récupération d'un collaborateur avec ses déclarations de compétences embarquées |
| `AgencyRepositoryTest`               | Persistance et récupération d'une agence                                                       |
| `CompetenceRepositoryTest`           | Persistance et récupération d'une compétence                                                   |
| `ProfessionRepositoryTest`           | Persistance et récupération d'un métier                                                        |
| `ProfessionCategoryRepositoryTest`   | Persistance et récupération d'une catégorie de métier                                          |
| `ExperienceLevelRepositoryTest`      | Persistance et récupération d'un niveau d'expérience                                           |
| `DomainRepositoryTest`               | Persistance et récupération d'un domaine                                                       |
| `CompetenceDomainRepositoryTest`     | Persistance et récupération d'une association entre une compétence et un domaine               |
| `ProfessionCompetenceRepositoryTest` | Persistance et récupération d'une association entre un métier et une compétence                |
| `ClientRepositoryTest`               | Persistance et récupération d'un client                                                        |
| `MissionRepositoryTest`              | Persistance et récupération d'une mission                                                      |
| `CollaboratorMissionRepositoryTest`  | Persistance et récupération d'une affectation entre un collaborateur et une mission            |
| `RoleRepositoryTest`                 | Persistance et récupération d'un rôle                                                          |
| `ToolRepositoryTest`                 | Persistance et récupération d'un outil                                                         |
| `ToolCategoryRepositoryTest`         | Persistance et récupération d'une catégorie d'outils                                           |
| `AuditLogRepositoryTest`             | Persistance et récupération d'une entrée d'historique                                          |
| `NotificationRepositoryTest`         | Persistance et récupération d'une notification                                                 |

Ces tests vérifient notamment l'enregistrement des documents, leur récupération depuis MongoDB, la génération des
identifiants et la cohérence des informations persistées.

`CompetenceDeclaration` ne possède pas de repository indépendant, car il s'agit d'un objet embarqué dans `Collaborator`.
Sa persistance est donc vérifiée dans le cadre des tests de `CollaboratorRepository`.

### 9.3 Tests unitaires de CompetenceDeclaration

Six tests unitaires vérifient la méthode `validate()` de `CompetenceDeclaration`.

Les scénarios couverts sont :

| Scénario                                            | Résultat attendu |
|-----------------------------------------------------|------------------|
| Déclaration avec un `competenceId` valide           | Acceptée         |
| Déclaration avec un `customName` valide             | Acceptée         |
| Absence des deux champs                             | Rejetée          |
| Présence simultanée des deux champs                 | Rejetée          |
| `competenceId` vide ou composé uniquement d'espaces | Rejetée          |
| `customName` vide ou composé uniquement d'espaces   | Rejetée          |

Ces tests vérifient la règle d'exclusivité entre les deux modes de déclaration :

- Une déclaration doit référencer une compétence du catalogue à travers `competenceId`, ou fournir un nom personnalisé à
  travers `customName`.
- Les deux champs ne peuvent pas être renseignés simultanément.
- Au moins l'un des deux champs doit être renseigné avec une valeur non vide.

Les limites du nombre d'expertises et de sujets sollicitables sont contrôlées séparément au niveau du modèle
`Collaborator`.

**Statut : Six tests unitaires implémentés.**

### 9.4 Tests unitaires de Collaborator

Sept tests unitaires vérifient la méthode `validate()` du modèle `Collaborator` et les limites appliquées à ses
déclarations de compétences embarquées.

Les scénarios couverts sont :

| Scénario                                                   | Résultat attendu |
|------------------------------------------------------------|------------------|
| Collaborateur sans déclaration de compétence               | Accepté          |
| Collaborateur avec une déclaration d'expertise             | Accepté          |
| Collaborateur avec trois déclarations d'expertise          | Accepté          |
| Collaborateur avec plus de trois déclarations d'expertise  | Rejeté           |
| Collaborateur avec deux déclarations sollicitables         | Accepté          |
| Collaborateur sans déclaration sollicitable                | Accepté          |
| Collaborateur avec plus de deux déclarations sollicitables | Rejeté           |

La méthode `Collaborator.validate()` parcourt les déclarations embarquées et appelle leur propre méthode `validate()`.

Elle contrôle également les limites suivantes :

- Maximum de trois déclarations avec `expertise = true`.
- Maximum de deux déclarations avec `solicitable = true`.

Ces contrôles sont réalisés au niveau du collaborateur, car ils portent sur le nombre total de déclarations
correspondant à chaque critère, et non sur une déclaration prise individuellement.

**Statut : Sept tests unitaires implémentés.**

### 9.5 Tests d'intégration

Les tests d'intégration vérifient le fonctionnement des associations logiques entre plusieurs documents MongoDB, en
utilisant les repositories concernés.

Ils permettent notamment de vérifier que les identifiants stockés dans les documents peuvent être exploités pour
retrouver les entités associées.

**CollaboratorCompetenceIntegrationTest**

Ce test vérifie la relation logique entre un document `Collaborator` et une entrée du catalogue `Competence`.

Le scénario couvre les opérations suivantes :

1. Création et enregistrement d'une compétence.
2. Récupération de son identifiant MongoDB.
3. Création d'une `CompetenceDeclaration` utilisant cet identifiant.
4. Enregistrement et récupération du collaborateur.
5. Récupération explicite de la compétence référencée à travers `CompetenceRepository`.
6. Vérification de la cohérence entre la déclaration et le document du catalogue.

**ProfessionCategoryIntegrationTest**

Ce test vérifie la relation logique entre `Profession` et `ProfessionCategory`.

Le scénario couvre :

1. Création et enregistrement d'une catégorie.
2. Récupération de son identifiant MongoDB.
3. Création d'un métier utilisant cet identifiant dans `categoryId`.
4. Enregistrement et récupération du métier.
5. Récupération explicite de la catégorie référencée.
6. Vérification de la cohérence entre les deux documents.

**Autres associations couvertes**

| Classe de test                        | Association vérifiée                                           |
|---------------------------------------|----------------------------------------------------------------|
| `CompetenceDomainIntegrationTest`     | `Competence` et `Domain`, à travers `CompetenceDomain`         |
| `ProfessionCompetenceIntegrationTest` | `Profession` et `Competence`, à travers `ProfessionCompetence` |
| `CollaboratorMissionIntegrationTest`  | `Collaborator` et `Mission`, à travers `CollaboratorMission`   |
| `ToolCategoryIntegrationTest`         | `Tool` et `ToolCategory`, à travers le champ `categoryId`      |

Ces tests vérifient que les associations logiques peuvent être exploitées à travers les identifiants et les repositories
concernés.

Ils ne constituent pas une vérification automatique de l'existence de toutes les références lors de chaque opération de
persistance.

### 9.6 Nettoyage et isolation des tests

Les classes de test concernées utilisent l'annotation `@AfterEach` afin de nettoyer les données après chaque test.

Pour les tests impliquant plusieurs repositories, le nettoyage est effectué explicitement pour chacun des repositories
concernés.

Cette organisation permet :

- D'éviter que les données créées par un test influencent les suivants.
- De limiter les interactions entre les tests.
- De conserver un environnement de test reproductible.

Le nettoyage est réalisé au niveau des repositories utilisés par chaque classe de test.

### 9.7 Résultats et limites de la couverture de tests

La couverture de tests actuelle comprend plusieurs niveaux de vérification :

- Les tests unitaires des règles de validation de `CompetenceDeclaration` et de `Collaborator`.
- Les tests de persistance des dix-sept modèles indépendants associés à leurs repositories.
- Les tests d'intégration des principales associations logiques entre documents MongoDB.
- Les tests de l'API de référence des professions, couvrant le repository, le service et le contrôleur.

Ces tests permettent de vérifier le fonctionnement des modèles, les règles de validation actuellement implémentées, les
opérations de persistance et l'exploitation des références logiques entre collections.

**Limites de la couverture actuelle**

Les tests ne valident pas encore les aspects suivants :

- La vérification systématique de l'existence des documents référencés dans tous les modèles.
- La suppression ou la désactivation d'une entrée de catalogue encore référencée.
- Les règles métier définitives concernant les déclarations personnalisées.
- Les recherches multicritères et leur comportement.
- Les performances sur des volumes représentatifs de données.
- L'efficacité des index sur les requêtes réelles de l'application.

Les index MongoDB sont définis dans les changelogs Liquibase, mais leur efficacité et leur impact sur les performances
restent à évaluer.

Ces éléments devront être approfondis à mesure que les services applicatifs et les fonctionnalités de recherche seront
développés.

## 10. Principes de développement retenus

Les principes suivants guident l'implémentation actuelle du backend ECA :

1. Séparer les modèles de données des interfaces d'accès aux données.
2. Utiliser les mécanismes standards de Spring Data MongoDB lorsque cela est pertinent.
3. Privilégier une structure documentaire adaptée aux usages attendus.
4. Éviter de créer des collections indépendantes sans besoin fonctionnel identifié.
5. Séparer les données de test des données de l'application.
6. Tester les opérations de persistance avant d'étendre le modèle.
7. Documenter les choix techniques et les hypothèses non validées.
8. Ne pas implémenter les intégrations externes qui ne font pas partie du périmètre actuel.

## 11. Points à valider

Les points suivants nécessitent encore une validation fonctionnelle ou technique auprès des interlocuteurs Alltech.

Ils ne constituent pas nécessairement des blocages pour l'implémentation actuelle. Ils doivent cependant être clarifiés
avant que les choix concernés deviennent des règles définitives du projet ECA.

| Sujet                         | Question                                                                                                                         |
|-------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| Périmètre du socle de données | Le modèle doit-il constituer une fondation réutilisable pour d'autres projets Alltech ?                                          |
| Référence agence              | Quel identifiant métier doit être utilisé pour représenter une agence gérée extérieurement ?                                     |
| Affectation agence            | Quelle est la source officielle des affectations d'agence et qui en assure la gestion ?                                          |
| Expérience                    | Quelle nomenclature officielle doit être utilisée pour les niveaux d'expérience ?                                                |
| Compétences personnalisées    | Quelles règles doivent encadrer la gestion et la validation des compétences personnalisées ?                                     |
| Catalogue compétences         | Comment gérer la désactivation ou la suppression d'une compétence encore référencée ?                                            |
| Domaines                      | Une compétence doit-elle obligatoirement appartenir à au moins un domaine ?                                                      |
| Métiers et compétences        | Les compétences associées à un métier sont-elles uniquement des suggestions ou doivent-elles suivre des règles complémentaires ? |
| Rôles                         | Quels rôles doivent être disponibles et quelles sont les règles d'attribution de ces rôles aux collaborateurs ?                  |
| Outils                        | Quelle est la liste officielle des outils internes à référencer et qui assure la gestion de ce catalogue ?                       |
| Catégories d'outils           | Quelle nomenclature de catégories doit être utilisée pour organiser les outils ?                                                 |
| Audit                         | Quelles modifications doivent être journalisées, et quelles informations doivent être conservées dans l'historique ?             |
| Notifications                 | Quels événements doivent déclencher une notification et selon quelles modalités doit-elle être présentée au collaborateur ?      |
| Clients et missions           | Quelles règles métier doivent encadrer la création, la visibilité et la gestion des clients et des missions ?                    |
| Affectations aux missions     | Quelles règles doivent encadrer les périodes d'affectation, notamment les chevauchements et les dates de fin ?                   |
| Initialisation                | Quel mécanisme d'initialisation et de migration doit être retenu pour MongoDB ?                                                  |
| Indexation                    | Quels critères de recherche nécessitent des index spécifiques ?                                                                  |
| Références                    | Quelles règles de cohérence doivent être appliquées aux références entre documents ?                                             |

Les décisions concernant l'identité des collaborateurs, les affectations, les catalogues, les rôles, les missions et les
règles métier devront être confirmées avec les interlocuteurs Alltech concernés.

Les choix d'implémentation technique pourront être approfondis progressivement au cours du développement.

## 12. Conclusion

La première phase du backend ECA a permis de mettre en place un socle technique initial reposant sur Java, Spring Boot,
Spring Data MongoDB et MongoDB.

Dix-sept modèles indépendants sont actuellement implémentés :

`Collaborator`, `Agency`, `Competence`, `Profession`, `ProfessionCategory`, `ExperienceLevel`, `Domain`,
`CompetenceDomain`, `ProfessionCompetence`, `Client`, `Mission`, `CollaboratorMission`, `Role`, `Tool`, `ToolCategory`,
`AuditLog` et `Notification`.

Les déclarations de compétences sont intégrées directement dans les documents `Collaborator` à travers le modèle
embarqué `CompetenceDeclaration`.

La couche d'accès aux données repose sur dix-sept repositories Spring Data MongoDB. Les associations entre les modèles
sont représentées par des identifiants et, lorsque cela est nécessaire, par des modèles d'association dédiés.

Les règles de validation actuellement implémentées sont centralisées dans les méthodes `validate()` des modèles
concernés. Elles sont couvertes par des tests unitaires, complétés par des tests de persistance et d'intégration.

L'initialisation de la base de données est gérée par Liquibase MongoDB. Les changelogs définissent la création des
collections, le chargement des données de référence et la création des index.

Une première API de référence permet également de consulter les professions actives à travers un endpoint REST documenté
et testé.

Ce socle constitue une base pour poursuivre le développement des services applicatifs, enrichir les API et préciser les
règles métier avec les interlocuteurs Alltech.

**Ce document décrit l'état de l'implémentation à la date de sa mise à jour. Il ne constitue pas une spécification
fonctionnelle définitive ni une validation officielle de l'ensemble des choix métier par Alltech.**