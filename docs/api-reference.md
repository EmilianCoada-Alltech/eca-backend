# Référence API — ECA Backend

**Version :** 0.2  
**Date :** 26/11/2026  
**Statut :** En cours de développement

---

## 1. Présentation

Ce document décrit les endpoints HTTP exposés par le backend de l'application ECA (Espace Collaborateur Alltech).

Il constitue la référence fonctionnelle et technique des API disponibles pour les applications clientes.

Pour chaque endpoint, il précise :

- La méthode HTTP et le chemin d'accès.
- La description de son objectif.
- Les paramètres éventuels.
- La structure des données retournées.
- Les codes de réponse HTTP identifiés.

Ce document sera enrichi au fur et à mesure du développement des endpoints.

## 2. Conventions générales

### 2.1 Préfixe des routes

Les endpoints de l'API utilisent le préfixe suivant :

`/api`

Les routes sont organisées par domaine fonctionnel. Les données de référence sont regroupées sous :

`/api/references`

### 2.2 Format des échanges

Les réponses de l'API sont retournées au format JSON.

Les DTO (*Data Transfer Objects*) définissent les structures de données exposées aux applications clientes. Les modèles
de persistance MongoDB ne sont pas directement exposés.

## 3. API de référence

Cette API regroupe les endpoints permettant de consulter les données de référence utilisées par l'application.

### 3.1 Professions

Cette API permet de consulter, créer, modifier et désactiver les professions enregistrées dans la collection MongoDB
`professions`.

Chaque profession est associée à une catégorie et possède un statut (`active`) permettant de contrôler sa disponibilité
dans l'application.

Les opérations de création, de modification et de désactivation sont également enregistrées dans la collection
`audit_logs`.

#### 3.1.1 Récupérer les professions actives

#### Récupérer les professions actives

| Propriété             | Valeur                                     |
|-----------------------|--------------------------------------------|
| Méthode HTTP          | `GET`                                      |
| Endpoint              | `/api/references/professions/active`       |
| Description           | Récupérer la liste des professions actives |
| Paramètres de chemin  | Aucun                                      |
| Paramètres de requête | Aucun                                      |
| Corps de la requête   | Aucun                                      |
| Réponse nominale      | `200 OK`                                   |

**Fonctionnement**

L'endpoint retourne les professions enregistrées dans la collection MongoDB `professions` dont le champ `active` est
égal à `true`.

Les professions inactives ne sont pas incluses dans le résultat.

Si aucune profession active n'existe, l'API retourne une liste vide.

**Exemple de réponse**

[
{
"id": "PROF-001",
"name": "Développeur Frontend",
"description": "Conçoit et développe les interfaces utilisateur des applications.",
"categoryId": "CAT-001",
"active": true
}
]

*Les données ci-dessus sont illustratives. La réponse réelle dépend des données enregistrées en base.*

**Implémentation technique**

La récupération utilise la méthode Spring Data MongoDB `findByActiveTrue()` du `ProfessionRepository`. Les résultats
sont ensuite convertis en DTO `ProfessionResponse` par le `ProfessionMapper`.

**Test automatisé**

La méthode est couverte par `ProfessionRepositoryTest`, `ProfessionServiceTest` et `ProfessionControllerTest`.

### 3.1.2 Récupérer toutes les professions

| Propriété             | Valeur                                                 |
|-----------------------|--------------------------------------------------------|
| Méthode HTTP          | `GET`                                                  |
| Endpoint              | `/api/references/professions/all`                      |
| Description           | Récupérer toutes les professions, actives et inactives |
| Paramètres de chemin  | Aucun                                                  |
| Paramètres de requête | Aucun                                                  |
| Corps de la requête   | Aucun                                                  |
| Réponse nominale      | `200 OK`                                               |

**Fonctionnement**

L'endpoint retourne l'ensemble des professions enregistrées dans la collection MongoDB `professions`, sans appliquer de
filtre sur le champ `active`.

Les professions actives et inactives sont donc toutes incluses dans le résultat.

Si aucune profession n'est enregistrée, l'API retourne une liste vide.

**Exemple de réponse**

