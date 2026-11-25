
package fr.alltech.eca.repository;

import fr.alltech.eca.model.CollaboratorRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CollaboratorRoleRepositoryTest {

    @Autowired
    private CollaboratorRoleRepository collaboratorRoleRepository;

    @AfterEach
    void tearDown() {
        collaboratorRoleRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveCollaboratorRole() {
        // Given
        CollaboratorRole collaboratorRole = new CollaboratorRole(
                "collaborator-role-123",
                "collaborator-123",
                "role-123"
        );

        // When
        CollaboratorRole savedCollaboratorRole =
                collaboratorRoleRepository.save(collaboratorRole);

        // Then
        assertThat(savedCollaboratorRole.getId())
                .isEqualTo("collaborator-role-123");

        assertThat(collaboratorRoleRepository.findById(
                "collaborator-role-123"
        ))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getCollaboratorId())
                            .isEqualTo("collaborator-123");
                    assertThat(found.getRoleId())
                            .isEqualTo("role-123");
                });
    }
}
