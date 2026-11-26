# Initialisation de la base de données

## 1. Objectif et principe

Ce document décrit le mécanisme d'initialisation de la base de données MongoDB du projet ECA Backend à l'aide de
Liquibase.

L'objectif est de disposer d'un mécanisme versionné et reproductible permettant de créer et de faire évoluer
progressivement la structure de la base de données.

Plutôt que de créer manuellement les collections depuis MongoDB Compass ou directement dans le code applicatif, leur
création est décrite dans des fichiers de changelog Liquibase.

Chaque modification de la structure de la base de données est ainsi représentée par un changeset identifié et suivi par
Liquibase.

### 1.1 Responsabilités de Liquibase

Dans le projet ECA Backend, Liquibase est utilisé pour :

- Décrire la structure initiale de la base de données.
- Créer les collections MongoDB nécessaires à l'application.
- Initialiser les données de référence et les données de test.
- Créer les index nécessaires aux principales opérations de recherche et de consultation.
- Conserver l'historique des modifications appliquées.
- Éviter de réexécuter les changements déjà appliqués.
- Permettre l'évolution progressive de la structure de la base de données.

Le périmètre actuel comprend :

- La création des 17 collections métier.
- Le chargement des données initiales définies dans les changelogs.
- La création de 31 index répartis sur les collections concernées.

Les évolutions ultérieures de la structure, des données et des index pourront être ajoutées dans de nouveaux changesets.

### 1.2 Principe de fonctionnement

Au démarrage de l'application, un composant dédié initialise Liquibase et lui transmet le changelog principal.

Liquibase compare les changesets déclarés dans ce fichier avec ceux déjà enregistrés dans sa base de suivi.

Seuls les changesets qui n'ont pas encore été exécutés sont appliqués.

Cette approche permet de conserver une trace des évolutions de la base de données et de rendre les initialisations
successives reproductibles.

## 2. Dépendance et configuration de Liquibase

### 2.1 Dépendance Maven

Le projet utilise l'extension Liquibase dédiée à MongoDB :

- **Dépendance :** `org.liquibase.ext:liquibase-mongodb`
- **Version :** `5.0.4`

Cette extension permet à Liquibase d'interagir directement avec MongoDB et d'exécuter des opérations adaptées à une base
de données documentaire.

Liquibase Core est également fourni de manière transitive par cette dépendance.

Le starter Spring Boot Liquibase classique n'est pas utilisé. L'initialisation est réalisée par une configuration Java
dédiée, afin de contrôler explicitement la création et l'exécution de l'instance Liquibase avec la connexion MongoDB.

### 2.2 Configuration de la connexion MongoDB

La connexion à MongoDB et l'activation de Liquibase sont définies dans les fichiers de configuration Spring Boot.

**Configuration par défaut — `application.properties` :**

    spring.mongodb.uri=mongodb://localhost:27017/eca
    eca.liquibase.enabled=false

Cette configuration utilise la base principale `eca`. Liquibase est désactivé par défaut afin d'éviter toute
modification automatique de cette base lors d'un démarrage standard de l'application.

**Environnement de test — profil `test` :**

    spring.mongodb.uri=mongodb://localhost:27017/eca_test
    eca.liquibase.enabled=true

Cette configuration utilise une base dédiée aux tests et permet d'exécuter les migrations Liquibase sur `eca_test`.

Les deux paramètres ont des responsabilités distinctes :

- `spring.mongodb.uri` définit l'adresse du serveur MongoDB et la base de données utilisée par l'application.
- `eca.liquibase.enabled` permet d'activer ou de désactiver l'initialisation Liquibase.

Le profil de test doit être activé explicitement lors de l'exécution afin de cibler la base `eca_test` et d'y appliquer
les changements déclarés dans les changelogs.

### 2.3 Configuration Java

La classe `LiquibaseConfiguration` assure l'initialisation de Liquibase au démarrage de l'application.

Elle utilise un composant Spring `ApplicationRunner`, exécuté lors du démarrage du contexte applicatif.

Le mécanisme repose sur les étapes suivantes :

1. Vérification de la propriété `eca.liquibase.enabled`.
2. Récupération de l'URI MongoDB configurée dans `spring.mongodb.uri`.
3. Création d'une connexion Liquibase à l'aide de `DatabaseFactory`.
4. Chargement du changelog principal avec `ClassLoaderResourceAccessor`.
5. Création de l'instance `Liquibase`.
6. Exécution de `liquibase.update("")` pour appliquer les changesets qui n'ont pas encore été exécutés.

