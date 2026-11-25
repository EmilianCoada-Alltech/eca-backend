package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.ProfessionCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class ProfessionCategoryRepositoryTest {

    @Autowired
    private ProfessionCategoryRepository professionCategoryRepository;


    @AfterEach
    void cleanUp() {
        professionCategoryRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveProfessionCategory() {
        ProfessionCategory professionCategory = new ProfessionCategory(
                "Development",
                "Groups professions related to software development, including frontend and backend development, full-stack engineering, application design, software architecture, and maintenance. Covers the creation, evolution, and improvement of software solutions.");

        ProfessionCategory savedCategory = professionCategoryRepository.save(professionCategory);

        assertThat(savedCategory.getId()).isNotNull();

        Optional<ProfessionCategory> foundCategory = professionCategoryRepository.findById(savedCategory.getId());

        assertThat(foundCategory).isPresent();

        ProfessionCategory retrievedCategory = foundCategory.orElseThrow();

        assertThat(retrievedCategory.getName()).isEqualTo(savedCategory.getName());
        assertThat(retrievedCategory.getDescription()).isEqualTo(savedCategory.getDescription());
    }
}
