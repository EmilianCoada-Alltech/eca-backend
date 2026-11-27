package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Agency;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository Agency.
 * Vérifie la persistance et la récupération des informations
 * relatives aux agences dans MongoDB.
 */
@DataMongoTest
class AgencyRepositoryTest {

    @Autowired
    private AgencyRepository agencyRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        agencyRepository.deleteAll();
    }

    /**
     * Vérifie qu'une agence peut être enregistrée puis récupérée
     * avec son nom, sa ville et son pays.
     */
    @Test
    void shouldSaveAndRetrieveAgency() {

        // Préparation : crée une agence avec ses informations géographiques.
        Agency agency = new Agency(
                "Alltech Niort",
                "Niort",
                "France"
        );

        // Exécution : enregistre l'agence dans MongoDB.
        Agency savedAgency = agencyRepository.save(agency);

        // Récupération : recherche l'agence enregistrée à partir de son identifiant.
        Optional<Agency> result =
                agencyRepository.findById(savedAgency.getId());

        // Vérification : confirme que l'enregistrement possède un identifiant
        // et que le repository retourne bien l'agence.
        assertThat(savedAgency.getId()).isNotNull();
        assertThat(result).isPresent();

        Agency retrievedAgency = result.orElseThrow();

        // Vérification : contrôle les informations de l'agence récupérée.
        assertThat(retrievedAgency.getName())
                .isEqualTo("Alltech Niort");

        assertThat(retrievedAgency.getCity())
                .isEqualTo("Niort");

        assertThat(retrievedAgency.getCountry())
                .isEqualTo("France");
    }
}