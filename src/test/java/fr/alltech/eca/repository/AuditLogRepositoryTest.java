
package fr.alltech.eca.repository;

import fr.alltech.eca.model.AuditLog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @AfterEach
    void tearDown() {
        auditLogRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveAuditLog() {
        // Given
        LocalDateTime changedAt = LocalDateTime.of(
                2026, 11, 25, 10, 30
        );

        AuditLog auditLog = new AuditLog(
                "audit-123",
                "Collaborator",
                "collaborator-123",
                "presentation",
                "Previous presentation",
                "Updated presentation",
                "admin-123",
                changedAt
        );

        // When
        AuditLog savedAuditLog = auditLogRepository.save(auditLog);

        // Then
        assertThat(savedAuditLog.getId()).isEqualTo("audit-123");

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
