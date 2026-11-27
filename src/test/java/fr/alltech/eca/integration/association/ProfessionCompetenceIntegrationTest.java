package fr.alltech.eca.integration.association;

import fr.alltech.eca.model.association.ProfessionCompetence;
import fr.alltech.eca.model.reference.Competence;
import fr.alltech.eca.model.reference.Profession;
import fr.alltech.eca.repository.association.ProfessionCompetenceRepository;
import fr.alltech.eca.repository.reference.CompetenceRepository;
import fr.alltech.eca.repository.reference.ProfessionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration de l'association entre un métier et une compétence.
 * Vérifie la persistance de l'association ainsi que la récupération
 * des documents auxquels elle fait référence.
 */
@DataMongoTest
class ProfessionCompetenceIntegrationTest {

    @Autowired
    private ProfessionRepository professionRepository;

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private ProfessionCompetenceRepository professionCompetenceRepository;

    /**
     * Vérifie qu'une compétence peut être associée à un métier
     * et que les documents liés restent accessibles à partir de leurs identifiants.
     */
    @Test
    void shouldAssociateProfessionWithCompetence() {

        // Préparation : crée et enregistre un métier.
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services and APIs",
                true,
                null
        );

        Profession savedProfession = professionRepository.save(profession);

        // Préparation : crée et enregistre une compétence.
        Competence competence = new Competence(
                "Spring Boot",
                "Java framework for building applications",
                true
        );

        Competence savedCompetence = competenceRepository.save(competence);

        // Préparation : crée l'association à partir des identifiants
        // des deux documents précédemment enregistrés.
        ProfessionCompetence association = new ProfessionCompetence(
                savedProfession.getId(),
                savedCompetence.getId()
        );

        ProfessionCompetence savedAssociation =
                professionCompetenceRepository.save(association);

        // Exécution : récupère l'association à partir de son identifiant.
        Optional<ProfessionCompetence> retrievedAssociation =
                professionCompetenceRepository.findById(savedAssociation.getId());

        // Vérification : confirme que l'association a bien été enregistrée.
        assertThat(retrievedAssociation).isPresent();

        ProfessionCompetence foundAssociation = retrievedAssociation.orElseThrow();

        // Vérifie que l'association référence les bons identifiants.
        assertThat(foundAssociation.getProfessionId())
                .isEqualTo(savedProfession.getId());

        assertThat(foundAssociation.getCompetenceId())
                .isEqualTo(savedCompetence.getId());

        // Exécution : récupère les documents référencés par l'association.
        Optional<Profession> retrievedProfession =
                professionRepository.findById(foundAssociation.getProfessionId());

        Optional<Competence> retrievedCompetence =
                competenceRepository.findById(foundAssociation.getCompetenceId());

        // Vérification : confirme que les deux documents sont accessibles.
        assertThat(retrievedProfession).isPresent();
        assertThat(retrievedCompetence).isPresent();

        Profession foundProfession = retrievedProfession.orElseThrow();
        Competence foundCompetence = retrievedCompetence.orElseThrow();

        // Vérifie que les documents récupérés correspondent aux données initiales.
        assertThat(foundProfession.getName()).isEqualTo("Backend developer");
        assertThat(foundCompetence.getName()).isEqualTo("Spring Boot");
    }

    /**
     * Nettoie les collections utilisées par le test après chaque exécution
     * afin d'éviter que les données persistent entre les tests.
     */
    @AfterEach
    void cleanUp() {
        professionCompetenceRepository.deleteAll();
        professionRepository.deleteAll();
        competenceRepository.deleteAll();
    }
}