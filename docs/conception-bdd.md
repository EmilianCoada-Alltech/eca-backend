# Conception de la base de données — ECA

**Projet :** ECA — Espace Collaborateur Alltech  
**Document :** Conception de la base de données  
**Version :** 1.0 — Document de travail  
**Date :** 25 novembre 2026  
**Statut :** Modélisation initiale — en cours de validation

---

## 1. Objectif et périmètre de la conception

### 1.1 Objectif

Ce document présente la conception de la base de données du projet ECA (Espace Collaborateur Alltech).

Il décrit la structure des données nécessaires à la gestion des profils collaborateurs et des informations associées,
notamment les agences, les métiers, les compétences, les missions, les rôles et les outils internes.

Il constitue une référence pour comprendre :

- Les modèles de données prévus pour ECA.
- Les attributs associés à chaque modèle.
- L'organisation des collections MongoDB.
- Les relations logiques entre les différents documents.
- Les choix de structuration et les points restant à confirmer.

### 1.2 Périmètre

La conception couvre les données identifiées pour le socle initial de la base ECA, notamment :

- Les profils des collaborateurs et leurs informations professionnelles.
- Les agences et les référentiels associés aux collaborateurs.
- Les compétences, les domaines et les associations entre compétences et métiers.
- Les clients, les missions et les affectations des collaborateurs.
- Les rôles attribués aux collaborateurs.
- Le catalogue des outils internes et leurs catégories.
- Les informations de traçabilité et les notifications.

La conception décrit la structure des données et leurs associations. Elle ne spécifie pas le fonctionnement détaillé des
interfaces, des services applicatifs, de l'authentification ou des intégrations externes.

### 1.3 Statut de la modélisation

La structure présentée dans ce document correspond au modèle initial actuellement implémenté dans MongoDB et dans le
projet ECA Backend.

La présence d'un modèle ou d'un attribut dans cette conception ne signifie toutefois pas que toutes les règles métier
associées ont été validées par Alltech.

Les éléments dont la définition fonctionnelle reste incertaine sont identifiés comme des points à confirmer. La
modélisation pourra évoluer en fonction des retours d'Alltech et des besoins fonctionnels précisés.

## 2. Conventions de modélisation

### 2.1 Convention linguistique et nomenclature

Les noms techniques des modèles, des attributs et des collections MongoDB utilisent l'anglais.

Cette convention s'applique notamment :

- Aux noms des modèles (`Collaborator`, `Agency`, `Competence`, etc.).
- Aux noms des attributs (`firstName`, `agencyId`, `hireDate`, etc.).
- Aux noms des collections MongoDB (`collaborators`, `agencies`, `competences`, etc.).
- Aux identifiants et références entre documents.

Les descriptions fonctionnelles et la documentation destinée aux équipes Alltech restent rédigées en français.

### 2.2 Identifiants des documents

Chaque modèle indépendant possède un attribut `id` de type `String`, utilisé comme identifiant du document MongoDB.

Cet attribut correspond au champ `_id` dans MongoDB.

Lorsqu'aucun identifiant n'est fourni lors de la création d'un document, MongoDB génère automatiquement un identifiant.

### 2.3 Références entre documents

Les associations entre les documents indépendants sont représentées par des identifiants de type `String`.

Un attribut tel que `agencyId`, `professionId` ou `clientId` contient ainsi l'identifiant du document auquel il fait
référence.

Ces références sont des associations logiques : MongoDB ne les traite pas comme des clés étrangères et ne garantit pas
automatiquement l'existence du document référencé.

La résolution des références nécessite une interrogation explicite des collections concernées.

### 2.4 Documents embarqués

Certains objets sont intégrés directement dans un document parent plutôt que stockés dans une collection indépendante.

C'est notamment le cas de `CompetenceDeclaration`, qui est intégré au document `Collaborator` sous la forme d'un
tableau.

Un objet embarqué ne possède pas de collection indépendante ni d'identifiant MongoDB propre dans le modèle actuel. Son
appartenance est déterminée par le document qui le contient.

### 2.5 Collections indépendantes et collections d'association

Les modèles indépendants disposent chacun de leur propre collection MongoDB.

Les relations nécessitant une association entre plusieurs documents peuvent être représentées par des collections
dédiées. C'est notamment le cas de `CompetenceDomain`, `ProfessionCompetence` et `CollaboratorMission`.

Cette organisation permet de conserver les documents concernés indépendamment tout en représentant leurs associations à
l'aide de leurs identifiants respectifs.

## 3. Vue d'ensemble des collections

La base de données `eca` repose sur dix-sept collections MongoDB indépendantes. Elles sont organisées par domaine
fonctionnel afin de distinguer les profils collaborateurs, les référentiels, les associations et les fonctionnalités
transversales.

### 3.1 Profils collaborateurs et organisation

