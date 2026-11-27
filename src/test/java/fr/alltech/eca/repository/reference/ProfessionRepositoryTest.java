package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Profession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository Profession.
 * Vérifie la persistance des professions ainsi que les recherches
 * permettant de filtrer les professions selon leur statut d'activité.
 */
@DataMongoTest
public class ProfessionRepositoryTest {

    @Autowired
    private ProfessionRepository professionRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        professionRepository.deleteAll();
    }

    /**
     * Vérifie qu'une profession peut être enregistrée puis récupérée
     * avec son nom, sa description, son statut et sa catégorie.
     */
    @Test
    void shouldSaveAndRetrieveProfession() {
        // Préparation : crée une profession avec ses informations métier.
        Profession profession = new Profession(
                "Backend developer",
                "Creates secure and scalable backend services, manages data persistence, develops RESTful APIs, and ensures system reliability through automated testing and monitoring.",
                true,
                "4");

        // Exécution : enregistre la profession dans MongoDB.
        Profession savedProfession = professionRepository.save(profession);

        // Récupération : recherche la profession enregistrée à partir de son identifiant.
        Optional<Profession> result =
                professionRepository.findById(savedProfession.getId());

        // Vérification : confirme que l'enregistrement possède un identifiant
        // et que le repository retourne bien la profession.
        assertThat(savedProfession.getId()).isNotNull();
        assertThat(result).isPresent();

        Profession retrievedProfession = result.orElseThrow();

        // Vérification : compare les informations récupérées avec celles enregistrées.
        assertThat(retrievedProfession.getName()).isEqualTo(savedProfession.getName());
        assertThat(retrievedProfession.getDescription()).isEqualTo(savedProfession.getDescription());
        assertThat(retrievedProfession.isActive()).isEqualTo(savedProfession.isActive());
        assertThat(retrievedProfession.getCategoryId()).isEqualTo(savedProfession.getCategoryId());
    }

    /**
     * Vérifie que la méthode findByActiveTrue() retourne uniquement
     * les professions dont le statut est actif.
     */
    @Test
    void shouldReturnOnlyActiveProfessions() {
        // Préparation : crée une profession active et une profession inactive.
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

        // Exécution : enregistre les deux professions dans MongoDB.
        professionRepository.saveAll(
                List.of(activeProfession, inactiveProfession)
        );

        // Exécution : récupère uniquement les professions actives.
        List<Profession> result = professionRepository.findByActiveTrue();

        // Vérification : confirme qu'une seule profession est retournée
        // et qu'il s'agit bien de la profession active.
        assertThat(result)
                .hasSize(1)
                .extracting(Profession::getName)
                .containsExactly("Backend developer");
    }

    /**
     * Vérifie que la recherche des professions actives retourne une liste vide
     * lorsqu'aucune profession active n'existe dans la collection.
     */
    @Test
    void shouldReturnEmptyListWhenNoActiveProfessionsExist() {
        // Préparation : crée une profession inactive.
        Profession inactiveProfession = new Profession(
                "Frontend developer",
                "Develops frontend interfaces.",
                false,
                "4"
        );

        // Exécution : enregistre la profession inactive dans MongoDB.
        professionRepository.save(inactiveProfession);

        // Exécution : recherche les professions actives.
        List<Profession> result = professionRepository.findByActiveTrue();

        // Vérification : confirme qu'aucune profession active n'est retournée.
        assertThat(result).isEmpty();
    }
}