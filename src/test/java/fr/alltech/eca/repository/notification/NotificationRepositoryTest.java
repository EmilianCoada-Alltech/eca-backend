package fr.alltech.eca.repository.notification;

import fr.alltech.eca.model.notification.Notification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du repository Notification.
 * Vérifie la persistance des notifications ainsi que les recherches
 * par identifiant de collaborateur et par statut de lecture.
 */
@DataMongoTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Supprime les données de la collection après chaque test
     * afin d'éviter toute interférence entre les exécutions.
     */
    @AfterEach
    void cleanUp() {
        notificationRepository.deleteAll();
    }

    /**
     * Vérifie qu'une notification peut être enregistrée puis récupérée
     * avec toutes ses informations intactes.
     */
    @Test
    void shouldSaveAndRetrieveNotification() {
        // Préparation : définit une date fixe pour rendre le test reproductible.
        LocalDateTime createdAt = LocalDateTime.of(
                2026, 11, 25, 11, 0
        );

        // Préparation : crée une notification destinée à un collaborateur.
        Notification notification = new Notification(
                "notification-123",
                "collaborator-123",
                "PROFILE_UPDATE",
                "Your profile has been updated.",
                "/profile",
                false,
                createdAt
        );

        // Exécution : enregistre la notification dans MongoDB.
        Notification savedNotification =
                notificationRepository.save(notification);

        // Vérification : confirme que l'identifiant de la notification est conservé.
        assertThat(savedNotification.getId())
                .isEqualTo("notification-123");

        // Récupération et vérification : recherche la notification par son identifiant
        // et contrôle l'ensemble de ses informations.
        assertThat(notificationRepository.findById("notification-123"))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getCollaboratorId())
                            .isEqualTo("collaborator-123");
                    assertThat(found.getType())
                            .isEqualTo("PROFILE_UPDATE");
                    assertThat(found.getMessage())
                            .isEqualTo("Your profile has been updated.");
                    assertThat(found.getLink()).isEqualTo("/profile");
                    assertThat(found.isRead()).isFalse();
                    assertThat(found.getCreatedAt()).isEqualTo(createdAt);
                });
    }

    /**
     * Vérifie que la recherche par identifiant de collaborateur
     * retourne uniquement les notifications qui lui sont destinées.
     */
    @Test
    void shouldFindNotificationsByCollaboratorId() {
        // Préparation : enregistre plusieurs notifications destinées
        // à deux collaborateurs différents.
        notificationRepository.saveAll(List.of(
                new Notification(
                        null,
                        "collaborator-123",
                        "PROFILE_UPDATE",
                        "Profile updated",
                        null,
                        false,
                        LocalDateTime.now()
                ),
                new Notification(
                        null,
                        "collaborator-123",
                        "NEW_MESSAGE",
                        "You have a new message",
                        "/messages",
                        true,
                        LocalDateTime.now()
                ),
                new Notification(
                        null,
                        "collaborator-456",
                        "PROFILE_UPDATE",
                        "Another collaborator notification",
                        null,
                        false,
                        LocalDateTime.now()
                )
        ));

        // Exécution : récupère les notifications du collaborateur ciblé.
        List<Notification> notifications =
                notificationRepository.findByCollaboratorId(
                        "collaborator-123"
                );

        // Vérification : confirme que deux notifications sont retournées
        // et qu'elles appartiennent toutes au collaborateur demandé.
        assertThat(notifications).hasSize(2);
        assertThat(notifications)
                .allMatch(n -> n.getCollaboratorId()
                        .equals("collaborator-123"));
    }

    /**
     * Vérifie que la recherche des notifications non lues
     * retourne uniquement celles du collaborateur concerné.
     */
    @Test
    void shouldFindUnreadNotificationsByCollaboratorId() {
        // Préparation : enregistre une notification lue et une notification non lue
        // pour le même collaborateur.
        notificationRepository.saveAll(List.of(
                new Notification(
                        null,
                        "collaborator-123",
                        "PROFILE_UPDATE",
                        "Profile updated",
                        null,
                        false,
                        LocalDateTime.now()
                ),
                new Notification(
                        null,
                        "collaborator-123",
                        "NEW_MESSAGE",
                        "You have a new message",
                        "/messages",
                        true,
                        LocalDateTime.now()
                )
        ));

        // Exécution : recherche uniquement les notifications non lues
        // du collaborateur ciblé.
        List<Notification> unreadNotifications =
                notificationRepository.findByCollaboratorIdAndReadFalse(
                        "collaborator-123"
                );

        // Vérification : confirme qu'une seule notification est retournée
        // et que son statut indique bien qu'elle n'a pas été lue.
        assertThat(unreadNotifications).hasSize(1);
        assertThat(unreadNotifications.get(0).isRead()).isFalse();
    }
}