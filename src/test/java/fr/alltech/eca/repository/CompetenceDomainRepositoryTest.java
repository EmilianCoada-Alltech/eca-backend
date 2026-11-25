package fr.alltech.eca.repository;

import fr.alltech.eca.model.CompetenceDomain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CompetenceDomainRepositoryTest {

    @Autowired
    private CompetenceDomainRepository competenceDomainRepository;

    @Test
    void shouldSaveAndRetrieveCompetenceDomain() {
        // Arrange: create a competence-domain association
        CompetenceDomain competenceDomain = new CompetenceDomain(
                "competence-123",
                "Backend development"
        );

        // Act: save the association
        CompetenceDomain savedCompetenceDomain =
                competenceDomainRepository.save(competenceDomain);

        // Retrieve the saved association
        Optional<CompetenceDomain> result =
                competenceDomainRepository.findById(savedCompetenceDomain.getId());

        // Assert: verify the result
        assertThat(savedCompetenceDomain.getId()).isNotNull();
        assertThat(result).isPresent();

        CompetenceDomain retrievedCompetenceDomain = result.orElseThrow();

        assertThat(retrievedCompetenceDomain.getCompetenceId())
                .isEqualTo("competence-123");
        assertThat(retrievedCompetenceDomain.getDomainId())
                .isEqualTo("Backend development");
    }

    @AfterEach
    void cleanUp() {
        competenceDomainRepository.deleteAll();
    }
}