La connexion à la base de données est ouverte à partir de l'URI MongoDB :

    DatabaseFactory.getInstance().openDatabase(mongoUri, null, null, null, null)

Le changelog principal utilisé est :

    db/changelog/db.changelog-master.xml

L'initialisation est conditionnée par la propriété `eca.liquibase.enabled=true`. Lorsque cette propriété est désactivée,
le mécanisme d'initialisation Liquibase n'est pas exécuté.

## 3. Organisation des fichiers de changelog

Les fichiers Liquibase sont regroupés dans le répertoire `src/main/resources/db/changelog/`.

L'organisation distingue les changements structurels, le chargement des données et la création des index.

### 3.1 Organisation des répertoires

La structure actuelle est la suivante :

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

Le répertoire `changes/` contient les migrations structurelles :

- `001-create-collections.xml` : création des 17 collections MongoDB.
- `002-create-indexes.xml` : création des index nécessaires aux principales opérations de recherche et de consultation.

Le répertoire `data/` contient les fichiers de chargement des données initiales, notamment les référentiels et les
données de test.

Le fichier `db.changelog-master.xml` centralise les références vers ces différents fichiers.

### 3.2 Organisation des changesets

Les changesets suivent une numérotation séquentielle afin de conserver des identifiants uniques dans l'ensemble des
migrations.

| Plage d'identifiants | Type de migration | Contenu                                                |
|----------------------|-------------------|--------------------------------------------------------|
| 001–017              | Structure         | Création des 17 collections MongoDB                    |
| 018–075              | Données           | Insertion des données initiales et des données de test |
| 076–106              | Index             | Création de 31 index sur les collections concernées    |

Cette organisation permet de distinguer clairement les différentes étapes d'initialisation de la base de données.

### 3.3 Changelog principal

Le fichier `db.changelog-master.xml` constitue le point d'entrée de Liquibase.

Il référence les différents fichiers de migration dans l'ordre d'exécution prévu :

1. Création des collections.
2. Chargement des données initiales.
3. Création des index.

Cette organisation garantit que les collections existent avant l'insertion des données et que les index sont créés après
le chargement des données initiales.

## 4. Contenu des changelogs

### 4.1 Changelog principal

Le fichier `db.changelog-master.xml` constitue le point d'entrée de Liquibase.

Il centralise les références vers les différents fichiers de changements. Lors de l'initialisation, Liquibase charge ce
fichier et parcourt les changelogs qui y sont déclarés.

Les migrations sont organisées dans l'ordre suivant :

1. Création des collections.
2. Chargement des données initiales.
3. Création des index.

Cette organisation permet de faire évoluer la base de données progressivement, en ajoutant de nouveaux fichiers de
changements au changelog principal sans modifier les changements déjà exécutés.

### 4.2 Création des collections

Le fichier `changes/001-create-collections.xml` contient les instructions de création des 17 collections métier de la
base de données ECA.

Chaque collection est créée au moyen d'un changeset Liquibase utilisant l'opération MongoDB `mongodb:createCollection`.

Les changesets sont identifiés individuellement, selon une convention de nommage séquentielle :

- `001-create-collaborators`
- `002-create-agencies`
- `003-create-competences`
- `004-create-professions`
- `005-create-profession-categories`
- `006-create-experience-levels`
- `007-create-domains`
- `008-create-competence-domains`
- `009-create-profession-competences`
- `010-create-clients`
- `011-create-missions`
- `012-create-collaborator-missions`
- `013-create-roles`
- `014-create-tools`
- `015-create-tool-categories`
- `016-create-audit-logs`
- `017-create-notifications`

Cette première migration établit la structure initiale de la base de données en créant les collections nécessaires aux
modèles persistants du projet.

Le modèle `CompetenceDeclaration` n'a pas de collection dédiée : il est intégré directement dans les documents
`Collaborator`.

### 4.3 Chargement des données initiales

Le répertoire `data/` contient les fichiers de changelog dédiés à l'insertion des données initiales.

Ces fichiers permettent notamment d'alimenter les référentiels nécessaires au fonctionnement du projet :

- Catégories de métiers.
- Métiers.
- Agences.
- Niveaux d'expérience.
- Rôles.
- Domaines.
- Compétences et autres données associées.

