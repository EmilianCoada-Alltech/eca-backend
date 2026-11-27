package fr.alltech.eca.repository.tool;

import fr.alltech.eca.model.tool.Tool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository Tool.
 * Vérifie la persistance et la récupération des outils
 * ainsi que la conservation de leurs informations dans MongoDB.
 */
@DataMongoTest
class ToolRepositoryTest {

    @Autowired
    private ToolRepository toolRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        toolRepository.deleteAll();
    }

    /**
     * Vérifie qu'un outil peut être enregistré puis récupéré
     * avec son identifiant, son nom, sa description, son URL,
     * sa catégorie et son statut d'activité.
     */
    @Test
    void shouldSaveAndRetrieveTool() {
        // Préparation : crée un outil avec ses informations métier.
        Tool tool = new Tool(
                "tool-123",
                "Confluence",
                "Internal documentation platform",
                "https://example.com",
                "category-123",
                true
        );

        // Exécution : enregistre l'outil dans MongoDB.
        Tool savedTool = toolRepository.save(tool);

        // Vérification : confirme que l'identifiant de l'outil est conservé.
        assertThat(savedTool.getId()).isEqualTo("tool-123");

        // Récupération et vérification : recherche l'outil par son identifiant
        // et contrôle les informations enregistrées.
        assertThat(toolRepository.findById("tool-123"))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getName()).isEqualTo("Confluence");
                    assertThat(found.getDescription())
                            .isEqualTo("Internal documentation platform");
                    assertThat(found.getUrl()).isEqualTo("https://example.com");
                    assertThat(found.getCategoryId()).isEqualTo("category-123");
                    assertThat(found.isActive()).isTrue();
                });
    }
}