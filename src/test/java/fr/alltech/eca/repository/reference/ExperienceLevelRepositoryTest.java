package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.ExperienceLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration du repository ExperienceLevel.
 * Vérifie la persistance et la récupération des niveaux d'expérience
 * dans MongoDB.
 */
@DataMongoTest
class ExperienceLevelRepositoryTest {

    @Autowired
    private ExperienceLevelRepository experienceLevelRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        experienceLevelRepository.deleteAll();
    }

    /**
     * Vérifie qu'un niveau d'expérience peut être enregistré
     * puis récupéré avec son nom.
     */
    @Test
    void shouldSaveAndRetrieveExperienceLevel() {
        // Préparation : crée un niveau d'expérience.
        ExperienceLevel experienceLevel = new ExperienceLevel("Junior");

        // Exécution : enregistre le niveau dans MongoDB.
        ExperienceLevel savedExperienceLevel =
                experienceLevelRepository.save(experienceLevel);

        // Récupération : recherche le niveau enregistré à partir de son identifiant.
        Optional<ExperienceLevel> retrievedExperienceLevel =
                experienceLevelRepository.findById(savedExperienceLevel.getId());

        // Vérification : confirme que le niveau existe, possède un identifiant
        // et conserve le nom attendu.
        assertTrue(retrievedExperienceLevel.isPresent());
        assertNotNull(savedExperienceLevel.getId());
        assertEquals("Junior", retrievedExperienceLevel.get().getName());
    }
}