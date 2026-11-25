package fr.alltech.eca.repository.mission;

import fr.alltech.eca.model.mission.Mission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class MissionRepositoryTest {

    @Autowired
    private MissionRepository missionRepository;

    @AfterEach
    void cleanUp() {
        missionRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveMission() {
        // Arrange: create a mission
        Mission mission = new Mission(
                "ECA Backend Development",
                "client-123",
                LocalDate.of(2026, 11, 23),
                LocalDate.of(2027, 1, 15),
                "Development of the ECA backend application",
                true
        );

        // Act: save the mission
        Mission savedMission = missionRepository.save(mission);

        // Retrieve the saved mission
        Optional<Mission> result =
                missionRepository.findById(savedMission.getId());

        // Assert: verify the result
        assertThat(savedMission.getId()).isNotNull();
        assertThat(result).isPresent();

        Mission retrievedMission = result.orElseThrow();

        assertThat(retrievedMission.getName())
                .isEqualTo("ECA Backend Development");
        assertThat(retrievedMission.getClientId())
                .isEqualTo("client-123");
        assertThat(retrievedMission.getStartDate())
                .isEqualTo(LocalDate.of(2026, 11, 23));
        assertThat(retrievedMission.getEndDate())
                .isEqualTo(LocalDate.of(2027, 1, 15));
        assertThat(retrievedMission.getDescription())
                .isEqualTo("Development of the ECA backend application");
        assertThat(retrievedMission.isVisible()).isTrue();
    }
}