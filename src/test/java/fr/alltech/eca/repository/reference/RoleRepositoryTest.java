package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository Role.
 * Vérifie la persistance et la récupération des rôles
 * ainsi que la conservation de leurs informations dans MongoDB.
 */
@DataMongoTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        roleRepository.deleteAll();
    }

    /**
     * Vérifie qu'un rôle peut être enregistré puis récupéré
     * avec son identifiant, son nom et sa description.
     */
    @Test
    void shouldSaveAndRetrieveRole() {
        // Préparation : crée un rôle avec son identifiant et ses informations.
        Role role = new Role(
                "role-123",
                "ADMINISTRATEUR",
                "Application administrator"
        );

        // Exécution : enregistre le rôle dans MongoDB.
        Role savedRole = roleRepository.save(role);

        // Vérification : confirme que l'identifiant du rôle est conservé.
        assertThat(savedRole.getId()).isEqualTo("role-123");

        // Récupération et vérification : recherche le rôle par son identifiant
        // et contrôle les informations enregistrées.
        assertThat(roleRepository.findById("role-123"))
                .isPresent()
                .get()
                .satisfies(foundRole -> {
                    assertThat(foundRole.getName())
                            .isEqualTo("ADMINISTRATEUR");
                    assertThat(foundRole.getDescription())
                            .isEqualTo("Application administrator");
                });
    }
}