| Collection              | Modèle               | Rôle dans la base                                               |
|-------------------------|----------------------|-----------------------------------------------------------------|
| `collaborators`         | `Collaborator`       | Profils des collaborateurs et informations professionnelles     |
| `agencies`              | `Agency`             | Informations relatives aux agences Alltech                      |
| `professions`           | `Profession`         | Référentiel des métiers                                         |
| `profession_categories` | `ProfessionCategory` | Catégories permettant de regrouper les métiers                  |
| `experience_levels`     | `ExperienceLevel`    | Référentiel des niveaux d'expérience                            |
| `roles`                 | `Role`               | Référentiel des rôles pouvant être attribués aux collaborateurs |

### 3.2 Compétences et domaines d'expertise

| Collection               | Modèle                 | Rôle dans la base                                                 |
|--------------------------|------------------------|-------------------------------------------------------------------|
| `competences`            | `Competence`           | Catalogue central des compétences                                 |
| `domains`                | `Domain`               | Référentiel des domaines de compétences                           |
| `competence_domains`     | `CompetenceDomain`     | Associations entre les compétences et les domaines                |
| `profession_competences` | `ProfessionCompetence` | Associations entre les métiers et les compétences correspondantes |

Les déclarations individuelles de compétences ne disposent pas d'une collection indépendante. Elles sont intégrées aux
documents `Collaborator` sous la forme d'objets `CompetenceDeclaration`.

### 3.3 Clients et missions

| Collection              | Modèle                | Rôle dans la base                            |
|-------------------------|-----------------------|----------------------------------------------|
| `clients`               | `Client`              | Informations relatives aux clients           |
| `missions`              | `Mission`             | Missions associées aux clients               |
| `collaborator_missions` | `CollaboratorMission` | Affectations des collaborateurs aux missions |

### 3.4 Catalogue des outils internes

| Collection        | Modèle         | Rôle dans la base                             |
|-------------------|----------------|-----------------------------------------------|
| `tools`           | `Tool`         | Catalogue des outils et applications internes |
| `tool_categories` | `ToolCategory` | Catégories permettant de regrouper les outils |

### 3.5 Fonctionnalités transversales

| Collection      | Modèle         | Rôle dans la base                                           |
|-----------------|----------------|-------------------------------------------------------------|
| `audit_logs`    | `AuditLog`     | Historique des modifications et informations de traçabilité |
| `notifications` | `Notification` | Notifications destinées aux collaborateurs                  |

### 3.6 Synthèse

La structure comprend :

- **17 collections indépendantes**, chacune associée à un modèle Java.
- **3 collections d'association** : `competence_domains`, `profession_competences` et `collaborator_missions`.
- **1 type d'objet embarqué** : `CompetenceDeclaration`, intégré dans `Collaborator` et ne disposant pas de collection
  propre.

Les collections d'association restent des collections indépendantes dans MongoDB. Elles permettent de représenter des
relations entre plusieurs documents sans intégrer leurs données respectives dans un même document.

## 4. Description des modèles

### 4.1 Collaborator

**Collection :** `collaborators`  
**Type :** Document indépendant

Le modèle `Collaborator` représente un collaborateur Alltech.

Il regroupe les informations d'identification, les informations professionnelles, les rôles attribués et les
déclarations de compétences associées à son profil.

| Attribut              | Type                        | Description                                          |
|-----------------------|-----------------------------|------------------------------------------------------|
| `id`                  | String                      | Identifiant du document MongoDB                      |
| `adIdentifier`        | String                      | Identifiant Active Directory du collaborateur        |
| `firstName`           | String                      | Prénom                                               |
| `lastName`            | String                      | Nom                                                  |
| `email`               | String                      | Adresse électronique                                 |
| `agencyId`            | String                      | Référence obligatoire vers une agence                |
| `professionId`        | String                      | Référence facultative vers un métier                 |
| `experienceLevelId`   | String                      | Référence facultative vers un niveau d'expérience    |
| `roleIds`             | List<String>                | Identifiants des rôles attribués au collaborateur    |
| `hireDate`            | LocalDate                   | Date d'arrivée chez Alltech                          |
| `presentation`        | String                      | Présentation professionnelle                         |
| `photoUrl`            | String                      | Adresse de la photographie du collaborateur          |
| `profileStatus`       | String                      | Statut du profil                                     |
| `onboardingCompleted` | boolean                     | Indique si l'onboarding est terminé                  |
| `searchable`          | boolean                     | Indique si le profil est consultable dans l'annuaire |
| `competences`         | List<CompetenceDeclaration> | Déclarations de compétences intégrées au profil      |

**Organisation des données**

Le document `Collaborator` centralise les informations propres à un collaborateur et contient plusieurs références vers
les collections de données de référence :

- `agencyId` référence un document de la collection `agencies`. Cette référence est obligatoire.
- `professionId` référence un document de la collection `professions`. Cette référence peut être absente lors de
  l'onboarding et être renseignée ultérieurement.
- `experienceLevelId` référence un document de la collection `experience_levels`. Cette référence peut être absente lors
  de l'onboarding et être renseignée ultérieurement par un administrateur ou les ressources humaines.
- `roleIds` contient les identifiants des documents de la collection `roles`.

Lorsqu'une référence facultative est renseignée, elle doit correspondre à un document existant dans la collection
concernée.

Les déclarations de compétences sont intégrées directement au document sous la forme d'un tableau `competences`. Chaque
élément est un objet `CompetenceDeclaration`.

