
package fr.alltech.eca.model.audit;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Représente une entrée du journal d'audit,
 * enregistrant les modifications effectuées sur les données de l'application.
 */
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    private String entityType;
    private String entityId;
    private String fieldName;

    private String oldValue;
    private String newValue;

    private String changedBy;
    private LocalDateTime changedAt;

    private String action;

    public AuditLog() {
    }

    public AuditLog(
            String id,
            String entityType,
            String entityId,
            String fieldName,
            String oldValue,
            String newValue,
            String changedBy,
            LocalDateTime changedAt,
            String action
    ) {
        this.id = id;
        this.entityType = entityType;
        this.entityId = entityId;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
