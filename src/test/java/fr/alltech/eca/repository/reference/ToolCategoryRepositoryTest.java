package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.ToolCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository ToolCategory.
 * Vérifie la persistance et la récupération des catégories d'outils
 * ainsi que la conservation de leurs informations dans MongoDB.
 */
@DataMongoTest
class ToolCategoryRepositoryTest {

    @Autowired
    private ToolCategoryRepository ToolCategoryRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        ToolCategoryRepository.deleteAll();
    }

    /**
     * Vérifie qu'une catégorie d'outils peut être enregistrée puis récupérée
     * avec son identifiant, son nom et sa description.
     */
    @Test
    void shouldSaveAndRetrieveToolCategory() {
        // Préparation : crée une catégorie d'outils avec son identifiant et ses informations.
        ToolCategory category = new ToolCategory(
                "category-123",
                "Communication",
                "Tools used for internal communication"
        );

        // Exécution : enregistre la catégorie dans MongoDB.
        ToolCategory savedCategory =
                ToolCategoryRepository.save(category);

        // Vérification : confirme que l'identifiant de la catégorie est conservé.
        assertThat(savedCategory.getId()).isEqualTo("category-123");

        // Récupération et vérification : recherche la catégorie par son identifiant
        // et contrôle les informations enregistrées.
        assertThat(ToolCategoryRepository.findById("category-123"))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getName()).isEqualTo("Communication");
                    assertThat(found.getDescription())
                            .isEqualTo("Tools used for internal communication");
                });
    }
}