Les insertions sont décrites dans des changesets Liquibase dont les identifiants couvrent la plage 018 à 075.

Les données de démonstration et de test permettent également de disposer d'un environnement cohérent pour vérifier les
relations entre les différentes collections.

Ces données sont fictives et ne constituent pas des données réelles de collaborateurs ou de clients Alltech.

### 4.4 Création des index

Le fichier `changes/002-create-indexes.xml` contient les instructions de création des index MongoDB.

Il regroupe 31 changesets, identifiés de 076 à 106.

Les index ont pour objectif de faciliter les principales opérations de recherche, de filtrage et de consultation des
données.

Ils couvrent notamment :

- Les identifiants et les informations de recherche des collaborateurs.
- Les déclarations de compétences et leur caractère sollicitable.
- Les associations entre collaborateurs et missions.
- Les références vers les clients et les missions.
- Les notifications destinées aux collaborateurs.
- Les noms et catégories des données de référence.
- Les informations utilisées pour la traçabilité et les journaux d'audit.

Un index unique est également défini sur `collaborators.adIdentifier` afin de garantir l'unicité de cet identifiant dans
la collection.

Les index sont créés après les collections et le chargement des données initiales. Leur définition est conservée dans
Liquibase afin que leur création soit suivie et reproductible.

## 5. Suivi des changements par Liquibase

Liquibase conserve un historique des changesets exécutés directement dans la base MongoDB.

Deux collections techniques sont utilisées à cette fin :

- `DATABASECHANGELOG` : conserve l'historique des changesets appliqués, avec leurs identifiants et leurs informations
  d'exécution.
- `DATABASECHANGELOGLOCK` : permet de gérer le verrouillage pendant l'exécution des migrations afin d'éviter plusieurs
  exécutions simultanées.

Lorsqu'un changeset est exécuté avec succès, Liquibase enregistre son exécution dans `DATABASECHANGELOG`.

Lors d'un démarrage ultérieur, Liquibase consulte cet historique pour déterminer les changements restant à appliquer.

Cette gestion permet notamment :

- D'éviter de recréer les collections déjà initialisées.
- De conserver une trace des migrations exécutées.
- De poursuivre les évolutions de la base sans rejouer les changements précédents.

## 6. Exécution et vérification de l'initialisation

### 6.1 Exécution au démarrage de l'application

L'initialisation Liquibase est déclenchée automatiquement au démarrage de l'application lorsque la propriété
`eca.liquibase.enabled` est définie à `true`.

Il n'est donc pas nécessaire de lancer manuellement un script SQL ou de créer les collections depuis MongoDB Compass.

L'initialisation complète comprend trois étapes :

1. Création des 17 collections métier.
2. Chargement des données initiales définies dans les changelogs.
3. Création des 31 index MongoDB.

Lors des démarrages suivants, Liquibase consulte son historique et n'exécute que les changesets qui n'ont pas encore été
appliqués.

**Environnement principal :**

La configuration par défaut cible la base `eca` avec Liquibase désactivé. Les migrations ne sont donc pas exécutées lors
d'un démarrage standard.

**Environnement de test :**

Le profil `test` cible la base `eca_test` et active Liquibase. Il permet de vérifier l'initialisation complète sans
modifier la base principale.

### 6.2 Vérification dans MongoDB Compass

Après l'exécution des migrations sur `eca_test`, les résultats peuvent être vérifiés à l'aide de MongoDB Compass.

La connexion doit pointer vers le serveur MongoDB utilisé par le projet, puis vers la base de données `eca_test`.

Les vérifications portent sur les éléments suivants :

- Présence des 17 collections métier attendues.
- Présence des données initiales dans les collections concernées.
- Présence des index définis dans le changelog `002-create-indexes.xml`.
- Présence des collections techniques `DATABASECHANGELOG` et `DATABASECHANGELOGLOCK`.
- Absence d'erreur d'initialisation dans les logs de l'application.

Les collections métier doivent correspondre à celles déclarées dans le fichier `001-create-collections.xml`.

Les index peuvent être consultés dans l'onglet dédié de chaque collection dans MongoDB Compass.

La présence des collections techniques confirme également que Liquibase a initialisé son mécanisme de suivi des
migrations.

### 6.3 Vérification de l'idempotence

L'idempotence du mécanisme a été vérifiée sur une base MongoDB de test dédiée, nommée `liquibase_test`.

