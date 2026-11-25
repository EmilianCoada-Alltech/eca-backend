package fr.alltech.eca.integration.association;

import fr.alltech.eca.model.association.CompetenceDomain;
import fr.alltech.eca.model.reference.Competence;
import fr.alltech.eca.model.reference.Domain;
import fr.alltech.eca.repository.association.CompetenceDomainRepository;
import fr.alltech.eca.repository.reference.CompetenceRepository;
import fr.alltech.eca.repository.reference.DomainRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CompetenceDomainIntegrationTest {

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private CompetenceDomainRepository competenceDomainRepository;

    @Test
    void shouldAssociateCompetenceWithDomain() {

        // Arrange: create and save a competence
        Competence competence = new Competence(
                "Spring Boot",
                "Java framework for building applications",
                true
        );

        Competence savedCompetence = competenceRepository.save(competence);

        // Arrange: create and save a domain
        Domain domain = new Domain(
                "Backend Development",
                "Competences related to backend development",
                true
        );

        Domain savedDomain = domainRepository.save(domain);

        // Arrange: create the association
        CompetenceDomain association = new CompetenceDomain(
                savedCompetence.getId(),
                savedDomain.getId()
        );

        CompetenceDomain savedAssociation =
                competenceDomainRepository.save(association);

        // Act: retrieve the association
        Optional<CompetenceDomain> retrievedAssociation =
                competenceDomainRepository.findById(savedAssociation.getId());

        // Assert: verify the association
        assertThat(retrievedAssociation).isPresent();

        CompetenceDomain foundAssociation = retrievedAssociation.orElseThrow();

        assertThat(foundAssociation.getCompetenceId())
                .isEqualTo(savedCompetence.getId());

        assertThat(foundAssociation.getDomainId())
                .isEqualTo(savedDomain.getId());

        // Act: retrieve the referenced documents
        Optional<Competence> retrievedCompetence =
                competenceRepository.findById(foundAssociation.getCompetenceId());

        Optional<Domain> retrievedDomain =
                domainRepository.findById(foundAssociation.getDomainId());

        // Assert: verify the referenced documents
        assertThat(retrievedCompetence).isPresent();
        assertThat(retrievedDomain).isPresent();

        Competence foundCompetence = retrievedCompetence.orElseThrow();
        Domain foundDomain = retrievedDomain.orElseThrow();

        assertThat(foundCompetence.getName()).isEqualTo("Spring Boot");
        assertThat(foundDomain.getName()).isEqualTo("Backend Development");
    }

    @AfterEach
    void cleanUp() {
        competenceDomainRepository.deleteAll();
        competenceRepository.deleteAll();
        domainRepository.deleteAll();
    }
}