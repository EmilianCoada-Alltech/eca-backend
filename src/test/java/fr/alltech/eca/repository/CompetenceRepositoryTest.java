
package fr.alltech.eca.repository;

import fr.alltech.eca.model.Competence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CompetenceRepositoryTest {

    @Autowired
    private CompetenceRepository competenceRepository;

    @Test
    void shouldSaveAndRetrieveCompetence() {

        // Arrange: create a competence
        Competence competence = new Competence(
                "Spring Boot",
                "Java framework for building applications",
                true
        );

        // Act: save the competence
        Competence savedCompetence =
                competenceRepository.save(competence);

        // Retrieve the saved competence
        Optional<Competence> result =
                competenceRepository.findById(savedCompetence.getId());

        // Assert: verify the result
        assertThat(savedCompetence.getId()).isNotNull();
        assertThat(result).isPresent();

        Competence retrievedCompetence = result.orElseThrow();

        assertThat(retrievedCompetence.getName())
                .isEqualTo("Spring Boot");

        assertThat(retrievedCompetence.getDescription())
                .isEqualTo("Java framework for building applications");

        assertThat(retrievedCompetence.isActive()).isTrue();

        // Clean up the test document
        competenceRepository.deleteById(savedCompetence.getId());
    }
}