**Visibilité du profil**

L'attribut `searchable` détermine si le profil peut apparaître dans les résultats de recherche de l'annuaire
collaborateur.

Les profils pour lesquels `searchable` vaut `false` doivent être exclus des résultats de recherche standards. Cette
règle relève de la logique applicative et doit être appliquée lors de l'exécution des requêtes de recherche.

Cette organisation permet de regrouper les informations du profil et ses déclarations de compétences dans un même
document MongoDB.

### 4.2 Agency

**Collection :** `agencies`  
**Type :** Document indépendant

Le modèle `Agency` représente une agence Alltech et regroupe ses principales informations d'identification et de
localisation.

| Attribut        | Type   | Description                     |
|-----------------|--------|---------------------------------|
| `id`            | String | Identifiant du document MongoDB |
| `name`          | String | Nom de l'agence                 |
| `streetAddress` | String | Adresse postale                 |
| `postalCode`    | String | Code postal                     |
| `city`          | String | Ville                           |
| `country`       | String | Pays                            |
| `phoneNumber`   | String | Numéro de téléphone             |

**Identifiant métier**

Un attribut `code`, correspondant notamment à d'anciens codes départementaux, avait initialement été envisagé pour
identifier les agences.

Cet attribut a été écarté provisoirement du modèle actuel, car son utilité fonctionnelle n'est pas établie.

La nécessité d'un identifiant métier spécifique pour associer les informations provenant de l'Active Directory aux
documents de la collection `agencies` reste à confirmer avec Alltech.

### 4.3 Competence

**Collection :** `competences`  
**Type :** Document indépendant

Le modèle `Competence` représente une entrée du catalogue central des compétences.

Ce catalogue permet de centraliser les compétences proposées aux collaborateurs et sert de référentiel commun pour les
déclarations individuelles et les associations avec les métiers et les domaines.

| Attribut      | Type    | Description                         |
|---------------|---------|-------------------------------------|
| `id`          | String  | Identifiant du document MongoDB     |
| `name`        | String  | Nom de la compétence                |
| `description` | String  | Description de la compétence        |
| `active`      | boolean | Indique si la compétence est active |

**Utilisation dans le modèle**

Le catalogue central peut être référencé depuis plusieurs parties du modèle de données :

- `CompetenceDeclaration.competenceId` permet à un collaborateur de déclarer une compétence existante.
- `CompetenceDomain` permet d'associer une compétence à un ou plusieurs domaines.
- `ProfessionCompetence` permet d'associer une compétence à un métier.

Les déclarations personnalisées qui ne correspondent pas à une entrée du catalogue sont représentées directement dans
`CompetenceDeclaration`, au moyen de l'attribut `customName`.

### 4.4 CompetenceDeclaration

**Type :** Objet embarqué dans `Collaborator`  
**Collection indépendante :** Aucune

Le modèle `CompetenceDeclaration` représente la manière dont un collaborateur déclare une compétence ou un sujet sur
lequel il peut être sollicité.

| Attribut       | Type    | Description                                                                   |
|----------------|---------|-------------------------------------------------------------------------------|
| `competenceId` | String  | Référence facultative vers une compétence du catalogue central                |
| `customName`   | String  | Nom personnalisé lorsque le sujet ne correspond pas à une entrée du catalogue |
| `expertise`    | boolean | Indique si le sujet est déclaré comme une expertise                           |
| `solicitable`  | boolean | Indique si le collaborateur accepte d'être sollicité sur ce sujet             |
| `source`       | String  | Origine de la déclaration                                                     |

**Association avec le catalogue central**

Une déclaration peut prendre deux formes :

- Être associée à une compétence existante au moyen de `competenceId`.
- Représenter une compétence ou un sujet personnalisé au moyen de `customName`.

Une règle de validation impose de renseigner exactement l'un de ces deux attributs. Ils ne peuvent être ni tous les deux
renseignés, ni tous les deux absents.

**Règles de déclaration**

Chaque collaborateur est soumis aux limites suivantes :

- Un maximum de trois déclarations peuvent être marquées comme expertises (`expertise = true`).
- Un maximum de deux déclarations peuvent être marquées comme sollicitables (`solicitable = true`).

Aucune limite globale n'est définie sur le nombre total de déclarations de compétences.

**Organisation dans MongoDB**

Les déclarations sont stockées dans le tableau `competences` du document `Collaborator`.

Elles ne possèdent pas d'identifiant ni de collection indépendante. Leur appartenance à un collaborateur est déterminée
par le document qui les contient.

Cette organisation permet de récupérer les déclarations avec le profil du collaborateur, sans nécessiter une collection
supplémentaire.

### 4.5 Profession

**Collection :** `professions`  
**Type :** Document indépendant

Le modèle `Profession` représente une entrée du catalogue des métiers d'Alltech.

| Attribut      | Type    | Description                            |
|---------------|---------|----------------------------------------|
| `id`          | String  | Identifiant du document MongoDB        |
| `name`        | String  | Nom du métier                          |
| `description` | String  | Description du métier                  |
| `categoryId`  | String  | Référence vers une catégorie de métier |
| `active`      | boolean | Indique si le métier est actif         |

