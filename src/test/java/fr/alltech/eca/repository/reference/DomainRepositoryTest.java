package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Domain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository Domain.
 * Vérifie la persistance et la récupération des domaines
 * ainsi que la conservation de leurs informations dans MongoDB.
 */
@DataMongoTest
class DomainRepositoryTest {

    @Autowired
    private DomainRepository domainRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        domainRepository.deleteAll();
    }

    /**
     * Vérifie qu'un domaine peut être enregistré puis récupéré
     * avec son nom, sa description et son statut d'activité.
     */
    @Test
    void shouldSaveAndRetrieveDomain() {

        // Préparation : crée un domaine avec ses informations métier.
        Domain domain = new Domain(
                "Backend Development",
                "Competences related to backend development",
                true
        );

        // Exécution : enregistre le domaine dans MongoDB.
        Domain savedDomain = domainRepository.save(domain);

        // Récupération : recherche le domaine enregistré à partir de son identifiant.
        Optional<Domain> result =
                domainRepository.findById(savedDomain.getId());

        // Vérification : confirme que l'enregistrement possède un identifiant
        // et que le repository retourne bien le domaine.
        assertThat(savedDomain.getId()).isNotNull();
        assertThat(result).isPresent();

        Domain retrievedDomain = result.orElseThrow();

        // Vérification : contrôle les informations du domaine récupéré.
        assertThat(retrievedDomain.getName())
                .isEqualTo("Backend Development");

        assertThat(retrievedDomain.getDescription())
                .isEqualTo("Competences related to backend development");

        assertThat(retrievedDomain.isActive()).isTrue();
    }
}