```json
[
  {
    "id": "PROF-001",
    "name": "Développeur Frontend",
    "description": "Conçoit et développe les interfaces utilisateur des applications.",
    "categoryId": "CAT-001",
    "active": true
  },
  {
    "id": "PROF-002",
    "name": "Développeur Backend",
    "description": "Conçoit et développe les composants serveur et les API.",
    "categoryId": "CAT-001",
    "active": false
  }
]
```

*Les données ci-dessus sont illustratives. La réponse réelle dépend des données enregistrées en base.*

**Implémentation technique**

La récupération utilise la méthode `findAll()` du `ProfessionRepository`. Les résultats sont ensuite convertis en DTO
`ProfessionResponse` par le `ProfessionMapper`.

**Test automatisé**

La méthode est couverte par `ProfessionServiceTest` et `ProfessionControllerTest`.

### 3.1.3 Récupérer une profession par son identifiant

| Propriété             | Valeur                                               |
|-----------------------|------------------------------------------------------|
| Méthode HTTP          | `GET`                                                |
| Endpoint              | `/api/references/professions/{id}`                   |
| Description           | Récupérer une profession à partir de son identifiant |
| Paramètre de chemin   | `id` : identifiant de la profession                  |
| Paramètres de requête | Aucun                                                |
| Corps de la requête   | Aucun                                                |
| Réponse nominale      | `200 OK`                                             |

**Exemple de requête**

```http
GET /api/references/professions/PROF-001
```

**Fonctionnement**

L'endpoint recherche une profession dans la collection MongoDB `professions` à partir de son identifiant.

La profession est retournée sous la forme d'un DTO `ProfessionResponse`.

Si aucune profession ne correspond à l'identifiant fourni, l'API retourne une réponse `404 Not Found`.

**Exemple de réponse**

```json
{
  "id": "PROF-001",
  "name": "Développeur Backend",
  "description": "Conçoit et développe les composants serveur et les API.",
  "categoryId": "CAT-001",
  "active": true
}
```

*Les données ci-dessus sont illustratives.*

**Implémentation technique**

La recherche utilise la méthode `findById()` du `ProfessionRepository`. Le `ProfessionService` gère le cas où la
profession est introuvable et le `ProfessionMapper` convertit le modèle en DTO.

**Tests automatisés**

Le comportement est couvert par `ProfessionServiceTest` et `ProfessionControllerTest`, notamment pour vérifier la
récupération d'une profession existante et le retour d'une erreur 404 lorsqu'elle est introuvable.

### 3.1.4 Créer une profession

| Propriété             | Valeur                        |
|-----------------------|-------------------------------|
| Méthode HTTP          | `POST`                        |
| Endpoint              | `/api/references/professions` |
| Description           | Créer une nouvelle profession |
| Paramètres de chemin  | Aucun                         |
| Paramètres de requête | Aucun                         |
| Corps de la requête   | `ProfessionRequest`           |
| Réponse nominale      | `201 Created`                 |

**Exemple de requête**

```http
POST /api/references/professions
```

```json
{
  "name": "Backend Developer",
  "description": "Develops backend services and APIs.",
  "categoryId": "CAT-001"
}
```

**Fonctionnement**

Avant la création, le service vérifie que la catégorie indiquée existe dans la collection MongoDB
`profession_categories`.

Un identifiant unique est ensuite généré à l'aide du mécanisme de séquences (`SequenceGenerator`).

La nouvelle profession est enregistrée dans la collection `professions` avec le champ `active` initialisé à `true`.

La création est également enregistrée dans la collection `audit_logs`. Une entrée d'audit est générée pour chacun des
quatre champs suivants : `name`, `description`, `categoryId` et `active`.

Si la catégorie indiquée n'existe pas, l'API retourne une réponse `404 Not Found`.

**Exemple de réponse**

```json
{
  "id": "PROF-063",
  "name": "Backend Developer",
  "description": "Develops backend services and APIs.",
  "categoryId": "CAT-001",
  "active": true
}
```

*L'identifiant présenté est illustratif. Sa valeur réelle dépend de la séquence enregistrée en base.*

**Codes de réponse**

| Code HTTP       | Signification                       |
|-----------------|-------------------------------------|
| `201 Created`   | Profession créée avec succès.       |
| `404 Not Found` | La catégorie indiquée n'existe pas. |

