package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Competence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository Competence.
 * Vérifie la persistance et la récupération des compétences
 * ainsi que la conservation de leurs informations dans MongoDB.
 */
@DataMongoTest
class CompetenceRepositoryTest {

    @Autowired
    private CompetenceRepository competenceRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        competenceRepository.deleteAll();
    }

    /**
     * Vérifie qu'une compétence peut être enregistrée puis récupérée
     * avec son nom, sa description et son statut d'activité.
     */
    @Test
    void shouldSaveAndRetrieveCompetence() {

        // Préparation : crée une compétence avec ses informations métier.
        Competence competence = new Competence(
                "Spring Boot",
                "Java framework for building applications",
                true
        );

        // Exécution : enregistre la compétence dans MongoDB.
        Competence savedCompetence =
                competenceRepository.save(competence);

        // Récupération : recherche la compétence enregistrée à partir de son identifiant.
        Optional<Competence> result =
                competenceRepository.findById(savedCompetence.getId());

        // Vérification : confirme que l'enregistrement possède un identifiant
        // et que le repository retourne bien la compétence.
        assertThat(savedCompetence.getId()).isNotNull();
        assertThat(result).isPresent();

        Competence retrievedCompetence = result.orElseThrow();

        // Vérification : contrôle les informations de la compétence récupérée.
        assertThat(retrievedCompetence.getName())
                .isEqualTo("Spring Boot");

        assertThat(retrievedCompetence.getDescription())
                .isEqualTo("Java framework for building applications");

        assertThat(retrievedCompetence.isActive()).isTrue();
    }
}