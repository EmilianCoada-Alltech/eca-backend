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

    @Test
    void shouldAssociateCollaboratorWithMission() {
        // Arrange: create and save a collaborator
        Collaborator collaborator = new Collaborator(
                "ad-5554",
                "John",
                "Doe",
                "john.doe@example.com",
                null
        );

        Collaborator savedCollaborator = collaboratorRepository.save(collaborator);

        // Arrange: create and save a client
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

        // Arrange: create and save a mission
        Mission mission = new Mission(
                "Backend Development",
                savedClient.getId(),
                LocalDate.of(2026, 11, 23),
                LocalDate.of(2027, 1, 15),
                "Development of backend services",
                true
        );

        Mission savedMission = missionRepository.save(mission);

        // Arrange: create the assignment
        CollaboratorMission assignment = new CollaboratorMission(
                savedCollaborator.getId(),
                savedMission.getId(),
                LocalDate.of(2026, 11, 23),
                null
        );

        CollaboratorMission savedAssignment =
                collaboratorMissionRepository.save(assignment);

        // Act: retrieve the assignment
        Optional<CollaboratorMission> retrievedAssignment =
                collaboratorMissionRepository.findById(savedAssignment.getId());

        // Assert: verify the assignment
        assertThat(retrievedAssignment).isPresent();

        CollaboratorMission foundAssignment = retrievedAssignment.orElseThrow();

        assertThat(foundAssignment.getCollaboratorId())
                .isEqualTo(savedCollaborator.getId());
        assertThat(foundAssignment.getMissionId())
                .isEqualTo(savedMission.getId());
        assertThat(foundAssignment.getEndDate()).isNull();

        // Act: retrieve the referenced documents
        Optional<Collaborator> retrievedCollaborator =
                collaboratorRepository.findById(foundAssignment.getCollaboratorId());

        Optional<Mission> retrievedMission =
                missionRepository.findById(foundAssignment.getMissionId());

        // Assert: verify the referenced documents
        assertThat(retrievedCollaborator).isPresent();
        assertThat(retrievedMission).isPresent();

        Collaborator foundCollaborator = retrievedCollaborator.orElseThrow();
        Mission foundMission = retrievedMission.orElseThrow();

        assertThat(foundCollaborator.getAdIdentifier()).isEqualTo("ad-5554");
        assertThat(foundMission.getName()).isEqualTo("Backend Development");

        // Act: retrieve the client referenced by the mission
        Optional<Client> retrievedClient =
                clientRepository.findById(foundMission.getClientId());

        // Assert: verify the client
        assertThat(retrievedClient).isPresent();
        assertThat(retrievedClient.orElseThrow().getName())
                .isEqualTo("Acme Corporation");
    }

    @AfterEach
    void cleanUp() {
        collaboratorMissionRepository.deleteAll();
        missionRepository.deleteAll();
        clientRepository.deleteAll();
        collaboratorRepository.deleteAll();
    }
}