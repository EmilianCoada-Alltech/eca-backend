
package fr.alltech.eca.repository.notification;

import fr.alltech.eca.model.notification.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Fournit les opérations d'accès aux données des notifications.
 */
public interface NotificationRepository
        extends MongoRepository<Notification, String> {

    /**
     * Récupère toutes les notifications d'un collaborateur.
     *
     * @param collaboratorId identifiant du collaborateur
     * @return la liste de ses notifications
     */
    List<Notification> findByCollaboratorId(String collaboratorId);

    /**
     * Récupère les notifications non lues d'un collaborateur.
     *
     * @param collaboratorId identifiant du collaborateur
     * @return la liste de ses notifications non lues
     */
    List<Notification> findByCollaboratorIdAndReadFalse(
            String collaboratorId
    );
}
