package fr.alltech.eca.repository;

import fr.alltech.eca.model.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @AfterEach
    void cleanUp() {
        clientRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveClient() {
        // Arrange: create a client
        Client client = new Client(
                "Acme Corporation",
                "10 Rue de la Paix",
                "75002",
                "Paris",
                "France",
                "+33123456789",
                "contact@acme.example",
                true
        );

        // Act: save the client
        Client savedClient = clientRepository.save(client);

        // Retrieve the saved client
        Optional<Client> result =
                clientRepository.findById(savedClient.getId());

        // Assert: verify the result
        assertThat(savedClient.getId()).isNotNull();
        assertThat(result).isPresent();

        Client retrievedClient = result.orElseThrow();

        assertThat(retrievedClient.getName()).isEqualTo("Acme Corporation");
        assertThat(retrievedClient.getStreetAddress()).isEqualTo("10 Rue de la Paix");
        assertThat(retrievedClient.getPostalCode()).isEqualTo("75002");
        assertThat(retrievedClient.getCity()).isEqualTo("Paris");
        assertThat(retrievedClient.getCountry()).isEqualTo("France");
        assertThat(retrievedClient.getPhoneNumber()).isEqualTo("+33123456789");
        assertThat(retrievedClient.getEmail()).isEqualTo("contact@acme.example");
        assertThat(retrievedClient.isVisible()).isTrue();
    }
}