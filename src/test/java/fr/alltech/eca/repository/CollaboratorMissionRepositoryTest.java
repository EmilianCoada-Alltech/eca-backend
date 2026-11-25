package fr.alltech.eca.repository;

import fr.alltech.eca.model.CollaboratorMission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CollaboratorMissionRepositoryTest {

    @Autowired
    private CollaboratorMissionRepository collaboratorMissionRepository;

    @AfterEach
    void cleanUp() {
        collaboratorMissionRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveCollaboratorMission() {
        // Arrange: create a collaborator-mission assignment
        CollaboratorMission collaboratorMission = new CollaboratorMission(
                "collaborator-123",
                "mission-456",
                LocalDate.of(2026, 11, 23),
                null
        );

        // Act: save the assignment
        CollaboratorMission savedCollaboratorMission =
                collaboratorMissionRepository.save(collaboratorMission);

        // Retrieve the saved assignment
        Optional<CollaboratorMission> result =
                collaboratorMissionRepository.findById(savedCollaboratorMission.getId());

        // Assert: verify the result
        assertThat(savedCollaboratorMission.getId()).isNotNull();
        assertThat(result).isPresent();

        CollaboratorMission retrievedCollaboratorMission = result.orElseThrow();

        assertThat(retrievedCollaboratorMission.getCollaboratorId())
                .isEqualTo("collaborator-123");
        assertThat(retrievedCollaboratorMission.getMissionId())
                .isEqualTo("mission-456");
        assertThat(retrievedCollaboratorMission.getStartDate())
                .isEqualTo(LocalDate.of(2026, 11, 23));
        assertThat(retrievedCollaboratorMission.getEndDate()).isNull();
    }
}