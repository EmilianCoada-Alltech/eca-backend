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

Les trois collections actuellement implémentées sont :

| Collection      | Modèle Java  | Type                 |
|-----------------|--------------|----------------------|
| `collaborators` | Collaborator | Document indépendant |
| `agencies`      | Agency       | Document indépendant |
| `competences`   | Competence   | Document indépendant |

`CompetenceDeclaration` est un objet embarqué dans le document `Collaborator`. Il ne possède donc pas de collection
indépendante.

### 3.2 Architecture conceptuelle actuelle

```text
MongoDB — eca
│
├── collaborators
│   └── Collaborator
│       └── competences[]
│           └── CompetenceDeclaration
│
├── agencies
│   └── Agency
│
└── competences
    └── Competence
```

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

Il regroupe les informations d'identification, les informations professionnelles et les déclarations de compétences.

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
| presentation        | String                      | Présentation professionnelle courte    |
| photoUrl            | String                      | Adresse de la photographie             |
| profileStatus       | String                      | Statut du profil                       |
| onboardingCompleted | boolean                     | Indique si l'onboarding est terminé    |
| searchable          | boolean                     | Indique si le profil est consultable   |
| competences         | List<CompetenceDeclaration> | Déclarations de compétences embarquées |

**Constructeurs**

Le modèle dispose :

- D'un constructeur sans argument.
- D'un constructeur permettant d'initialiser les principales informations du collaborateur.

Le constructeur ne réalise aucune communication avec Active Directory. Il permet uniquement d'initialiser un objet Java
avec les informations attendues.

**Initialisation des compétences**

La liste `competences` est initialisée avec une `ArrayList` vide.

Cela permet d'ajouter des déclarations sans devoir vérifier préalablement si la liste est nulle.

**Remarque :** le nom `biography` pourra être remplacé par `presentation`, plus représentatif d'une courte présentation
professionnelle. Ce changement reste à effectuer dans le code et dans la documentation.

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

La résolution de cette référence nécessite une interrogation du catalogue des compétences à partir de l'identifiant
enregistré.

**Déclaration personnalisée**

Le champ `customName` permet d'envisager une déclaration qui ne correspond pas encore à une entrée du catalogue central.

La règle de validation entre `competenceId` et `customName` reste à implémenter.

---

## 5. Relations entre les modèles

### 5.1 Collaborator et Agency

Le modèle `Collaborator` possède un champ `agencyId`.

Ce champ représente une référence vers un document de la collection `agencies`.

La relation est actuellement représentée par un identifiant de type String.

La gestion de l'affectation d'agence est externe et ne doit pas être confondue avec une responsabilité de
synchronisation Active Directory du présent projet.

### 5.2 Collaborator et Competence

Un collaborateur peut déclarer plusieurs compétences.

Ces déclarations sont stockées directement dans le document `Collaborator`, sous la forme d'une liste de
`CompetenceDeclaration`.

Chaque déclaration peut contenir une référence vers une entrée du catalogue central.

### 5.3 Choix de l'embarquement

Le choix d'embarquer `CompetenceDeclaration` dans `Collaborator` repose sur plusieurs considérations :

- Une déclaration appartient à un collaborateur.
- Elle n'a pas actuellement de cycle de vie indépendant.
- Elle est généralement consultée avec les informations du collaborateur.
- Son embarquement permet de récupérer les déclarations avec le document parent sans requête supplémentaire.

Le catalogue `Competence` reste indépendant afin de centraliser les définitions des compétences.

Ce choix est une proposition technique actuelle, qui pourra être réévaluée selon les besoins de recherche, de mise à
jour et de gestion des déclarations.

---

## 6. Couche d'accès aux données

### 6.1 Principe général

L'accès aux collections MongoDB est assuré par Spring Data MongoDB.

Les interfaces de repository héritent de `MongoRepository`, qui fournit les opérations standards de persistance.

Spring Data génère automatiquement les implémentations nécessaires au démarrage de l'application.

Aucune implémentation manuelle des opérations CRUD standards n'est nécessaire à ce stade.

### 6.2 Repositories implémentés

| Repository             | Modèle géré  | Type d'identifiant |
|------------------------|--------------|--------------------|
| CollaboratorRepository | Collaborator | String             |
| AgencyRepository       | Agency       | String             |
| CompetenceRepository   | Competence   | String             |

Les trois interfaces héritent de `MongoRepository`.

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

---

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

Les tests utilisent une base de données distincte :

`eca_test`

La configuration est définie au niveau de l'annotation `@DataMongoTest` dans les classes de test.

Cela permet de séparer les données de test des données utilisées par l'application.

---

## 8. Tests d'intégration

### 8.1 Objectif

Les tests actuels vérifient le fonctionnement réel de la persistance MongoDB à travers les repositories Spring Data.

Ils utilisent :

- JUnit 5 pour l'exécution des tests.
- Spring Boot Test pour la configuration de l'environnement.
- `@DataMongoTest` pour charger le contexte de test dédié à MongoDB.
- AssertJ pour les assertions.

Les tests se connectent à MongoDB sur la base `eca_test`.

### 8.2 CollaboratorRepositoryTest

