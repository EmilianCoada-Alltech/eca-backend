package fr.alltech.eca.integration;

import fr.alltech.eca.model.Competence;
import fr.alltech.eca.model.Profession;
import fr.alltech.eca.model.ProfessionCompetence;
import fr.alltech.eca.repository.CompetenceRepository;
import fr.alltech.eca.repository.ProfessionCompetenceRepository;
import fr.alltech.eca.repository.ProfessionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ProfessionCompetenceIntegrationTest {

    @Autowired
    private ProfessionRepository professionRepository;

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private ProfessionCompetenceRepository professionCompetenceRepository;

    @Test
    void shouldAssociateProfessionWithCompetence() {

        // Arrange: create and save a profession
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services and APIs",
                true,
                null
        );

        Profession savedProfession = professionRepository.save(profession);

        // Arrange: create and save a competence
        Competence competence = new Competence(
                "Spring Boot",
                "Java framework for building applications",
                true
        );

        Competence savedCompetence = competenceRepository.save(competence);

        // Arrange: create the association
        ProfessionCompetence association = new ProfessionCompetence(
                savedProfession.getId(),
                savedCompetence.getId()
        );

        ProfessionCompetence savedAssociation =
                professionCompetenceRepository.save(association);

        // Act: retrieve the association
        Optional<ProfessionCompetence> retrievedAssociation =
                professionCompetenceRepository.findById(savedAssociation.getId());

        // Assert: verify the association
        assertThat(retrievedAssociation).isPresent();

        ProfessionCompetence foundAssociation = retrievedAssociation.orElseThrow();

        assertThat(foundAssociation.getProfessionId())
                .isEqualTo(savedProfession.getId());

        assertThat(foundAssociation.getCompetenceId())
                .isEqualTo(savedCompetence.getId());

        // Act: retrieve the referenced documents
        Optional<Profession> retrievedProfession =
                professionRepository.findById(foundAssociation.getProfessionId());

        Optional<Competence> retrievedCompetence =
                competenceRepository.findById(foundAssociation.getCompetenceId());

        // Assert: verify the referenced documents
        assertThat(retrievedProfession).isPresent();
        assertThat(retrievedCompetence).isPresent();

        Profession foundProfession = retrievedProfession.orElseThrow();
        Competence foundCompetence = retrievedCompetence.orElseThrow();

        assertThat(foundProfession.getName()).isEqualTo("Backend developer");
        assertThat(foundCompetence.getName()).isEqualTo("Spring Boot");
    }

    @AfterEach
    void cleanUp() {
        professionCompetenceRepository.deleteAll();
        professionRepository.deleteAll();
        competenceRepository.deleteAll();
    }
}