**Association avec les catégories de métiers**

Le champ `categoryId` représente une référence logique vers un document de la collection `profession_categories`.

Cette organisation permet de regrouper plusieurs métiers au sein d'une même catégorie, tout en conservant les documents
indépendants.

### 4.6 ProfessionCategory

**Collection :** `profession_categories`  
**Type :** Document indépendant

Le modèle `ProfessionCategory` représente une catégorie permettant de regrouper plusieurs métiers au sein du catalogue
des professions.

| Attribut      | Type   | Description                     |
|---------------|--------|---------------------------------|
| `id`          | String | Identifiant du document MongoDB |
| `name`        | String | Nom de la catégorie             |
| `description` | String | Description de la catégorie     |

**Association avec les métiers**

La collection `profession_categories` constitue un référentiel de catégories utilisé par les documents `Profession`.

Le champ `Profession.categoryId` contient l'identifiant de la catégorie associée au métier. Plusieurs métiers peuvent
ainsi appartenir à une même catégorie.

### 4.7 ExperienceLevel

**Collection :** `experience_levels`  
**Type :** Document indépendant

Le modèle `ExperienceLevel` représente un niveau d'expérience pouvant être associé au profil d'un collaborateur.

| Attribut | Type   | Description                     |
|----------|--------|---------------------------------|
| `id`     | String | Identifiant du document MongoDB |
| `name`   | String | Nom du niveau d'expérience      |

**Association avec les collaborateurs**

Le champ `Collaborator.experienceLevelId` contient l'identifiant du niveau d'expérience associé au collaborateur.

Cette référence permet de centraliser les niveaux d'expérience dans un catalogue indépendant et de les réutiliser pour
plusieurs profils.

### 4.8 Domain

**Collection:** `domains`  
**Type:** Document indépendant

Un domaine regroupe plusieurs compétences liées à un même sujet, secteur ou champ d'activité. Il permet de structurer le
référentiel de compétences et de faciliter leur consultation par domaine.

| Attribut      | Type    | Description                                                        |
|---------------|---------|--------------------------------------------------------------------|
| `id`          | String  | Identifiant unique du domaine.                                     |
| `name`        | String  | Nom du domaine.                                                    |
| `description` | String  | Description du domaine.                                            |
| `active`      | boolean | Indique si le domaine est actif et disponible dans le référentiel. |

Un domaine peut être associé à plusieurs compétences au moyen de la collection `competence_domains`.

### 4.9 CompetenceDomain

**Collection:** `competence_domains`  
**Type:** Document d'association

Cette collection établit les associations entre les compétences et les domaines. Elle permet de représenter une relation
plusieurs-à-plusieurs : une compétence peut appartenir à plusieurs domaines et un domaine peut regrouper plusieurs
compétences.

| Attribut       | Type   | Description                                                                                  |
|----------------|--------|----------------------------------------------------------------------------------------------|
| `id`           | String | Identifiant unique de l'association.                                                         |
| `competenceId` | String | Identifiant de la compétence associée. Référence un document de la collection `competences`. |
| `domainId`     | String | Identifiant du domaine associé. Référence un document de la collection `domains`.            |

Chaque document représente une association entre une compétence et un domaine.

### 4.10 ProfessionCompetence

**Collection:** `profession_competences`  
**Type:** Document d'association

Cette collection associe les professions aux compétences du référentiel. Elle permet d'identifier les compétences
généralement attendues ou pertinentes pour une profession donnée.

| Attribut       | Type   | Description                                                                                  |
|----------------|--------|----------------------------------------------------------------------------------------------|
| `id`           | String | Identifiant unique de l'association.                                                         |
| `professionId` | String | Identifiant de la profession associée. Référence un document de la collection `professions`. |
| `competenceId` | String | Identifiant de la compétence associée. Référence un document de la collection `competences`. |

Cette association représente une relation plusieurs-à-plusieurs : une profession peut être associée à plusieurs
compétences et une compétence peut être pertinente pour plusieurs professions.

**Important :** cette association définit des compétences liées à une profession. Elle ne signifie pas que chaque
collaborateur exerçant cette profession possède automatiquement ces compétences dans son profil.

### 4.11 Client

**Collection:** `clients`  
**Type:** Document indépendant

Un client représente une organisation pour laquelle Alltech peut réaliser des missions. Les informations de contact et
de localisation sont conservées afin de permettre l'identification du client associé à une mission.

| Attribut        | Type    | Description                                          |
|-----------------|---------|------------------------------------------------------|
| `id`            | String  | Identifiant unique du client.                        |
| `name`          | String  | Nom du client.                                       |
| `streetAddress` | String  | Adresse postale du client.                           |
| `postalCode`    | String  | Code postal du client.                               |
| `city`          | String  | Ville du client.                                     |
| `country`       | String  | Pays du client.                                      |
| `phoneNumber`   | String  | Numéro de téléphone du client.                       |
| `email`         | String  | Adresse e-mail du client.                            |
| `isVisible`     | boolean | Indique si le client est visible dans l'application. |

