package fr.alltech.eca.repository.audit;

import fr.alltech.eca.model.audit.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données du journal d'audit.
 */
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}
