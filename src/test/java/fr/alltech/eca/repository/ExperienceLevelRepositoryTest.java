package fr.alltech.eca.repository;

import fr.alltech.eca.model.ExperienceLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ExperienceLevelRepositoryTest {

    @Autowired
    private ExperienceLevelRepository experienceLevelRepository;

    @AfterEach
    void tearDown() {
        experienceLevelRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveExperienceLevel() {
        // Given
        ExperienceLevel experienceLevel = new ExperienceLevel("Junior");

        // When
        ExperienceLevel savedExperienceLevel =
                experienceLevelRepository.save(experienceLevel);

        Optional<ExperienceLevel> retrievedExperienceLevel =
                experienceLevelRepository.findById(savedExperienceLevel.getId());

        // Then
        assertTrue(retrievedExperienceLevel.isPresent());
        assertNotNull(savedExperienceLevel.getId());
        assertEquals("Junior", retrievedExperienceLevel.get().getName());
    }
}