Un client peut être associé à plusieurs missions. La relation est portée par l'attribut `clientId` de la collection
`missions`.

### 4.12 Mission

**Collection:** `missions`  
**Type:** Document indépendant

Une mission représente une prestation ou une intervention réalisée pour un client. Elle contient les informations
générales permettant de l'identifier, de la décrire et de définir sa période.

| Attribut      | Type      | Description                                                                      |
|---------------|-----------|----------------------------------------------------------------------------------|
| `id`          | String    | Identifiant unique de la mission.                                                |
| `name`        | String    | Nom de la mission.                                                               |
| `clientId`    | String    | Identifiant du client associé. Référence un document de la collection `clients`. |
| `startDate`   | LocalDate | Date de début de la mission.                                                     |
| `endDate`     | LocalDate | Date de fin de la mission.                                                       |
| `description` | String    | Description de la mission.                                                       |
| `isVisible`   | boolean   | Indique si la mission est visible dans l'application.                            |

Une mission est associée à un client par l'intermédiaire de `clientId`. Les collaborateurs participant à cette mission
sont associés au moyen de la collection `collaborator_missions`.

### 4.13 CollaboratorMission

**Collection:** `collaborator_missions`  
**Type:** Document d'association

Cette collection représente la participation des collaborateurs aux missions. Elle permet de gérer une relation
plusieurs-à-plusieurs : un collaborateur peut participer à plusieurs missions et une mission peut mobiliser plusieurs
collaborateurs.

| Attribut         | Type      | Description                                                                                   |
|------------------|-----------|-----------------------------------------------------------------------------------------------|
| `id`             | String    | Identifiant unique de l'association.                                                          |
| `collaboratorId` | String    | Identifiant du collaborateur associé. Référence un document de la collection `collaborators`. |
| `missionId`      | String    | Identifiant de la mission associée. Référence un document de la collection `missions`.        |
| `startDate`      | LocalDate | Date de début de la participation du collaborateur à la mission.                              |
| `endDate`        | LocalDate | Date de fin de la participation du collaborateur à la mission.                                |

Chaque document représente la participation d'un collaborateur à une mission sur une période donnée.

**Remarque :** l'absence de `endDate` peut indiquer que la participation est toujours en cours.

### 4.14 Role

**Collection:** `roles`  
**Type:** Document indépendant

Un rôle définit une fonction ou un niveau de responsabilité attribué à un collaborateur dans l'application. Il permet
notamment de structurer les droits ou les catégories d'accès associés aux profils.

| Attribut      | Type   | Description                 |
|---------------|--------|-----------------------------|
| `id`          | String | Identifiant unique du rôle. |
| `name`        | String | Nom du rôle.                |
| `description` | String | Description du rôle.        |

Un collaborateur peut être associé à un ou plusieurs rôles au moyen de l'attribut `roleIds` de la collection
`collaborators`.

### 4.15 Tool

**Collection:** `tools`  
**Type:** Document indépendant

Un outil représente une application, une plateforme ou une ressource logicielle utilisée au sein d'Alltech. Il permet de
centraliser les informations relatives aux outils disponibles pour les collaborateurs.

| Attribut      | Type    | Description                                                                                     |
|---------------|---------|-------------------------------------------------------------------------------------------------|
| `id`          | String  | Identifiant unique de l'outil.                                                                  |
| `name`        | String  | Nom de l'outil.                                                                                 |
| `description` | String  | Description de l'outil et de son usage.                                                         |
| `url`         | String  | Adresse permettant d'accéder à l'outil.                                                         |
| `categoryId`  | String  | Identifiant de la catégorie associée. Référence un document de la collection `tool_categories`. |
| `active`      | boolean | Indique si l'outil est actif et disponible.                                                     |

Un outil est associé à une catégorie afin de faciliter le classement et la consultation des ressources logicielles.

### 4.16 ToolCategory

**Collection:** `tool_categories`  
**Type:** Document indépendant

Une catégorie permet de regrouper les outils selon leur nature ou leur usage. Elle facilite leur classement et leur
consultation dans l'application.

| Attribut      | Type   | Description                         |
|---------------|--------|-------------------------------------|
| `id`          | String | Identifiant unique de la catégorie. |
| `name`        | String | Nom de la catégorie.                |
| `description` | String | Description de la catégorie.        |

Les outils sont associés à une catégorie au moyen de l'attribut `categoryId` de la collection `tools`.

### 4.17 AuditLog

**Collection:** `audit_logs`  
**Type:** Document indépendant

Un journal d'audit représente une entrée retraçant une modification effectuée sur une entité de l'application. Il
conserve les informations permettant d'identifier l'élément modifié, le champ concerné, les anciennes et nouvelles
valeurs, ainsi que l'auteur et la date de la modification.

| Attribut     | Type          | Description                                              |
|--------------|---------------|----------------------------------------------------------|
| `id`         | String        | Identifiant unique de l'entrée du journal.               |
| `entityType` | String        | Type de l'entité concernée par la modification.          |
| `entityId`   | String        | Identifiant de l'entité modifiée.                        |
| `fieldName`  | String        | Nom du champ modifié.                                    |
| `oldValue`   | String        | Ancienne valeur du champ.                                |
| `newValue`   | String        | Nouvelle valeur du champ.                                |
| `changedBy`  | String        | Identifiant ou référence de l'auteur de la modification. |
| `changedAt`  | LocalDateTime | Date et heure de la modification.                        |

