package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Profession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class ProfessionRepositoryTest {

    @Autowired
    private ProfessionRepository professionRepository;

    @AfterEach
    void cleanUp() {
        professionRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveProfession() {
        Profession profession = new Profession(
                "Backend developer",
                "Creates secure and scalable backend services, manages data persistence, develops RESTful APIs, and ensures system reliability through automated testing and monitoring.",
                true,
                "4");

        Profession savedProfession = professionRepository.save(profession);

        Optional<Profession> result =
                professionRepository.findById(savedProfession.getId());
        
        assertThat(savedProfession.getId()).isNotNull();
        assertThat(result).isPresent();

        Profession retrievedProfession = result.orElseThrow();

        assertThat(retrievedProfession.getName()).isEqualTo(savedProfession.getName());
        assertThat(retrievedProfession.getDescription()).isEqualTo(savedProfession.getDescription());
        assertThat(retrievedProfession.isActive()).isEqualTo(savedProfession.isActive());
        assertThat(retrievedProfession.getCategoryId()).isEqualTo(savedProfession.getCategoryId());
    }

    @Test
    void shouldReturnOnlyActiveProfessions() {
        Profession activeProfession = new Profession(
                "Backend developer",
                "Develops backend services.",
                true,
                "4"
        );

        Profession inactiveProfession = new Profession(
                "Frontend developer",
                "Develops frontend interfaces.",
                false,
                "4"
        );

        professionRepository.saveAll(
                List.of(activeProfession, inactiveProfession)
        );

        List<Profession> result = professionRepository.findByActiveTrue();

        assertThat(result)
                .hasSize(1)
                .extracting(Profession::getName)
                .containsExactly("Backend developer");
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveProfessionsExist() {
        Profession inactiveProfession = new Profession(
                "Frontend developer",
                "Develops frontend interfaces.",
                false,
                "4"
        );

        professionRepository.save(inactiveProfession);

        List<Profession> result = professionRepository.findByActiveTrue();

        assertThat(result).isEmpty();
    }
}
