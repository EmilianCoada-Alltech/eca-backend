package fr.alltech.eca.integration.collaborator;

import fr.alltech.eca.common.enums.CompetenceSource;
import fr.alltech.eca.model.collaborator.Collaborator;
import fr.alltech.eca.model.collaborator.CompetenceDeclaration;
import fr.alltech.eca.model.reference.Competence;
import fr.alltech.eca.repository.collaborator.CollaboratorRepository;
import fr.alltech.eca.repository.reference.CompetenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test d'intégration des déclarations de compétences d'un collaborateur.
 * Vérifie la persistance d'une compétence intégrée au document du collaborateur
 * ainsi que la référence vers la compétence du catalogue.
 */
@DataMongoTest
class CollaboratorCompetenceIntegrationTest {

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    /**
     * Vérifie qu'un collaborateur peut déclarer une compétence issue du catalogue
     * et que cette déclaration est correctement enregistrée avec son profil.
     */
    @Test
    void shouldAssociateCompetenceWithCollaborator() {

        // Préparation : crée et enregistre une compétence dans le catalogue.
        Competence competence = new Competence(
                "Spring Boot",
                "Java framework for building applications",
                true
        );

        Competence savedCompetence =
                competenceRepository.save(competence);

        // Préparation : crée une déclaration utilisant l'identifiant
        // de la compétence précédemment enregistrée.
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                savedCompetence.getId(),
                null,
                true,
                true,
                CompetenceSource.CATALOGUE
        );

        // Préparation : crée un collaborateur et lui associe la déclaration.
        Collaborator collaborator = new Collaborator(
                "test-ad-integration",
                "John",
                "Doe",
                "john.doe@example.test",
                "agency-test-id"
        );

        // La déclaration est intégrée à la liste des compétences du collaborateur.
        collaborator.getCompetences().add(declaration);

        // Exécution : enregistre le collaborateur avec sa déclaration intégrée.
        Collaborator savedCollaborator =
                collaboratorRepository.save(collaborator);

        // Récupère le collaborateur enregistré à partir de son identifiant.
        Optional<Collaborator> result =
                collaboratorRepository.findById(savedCollaborator.getId());

        // Vérification : confirme que le collaborateur a bien été enregistré.
        assertThat(result).isPresent();

        Collaborator retrievedCollaborator = result.orElseThrow();

        // Vérifie que le collaborateur possède une seule déclaration de compétence.
        assertThat(retrievedCollaborator.getCompetences()).hasSize(1);

        CompetenceDeclaration retrievedDeclaration =
                retrievedCollaborator.getCompetences().getFirst();

        // Vérifie que la déclaration conserve la référence vers le catalogue
        // ainsi que les indicateurs d'expertise et de disponibilité.
        assertThat(retrievedDeclaration.getCompetenceId())
                .isEqualTo(savedCompetence.getId());

        assertThat(retrievedDeclaration.isExpertise()).isTrue();
        assertThat(retrievedDeclaration.isSolicitable()).isTrue();

        // Vérifie que la compétence référencée existe toujours dans le catalogue.
        Optional<Competence> retrievedCompetence =
                competenceRepository.findById(
                        retrievedDeclaration.getCompetenceId()
                );

        assertThat(retrievedCompetence).isPresent();
        assertThat(retrievedCompetence.orElseThrow().getName())
                .isEqualTo("Spring Boot");

        // Nettoyage : supprime les deux documents créés pendant le test.
        collaboratorRepository.deleteById(savedCollaborator.getId());
        competenceRepository.deleteById(savedCompetence.getId());
    }
}