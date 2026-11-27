package fr.alltech.eca.integration.reference;

import fr.alltech.eca.model.reference.Profession;
import fr.alltech.eca.model.reference.ProfessionCategory;
import fr.alltech.eca.repository.reference.ProfessionCategoryRepository;
import fr.alltech.eca.repository.reference.ProfessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test d'intégration de l'association entre un métier et sa catégorie.
 * Vérifie la persistance de la référence vers la catégorie et la récupération
 * du document correspondant.
 */
@DataMongoTest
public class ProfessionCategoryIntegrationTest {

    @Autowired
    private ProfessionRepository professionRepository;

    @Autowired
    private ProfessionCategoryRepository professionCategoryRepository;

    /**
     * Vérifie qu'un métier peut être associé à une catégorie existante
     * et que cette association est correctement conservée en base de données.
     */
    @Test
    void shouldAssociateProfessionWithCategory() {

        // Préparation : crée et enregistre une catégorie de métiers.
        ProfessionCategory category = new ProfessionCategory(
                "Development",
                "Groups professions related to software development, including frontend and backend development, full-stack engineering, application design, software architecture, and maintenance. Covers the creation, evolution, and improvement of software solutions.");

        ProfessionCategory savedCategory = professionCategoryRepository.save(category);

        // Préparation : crée un métier en utilisant l'identifiant
        // de la catégorie précédemment enregistrée.
        Profession profession = new Profession(
                "Backend developer",
                "Creates secure and scalable backend services, manages data persistence, develops RESTful APIs, and ensures system reliability through automated testing and monitoring.",
                true,
                savedCategory.getId());

        Profession savedProfession = professionRepository.save(profession);

        // Exécution : récupère le métier à partir de son identifiant.
        Optional<Profession> retrievedProfession =
                professionRepository.findById(savedProfession.getId());

        // Vérification : confirme que le métier a bien été enregistré.
        assertThat(retrievedProfession).isPresent();

        Profession foundProfession = retrievedProfession.orElseThrow();

        // Exécution : récupère la catégorie référencée par le métier.
        Optional<ProfessionCategory> retrievedCategory =
                professionCategoryRepository.findById(foundProfession.getCategoryId());

        // Vérification : confirme que la catégorie référencée existe.
        assertThat(retrievedCategory).isPresent();

        ProfessionCategory foundCategory = retrievedCategory.orElseThrow();

        // Vérifie que l'identifiant de catégorie du métier correspond
        // bien à celui du document de catégorie récupéré.
        assertThat(foundProfession.getCategoryId())
                .isEqualTo(foundCategory.getId());

        assertThat(foundCategory.getName())
                .isEqualTo("Development");

        // Nettoyage : supprime les documents créés pendant le test.
        professionRepository.deleteById(savedProfession.getId());
        professionCategoryRepository.deleteById(savedCategory.getId());
    }
}