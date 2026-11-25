
package fr.alltech.eca.repository.notification;

import fr.alltech.eca.model.notification.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository
        extends MongoRepository<Notification, String> {

    List<Notification> findByCollaboratorId(String collaboratorId);

    List<Notification> findByCollaboratorIdAndReadFalse(
            String collaboratorId
    );
}
