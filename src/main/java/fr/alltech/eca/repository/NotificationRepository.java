
package fr.alltech.eca.repository;

import fr.alltech.eca.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository
        extends MongoRepository<Notification, String> {

    List<Notification> findByCollaboratorId(String collaboratorId);

    List<Notification> findByCollaboratorIdAndReadFalse(
            String collaboratorId
    );
}
