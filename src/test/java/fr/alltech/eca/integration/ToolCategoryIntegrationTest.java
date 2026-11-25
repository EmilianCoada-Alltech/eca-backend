
package fr.alltech.eca.integration;

import fr.alltech.eca.model.Tool;
import fr.alltech.eca.model.ToolCategory;
import fr.alltech.eca.repository.ToolCategoryRepository;
import fr.alltech.eca.repository.ToolRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ToolCategoryIntegrationTest {

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private ToolCategoryRepository toolCategoryRepository;

    @AfterEach
    void tearDown() {
        toolRepository.deleteAll();
        toolCategoryRepository.deleteAll();
    }

    @Test
    void shouldAssociateToolWithExistingCategory() {
        // Given
        ToolCategory category = toolCategoryRepository.save(
                new ToolCategory(
                        null,
                        "Communication",
                        "Tools used for internal communication"
                )
        );

        Tool tool = new Tool(
                null,
                "Microsoft Teams",
                "Internal communication and collaboration platform",
                "https://teams.microsoft.com",
                category.getId(),
                true
        );

        // When
        Tool savedTool = toolRepository.save(tool);

        // Then
        assertThat(toolRepository.findById(savedTool.getId()))
                .isPresent()
                .get()
                .satisfies(foundTool -> {
                    assertThat(foundTool.getCategoryId())
                            .isEqualTo(category.getId());
                    assertThat(foundTool.getName())
                            .isEqualTo("Microsoft Teams");
                });

        assertThat(toolCategoryRepository.findById(category.getId()))
                .isPresent()
                .get()
                .satisfies(foundCategory -> {
                    assertThat(foundCategory.getName())
                            .isEqualTo("Communication");
                });
    }
}
