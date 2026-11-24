## Convention linguistique et nomenclature technique

### Choix retenu

Le projet ECA adopte l'anglais comme langue de référence pour l'ensemble des éléments techniques du code et de la base de données.

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

Ce choix concerne exclusivement la nomenclature technique. Il ne modifie pas la langue de travail documentaire ni la terminologie fonctionnelle utilisée par Alltech.

### Correspondance terminologique français / anglais

Afin de faciliter la compréhension du modèle de données par les collaborateurs ne souhaitant pas utiliser l'anglais dans leur lecture technique, un glossaire de correspondance est maintenu.

| Terminologie métier (FR) | Nomenclature technique (EN) |
|---|---|
| Collaborateur | Collaborator |
| Identifiant Active Directory | adIdentifier |
| Prénom | firstName |
| Nom | lastName |
| Agence | Agency |
| Métier | Profession |
| Niveau d'expérience | ExperienceLevel |
| Date d'arrivée chez Alltech | hireDate |
| Présentation | biography |
| Compétence | Skill |
| Expertise | Expertise |
| Sujet sur lequel le collaborateur peut être sollicité | SolicitableTopic |
| Mission | Assignment |
| Client | Client |
| Rôle | Role |
| Journal d'audit | AuditLog |

Ce glossaire sera enrichi progressivement au fur et à mesure de l'implémentation du modèle de données.