**Implémentation technique**

La création est gérée par la méthode `createProfession()` du `ProfessionService`.

Elle utilise notamment :

- `ProfessionCategoryRepository` pour vérifier l'existence de la catégorie.
- `SequenceGenerator` pour générer l'identifiant.
- `ProfessionRepository` pour enregistrer la profession.
- `AuditLogRepository` pour enregistrer les modifications.

**Tests automatisés**

Le comportement est couvert par `ProfessionServiceTest` et `ProfessionControllerTest`, notamment pour vérifier la
création d'une profession, la génération de son identifiant, la journalisation et le rejet d'une catégorie inexistante.

### 3.1.5 Modifier une profession

| Propriété             | Valeur                              |
|-----------------------|-------------------------------------|
| Méthode HTTP          | `PUT`                               |
| Endpoint              | `/api/references/professions/{id}`  |
| Description           | Modifier une profession existante   |
| Paramètre de chemin   | `id` : identifiant de la profession |
| Paramètres de requête | Aucun                               |
| Corps de la requête   | `ProfessionRequest`                 |
| Réponse nominale      | `200 OK`                            |

**Exemple de requête**

```http
PUT /api/references/professions/PROF-001
```

```json
{
  "name": "Senior Backend Developer",
  "description": "Designs and develops complex backend services and APIs.",
  "categoryId": "CAT-001"
}
```

**Fonctionnement**

Le service recherche d'abord la profession à modifier dans la collection `professions`.

Il vérifie ensuite que la catégorie indiquée dans la requête existe dans la collection `profession_categories`.

Les champs `name`, `description` et `categoryId` sont comparés aux valeurs actuellement enregistrées. Seuls les champs
dont la valeur a changé sont mis à jour.

Chaque modification détectée génère une entrée correspondante dans la collection `audit_logs`, contenant notamment
l'ancienne et la nouvelle valeur.

Si aucune modification n'est détectée, aucune entrée d'audit n'est créée.

Le champ `active` n'est pas modifié par cet endpoint.

Si la profession ou la catégorie indiquée n'existe pas, l'API retourne une réponse `404 Not Found`.

**Exemple de réponse**

```json
{
  "id": "PROF-001",
  "name": "Senior Backend Developer",
  "description": "Designs and develops complex backend services and APIs.",
  "categoryId": "CAT-001",
  "active": true
}
```

*Les données ci-dessus sont illustratives.*

**Codes de réponse**

| Code HTTP       | Signification                                        |
|-----------------|------------------------------------------------------|
| `200 OK`        | Profession modifiée avec succès.                     |
| `404 Not Found` | La profession ou la catégorie indiquée n'existe pas. |

**Implémentation technique**

La modification est gérée par la méthode `updateProfession()` du `ProfessionService`.

Elle utilise notamment :

- `ProfessionRepository` pour récupérer et enregistrer la profession.
- `ProfessionCategoryRepository` pour vérifier l'existence de la catégorie.
- `AuditLogRepository` pour enregistrer uniquement les champs modifiés.

**Tests automatisés**

Le comportement est couvert par `ProfessionServiceTest` et `ProfessionControllerTest`, notamment pour vérifier la
modification, la journalisation sélective des changements et la gestion des ressources introuvables.

### 3.1.6 Désactiver une profession

| Propriété             | Valeur                              |
|-----------------------|-------------------------------------|
| Méthode HTTP          | `DELETE`                            |
| Endpoint              | `/api/references/professions/{id}`  |
| Description           | Désactiver une profession existante |
| Paramètre de chemin   | `id` : identifiant de la profession |
| Paramètres de requête | Aucun                               |
| Corps de la requête   | Aucun                               |
| Réponse nominale      | `204 No Content`                    |

**Exemple de requête**

```http
DELETE /api/references/professions/PROF-001
```

**Fonctionnement**

La suppression d'une profession est logique (*soft delete*) : le document n'est pas supprimé de la collection MongoDB
`professions`.

Le service modifie simplement le champ `active`, qui passe de `true` à `false`.

Cette opération est enregistrée dans la collection `audit_logs`, avec l'ancienne valeur (`true`) et la nouvelle valeur
(`false`) du champ `active`.

