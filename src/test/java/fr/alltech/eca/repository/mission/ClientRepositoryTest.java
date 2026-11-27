package fr.alltech.eca.repository.mission;

import fr.alltech.eca.model.mission.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository Client.
 * Vérifie la persistance et la récupération des informations
 * relatives aux clients dans MongoDB.
 */
@DataMongoTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        clientRepository.deleteAll();
    }

    /**
     * Vérifie qu'un client peut être enregistré puis récupéré
     * avec l'ensemble de ses informations intactes.
     */
    @Test
    void shouldSaveAndRetrieveClient() {
        // Préparation : crée un client avec ses coordonnées et son statut de visibilité.
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

        // Exécution : enregistre le client dans MongoDB.
        Client savedClient = clientRepository.save(client);

        // Récupération : recherche le client enregistré à partir de son identifiant.
        Optional<Client> result =
                clientRepository.findById(savedClient.getId());

        // Vérification : confirme que l'enregistrement possède un identifiant
        // et que le repository retourne bien le client.
        assertThat(savedClient.getId()).isNotNull();
        assertThat(result).isPresent();

        Client retrievedClient = result.orElseThrow();

        // Vérification : contrôle les coordonnées et les informations du client.
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