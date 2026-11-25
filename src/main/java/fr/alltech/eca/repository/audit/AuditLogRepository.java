package fr.alltech.eca.repository.audit;

import fr.alltech.eca.model.audit.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}
