package fr.alltech.eca.repository;

import fr.alltech.eca.model.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @AfterEach
    void cleanUp() {
        roleRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveRole() {
        // Given
        Role role = new Role(
                "role-123",
                "ADMINISTRATEUR",
                "Application administrator"
        );

        // When
        Role savedRole = roleRepository.save(role);

        // Then
        assertThat(savedRole.getId()).isEqualTo("role-123");

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