Chaque document correspond à une modification tracée.

**Remarque :** la présence de ce modèle dans la conception ne signifie pas qu'un mécanisme automatique de journalisation
est déjà implémenté.

### 4.18 Notification

**Collection:** `notifications`  
**Type:** Document indépendant

Une notification représente un message destiné à un collaborateur. Elle permet de conserver une information à lui
communiquer, éventuellement accompagnée d'un lien vers une ressource ou une fonctionnalité de l'application.

| Attribut         | Type          | Description                                                                                        |
|------------------|---------------|----------------------------------------------------------------------------------------------------|
| `id`             | String        | Identifiant unique de la notification.                                                             |
| `collaboratorId` | String        | Identifiant du collaborateur destinataire. Référence un document de la collection `collaborators`. |
| `type`           | String        | Type de notification.                                                                              |
| `message`        | String        | Contenu du message affiché au collaborateur.                                                       |
| `link`           | String        | Lien éventuel vers une ressource ou une fonctionnalité de l'application.                           |
| `read`           | boolean       | Indique si la notification a été consultée.                                                        |
| `createdAt`      | LocalDateTime | Date et heure de création de la notification.                                                      |

Chaque notification est associée à un collaborateur destinataire.

**Remarque :** la présence de ce modèle ne signifie pas qu'un mécanisme de génération ou de distribution automatique des
notifications est déjà implémenté.

## 5. Relations entre les modèles

Les relations entre les collections sont établies à l'aide d'identifiants (`String`). MongoDB ne crée pas
automatiquement de contraintes de clés étrangères : la cohérence des références doit être assurée par la logique
applicative.

### 5.1 Relations liées aux collaborateurs

| Modèle source | Attribut            | Modèle cible          | Relation                                                                                      |
|---------------|---------------------|-----------------------|-----------------------------------------------------------------------------------------------|
| Collaborator  | `agencyId`          | Agency                | Référence obligatoire. Plusieurs collaborateurs peuvent être rattachés à une même agence.     |
| Collaborator  | `professionId`      | Profession            | Référence facultative. Plusieurs collaborateurs peuvent exercer une même profession.          |
| Collaborator  | `experienceLevelId` | ExperienceLevel       | Référence facultative. Plusieurs collaborateurs peuvent partager un même niveau d'expérience. |
| Collaborator  | `roleIds`           | Role                  | Un collaborateur peut posséder plusieurs rôles.                                               |
| Collaborator  | `competences`       | CompetenceDeclaration | Les déclarations de compétences sont intégrées directement au document du collaborateur.      |

Les références vers `Agency`, `Profession` et `ExperienceLevel` sont représentées par des identifiants (`String`).

La référence vers une agence est obligatoire. Celle vers une profession peut être absente pendant l'onboarding, tandis
que celle vers un niveau d'expérience peut être renseignée ultérieurement par un administrateur ou les ressources
humaines.

Lorsqu'une référence facultative est renseignée, elle doit correspondre à un document existant dans la collection cible.

Les déclarations de compétences peuvent référencer une compétence du référentiel via `competenceId`, ou contenir une
compétence personnalisée via `customName`.

### 5.2 Relations liées aux professions et aux compétences

| Modèle source        | Attribut       | Modèle cible       | Relation                                                       |
|----------------------|----------------|--------------------|----------------------------------------------------------------|
| Profession           | `categoryId`   | ProfessionCategory | Plusieurs professions peuvent appartenir à une même catégorie. |
| CompetenceDomain     | `competenceId` | Competence         | Association entre une compétence et un domaine.                |
| CompetenceDomain     | `domainId`     | Domain             | Association entre un domaine et une compétence.                |
| ProfessionCompetence | `professionId` | Profession         | Association entre une profession et une compétence.            |
| ProfessionCompetence | `competenceId` | Competence         | Association entre une compétence et une profession.            |

Les collections `competence_domains` et `profession_competences` permettent de représenter des relations
plusieurs-à-plusieurs sans intégrer directement les listes d'identifiants dans les documents concernés.

### 5.3 Relations liées aux clients et aux missions

| Modèle source       | Attribut         | Modèle cible | Relation                                               |
|---------------------|------------------|--------------|--------------------------------------------------------|
| Mission             | `clientId`       | Client       | Un client peut être associé à plusieurs missions.      |
| CollaboratorMission | `collaboratorId` | Collaborator | Un collaborateur peut participer à plusieurs missions. |
| CollaboratorMission | `missionId`      | Mission      | Une mission peut mobiliser plusieurs collaborateurs.   |

La collection `collaborator_missions` permet de conserver les périodes de participation des collaborateurs aux missions.

### 5.4 Relations liées aux outils

