package fr.alltech.eca.repository;

import fr.alltech.eca.model.ProfessionCompetence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ProfessionCompetenceRepositoryTest {

    @Autowired
    private ProfessionCompetenceRepository professionCompetenceRepository;

    @AfterEach
    void cleanUp() {
        professionCompetenceRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveProfessionCompetence() {
        // Arrange: create a profession-competence association
        ProfessionCompetence professionCompetence = new ProfessionCompetence(
                "profession-123",
                "competence-456"
        );

        // Act: save the association
        ProfessionCompetence savedProfessionCompetence =
                professionCompetenceRepository.save(professionCompetence);

        // Retrieve the saved association
        Optional<ProfessionCompetence> result =
                professionCompetenceRepository.findById(savedProfessionCompetence.getId());

        // Assert: verify the result
        assertThat(savedProfessionCompetence.getId()).isNotNull();
        assertThat(result).isPresent();

        ProfessionCompetence retrievedProfessionCompetence = result.orElseThrow();

        assertThat(retrievedProfessionCompetence.getProfessionId())
                .isEqualTo("profession-123");
        assertThat(retrievedProfessionCompetence.getCompetenceId())
                .isEqualTo("competence-456");
    }
}