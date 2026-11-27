package fr.alltech.eca.repository.collaborator;

import fr.alltech.eca.common.enums.CompetenceSource;
import fr.alltech.eca.config.CollaboratorValidationCallback;
import fr.alltech.eca.model.collaborator.Collaborator;
import fr.alltech.eca.model.collaborator.CompetenceDeclaration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests d'intégration du repository Collaborator.
 * Vérifie la persistance des collaborateurs, la conservation des déclarations
 * de compétences intégrées et des rôles, ainsi que l'application des règles
 * de validation avant l'enregistrement.
 */
@DataMongoTest
@Import(CollaboratorValidationCallback.class)
class CollaboratorRepositoryTest {

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        collaboratorRepository.deleteAll();
    }

    /**
     * Vérifie qu'un collaborateur peut être enregistré puis récupéré
     * avec ses informations principales et ses déclarations de compétences intégrées.
     */
    @Test
    void shouldSaveAndRetrieveCollaborator() {

        // Préparation : crée un collaborateur avec un identifiant AD unique.
        Collaborator collaborator = new Collaborator(
                "test-ad-" + UUID.randomUUID(),
                "John",
                "Doe",
                "john.doe@example.test",
                "agency-test-id"
        );

        // Préparation : crée une déclaration de compétence issue du catalogue.
        CompetenceDeclaration competence = new CompetenceDeclaration(
                "spring-boot-test-id",
                null,
                true,
                true,
                CompetenceSource.CATALOGUE
        );

        // Ajoute la déclaration à la liste des compétences intégrées au collaborateur.
        collaborator.getCompetences().add(competence);

        // Exécution : enregistre le collaborateur et ses données intégrées dans MongoDB.
        Collaborator savedCollaborator =
                collaboratorRepository.save(collaborator);


        // Récupération : recherche le collaborateur enregistré à partir de son identifiant.
        Optional<Collaborator> result =
                collaboratorRepository.findById(savedCollaborator.getId());

        // Vérification : confirme que l'enregistrement possède un identifiant
        // et que le repository retourne bien le collaborateur.
        assertThat(savedCollaborator.getId()).isNotNull();
        assertThat(result).isPresent();

        Collaborator retrievedCollaborator = result.orElseThrow();

        // Vérification : contrôle les principales informations du collaborateur.
        assertThat(retrievedCollaborator.getFirstName()).isEqualTo("John");
        assertThat(retrievedCollaborator.getLastName()).isEqualTo("Doe");
        assertThat(retrievedCollaborator.getEmail())
                .isEqualTo("john.doe@example.test");

        // Vérification : contrôle la présence et le contenu de la déclaration
        // de compétence intégrée au document Collaborator.
        assertThat(retrievedCollaborator.getCompetences()).hasSize(1);
        assertThat(retrievedCollaborator.getCompetences().getFirst().getCompetenceId())
                .isEqualTo("spring-boot-test-id");
        assertThat(retrievedCollaborator.getCompetences().getFirst().isExpertise())
                .isTrue();
    }

    /**
     * Vérifie qu'un collaborateur contenant une déclaration de compétence invalide
     * ne peut pas être enregistré et qu'aucun document n'est persisté.
     */
    @Test
    void shouldRejectCollaboratorWithInvalidCompetenceDeclaration() {

        // Préparation : crée un collaborateur avec un identifiant AD unique.
        Collaborator collaborator = new Collaborator(
                "test-ad-" + UUID.randomUUID(),
                "John",
                "Doe",
                "john.doe@example.test",
                "agency-test-id"
        );

        // Préparation : crée une déclaration invalide, sans identifiant
        // ni nom de compétence.
        CompetenceDeclaration competence = new CompetenceDeclaration(
                null,
                null,
                true,
                true,
                CompetenceSource.CATALOGUE
        );

        // Ajoute la déclaration invalide au collaborateur.
        collaborator.getCompetences().add(competence);

        // Vérification : l'enregistrement doit échouer lors de la validation.
        assertThatThrownBy(() -> {
            collaboratorRepository.save(collaborator);
        }).isInstanceOf(IllegalArgumentException.class);

        // Vérification : confirme qu'aucun collaborateur n'a été enregistré.
        assertThat(collaboratorRepository.count()).isZero();
    }

    /**
     * Vérifie que les rôles associés à un collaborateur
     * sont correctement enregistrés et récupérés depuis MongoDB.
     */
    @Test
    void shouldSaveAndRetrieveCollaboratorWithRoles() {
        // Préparation : crée un collaborateur.
        Collaborator collaborator = new Collaborator(
                "AD123",
                "John",
                "Doe",
                "john.doe@example.com",
                "agency-123"
        );

        // Préparation : associe deux rôles au collaborateur.
        collaborator.setRoleIds(List.of(
                "COLLABORATEUR",
                "REDACTEUR"
        ));

        // Exécution : enregistre le collaborateur et ses rôles.
        Collaborator savedCollaborator =
                collaboratorRepository.save(collaborator);

        // Vérification : récupère le collaborateur et confirme
        // que les rôles sont conservés dans le même ordre.
        assertThat(collaboratorRepository.findById(savedCollaborator.getId()))
                .isPresent()
                .get()
                .satisfies(found -> assertThat(found.getRoleIds())
                        .containsExactly(
                                "COLLABORATEUR",
                                "REDACTEUR"
                        ));
    }
}