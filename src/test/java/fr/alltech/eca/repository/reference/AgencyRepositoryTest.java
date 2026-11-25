package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Agency;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class AgencyRepositoryTest {

    @Autowired
    private AgencyRepository agencyRepository;

    @AfterEach
    void cleanUp() {
        agencyRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveAgency() {

        // Arrange: create an agency
        Agency agency = new Agency(
                "Alltech Niort",
                "Niort",
                "France"
        );

        // Act: save the agency
        Agency savedAgency = agencyRepository.save(agency);

        // Retrieve the saved agency
        Optional<Agency> result =
                agencyRepository.findById(savedAgency.getId());

        // Assert: verify the result
        assertThat(savedAgency.getId()).isNotNull();
        assertThat(result).isPresent();

        Agency retrievedAgency = result.orElseThrow();

        assertThat(retrievedAgency.getName())
                .isEqualTo("Alltech Niort");

        assertThat(retrievedAgency.getCity())
                .isEqualTo("Niort");

        assertThat(retrievedAgency.getCountry())
                .isEqualTo("France");
    }
}
