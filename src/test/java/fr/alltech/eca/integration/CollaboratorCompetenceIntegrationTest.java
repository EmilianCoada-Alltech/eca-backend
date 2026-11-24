
package fr.alltech.eca.integration;

import fr.alltech.eca.model.Collaborator;
import fr.alltech.eca.model.Competence;
import fr.alltech.eca.model.CompetenceDeclaration;
import fr.alltech.eca.repository.CollaboratorRepository;
import fr.alltech.eca.repository.CompetenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CollaboratorCompetenceIntegrationTest {

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Test
    void shouldAssociateCompetenceWithCollaborator() {

        // Arrange: create and save a competence in the catalogue
        Competence competence = new Competence(
                "Spring Boot",
                "Java framework for building applications",
                true
        );

        Competence savedCompetence =
                competenceRepository.save(competence);

        // Arrange: create a declaration using the generated competence ID
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                savedCompetence.getId(),
                null,
                true,
                true,
                "catalogue"
        );

        // Create a collaborator
        Collaborator collaborator = new Collaborator(
                "test-ad-integration",
                "John",
                "Doe",
                "john.doe@example.test",
                "agency-test-id"
        );

        collaborator.getCompetences().add(declaration);

        // Act: save the collaborator
        Collaborator savedCollaborator =
                collaboratorRepository.save(collaborator);

        // Retrieve the collaborator
        Optional<Collaborator> result =
                collaboratorRepository.findById(savedCollaborator.getId());

        // Assert: verify the collaborator and its declaration
        assertThat(result).isPresent();

        Collaborator retrievedCollaborator = result.orElseThrow();

        assertThat(retrievedCollaborator.getCompetences()).hasSize(1);

        CompetenceDeclaration retrievedDeclaration =
                retrievedCollaborator.getCompetences().getFirst();

        assertThat(retrievedDeclaration.getCompetenceId())
                .isEqualTo(savedCompetence.getId());

        assertThat(retrievedDeclaration.isExpertise()).isTrue();
        assertThat(retrievedDeclaration.isSolicitable()).isTrue();

        // Verify that the referenced competence exists in the catalogue
        Optional<Competence> retrievedCompetence =
                competenceRepository.findById(
                        retrievedDeclaration.getCompetenceId()
                );

        assertThat(retrievedCompetence).isPresent();
        assertThat(retrievedCompetence.orElseThrow().getName())
                .isEqualTo("Spring Boot");

        // Clean up both documents
        collaboratorRepository.deleteById(savedCollaborator.getId());
        competenceRepository.deleteById(savedCompetence.getId());
    }
}
