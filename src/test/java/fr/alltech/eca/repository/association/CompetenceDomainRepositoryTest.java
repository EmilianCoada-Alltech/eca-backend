package fr.alltech.eca.repository.association;

import fr.alltech.eca.model.association.CompetenceDomain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository CompetenceDomain.
 * Vérifie la persistance et la récupération des associations
 * entre compétences et domaines dans MongoDB.
 */
@DataMongoTest
class CompetenceDomainRepositoryTest {

    @Autowired
    private CompetenceDomainRepository competenceDomainRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        competenceDomainRepository.deleteAll();
    }

    /**
     * Vérifie qu'une association entre une compétence et un domaine
     * peut être enregistrée puis récupérée avec ses informations intactes.
     */
    @Test
    void shouldSaveAndRetrieveCompetenceDomain() {
        // Préparation : crée une association entre une compétence et un domaine.
        CompetenceDomain competenceDomain = new CompetenceDomain(
                "competence-123",
                "Backend development"
        );

        // Exécution : enregistre l'association dans MongoDB.
        CompetenceDomain savedCompetenceDomain =
                competenceDomainRepository.save(competenceDomain);

        // Récupération : recherche l'association enregistrée à partir de son identifiant.
        Optional<CompetenceDomain> result =
                competenceDomainRepository.findById(savedCompetenceDomain.getId());

        // Vérification : confirme que l'enregistrement possède un identifiant
        // et que le repository retourne bien le document.
        assertThat(savedCompetenceDomain.getId()).isNotNull();
        assertThat(result).isPresent();

        CompetenceDomain retrievedCompetenceDomain = result.orElseThrow();

        // Vérification : contrôle les références vers la compétence et le domaine.
        assertThat(retrievedCompetenceDomain.getCompetenceId())
                .isEqualTo("competence-123");
        assertThat(retrievedCompetenceDomain.getDomainId())
                .isEqualTo("Backend development");
    }
}