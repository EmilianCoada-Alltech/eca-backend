package fr.alltech.eca.repository.association;

import fr.alltech.eca.model.association.ProfessionCompetence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository ProfessionCompetence.
 * Vérifie la persistance et la récupération des associations
 * entre professions et compétences dans MongoDB.
 */
@DataMongoTest
class ProfessionCompetenceRepositoryTest {

    @Autowired
    private ProfessionCompetenceRepository professionCompetenceRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        professionCompetenceRepository.deleteAll();
    }

    /**
     * Vérifie qu'une association entre une profession et une compétence
     * peut être enregistrée puis récupérée avec ses informations intactes.
     */
    @Test
    void shouldSaveAndRetrieveProfessionCompetence() {
        // Préparation : crée une association entre une profession et une compétence.
        ProfessionCompetence professionCompetence = new ProfessionCompetence(
                "profession-123",
                "competence-456"
        );

        // Exécution : enregistre l'association dans MongoDB.
        ProfessionCompetence savedProfessionCompetence =
                professionCompetenceRepository.save(professionCompetence);

        // Récupération : recherche l'association enregistrée à partir de son identifiant.
        Optional<ProfessionCompetence> result =
                professionCompetenceRepository.findById(savedProfessionCompetence.getId());

        // Vérification : confirme que l'enregistrement possède un identifiant
        // et que le repository retourne bien le document.
        assertThat(savedProfessionCompetence.getId()).isNotNull();
        assertThat(result).isPresent();

        ProfessionCompetence retrievedProfessionCompetence = result.orElseThrow();

        // Vérification : contrôle les références vers la profession et la compétence.
        assertThat(retrievedProfessionCompetence.getProfessionId())
                .isEqualTo("profession-123");
        assertThat(retrievedProfessionCompetence.getCompetenceId())
                .isEqualTo("competence-456");
    }
}