package fr.alltech.eca.repository.notification;

import fr.alltech.eca.model.notification.Notification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @AfterEach
    void cleanUp() {
        notificationRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveNotification() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(
                2026, 11, 25, 11, 0
        );

        Notification notification = new Notification(
                "notification-123",
                "collaborator-123",
                "PROFILE_UPDATE",
                "Your profile has been updated.",
                "/profile",
                false,
                createdAt
        );

        // When
        Notification savedNotification =
                notificationRepository.save(notification);

        // Then
        assertThat(savedNotification.getId())
                .isEqualTo("notification-123");

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

    @Test
    void shouldFindNotificationsByCollaboratorId() {
        // Given
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

        // When
        List<Notification> notifications =
                notificationRepository.findByCollaboratorId(
                        "collaborator-123"
                );

        // Then
        assertThat(notifications).hasSize(2);
        assertThat(notifications)
                .allMatch(n -> n.getCollaboratorId()
                        .equals("collaborator-123"));
    }

    @Test
    void shouldFindUnreadNotificationsByCollaboratorId() {
        // Given
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

        // When
        List<Notification> unreadNotifications =
                notificationRepository.findByCollaboratorIdAndReadFalse(
                        "collaborator-123"
                );

        // Then
        assertThat(unreadNotifications).hasSize(1);
        assertThat(unreadNotifications.get(0).isRead()).isFalse();
    }
}
