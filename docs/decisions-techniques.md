## Convention linguistique et nomenclature technique

### Choix retenu

Le projet ECA adopte l'anglais comme langue de référence pour l'ensemble des éléments techniques du code et de la base
de données.

Cette convention s'applique notamment :

- Aux noms des classes, attributs, méthodes et variables Java.
- Aux noms des collections et des champs MongoDB.
- Aux noms des packages et des composants techniques.
- Aux identifiants et aux références utilisés dans le modèle de données.

La documentation fonctionnelle et technique destinée aux équipes Alltech reste rédigée en français.

Les libellés et contenus destinés aux utilisateurs finaux restent également en français, sauf besoin particulier.

### Justification

L'utilisation d'une nomenclature anglaise permet :

- D'assurer une cohérence avec les conventions couramment utilisées dans l'écosystème Java et Spring.
- De faciliter la lecture et la maintenance du code par des développeurs ayant des profils linguistiques différents.
- D'éviter le mélange de termes français et anglais au sein d'une même classe ou structure de données.
- De conserver une nomenclature homogène dans l'ensemble du projet.

Ce choix concerne exclusivement la nomenclature technique. Il ne modifie pas la langue de travail documentaire ni la
terminologie fonctionnelle utilisée par Alltech.

### Correspondance terminologique français / anglais

Afin de faciliter la compréhension du modèle de données par les collaborateurs ne souhaitant pas utiliser l'anglais dans
leur lecture technique, un glossaire de correspondance est maintenu.

| Terminologie métier (FR)                              | Nomenclature technique (EN) |
|-------------------------------------------------------|-----------------------------|
| Collaborateur                                         | Collaborator                |
| Identifiant Active Directory                          | adIdentifier                |
| Prénom                                                | firstName                   |
| Nom                                                   | lastName                    |
| Agence                                                | Agency                      |
| Métier                                                | Profession                  |
| Niveau d'expérience                                   | ExperienceLevel             |
| Date d'arrivée chez Alltech                           | hireDate                    |
| Présentation                                          | presentation                |
| Compétence                                            | Competence                  |
| Déclaration de compétence                             | CompetenceDeclaration       |
| Identifiant de compétence                             | competenceId                |
| Nom personnalisé de compétence                        | customName                  |
| Expertise                                             | expertise                   |
| Sujet sur lequel le collaborateur peut être sollicité | solicitable                 |
| Origine de la déclaration                             | source                      |
| Mission                                               | Assignment                  |
| Client                                                | Client                      |
| Rôle                                                  | Role                        |
| Journal d'audit                                       | AuditLog                    |

Ce glossaire sera enrichi progressivement au fur et à mesure de l'implémentation du modèle de données.

Les termes `Assignment`, `Client`, `Role` et `AuditLog` correspondent à des concepts envisagés dans le modèle
fonctionnel et technique global d'ECA.

Leur présence dans ce glossaire ne signifie pas que les classes ou collections correspondantes sont actuellement
implémentées dans le backend.

## Modélisation des déclarations de compétences

**Statut : Proposition technique retenue pour l'implémentation initiale — à réévaluer si les besoins évoluent.**

## Validation des données et gestion des références

**Statut : Décision technique retenue pour l'implémentation initiale.**

### Contexte

La persistance des documents MongoDB nécessite de garantir une cohérence minimale des données enregistrées.

Deux catégories de validation ont été distinguées :

- La validation structurelle des objets et de leurs données internes.
- La vérification de la cohérence des références entre les différents documents MongoDB.

Ces deux responsabilités ne nécessitent pas nécessairement d'être traitées au même niveau de l'application.

### Décision technique retenue

La validation structurelle des déclarations de compétences est effectuée dans le modèle `CompetenceDeclaration`, à
travers sa méthode `validate()`.

Cette validation garantit qu'une déclaration contient exactement l'un des deux éléments suivants :

- Un identifiant de compétence `competenceId`.
- Un nom personnalisé `customName`.

La méthode `Collaborator.validate()` parcourt les déclarations embarquées et déclenche leur validation individuelle.

L'exécution automatique de cette validation lors de la persistance est assurée par `CollaboratorValidationCallback`, qui
implémente `BeforeConvertCallback<Collaborator>`.

### Responsabilité de la vérification des références

La vérification de l'existence des documents référencés n'est pas intégrée au callback de persistance.

Les champs tels que `agencyId`, `professionId`, `experienceLevelId`, `competenceId` et `categoryId` restent des
identifiants simples de type `String`.

La vérification de leur correspondance avec des documents existants est différée à une future couche de services
applicatifs.

Cette séparation permet de conserver un callback de persistance simple, sans introduire de dépendances vers plusieurs
repositories pour effectuer des contrôles relevant de la logique applicative.

### Justification

Cette organisation permet :

- De centraliser la validation structurelle des déclarations de compétences.
- D'assurer son exécution avant la conversion des objets en documents MongoDB.
- De conserver une couche de persistance indépendante des règles de résolution des références.
- D'éviter d'introduire prématurément une logique métier dans les mécanismes de persistance.

### Limites et évolutions possibles

Le callback intervient uniquement lors des opérations passant par le mécanisme de conversion Spring Data MongoDB.

Il ne garantit pas la cohérence des données écrites directement dans MongoDB en dehors de l'application.

La vérification des références, ainsi que les règles métier associées aux catalogues et à leurs éléments, devront être
définies dans les futures couches applicatives.

Cette décision représente une organisation technique retenue pour l'implémentation initiale. Elle ne constitue pas une
validation officielle des règles métier par Alltech.

### Contexte

ECA doit permettre à chaque collaborateur de déclarer ses compétences, ses domaines d'expertise et les sujets sur
lesquels il accepte d'aider ses collègues.

Deux approches ont été étudiées pour stocker ces déclarations dans MongoDB :

- Une collection indépendante contenant les déclarations de compétences de chaque collaborateur.
- Un tableau de déclarations intégré directement dans le document du collaborateur.

### Décision technique provisoire

Les déclarations de compétences seront intégrées au document `Collaborator`, sous la forme d'un tableau d'objets
`CompetenceDeclaration`.

Le catalogue central des compétences restera indépendant dans la collection `competences`.

Chaque déclaration pourra ainsi :

- Référencer une compétence du catalogue via `competenceId`.
- Ou contenir un intitulé personnalisé via `customName`.
- Indiquer si le collaborateur considère cette compétence comme une expertise (`expertise`).
- Indiquer s'il accepte d'être sollicité sur ce sujet (`solicitable`).
- Conserver une information sur l'origine de la déclaration (`source`).

### Justification

Le volume envisagé est d'environ 2 000 collaborateurs, avec une estimation indicative de 10 déclarations par
collaborateur, soit environ 20 000 déclarations.

Ce volume reste modeste pour MongoDB. Les déclarations sont directement liées au profil d'un collaborateur et devraient
généralement être consultées avec celui-ci.

L'intégration dans le document `Collaborator` permet donc de conserver une structure simple et d'éviter une collection
supplémentaire ainsi que des opérations de rapprochement pour récupérer les déclarations d'un profil.

### Points de vigilance

Cette approche devra être réévaluée si :

- Le nombre de déclarations par collaborateur devient important ou non maîtrisé.
- Les déclarations nécessitent un cycle de vie ou un historique indépendant.
- Des besoins d'analyse ou de traitement indépendant apparaissent.
- Les performances observées montrent que cette organisation n'est plus adaptée.

Cette décision est un choix d'architecture pour la première version et ne constitue pas une exigence fonctionnelle
définitivement validée par Alltech.