package fr.alltech.eca.repository;

import fr.alltech.eca.model.ToolCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ToolCategoryRepositoryTest {

    @Autowired
    private ToolCategoryRepository ToolCategoryRepository;

    @AfterEach
    void cleanUp() {
        ToolCategoryRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveToolCategory() {
        // Given
        ToolCategory category = new ToolCategory(
                "category-123",
                "Communication",
                "Tools used for internal communication"
        );

        // When
        ToolCategory savedCategory =
                ToolCategoryRepository.save(category);

        // Then
        assertThat(savedCategory.getId()).isEqualTo("category-123");

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
