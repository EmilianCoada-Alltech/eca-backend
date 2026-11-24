### Identifiant des agences

Le modèle initial prévoyait un attribut `code` pour les agences, correspondant notamment à d'anciens codes départementaux.

Cet attribut a été écarté provisoirement du modèle ECA, car son utilité fonctionnelle n'est pas établie.

**Point à confirmer avec Alltech :** les informations d'agence provenant de l'Active Directory nécessitent-elles un identifiant métier spécifique pour être associées aux enregistrements de la collection `agencies`, ou une correspondance basée sur les informations existantes (nom, localisation ou autre attribut) est-elle suffisante ?

### Déclarations de compétences des collaborateurs

Les déclarations de compétences sont intégrées au document `Collaborator` sous la forme d'un tableau d'objets `CompetenceDeclaration`.

Chaque déclaration comporte les attributs suivants :

| Attribut | Description |
|---|---|
| `competenceId` | Référence facultative à une compétence du catalogue central |
| `customName` | Intitulé personnalisé lorsque la compétence n'existe pas dans le catalogue |
| `expertise` | Indique si le collaborateur déclare cette compétence comme une expertise |
| `solicitable` | Indique si le collaborateur accepte d'être sollicité sur ce sujet |
| `source` | Origine de la déclaration |

Une déclaration peut donc être associée à une compétence du catalogue ou correspondre à un sujet personnalisé.

L'identifiant du collaborateur n'est pas nécessaire dans `CompetenceDeclaration`, car l'appartenance est déterminée par le document `Collaborator` qui contient le tableau.

**Organisation MongoDB proposée :**
- `collaborators` : profils des collaborateurs et leurs déclarations de compétences intégrées.
- `competences` : catalogue central des compétences.

Cette organisation est une proposition technique initiale, susceptible d'évoluer selon les besoins fonctionnels et les validations à venir.