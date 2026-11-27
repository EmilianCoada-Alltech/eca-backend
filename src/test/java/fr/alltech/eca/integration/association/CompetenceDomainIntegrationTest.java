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

/**
 * Tests d'intégration de l'association entre une compétence et un domaine.
 * Vérifie la persistance de l'association ainsi que la récupération
 * des documents auxquels elle fait référence.
 */
@DataMongoTest
class CompetenceDomainIntegrationTest {

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private CompetenceDomainRepository competenceDomainRepository;

    /**
     * Vérifie qu'une compétence peut être associée à un domaine
     * et que les documents liés restent accessibles à partir de leurs identifiants.
     */
    @Test
    void shouldAssociateCompetenceWithDomain() {

        // Préparation : crée et enregistre une compétence.
        Competence competence = new Competence(
                "Spring Boot",
                "Java framework for building applications",
                true
        );

        Competence savedCompetence = competenceRepository.save(competence);

        // Préparation : crée et enregistre un domaine.
        Domain domain = new Domain(
                "Backend Development",
                "Competences related to backend development",
                true
        );

        Domain savedDomain = domainRepository.save(domain);

        // Préparation : crée l'association à partir des identifiants
        // des deux documents précédemment enregistrés.
        CompetenceDomain association = new CompetenceDomain(
                savedCompetence.getId(),
                savedDomain.getId()
        );

        CompetenceDomain savedAssociation =
                competenceDomainRepository.save(association);

        // Exécution : récupère l'association à partir de son identifiant.
        Optional<CompetenceDomain> retrievedAssociation =
                competenceDomainRepository.findById(savedAssociation.getId());

        // Vérification : confirme que l'association a bien été enregistrée.
        assertThat(retrievedAssociation).isPresent();

        CompetenceDomain foundAssociation = retrievedAssociation.orElseThrow();

        // Vérifie que l'association référence les bons identifiants.
        assertThat(foundAssociation.getCompetenceId())
                .isEqualTo(savedCompetence.getId());

        assertThat(foundAssociation.getDomainId())
                .isEqualTo(savedDomain.getId());

        // Exécution : récupère les documents référencés par l'association.
        Optional<Competence> retrievedCompetence =
                competenceRepository.findById(foundAssociation.getCompetenceId());

        Optional<Domain> retrievedDomain =
                domainRepository.findById(foundAssociation.getDomainId());

        // Vérification : confirme que les deux documents sont accessibles.
        assertThat(retrievedCompetence).isPresent();
        assertThat(retrievedDomain).isPresent();

        Competence foundCompetence = retrievedCompetence.orElseThrow();
        Domain foundDomain = retrievedDomain.orElseThrow();

        // Vérifie que les documents récupérés correspondent aux données initiales.
        assertThat(foundCompetence.getName()).isEqualTo("Spring Boot");
        assertThat(foundDomain.getName()).isEqualTo("Backend Development");
    }

    /**
     * Nettoie les collections utilisées par le test après chaque exécution
     * afin d'éviter que les données persistent entre les tests.
     */
    @AfterEach
    void cleanUp() {
        competenceDomainRepository.deleteAll();
        competenceRepository.deleteAll();
        domainRepository.deleteAll();
    }
}