Le test a consisté à exécuter deux fois successivement une migration contenant un changeset de création de collection.

**Premier lancement :**

Liquibase a exécuté le changeset et créé la collection `liquibase_probe`.

**Deuxième lancement :**

Liquibase a reconnu que le changeset avait déjà été appliqué et ne l'a pas exécuté une nouvelle fois.

Le résultat affiché était :

- Run: 0
- Previously run: 1
- Total: 1

Ce résultat confirme que Liquibase s'appuie correctement sur son historique pour éviter de réexécuter un changeset déjà
appliqué.

Cette vérification valide le comportement de suivi des migrations et l'opération de création de collection testée avec
MongoDB 9.0.8 et Liquibase MongoDB 5.0.4. Elle ne constitue pas une validation exhaustive de toutes les opérations
proposées par l'extension MongoDB.

## 7. Conventions pour les évolutions futures

Afin de conserver un historique fiable et cohérent des évolutions de la base de données, les changements futurs doivent
respecter quelques conventions.

### 7.1 Ne pas modifier les changesets déjà exécutés

Une fois qu'un changeset a été appliqué sur une base de données, il ne doit plus être modifié.

Liquibase identifie les changesets déjà exécutés et conserve leur empreinte dans son historique. Modifier un changeset
existant peut entraîner une erreur de validation lors d'une exécution ultérieure.

Toute évolution doit donc être décrite dans un nouveau changeset.

### 7.2 Ajouter de nouveaux changesets

Les évolutions doivent être ajoutées dans de nouveaux changesets, en respectant l'organisation existante des fichiers de
changelog.

Les identifiants des changesets doivent rester uniques et suivre une numérotation séquentielle.

Les évolutions peuvent notamment concerner :

- L'ajout de nouvelles collections.
- L'ajout ou la modification de champs.
- L'insertion ou la mise à jour de données de référence.
- La création de nouveaux index.
- La modification de la structure des documents existants.

Les nouveaux index doivent être déclarés dans le fichier `changes/002-create-indexes.xml` ou dans un nouveau fichier de
changelog dédié, selon l'ampleur de l'évolution.

Toute modification d'un index déjà créé doit être réalisée au moyen d'un nouveau changeset. Il convient de supprimer
l'ancien index et de créer le nouvel index dans une migration dédiée, sans modifier un changeset déjà exécuté.

### 7.3 Séparer la structure et les données

Les changements concernant la structure de la base de données doivent rester distincts des opérations de chargement des
données.

L'organisation actuelle repose sur deux catégories de fichiers :

- `changes/` : création des collections et des index.
- `data/` : insertion des données de référence et des données initiales.

Cette séparation facilite la maintenance des migrations et permet de faire évoluer indépendamment la structure de la
base et son contenu.

Toute nouvelle donnée initiale doit être ajoutée dans un changeset dédié, en conservant la cohérence des identifiants et
des références entre les collections.

## 8. État actuel et travaux restants

### 8.1 Éléments implémentés

À la date de rédaction de ce document, les éléments suivants sont implémentés dans le projet :

- Dépendance Liquibase MongoDB 5.0.4 intégrée au projet Maven.
- Configuration Java dédiée à l'initialisation de Liquibase.
- Activation et désactivation du mécanisme via `eca.liquibase.enabled`.
- Configuration de la connexion à MongoDB pour les bases `eca` et `eca_test`.
- Organisation des fichiers de changelog.
- Changelog initial contenant 17 changesets de création de collections.
- Mise en place du chargement des données initiales au moyen de changesets numérotés de 018 à 075.
- Création du fichier de changelog des index contenant 31 changesets numérotés de 076 à 106.
- Définition des index sur les collections concernées, notamment l'index unique sur `collaborators.adIdentifier`.
- Mise en place du suivi des migrations avec `DATABASECHANGELOG` et `DATABASECHANGELOGLOCK`.
- Vérification expérimentale de l'idempotence sur une base MongoDB de test.

Les fichiers de migration couvrent ainsi la création des collections, l'insertion des données initiales et la création
des index.

### 8.2 Éléments restant à vérifier ou à compléter

Les points suivants restent à traiter avant de considérer l'initialisation comme entièrement validée :

- Exécuter et vérifier l'ensemble des changesets sur la base `eca_test`, notamment les migrations d'insertion des
  données et de création des 31 index.