Si la profession est déjà inactive, aucune nouvelle entrée d'audit n'est créée.

Si la profession n'existe pas, l'API retourne une réponse `404 Not Found`.

**Codes de réponse**

| Code HTTP        | Signification                                                           |
|------------------|-------------------------------------------------------------------------|
| `204 No Content` | Profession désactivée avec succès. Aucune réponse JSON n'est retournée. |
| `404 Not Found`  | La profession demandée n'existe pas.                                    |

**Implémentation technique**

La désactivation est gérée par la méthode `deactivateProfession()` du `ProfessionService`.

Elle utilise :

- `ProfessionRepository` pour rechercher et mettre à jour la profession.
- `AuditLogRepository` pour enregistrer la désactivation.

**Tests automatisés**

Le comportement est couvert par `ProfessionServiceTest` et `ProfessionControllerTest`, notamment pour vérifier la
désactivation, la création de l'entrée d'audit, l'absence de doublon d'audit et la gestion d'une profession inexistante.

### 3.1.7 DTO de requête et de réponse

L'API utilise des objets de transfert de données (*Data Transfer Objects* ou DTO) afin de définir les structures
échangées avec les applications clientes.

Les modèles de persistance MongoDB ne sont pas directement exposés par les endpoints.

**DTO de requête : `ProfessionRequest`**

Ce DTO est utilisé lors de la création et de la modification d'une profession.

```java
public record ProfessionRequest(
        String name,
        String description,
        String categoryId
) {
}
```

| Champ         | Type   | Description                          |
|---------------|--------|--------------------------------------|
| `name`        | String | Nom de la profession                 |
| `description` | String | Description de la profession         |
| `categoryId`  | String | Identifiant de la catégorie associée |

**DTO de réponse : `ProfessionResponse`**

Ce DTO définit la structure des données retournées par les endpoints de consultation, de création et de modification.

```java
public record ProfessionResponse(
        String id,
        String name,
        String description,
        String categoryId,
        boolean active
) {
}
```

| Champ         | Type    | Description                          |
|---------------|---------|--------------------------------------|
| `id`          | String  | Identifiant unique de la profession  |
| `name`        | String  | Nom de la profession                 |
| `description` | String  | Description de la profession         |
| `categoryId`  | String  | Identifiant de la catégorie associée |
| `active`      | boolean | Indique si la profession est active  |

**Conversion des données**

La conversion entre le modèle `Profession` et le DTO `ProfessionResponse` est assurée par `ProfessionMapper`.

Cette séparation permet de contrôler les données exposées par l'API indépendamment de la structure interne de
persistance.

### 3.1.8 Journalisation des modifications

Les opérations de création, de modification et de désactivation des professions sont enregistrées dans la collection
MongoDB `audit_logs`.

Cette journalisation permet de conserver un historique des changements effectués sur les professions.

**Structure d'une entrée d'audit**

Chaque entrée contient les informations suivantes :

| Champ        | Description                      |
|--------------|----------------------------------|
| `entityType` | Type d'entité concernée          |
| `entityId`   | Identifiant de l'entité modifiée |
| `fieldName`  | Nom du champ concerné            |
| `oldValue`   | Ancienne valeur du champ         |
| `newValue`   | Nouvelle valeur du champ         |
| `changedBy`  | Auteur de la modification        |
| `changedAt`  | Date et heure de la modification |
| `action`     | Type d'opération effectuée       |

**Types d'opérations enregistrées**

| Opération     | Action   | Comportement                                                                |
|---------------|----------|-----------------------------------------------------------------------------|
| Création      | `CREATE` | Une entrée est créée pour chacun des champs initialisés.                    |
| Modification  | `UPDATE` | Une entrée est créée uniquement pour chaque champ modifié.                  |
| Désactivation | `DELETE` | Une entrée est créée pour le passage du champ `active` de `true` à `false`. |

**Exemple d'entrée d'audit**

```json
{
  "entityType": "Profession",
  "entityId": "PROF-001",
  "fieldName": "name",
  "oldValue": "Backend Developer",
  "newValue": "Senior Backend Developer",
  "changedBy": "ADMIN",
  "changedAt": "2026-11-27T10:30:00",
  "action": "UPDATE"
}
```

