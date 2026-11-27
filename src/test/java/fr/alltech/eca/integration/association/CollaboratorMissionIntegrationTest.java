package fr.alltech.eca.integration.association;

import fr.alltech.eca.model.association.CollaboratorMission;
import fr.alltech.eca.model.collaborator.Collaborator;
import fr.alltech.eca.model.mission.Client;
import fr.alltech.eca.model.mission.Mission;
import fr.alltech.eca.repository.association.CollaboratorMissionRepository;
import fr.alltech.eca.repository.collaborator.CollaboratorRepository;
import fr.alltech.eca.repository.mission.ClientRepository;
import fr.alltech.eca.repository.mission.MissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration de l'association entre un collaborateur et une mission.
 * Vérifie la persistance de l'affectation ainsi que la récupération
 * des documents auxquels elle fait référence.
 */
@DataMongoTest
class CollaboratorMissionIntegrationTest {

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private CollaboratorMissionRepository collaboratorMissionRepository;

    /**
     * Vérifie qu'un collaborateur peut être associé à une mission
     * et que les documents liés restent accessibles depuis leurs identifiants.
     */
    @Test
    void shouldAssociateCollaboratorWithMission() {
        // Préparation : crée et enregistre un collaborateur.
        Collaborator collaborator = new Collaborator(
                "ad-5554",
                "John",
                "Doe",
                "john.doe@example.com",
                null
        );

        Collaborator savedCollaborator = collaboratorRepository.save(collaborator);

        // Préparation : crée et enregistre le client associé à la mission.
        Client client = new Client(
                "Acme Corporation",
                "10 Rue de la Paix",
                "75002",
                "Paris",
                "France",
                "+33123456789",
                "contact@acme.example",
                true
        );

        Client savedClient = clientRepository.save(client);

        // Préparation : crée une mission rattachée au client enregistré.
        Mission mission = new Mission(
                "Backend Development",
                savedClient.getId(),
                LocalDate.of(2026, 11, 23),
                LocalDate.of(2027, 1, 15),
                "Development of backend services",
                true
        );

        Mission savedMission = missionRepository.save(mission);

        // Préparation : crée l'affectation entre le collaborateur et la mission.
        // La date de fin est nulle car l'affectation est toujours en cours.
        CollaboratorMission assignment = new CollaboratorMission(
                savedCollaborator.getId(),
                savedMission.getId(),
                LocalDate.of(2026, 11, 23),
                null
        );

        CollaboratorMission savedAssignment =
                collaboratorMissionRepository.save(assignment);

        // Exécution : récupère l'affectation à partir de son identifiant.
        Optional<CollaboratorMission> retrievedAssignment =
                collaboratorMissionRepository.findById(savedAssignment.getId());

        // Vérification : confirme que l'affectation a bien été enregistrée.
        assertThat(retrievedAssignment).isPresent();

        CollaboratorMission foundAssignment = retrievedAssignment.orElseThrow();

        // Vérifie que l'affectation référence les bons documents.
        assertThat(foundAssignment.getCollaboratorId())
                .isEqualTo(savedCollaborator.getId());
        assertThat(foundAssignment.getMissionId())
                .isEqualTo(savedMission.getId());
        assertThat(foundAssignment.getEndDate()).isNull();

        // Exécution : récupère le collaborateur et la mission référencés
        // par l'affectation à partir de leurs identifiants.
        Optional<Collaborator> retrievedCollaborator =
                collaboratorRepository.findById(foundAssignment.getCollaboratorId());

        Optional<Mission> retrievedMission =
                missionRepository.findById(foundAssignment.getMissionId());

        // Vérification : confirme que les deux documents sont accessibles.
        assertThat(retrievedCollaborator).isPresent();
        assertThat(retrievedMission).isPresent();

        Collaborator foundCollaborator = retrievedCollaborator.orElseThrow();
        Mission foundMission = retrievedMission.orElseThrow();

        // Vérifie que les documents récupérés correspondent aux données initiales.
        assertThat(foundCollaborator.getAdIdentifier()).isEqualTo("ad-5554");
        assertThat(foundMission.getName()).isEqualTo("Backend Development");

        // Exécution : récupère le client référencé par la mission.
        Optional<Client> retrievedClient =
                clientRepository.findById(foundMission.getClientId());

        // Vérification : confirme que le client associé à la mission est accessible.
        assertThat(retrievedClient).isPresent();
        assertThat(retrievedClient.orElseThrow().getName())
                .isEqualTo("Acme Corporation");
    }

    /**
     * Nettoie les collections utilisées par le test après chaque exécution
     * afin d'éviter que les données persistent entre les tests.
     */
    @AfterEach
    void cleanUp() {
        collaboratorMissionRepository.deleteAll();
        missionRepository.deleteAll();
        clientRepository.deleteAll();
        collaboratorRepository.deleteAll();
    }
}