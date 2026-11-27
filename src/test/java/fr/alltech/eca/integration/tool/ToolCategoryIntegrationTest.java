package fr.alltech.eca.integration.tool;

import fr.alltech.eca.model.reference.ToolCategory;
import fr.alltech.eca.model.tool.Tool;
import fr.alltech.eca.repository.reference.ToolCategoryRepository;
import fr.alltech.eca.repository.tool.ToolRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test d'intégration de l'association entre un outil et sa catégorie.
 * Vérifie la persistance de l'outil ainsi que la référence vers sa catégorie.
 */
@SpringBootTest
class ToolCategoryIntegrationTest {

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private ToolCategoryRepository toolCategoryRepository;

    /**
     * Nettoie les collections utilisées par le test après chaque exécution
     * afin d'éviter que les données persistent entre les tests.
     */
    @AfterEach
    void tearDown() {
        toolRepository.deleteAll();
        toolCategoryRepository.deleteAll();
    }

    /**
     * Vérifie qu'un outil peut être associé à une catégorie existante
     * et que cette association est correctement conservée en base de données.
     */
    @Test
    void shouldAssociateToolWithExistingCategory() {
        // Préparation : crée et enregistre une catégorie d'outils.
        ToolCategory category = toolCategoryRepository.save(
                new ToolCategory(
                        null,
                        "Communication",
                        "Tools used for internal communication"
                )
        );

        // Préparation : crée un outil en référençant la catégorie enregistrée.
        Tool tool = new Tool(
                null,
                "Microsoft Teams",
                "Internal communication and collaboration platform",
                "https://teams.microsoft.com",
                category.getId(),
                true
        );

        // Exécution : enregistre l'outil dans la base de données.
        Tool savedTool = toolRepository.save(tool);

        // Vérification : confirme que l'outil existe et référence la bonne catégorie.
        assertThat(toolRepository.findById(savedTool.getId()))
                .isPresent()
                .get()
                .satisfies(foundTool -> {
                    assertThat(foundTool.getCategoryId())
                            .isEqualTo(category.getId());
                    assertThat(foundTool.getName())
                            .isEqualTo("Microsoft Teams");
                });

        // Vérification : confirme que la catégorie référencée existe
        // et que ses données correspondent à celles enregistrées.
        assertThat(toolCategoryRepository.findById(category.getId()))
                .isPresent()
                .get()
                .satisfies(foundCategory -> {
                    assertThat(foundCategory.getName())
                            .isEqualTo("Communication");
                });
    }
}