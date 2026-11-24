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

## Modélisation des déclarations de compétences

**Statut : Proposition technique retenue pour l'implémentation initiale — à réévaluer si les besoins évoluent.**

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