*Exemple illustratif. Les valeurs réelles dépendent de l'opération effectuée.*

**Implémentation technique**

- Modèle : `AuditLog`
- Repository : `AuditLogRepository`
- Collection MongoDB : `audit_logs`

La journalisation est déclenchée par le `ProfessionService` lors des opérations concernées.

À ce stade, la valeur `ADMIN` est utilisée comme auteur de modification. Elle devra être remplacée par l'identité de
l'utilisateur authentifié lorsque la gestion des utilisateurs et des droits d'accès sera intégrée.

### 3.1.9 Implémentation technique et tests automatisés

**Architecture technique**

L'API Professions suit une architecture en couches afin de séparer les responsabilités :

| Couche     | Classe                 | Responsabilité                                                  |
|------------|------------------------|-----------------------------------------------------------------|
| Controller | `ProfessionController` | Expose les endpoints HTTP et gère les réponses.                 |
| Service    | `ProfessionService`    | Contient la logique métier et orchestre les opérations.         |
| Repository | `ProfessionRepository` | Assure l'accès aux données MongoDB.                             |
| Mapper     | `ProfessionMapper`     | Convertit les modèles en DTO de réponse.                        |
| DTO        | `ProfessionRequest`    | Définit les données reçues lors des créations et modifications. |
| DTO        | `ProfessionResponse`   | Définit les données retournées par l'API.                       |
| Modèle     | `Profession`           | Représente une profession dans MongoDB.                         |

**Collections MongoDB utilisées**

| Collection              | Utilisation                                                   |
|-------------------------|---------------------------------------------------------------|
| `professions`           | Stockage des professions.                                     |
| `profession_categories` | Vérification des catégories associées.                        |
| `audit_logs`            | Historisation des créations, modifications et désactivations. |
| `database_sequences`    | Gestion de la génération des identifiants.                    |

**Tests automatisés**

Les tests sont répartis sur trois niveaux :

| Couche     | Classe de test             | Vérifications                                                                                     |
|------------|----------------------------|---------------------------------------------------------------------------------------------------|
| Repository | `ProfessionRepositoryTest` | Persistance et récupération des professions, notamment les professions actives.                   |
| Service    | `ProfessionServiceTest`    | Consultation, création, modification, désactivation, validation des catégories et journalisation. |
| Controller | `ProfessionControllerTest` | Endpoints HTTP, structures JSON, codes de réponse et gestion des erreurs.                         |

La classe `ProfessionServiceTest` contient 15 tests et la classe `ProfessionControllerTest` en contient 12. Les deux
suites ont été exécutées avec succès.

Les tests du service utilisent JUnit 5, Mockito et AssertJ afin d'isoler la logique métier de ses dépendances.

Les tests du controller utilisent `@WebMvcTest`, MockMvc et `@MockitoBean` afin de vérifier le comportement HTTP sans
démarrer l'ensemble de l'application.

Les tests du repository vérifient les interactions avec MongoDB.

**État actuel**

L'ensemble des tests du service et du controller est validé. Les tests du repository sont également présents dans le
projet.

---

### 3.2 Catégories de professions

#### Récupérer les catégories de professions

| Propriété             | Valeur                                           |
|-----------------------|--------------------------------------------------|
| Méthode HTTP          | `GET`                                            |
| Endpoint              | `/api/references/profession-categories`          |
| Description           | Récupérer la liste des catégories de professions |
| Paramètres de chemin  | Aucun                                            |
| Paramètres de requête | Aucun                                            |
| Corps de la requête   | Aucun                                            |
| Réponse nominale      | `200 OK`                                         |

**Fonctionnement**

L'endpoint retourne l'ensemble des catégories enregistrées dans la collection MongoDB `profession_categories`.

Aucun filtrage par statut n'est appliqué, le modèle `ProfessionCategory` ne possédant pas de champ `active`.

Si aucune catégorie n'est enregistrée, l'endpoint retourne une liste vide.

**Exemple de réponse**

```json
[
  {
    "id": "CAT-01",
    "name": "Development",
    "description": "Groups professions related to software development."
  }
]
```

*Les données ci-dessus sont illustratives. La réponse réelle dépend des données