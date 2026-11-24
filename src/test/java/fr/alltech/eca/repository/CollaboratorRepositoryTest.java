
package fr.alltech.eca.repository;

import fr.alltech.eca.model.Collaborator;
import fr.alltech.eca.model.CompetenceDeclaration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CollaboratorRepositoryTest {

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Test
    void shouldSaveAndRetrieveCollaborator() {

        // Arrange: create a collaborator
        Collaborator collaborator = new Collaborator(
                "test-ad-" + UUID.randomUUID(),
                "John",
                "Doe",
                "john.doe@example.test",
                "agency-test-id"
        );

        // Add an embedded competence declaration
        CompetenceDeclaration competence = new CompetenceDeclaration(
                "spring-boot-test-id",
                null,
                true,
                true,
                "catalogue"
        );

        collaborator.getCompetences().add(competence);

        // Act: save the collaborator
        Collaborator savedCollaborator =
                collaboratorRepository.save(collaborator);

        // Retrieve the saved collaborator
        Optional<Collaborator> result =
                collaboratorRepository.findById(savedCollaborator.getId());

        // Assert: verify the result
        assertThat(savedCollaborator.getId()).isNotNull();
        assertThat(result).isPresent();

        Collaborator retrievedCollaborator = result.orElseThrow();

        assertThat(retrievedCollaborator.getFirstName()).isEqualTo("John");
        assertThat(retrievedCollaborator.getLastName()).isEqualTo("Doe");
        assertThat(retrievedCollaborator.getEmail())
                .isEqualTo("john.doe@example.test");

        // Verify the embedded competence declaration
        assertThat(retrievedCollaborator.getCompetences()).hasSize(1);
        assertThat(retrievedCollaborator.getCompetences().getFirst().getCompetenceId())
                .isEqualTo("spring-boot-test-id");
        assertThat(retrievedCollaborator.getCompetences().getFirst().isExpertise())
                .isTrue();

        // Clean up the test document
        collaboratorRepository.deleteById(savedCollaborator.getId());
    }
}
