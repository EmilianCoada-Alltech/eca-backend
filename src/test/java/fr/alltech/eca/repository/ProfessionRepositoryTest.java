package fr.alltech.eca.repository;

import fr.alltech.eca.model.Profession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class ProfessionRepositoryTest {

    @Autowired
    private ProfessionRepository professionRepository;

    @Test
    void shouldSaveAndRetrieveProfession() {
        Profession profession = new Profession(
                "Backend developer",
                "Creates secure and scalable backend services, manages data persistence, develops RESTful APIs, and ensures system reliability through automated testing and monitoring.",
                true,
                "4");

        Profession savedProfession = professionRepository.save(profession);
        
        assertThat(savedProfession.getId()).isNotNull();

        Optional<Profession> result =
                professionRepository.findById(savedProfession.getId());

        assertThat(result).isPresent();

        assertThat(savedProfession.getId()).isNotNull();
        assertThat(result).isPresent();

        Profession retrievedProfession = result.orElseThrow();

        assertThat(retrievedProfession.getName()).isEqualTo(savedProfession.getName());
        assertThat(retrievedProfession.getDescription()).isEqualTo(savedProfession.getDescription());
        assertThat(retrievedProfession.isActive()).isEqualTo(savedProfession.isActive());
        assertThat(retrievedProfession.getCategoryId()).isEqualTo(savedProfession.getCategoryId());

        professionRepository.deleteById(savedProfession.getId());
    }
}
