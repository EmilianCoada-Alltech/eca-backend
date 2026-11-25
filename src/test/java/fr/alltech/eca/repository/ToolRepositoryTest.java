package fr.alltech.eca.repository;

import fr.alltech.eca.model.Tool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ToolRepositoryTest {

    @Autowired
    private ToolRepository toolRepository;

    @AfterEach
    void cleanUp() {
        toolRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveTool() {
        // Given
        Tool tool = new Tool(
                "tool-123",
                "Confluence",
                "Internal documentation platform",
                "https://example.com",
                "category-123",
                true
        );

        // When
        Tool savedTool = toolRepository.save(tool);

        // Then
        assertThat(savedTool.getId()).isEqualTo("tool-123");

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
