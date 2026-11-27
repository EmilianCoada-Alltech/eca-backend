
package fr.alltech.eca.model.notification;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Représente une notification destinée à un collaborateur,
 * avec son contenu, son lien éventuel et son statut de lecture.
 */
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String collaboratorId;
    private String type;
    private String message;
    private String link;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(
            String id,
            String collaboratorId,
            String type,
            String message,
            String link,
            boolean read,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.collaboratorId = collaboratorId;
        this.type = type;
        this.message = message;
        this.link = link;
        this.read = read;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollaboratorId() {
        return collaboratorId;
    }

    public void setCollaboratorId(String collaboratorId) {
        this.collaboratorId = collaboratorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