**Objectif :** vérifier la persistance d'un document Collaborator et de ses déclarations embarquées.

Le test :

1. Crée un collaborateur.
2. Ajoute une déclaration de compétence.
3. Enregistre le collaborateur.
4. Récupère le document à partir de son identifiant.
5. Vérifie les informations principales et la présence de la déclaration.
6. Supprime le document de test.

**Résultat :** test exécuté avec succès.

### 8.3 CompetenceRepositoryTest

**Objectif :** vérifier la persistance du catalogue des compétences.

Le test :

1. Crée une compétence.
2. Enregistre la compétence.
3. Récupère le document à partir de son identifiant.
4. Vérifie ses informations.
5. Supprime le document de test.

**Résultat :** test exécuté avec succès.

### 8.4 CollaboratorCompetenceIntegrationTest

**Objectif :** vérifier le fonctionnement de la relation logique entre un document Collaborator et une entrée du
catalogue Competence.

Le test :

1. Crée une compétence.
2. Enregistre cette compétence dans la collection `competences`.
3. Récupère l'identifiant généré par MongoDB.
4. Crée une `CompetenceDeclaration` contenant cet identifiant.
5. Ajoute la déclaration à la liste des compétences du collaborateur.
6. Enregistre le collaborateur.
7. Récupère le collaborateur depuis MongoDB.
8. Vérifie la présence de la déclaration et la correspondance de son identifiant.
9. Utilise l'identifiant stocké dans la déclaration pour récupérer la compétence originale.
10. Vérifie que la compétence récupérée correspond à celle initialement créée.
11. Supprime les documents de test.

**Résultat :** test exécuté avec succès.

### 8.5 Limites des tests actuels

Les tests démontrent le fonctionnement de la persistance et de la référence logique entre les documents.

Ils ne valident pas encore :

- L'existence obligatoire de la compétence référencée.
- La suppression ou la désactivation d'une compétence encore référencée.
- Les règles métier des déclarations personnalisées.
- Les recherches multicritères.
- Les performances sur des volumes représentatifs de données.
- Les règles de validation des autres modèles.
- Les index métier.

Ces éléments devront être traités dans les prochaines étapes.

---

## 9. État actuel de l'implémentation

### 9.1 Éléments implémentés

| Élément                                  | État        |
|------------------------------------------|-------------|
| Projet Spring Boot                       | Implémenté  |
| Configuration MongoDB                    | Implémentée |
| Modèle Collaborator                      | Implémenté  |
| Modèle Agency                            | Implémenté  |
| Modèle Competence                        | Implémenté  |
| Modèle CompetenceDeclaration             | Implémenté  |
| CollaboratorRepository                   | Implémenté  |
| AgencyRepository                         | Implémenté  |
| CompetenceRepository                     | Implémenté  |
| Test de persistance Collaborator         | Réussi      |
| Test de persistance Competence           | Réussi      |
| Test de relation Collaborator–Competence | Réussi      |

### 9.2 Éléments restant à développer

Les éléments suivants sont envisagés pour les prochaines étapes :

- Modèle Profession.
- Modèle ExperienceLevel.
- Modèle Domain.
- Relation entre Competence et Domain.
- Initialisation de la base de données.
- Gestion des index métier.
- Règles de validation.
- Extension des tests d'intégration.

La priorité reste la construction d'une structure de données cohérente et documentée.

---

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

Les décisions suivantes nécessitent encore une validation technique ou fonctionnelle.

| Sujet                         | Question                                                                                                         |
|-------------------------------|------------------------------------------------------------------------------------------------------------------|
| Périmètre du socle de données | Le modèle doit-il être spécifique à ECA ou constituer une fondation réutilisable pour d'autres projets Alltech ? |
| Référence agence              | Quel identifiant métier doit être utilisé pour représenter une agence gérée extérieurement ?                     |
| Métier                        | Le catalogue Profession doit-il être associé à des catégories ?                                                  |
| Compétences                   | Comment gérer les compétences personnalisées qui ne figurent pas dans le catalogue ?                             |
| Domaines                      | Une compétence doit-elle obligatoirement appartenir à au moins un domaine ?                                      |
| Expérience                    | Quelle nomenclature doit être utilisée pour les niveaux d'expérience ?                                           |
| Suppression                   | Comment gérer les références lorsqu'un élément de catalogue est désactivé ou supprimé ?                          |
| Initialisation                | Quel mécanisme d'initialisation et de migration doit être retenu pour MongoDB ?                                  |
| Indexation                    | Quels critères de recherche nécessitent des index spécifiques ?                                                  |

---

## 12. Conclusion

La première étape du backend ECA a permis de mettre en place une structure initiale de données persistantes avec MongoDB
et Spring Data.

Les modèles principaux du premier périmètre sont implémentés et leurs opérations de persistance ont été vérifiées par
des tests d'intégration.

La relation entre les déclarations de compétences des collaborateurs et le catalogue central a également été démontrée.

Le travail peut maintenant se poursuivre vers l'extension progressive du modèle de données, l'initialisation de la base
et la définition des règles de validation et d'indexation.

**Ce document décrit l'état actuel du développement. Il ne constitue pas une spécification fonctionnelle définitive ni
une validation officielle des choix métier par Alltech.**