
package fr.alltech.eca.repository;

import fr.alltech.eca.model.Domain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class DomainRepositoryTest {

    @Autowired
    private DomainRepository domainRepository;

    @Test
    void shouldSaveAndRetrieveDomain() {

        // Arrange: create a domain
        Domain domain = new Domain(
                "Backend Development",
                "Competences related to backend development",
                true
        );

        // Act: save the domain
        Domain savedDomain = domainRepository.save(domain);

        // Retrieve the saved domain
        Optional<Domain> result =
                domainRepository.findById(savedDomain.getId());

        // Assert: verify the result
        assertThat(savedDomain.getId()).isNotNull();
        assertThat(result).isPresent();

        Domain retrievedDomain = result.orElseThrow();

        assertThat(retrievedDomain.getName())
                .isEqualTo("Backend Development");

        assertThat(retrievedDomain.getDescription())
                .isEqualTo("Competences related to backend development");

        assertThat(retrievedDomain.isActive()).isTrue();
    }

    @AfterEach
    void cleanUp() {
        domainRepository.deleteAll();
    }
}