| Modèle source | Attribut     | Modèle cible | Relation                                                  |
|---------------|--------------|--------------|-----------------------------------------------------------|
| Tool          | `categoryId` | ToolCategory | Plusieurs outils peuvent appartenir à une même catégorie. |

### 5.5 Relations transversales

| Modèle source | Attribut                   | Modèle cible     | Relation                                                             |
|---------------|----------------------------|------------------|----------------------------------------------------------------------|
| Notification  | `collaboratorId`           | Collaborator     | Une notification est destinée à un collaborateur.                    |
| AuditLog      | `entityType` et `entityId` | Entité concernée | Une entrée d'audit identifie l'entité modifiée de manière générique. |

La relation portée par `AuditLog` est volontairement générique : elle peut concerner différents types d'entités. Elle ne
correspond donc pas à une référence directe vers une collection unique.

### 5.6 Synthèse des associations

Les principales associations du modèle sont les suivantes :

- **Collaborator – Agency :** rattachement d'un collaborateur à une agence.
- **Collaborator – Profession :** association d'un collaborateur à une profession.
- **Collaborator – ExperienceLevel :** association d'un collaborateur à un niveau d'expérience.
- **Collaborator – Role :** association d'un collaborateur à un ou plusieurs rôles.
- **Collaborator – Competence :** déclarations de compétences intégrées au document du collaborateur.
- **Profession – ProfessionCategory :** classement des professions par catégorie.
- **Competence – Domain :** association plusieurs-à-plusieurs via `competence_domains`.
- **Profession – Competence :** association plusieurs-à-plusieurs via `profession_competences`.
- **Client – Mission :** rattachement des missions à leurs clients.
- **Collaborator – Mission :** association plusieurs-à-plusieurs via `collaborator_missions`.
- **Tool – ToolCategory :** classement des outils par catégorie.
- **Collaborator – Notification :** rattachement des notifications à leurs destinataires.
- **AuditLog – Entité :** traçabilité générique des modifications.

## 6. Choix de modélisation spécifiques

Cette section présente les particularités structurelles du modèle de données et les règles qui influencent son
organisation.

### 6.1 Déclarations de compétences intégrées au collaborateur

Le modèle `CompetenceDeclaration` est intégré directement dans le document `Collaborator`, sous la forme d'une liste
portée par l'attribut `competences`.

Il ne possède donc pas de collection MongoDB indépendante.

Chaque déclaration peut prendre l'une des deux formes suivantes :

- Une compétence issue du référentiel, identifiée par `competenceId`.
- Une compétence personnalisée, décrite par `customName`.

Une déclaration doit renseigner exactement l'un de ces deux attributs. Ils ne doivent être ni simultanément renseignés,
ni simultanément absents.

Les attributs `expertise` et `solicitable` permettent de qualifier chaque déclaration :

- Un collaborateur peut déclarer au maximum trois compétences comme expertises (`expertise = true`).
- Un collaborateur peut indiquer au maximum deux sujets sur lesquels il accepte d'être sollicité (`solicitable = true`).

Aucune limite globale n'est définie sur le nombre de déclarations de compétences.

L'attribut `source` permet de conserver l'origine de la déclaration.

### 6.2 Références entre collections

Les relations entre les documents reposent sur des identifiants de type `String`.

Ces identifiants permettent de retrouver les documents associés, mais ne constituent pas des clés étrangères au sens
relationnel du terme.

La cohérence des références doit être contrôlée par la logique applicative, notamment pour vérifier qu'un identifiant
utilisé correspond bien à un document existant dans la collection cible.

### 6.3 Identifiants

Chaque modèle persistant possède un attribut `id` de type `String`, utilisé comme identifiant du document MongoDB.

Les attributs de référence utilisent également le type `String`, afin de conserver une convention homogène dans
l'ensemble du modèle.

### 6.4 Identifiant de l'agence

Le modèle `Agency` ne contient pas actuellement d'attribut `code`.

Cet attribut a été écarté provisoirement dans l'attente d'une confirmation de son utilité et de la disponibilité d'un
référentiel d'agences faisant autorité chez Alltech.

Il pourra être ajouté ultérieurement si le besoin est confirmé.

### 6.5 Modèles d'association

Trois collections sont dédiées à la représentation des relations plusieurs-à-plusieurs :

- `competence_domains` : association entre les compétences et les domaines.
- `profession_competences` : association entre les professions et les compétences.
- `collaborator_missions` : association entre les collaborateurs et les missions.

Ces associations sont représentées par des documents indépendants contenant leurs propres identifiants et les références
vers les documents concernés.

### 6.6 Validation des déclarations intégrées

Le modèle `CompetenceDeclaration` contient une méthode de validation qui vérifie la cohérence de chaque déclaration de
compétence.

Cette validation impose de renseigner exactement l'un des deux attributs suivants :

- `competenceId`, pour une compétence issue du référentiel.
- `customName`, pour une compétence personnalisée.

Le modèle `Collaborator` contrôle également les déclarations qu'il contient et applique les limites suivantes :

- Trois déclarations au maximum peuvent être marquées comme expertises.
- Deux déclarations au maximum peuvent être marquées comme sollicitables.

Ces règles sont implémentées dans les modèles et couvertes par des tests unitaires.