- Vérifier le comportement idempotent lors d'un second démarrage avec l'ensemble des migrations.
- Confirmer que les tests automatisés utilisent bien `eca_test` et ne déclenchent pas de modifications non souhaitées
  sur la base principale `eca`.
- Vérifier le comportement de l'application lorsque l'initialisation Liquibase échoue.
- Améliorer les messages de journalisation de la configuration Liquibase si nécessaire.
- Vérifier la cohérence des données initiales et des références entre les différentes collections.
- Confirmer avec Alltech les référentiels et les règles métier qui restent à valider.

Ces éléments pourront être intégrés progressivement dans de nouveaux changesets, sans modifier les migrations déjà
appliquées.

## 9. Journalisation et diagnostic

### 9.1 Lecture des logs Liquibase

Lors de l'exécution d'une migration, Liquibase affiche un résumé permettant de suivre les changements appliqués.

Les principales informations sont :

- `Run` : nombre de changesets exécutés pendant cette opération.
- `Previously run` : nombre de changesets déjà exécutés lors de précédentes initialisations.
- `Total` : nombre total de changesets pris en compte.

Par exemple, lors du premier lancement avec 17 changesets :

    Run: 17
    Previously run: 0
    Total: 17

Lors d'un lancement ultérieur, si tous les changesets ont déjà été appliqués :

    Run: 0
    Previously run: 17
    Total: 17

Ces informations permettent de vérifier rapidement si Liquibase a exécuté de nouvelles migrations ou si la base était
déjà à jour.

### 9.2 Diagnostic en cas d'erreur

En cas d'échec de l'initialisation, les premières vérifications à effectuer sont :

- Vérifier que le serveur MongoDB est démarré et accessible.
- Vérifier que l'URI MongoDB configurée dans `spring.mongodb.uri` est correcte.
- Vérifier que la base de données ciblée est bien celle attendue.
- Vérifier que le changelog principal et les fichiers référencés sont présents dans les ressources du projet.
- Consulter les logs pour identifier le changeset à l'origine de l'erreur.
- Vérifier l'état des collections techniques `DATABASECHANGELOG` et `DATABASECHANGELOGLOCK`.

Une erreur liée à un changeset déjà exécuté peut notamment provenir d'une modification apportée à un changeset après son
application.

Dans ce cas, il convient de vérifier l'historique Liquibase et de privilégier la création d'un nouveau changeset plutôt
que la modification d'une migration existante.

### 9.3 Gestion du verrouillage

Liquibase utilise la collection `DATABASECHANGELOGLOCK` pour éviter que plusieurs processus exécutent simultanément des
migrations sur la même base.

Si une exécution est interrompue de manière inattendue, il peut être nécessaire de vérifier l'état de ce verrou.

La suppression ou la modification manuelle du verrou ne doit pas être effectuée sans avoir vérifié qu'aucune migration
n'est encore en cours.

Toute intervention doit être réalisée avec précaution afin de ne pas compromettre l'historique des migrations.

## 10. Particularités de l'environnement de test

### 10.1 Base de données dédiée

L'environnement de test utilise une base MongoDB distincte de la base principale :

    mongodb://localhost:27017/eca_test

Cette séparation permet d'exécuter les migrations et les tests sans modifier les données de la base principale `eca`.

La configuration associée est définie dans le profil Spring Boot `test`.

### 10.2 Activation de Liquibase

Dans le profil `test`, la propriété suivante est définie :

    eca.liquibase.enabled=true

Ce paramétrage active l'initialisation Liquibase lors du démarrage de l'application avec ce profil.

Les changesets déclarés dans le changelog principal sont alors exécutés sur `eca_test`, notamment :

- La création des 17 collections.
- Le chargement des données initiales.
- La création des 31 index.

La configuration par défaut conserve Liquibase désactivé afin d'éviter toute modification automatique de la base
principale `eca`.

### 10.3 Conséquences pour l'exécution des tests

Avant d'exécuter les tests automatisés, il convient de vérifier :

- Que l'environnement utilise bien la base `eca_test`.
- Que les tests ne pointent pas accidentellement vers la base principale `eca`.
- Que les collections nécessaires aux tests sont disponibles.
- Que les données utilisées par les tests sont isolées des données réelles.
- Que les migrations Liquibase sont exécutées uniquement sur la base de test lorsque le profil correspondant est activé.

L'utilisation d'une base dédiée permet de tester les migrations et les opérations de persistance sans intervenir sur la
base principale.