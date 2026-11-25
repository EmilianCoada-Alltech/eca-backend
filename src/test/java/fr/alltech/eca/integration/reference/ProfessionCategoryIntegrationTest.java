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

@DataMongoTest
public class ProfessionCategoryIntegrationTest {

    @Autowired
    private ProfessionRepository professionRepository;

    @Autowired
    private ProfessionCategoryRepository professionCategoryRepository;

    @Test
    void shouldAssociateProfessionWithCategory() {
        ProfessionCategory category = new ProfessionCategory(
                "Development",
                "Groups professions related to software development, including frontend and backend development, full-stack engineering, application design, software architecture, and maintenance. Covers the creation, evolution, and improvement of software solutions.");

        ProfessionCategory savedCategory = professionCategoryRepository.save(category);

        Profession profession = new Profession(
                "Backend developer",
                "Creates secure and scalable backend services, manages data persistence, develops RESTful APIs, and ensures system reliability through automated testing and monitoring.",
                true,
                savedCategory.getId());

        Profession savedProfession = professionRepository.save(profession);

        Optional<Profession> retrievedProfession =
                professionRepository.findById(savedProfession.getId());

        assertThat(retrievedProfession).isPresent();

        Profession foundProfession = retrievedProfession.orElseThrow();

        Optional<ProfessionCategory> retrievedCategory =
                professionCategoryRepository.findById(foundProfession.getCategoryId());

        assertThat(retrievedCategory).isPresent();

        ProfessionCategory foundCategory = retrievedCategory.orElseThrow();

        assertThat(foundProfession.getCategoryId())
                .isEqualTo(foundCategory.getId());

        assertThat(foundCategory.getName())
                .isEqualTo("Development");

        professionRepository.deleteById(savedProfession.getId());
        professionCategoryRepository.deleteById(savedCategory.getId());
    }
}