**Limites actuelles de l'implémentation**

Les méthodes de validation ne sont pas exécutées automatiquement par Spring Data MongoDB lors de la persistance d'un
document. Elles devront être appelées explicitement par la couche service lors de la création ou de la mise à jour d'un
collaborateur.

De même, la vérification de l'existence des documents référencés dans les autres collections devra être assurée par
cette couche applicative.

La validation des modèles est donc implémentée et testée, mais son intégration dans le processus complet de persistance
reste à réaliser.

## 7. Points à confirmer avec Alltech

Plusieurs éléments du modèle de données restent à valider avec les interlocuteurs métier et techniques d'Alltech. Les
choix présentés dans cette conception constituent une base de travail et ne doivent pas être considérés comme des règles
métier définitivement confirmées.

### 7.1 Référentiels et données de référence

- **Agences :** existe-t-il un référentiel officiel des agences ? Un code agence doit-il être conservé en complément de
  son identifiant ?
- **Professions et catégories :** existe-t-il une liste officielle des professions et de leurs catégories ?
- **Niveaux d'expérience :** quelles sont les valeurs autorisées et leur signification ?
- **Compétences :** existe-t-il un référentiel centralisé des compétences à reprendre ou à importer ?
- **Domaines :** quelle nomenclature doit être utilisée pour regrouper les compétences ?
- **Rôles :** quels rôles doivent être disponibles et quelles permissions leur sont associées ?
- **Catégories d'outils :** existe-t-il une classification officielle des outils internes ?

### 7.2 Profils collaborateurs

- **Identifiant AD :** l'attribut `adIdentifier` doit-il correspondre exactement à l'identifiant de connexion Active
  Directory ? Quel attribut ou identifiant doit être utilisé comme référence ?
- **Rattachement à une agence :** quelles sont les règles de gestion du rattachement d'un collaborateur à une agence ?
  Un changement d'agence doit-il conserver un historique ?
- **Profession :** le métier peut être absent lors de l'onboarding et renseigné ultérieurement. Existe-t-il des règles
  particulières concernant le choix ou le changement de profession ?
- **Rôles :** le modèle permet d'attribuer plusieurs rôles à un collaborateur. Quels rôles doivent être disponibles et
  quelles permissions sont associées à chacun ?
- **Statut du profil :** quelles sont les valeurs autorisées pour `profileStatus` et quelles sont les règles de
  transition entre ces statuts ?
- **Visibilité :** quelles règles déterminent les valeurs de `searchable` et `onboardingCompleted` ? À quel moment un
  profil devient-il consultable dans l'annuaire ?
- **Présentation :** quelles sont les contraintes éventuelles sur le contenu et la longueur de la présentation d'un
  collaborateur ?

### 7.3 Déclarations de compétences

- **Compétences personnalisées :** dans quelles conditions un collaborateur peut-il déclarer une compétence absente du
  référentiel ?
- **Niveau d'expertise :** que représente précisément le booléen `expertise` ?
- **Sollicitabilité :** quelles sont les règles métier associées à `solicitable` ?
- **Source :** quelles valeurs sont autorisées pour `source` et que représentent-elles ?
- **Validation :** une compétence référencée doit-elle obligatoirement exister et être active dans le référentiel ?

### 7.4 Clients et missions

- **Clients :** que signifie précisément `isVisible` et quelles règles déterminent la visibilité d'un client ?
- **Missions :** quelles sont les règles de gestion des dates de début et de fin ?
- **Participation :** un collaborateur peut-il être affecté plusieurs fois à une même mission sur des périodes
  différentes ?
- **Visibilité des missions :** quelles règles déterminent la valeur de `isVisible` ?

### 7.5 Outils, notifications et audit

- **Outils :** quelles informations complémentaires sont nécessaires pour présenter les outils aux collaborateurs ?
- **Notifications :** quels événements doivent déclencher une notification et quelles sont les règles de conservation ?
- **Journalisation :** quelles entités et quelles modifications doivent être tracées dans `audit_logs` ?
- **Auteur des modifications :** quelle référence doit être conservée dans `changedBy` ?
- **Valeurs historisées :** les champs `oldValue` et `newValue` doivent-ils rester de simples chaînes de caractères ou
  permettre de conserver des valeurs structurées ?

### 7.6 Validation globale du modèle

Avant de considérer la conception comme définitive, les points suivants doivent également être examinés :

- La liste des 17 collections correspond-elle bien au périmètre fonctionnel attendu ?
- Les relations proposées correspondent-elles aux règles métier réelles ?
- Certaines données doivent-elles être intégrées directement dans un document plutôt que conservées dans une collection
  indépendante ?
- Une contrainte d'unicité est-elle nécessaire sur les adresses e-mail des collaborateurs ? L'unicité de `adIdentifier`
  est déjà assurée par un index unique dans MongoDB.
- Des règles de suppression, d'archivage ou de désactivation doivent-elles être définies pour les données de référence
  et les données métier ?

Les réponses à ces questions permettront de confirmer les choix de modélisation, d'ajuster les attributs si nécessaire
et de préparer les évolutions de la base de données.