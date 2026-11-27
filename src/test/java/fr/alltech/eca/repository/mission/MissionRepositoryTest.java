package fr.alltech.eca.repository.mission;

import fr.alltech.eca.model.mission.Mission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository Mission.
 * Vérifie la persistance et la récupération des missions
 * ainsi que la conservation de leurs informations dans MongoDB.
 */
@DataMongoTest
class MissionRepositoryTest {

    @Autowired
    private MissionRepository missionRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        missionRepository.deleteAll();
    }

    /**
     * Vérifie qu'une mission peut être enregistrée puis récupérée
     * avec ses informations, ses dates et son statut de visibilité.
     */
    @Test
    void shouldSaveAndRetrieveMission() {
        // Préparation : crée une mission associée à un client existant.
        Mission mission = new Mission(
                "ECA Backend Development",
                "client-123",
                LocalDate.of(2026, 11, 23),
                LocalDate.of(2027, 1, 15),
                "Development of the ECA backend application",
                true
        );

        // Exécution : enregistre la mission dans MongoDB.
        Mission savedMission = missionRepository.save(mission);

        // Récupération : recherche la mission enregistrée à partir de son identifiant.
        Optional<Mission> result =
                missionRepository.findById(savedMission.getId());

        // Vérification : confirme que l'enregistrement possède un identifiant
        // et que le repository retourne bien la mission.
        assertThat(savedMission.getId()).isNotNull();
        assertThat(result).isPresent();

        Mission retrievedMission = result.orElseThrow();

        // Vérification : contrôle les informations générales de la mission.
        assertThat(retrievedMission.getName())
                .isEqualTo("ECA Backend Development");
        assertThat(retrievedMission.getClientId())
                .isEqualTo("client-123");

        // Vérification : contrôle les dates de début et de fin.
        assertThat(retrievedMission.getStartDate())
                .isEqualTo(LocalDate.of(2026, 11, 23));
        assertThat(retrievedMission.getEndDate())
                .isEqualTo(LocalDate.of(2027, 1, 15));

        // Vérification : contrôle la description et le statut de visibilité.
        assertThat(retrievedMission.getDescription())
                .isEqualTo("Development of the ECA backend application");
        assertThat(retrievedMission.isVisible()).isTrue();
    }
}