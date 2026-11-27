package fr.alltech.eca.repository.association;

import fr.alltech.eca.model.association.CollaboratorMission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository CollaboratorMission.
 * Vérifie la persistance et la récupération des associations
 * entre collaborateurs et missions dans MongoDB.
 */
@DataMongoTest
class CollaboratorMissionRepositoryTest {

    @Autowired
    private CollaboratorMissionRepository collaboratorMissionRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        collaboratorMissionRepository.deleteAll();
    }

    /**
     * Vérifie qu'une association entre un collaborateur et une mission
     * peut être enregistrée puis récupérée avec ses informations intactes.
     */
    @Test
    void shouldSaveAndRetrieveCollaboratorMission() {
        // Préparation : crée une affectation entre un collaborateur et une mission.
        CollaboratorMission collaboratorMission = new CollaboratorMission(
                "collaborator-123",
                "mission-456",
                LocalDate.of(2026, 11, 23),
                null
        );

        // Exécution : enregistre l'affectation dans MongoDB.
        CollaboratorMission savedCollaboratorMission =
                collaboratorMissionRepository.save(collaboratorMission);

        // Récupération : recherche l'affectation enregistrée à partir de son identifiant.
        Optional<CollaboratorMission> result =
                collaboratorMissionRepository.findById(savedCollaboratorMission.getId());

        // Vérification : confirme que l'enregistrement possède un identifiant
        // et que le repository retourne bien le document.
        assertThat(savedCollaboratorMission.getId()).isNotNull();
        assertThat(result).isPresent();

        CollaboratorMission retrievedCollaboratorMission = result.orElseThrow();

        // Vérification : contrôle les références vers le collaborateur et la mission.
        assertThat(retrievedCollaboratorMission.getCollaboratorId())
                .isEqualTo("collaborator-123");
        assertThat(retrievedCollaboratorMission.getMissionId())
                .isEqualTo("mission-456");

        // Vérification : contrôle les dates de début et de fin de l'affectation.
        assertThat(retrievedCollaboratorMission.getStartDate())
                .isEqualTo(LocalDate.of(2026, 11, 23));
        assertThat(retrievedCollaboratorMission.getEndDate()).isNull();
    }
}