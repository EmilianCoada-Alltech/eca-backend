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
- Conserver l'historique des modifications appliquées.
- Éviter de réexécuter les changements déjà appliqués.
- Permettre l'évolution progressive de la structure de la base de données.

Le périmètre actuel couvre la création des 17 collections métier du projet.

Les données de référence, les index et les évolutions ultérieures de la structure pourront être ajoutés dans de nouveaux
changesets.

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

La connexion à MongoDB est définie dans les fichiers de configuration Spring Boot.

**Environnement principal — `src/main/resources/application.properties` :**

    spring.mongodb.uri=mongodb://localhost:27017/eca
    eca.liquibase.enabled=true

**Environnement de test — `src/test/resources/application.properties` :**

    spring.mongodb.uri=mongodb://localhost:27017/eca_test
    eca.liquibase.enabled=false

Les deux paramètres ont des responsabilités distinctes :

- `spring.mongodb.uri` définit l'adresse du serveur MongoDB et la base de données utilisée par l'application.
- `eca.liquibase.enabled` permet d'activer ou de désactiver l'initialisation Liquibase.

L'initialisation automatique est activée dans la configuration principale et désactivée dans la configuration de test
afin d'éviter que les tests ne déclenchent des modifications de structure non souhaitées.

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

L'organisation actuelle est la suivante :

    src/main/resources/
    └── db/
        └── changelog/
            ├── db.changelog-master.xml
            ├── changes/
            │   └── 001-create-collections.xml
            └── data/

Chaque élément possède un rôle précis :

- `db.changelog-master.xml` : fichier principal chargé par Liquibase. Il référence les différents fichiers de
  changements à exécuter.
- `changes/` : répertoire contenant les fichiers décrivant les évolutions de la structure de la base de données.
- `data/` : répertoire prévu pour accueillir les éventuels fichiers de chargement des données de référence.

Le fichier `001-create-collections.xml` contient actuellement les instructions nécessaires à la création des 17
collections métier du projet.

Le répertoire `data/` est actuellement vide : aucun jeu de données initial n'est encore chargé par Liquibase.

## 4. Contenu des changelogs

### 4.1 Changelog principal

Le fichier `db.changelog-master.xml` constitue le point d'entrée de Liquibase.

Il centralise les références vers les différents fichiers de changements. Lors de l'initialisation, Liquibase charge ce
fichier et parcourt les changelogs qui y sont déclarés.

Actuellement, il référence le fichier de création des collections :

    changes/001-create-collections.xml

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

Au premier démarrage sur une base vierge, Liquibase exécute les changesets déclarés dans le changelog principal et crée
les 17 collections métier.

Lors des démarrages suivants, Liquibase consulte son historique et n'exécute que les changesets qui n'ont pas encore été
appliqués.

### 6.2 Vérification dans MongoDB Compass

Après le démarrage de l'application, la création des collections peut être vérifiée à l'aide de MongoDB Compass.

La connexion doit pointer vers le serveur MongoDB utilisé par le projet, puis vers la base de données `eca`.

Les vérifications effectuées sont les suivantes :

- Présence des 17 collections métier attendues.
- Présence des collections techniques `DATABASECHANGELOG` et `DATABASECHANGELOGLOCK`.
- Absence d'erreur d'initialisation dans les logs de l'application.

Les collections métier doivent correspondre à celles déclarées dans le fichier `001-create-collections.xml`.

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

Les évolutions doivent être ajoutées dans de nouveaux fichiers ou dans les fichiers de changelog existants, selon
l'organisation retenue.

Les identifiants des changesets doivent rester uniques et suivre une convention de nommage cohérente.

Exemples d'évolutions futures :

- Création d'index sur certaines collections.
- Ajout de collections supplémentaires.
- Ajout ou modification de champs.
- Insertion de données de référence.
- Modification de la structure de documents existants.

### 7.3 Séparer la structure et les données

Les changements concernant la structure de la base de données doivent rester distincts des éventuelles opérations de
chargement de données.

Le répertoire `data/` est prévu pour accueillir les fichiers de changelog dédiés aux données de référence.

Les données initiales ne doivent être ajoutées qu'après validation des référentiels et des valeurs attendues par
Alltech.

## 8. État actuel et travaux restants

### 8.1 Éléments implémentés

À la date de rédaction de ce document, les éléments suivants sont implémentés :

- Dépendance Liquibase MongoDB 5.0.4 intégrée au projet Maven.
- Configuration Java dédiée à l'initialisation de Liquibase.
- Activation et désactivation du mécanisme via `eca.liquibase.enabled`.
- Organisation des fichiers de changelog.
- Changelog initial contenant 17 changesets de création de collections.
- Création effective des collections sur MongoDB 9.0.8.
- Mise en place du suivi des migrations avec `DATABASECHANGELOG` et `DATABASECHANGELOGLOCK`.
- Vérification expérimentale de l'idempotence sur une base MongoDB de test.

### 8.2 Éléments restant à vérifier ou à compléter

Les points suivants restent à traiter avant de considérer l'initialisation comme entièrement validée :

- Vérifier le comportement idempotent lors d'un second démarrage de l'application sur la base principale `eca`.
- Confirmer que les tests automatisés utilisent bien `eca_test` sans déclencher l'initialisation Liquibase.
- Vérifier le comportement de l'application lorsque l'initialisation Liquibase échoue.
- Améliorer les messages de journalisation de la configuration Liquibase si nécessaire.
- Ajouter les index après identification des besoins réels de requêtes.
- Ajouter les données de référence lorsque les référentiels auront été confirmés par Alltech.

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

La configuration de test (`src/test/resources/application.properties`) définit une base MongoDB distincte :

    mongodb://localhost:27017/eca_test

Cette configuration est destinée à isoler les opérations réalisées pendant les tests de la base principale `eca`.

L'utilisation effective de cette base par l'ensemble des tests automatisés reste à vérifier.

### 10.2 Désactivation de Liquibase

Dans `src/test/resources/application.properties`, la propriété suivante est définie :

    eca.liquibase.enabled=false

Ce paramétrage désactive l'initialisation automatique de Liquibase pendant les tests.

Par conséquent, les collections ne sont pas automatiquement créées par Liquibase dans `eca_test`.

Les tests nécessitant des collections doivent donc gérer explicitement leur environnement de test, notamment leur
création et leur nettoyage lorsque cela est nécessaire.

### 10.3 Conséquences pour l'exécution des tests

Avant d'exécuter les tests automatisés, il convient de vérifier :

- Que l'environnement de test utilise bien la base `eca_test`.
- Que les tests ne pointent pas accidentellement vers la base principale `eca`.
- Que les collections nécessaires aux tests sont disponibles.
- Que les données utilisées par les tests sont isolées des données réelles.

La désactivation de Liquibase dans l'environnement de test concerne uniquement son exécution automatique au démarrage.
Elle ne remplace pas la préparation de l'environnement nécessaire aux tests d'intégration.