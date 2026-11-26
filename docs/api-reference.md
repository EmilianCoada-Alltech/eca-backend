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

#### Récupérer les professions actives

| Propriété             | Valeur                                     |
|-----------------------|--------------------------------------------|
| Méthode HTTP          | `GET`                                      |
| Endpoint              | `/api/references/professions`              |
| Description           | Récupérer la liste des professions actives |
| Paramètres de chemin  | Aucun                                      |
| Paramètres de requête | Aucun                                      |
| Corps de la requête   | Aucun                                      |
| Réponse nominale      | `200 OK`                                   |

**Fonctionnement**

L'endpoint retourne les professions enregistrées dans la collection MongoDB `professions`, pour lesquelles le champ
`active` est égal à `true`.

Les professions inactives ne sont pas retournées.

**Exemple de réponse**

```json
[
  {
    "id": "PROF-001",
    "name": "Développeur Frontend",
    "description": "Conçoit et développe les interfaces utilisateur des applications.",
    "categoryId": "CAT-01",
    "active": true
  },
  {
    "id": "PROF-002",
    "name": "Développeur Backend",
    "description": "Conçoit et développe les composants serveur et les API.",
    "categoryId": "CAT-01",
    "active": true
  }
]
```

*Les descriptions ci-dessus sont illustratives. La réponse réelle dépend des données enregistrées en base.*

**DTO de réponse :** `ProfessionResponse`

| Champ         | Type    | Description                          |
|---------------|---------|--------------------------------------|
| `id`          | String  | Identifiant de la profession         |
| `name`        | String  | Nom de la profession                 |
| `description` | String  | Description de la profession         |
| `categoryId`  | String  | Identifiant de la catégorie associée |
| `active`      | Boolean | Indique si la profession est active  |

**Codes de réponse**

| Code HTTP | Signification                                                                                      |
|-----------|----------------------------------------------------------------------------------------------------|
| `200 OK`  | La requête a été traitée avec succès. Une liste de professions est retournée, éventuellement vide. |

**Implémentation technique**

- Controller : `ProfessionController`
- Service : `ProfessionService`
- Repository : `ProfessionRepository`
- Mapper : `ProfessionMapper`
- DTO : `ProfessionResponse`
- Collection MongoDB : `professions`

La récupération des données utilise la méthode Spring Data MongoDB `findByActiveTrue()`.

**Tests automatisés**

L'endpoint et ses différentes couches font l'objet de tests automatisés afin de vérifier le bon fonctionnement de la
récupération des professions actives.

Les tests sont répartis sur trois niveaux :

| Couche     | Classe de test             | Vérifications                                                                   |
|------------|----------------------------|---------------------------------------------------------------------------------|
| Repository | `ProfessionRepositoryTest` | Persistance, récupération des professions actives et gestion d'un résultat vide |
| Service    | `ProfessionServiceTest`    | Conversion des modèles en DTO et gestion d'un résultat vide                     |
| Controller | `ProfessionControllerTest` | Réponse HTTP 200, structure JSON et gestion d'une liste vide                    |

Les tests du repository utilisent une base MongoDB dédiée aux tests (`eca_test`). Les tests du service et du controller
utilisent Mockito afin d'isoler les composants testés.

**Couverture actuelle : 7 tests automatisés.**

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