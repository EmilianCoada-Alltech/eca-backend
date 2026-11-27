package fr.alltech.eca.repository.audit;

import fr.alltech.eca.model.audit.AuditLog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository AuditLog.
 * Vérifie la persistance et la récupération des journaux d'audit
 * ainsi que la conservation des informations relatives aux modifications.
 */
@DataMongoTest
class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        auditLogRepository.deleteAll();
    }

    /**
     * Vérifie qu'un journal d'audit peut être enregistré puis récupéré
     * avec ses informations de modification intactes.
     */
    @Test
    void shouldSaveAndRetrieveAuditLog() {
        // Préparation : définit une date fixe pour rendre le test reproductible.
        LocalDateTime changedAt = LocalDateTime.of(
                2026, 11, 25, 10, 30
        );

        // Préparation : crée un journal décrivant une modification
        // de la présentation d'un collaborateur.
        AuditLog auditLog = new AuditLog(
                "audit-123",
                "Collaborator",
                "collaborator-123",
                "presentation",
                "Previous presentation",
                "Updated presentation",
                "admin-123",
                changedAt,
                "CREATE"
        );

        // Exécution : enregistre le journal d'audit dans MongoDB.
        AuditLog savedAuditLog = auditLogRepository.save(auditLog);

        // Vérification : confirme que l'identifiant a été conservé.
        assertThat(savedAuditLog.getId()).isEqualTo("audit-123");

        // Récupération et vérification : recherche le journal par son identifiant
        // et contrôle les différentes informations enregistrées.
        assertThat(auditLogRepository.findById("audit-123"))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getEntityType())
                            .isEqualTo("Collaborator");
                    assertThat(found.getEntityId())
                            .isEqualTo("collaborator-123");
                    assertThat(found.getFieldName())
                            .isEqualTo("presentation");
                    assertThat(found.getOldValue())
                            .isEqualTo("Previous presentation");
                    assertThat(found.getNewValue())
                            .isEqualTo("Updated presentation");
                    assertThat(found.getChangedBy())
                            .isEqualTo("admin-123");
                    assertThat(found.getChangedAt())
                            .isEqualTo(changedAt);
                });
    }
}