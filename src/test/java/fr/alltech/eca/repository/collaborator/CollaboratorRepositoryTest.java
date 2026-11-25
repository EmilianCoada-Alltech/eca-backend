
package fr.alltech.eca.repository.collaborator;

import fr.alltech.eca.config.CollaboratorValidationCallback;
import fr.alltech.eca.model.collaborator.Collaborator;
import fr.alltech.eca.model.collaborator.CompetenceDeclaration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import(CollaboratorValidationCallback.class)
class CollaboratorRepositoryTest {

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @AfterEach
    void cleanUp() {
        collaboratorRepository.deleteAll();
    }

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
    }

    @Test
    void shouldRejectCollaboratorWithInvalidCompetenceDeclaration() {

        Collaborator collaborator = new Collaborator(
                "test-ad-" + UUID.randomUUID(),
                "John",
                "Doe",
                "john.doe@example.test",
                "agency-test-id"
        );

        CompetenceDeclaration competence = new CompetenceDeclaration(
                null,
                null,
                true,
                true,
                "catalogue"
        );

        collaborator.getCompetences().add(competence);

        assertThatThrownBy(() -> {
            collaboratorRepository.save(collaborator);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThat(collaboratorRepository.count()).isZero();
    }

    @Test
    void shouldSaveAndRetrieveCollaboratorWithRoles() {
        // Given
        Collaborator collaborator = new Collaborator(
                "AD123",
                "John",
                "Doe",
                "john.doe@example.com",
                "agency-123"
        );

        collaborator.setRoleIds(List.of(
                "COLLABORATEUR",
                "REDACTEUR"
        ));

        // When
        Collaborator savedCollaborator =
                collaboratorRepository.save(collaborator);

        // Then
        assertThat(collaboratorRepository.findById(savedCollaborator.getId()))
                .isPresent()
                .get()
                .satisfies(found -> assertThat(found.getRoleIds())
                        .containsExactly(
                                "COLLABORATEUR",
                                "REDACTEUR"
                        ));
    }
}
