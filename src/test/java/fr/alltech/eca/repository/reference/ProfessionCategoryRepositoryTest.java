package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.ProfessionCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository ProfessionCategory.
 * Vérifie la persistance et la récupération des catégories de professions
 * ainsi que la conservation de leurs informations dans MongoDB.
 */
@DataMongoTest
public class ProfessionCategoryRepositoryTest {

    @Autowired
    private ProfessionCategoryRepository professionCategoryRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        professionCategoryRepository.deleteAll();
    }

    /**
     * Vérifie qu'une catégorie de profession peut être enregistrée
     * puis récupérée avec son nom et sa description.
     */
    @Test
    void shouldSaveAndRetrieveProfessionCategory() {
        // Préparation : crée une catégorie de profession avec sa description métier.
        ProfessionCategory professionCategory = new ProfessionCategory(
                "Development",
                "Groups professions related to software development, including frontend and backend development, full-stack engineering, application design, software architecture, and maintenance. Covers the creation, evolution, and improvement of software solutions.");

        // Exécution : enregistre la catégorie dans MongoDB.
        ProfessionCategory savedCategory = professionCategoryRepository.save(professionCategory);

        // Vérification : confirme que MongoDB a attribué un identifiant à la catégorie.
        assertThat(savedCategory.getId()).isNotNull();

        // Récupération : recherche la catégorie enregistrée à partir de son identifiant.
        Optional<ProfessionCategory> foundCategory = professionCategoryRepository.findById(savedCategory.getId());

        // Vérification : confirme que la catégorie a bien été retrouvée.
        assertThat(foundCategory).isPresent();

        ProfessionCategory retrievedCategory = foundCategory.orElseThrow();

        // Vérification : compare les informations récupérées avec celles enregistrées.
        assertThat(retrievedCategory.getName()).isEqualTo(savedCategory.getName());
        assertThat(retrievedCategory.getDescription()).isEqualTo(savedCategory.getDescription());
    }
}