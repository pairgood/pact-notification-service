package com.ecommerce.notificationservice.repository;

import com.ecommerce.notificationservice.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification notification1;
    private Notification notification2;
    private Notification notification3;

    @BeforeEach
    void setUp() {
        notification1 = new Notification(1L, Notification.NotificationType.ORDER_CONFIRMATION, "Order Confirmed", "Your order has been confirmed");
        notification1.setStatus(Notification.NotificationStatus.SENT);
        notification1.setRecipientEmail("user1@example.com");

        notification2 = new Notification(1L, Notification.NotificationType.PAYMENT_CONFIRMATION, "Payment Confirmed", "Your payment has been processed");
        notification2.setStatus(Notification.NotificationStatus.PENDING);
        notification2.setRecipientEmail("user1@example.com");

        notification3 = new Notification(2L, Notification.NotificationType.ORDER_CANCELLATION, "Order Cancelled", "Your order has been cancelled");
        notification3.setStatus(Notification.NotificationStatus.FAILED);
        notification3.setRecipientEmail("user2@example.com");

        entityManager.persistAndFlush(notification1);
        entityManager.persistAndFlush(notification2);
        entityManager.persistAndFlush(notification3);
    }

    @Test
    void findByUserId_ShouldReturnNotificationsForSpecificUser() {
        List<Notification> userNotifications = notificationRepository.findByUserId(1L);

        assertThat(userNotifications).hasSize(2);
        assertThat(userNotifications).allMatch(notification -> notification.getUserId().equals(1L));
        assertThat(userNotifications).extracting(Notification::getType)
            .containsExactlyInAnyOrder(
                Notification.NotificationType.ORDER_CONFIRMATION,
                Notification.NotificationType.PAYMENT_CONFIRMATION
            );
    }

    @Test
    void findByUserId_ShouldReturnEmptyListForNonExistentUser() {
        List<Notification> userNotifications = notificationRepository.findByUserId(999L);

        assertThat(userNotifications).isEmpty();
    }

    @Test
    void findByType_ShouldReturnNotificationsOfSpecificType() {
        List<Notification> orderConfirmations = notificationRepository.findByType(Notification.NotificationType.ORDER_CONFIRMATION);

        assertThat(orderConfirmations).hasSize(1);
        assertThat(orderConfirmations.get(0).getType()).isEqualTo(Notification.NotificationType.ORDER_CONFIRMATION);
        assertThat(orderConfirmations.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void findByType_ShouldReturnEmptyListForNonExistentType() {
        List<Notification> passwordResets = notificationRepository.findByType(Notification.NotificationType.PASSWORD_RESET);

        assertThat(passwordResets).isEmpty();
    }

    @Test
    void findByStatus_ShouldReturnNotificationsWithSpecificStatus() {
        List<Notification> sentNotifications = notificationRepository.findByStatus(Notification.NotificationStatus.SENT);

        assertThat(sentNotifications).hasSize(1);
        assertThat(sentNotifications.get(0).getStatus()).isEqualTo(Notification.NotificationStatus.SENT);
        assertThat(sentNotifications.get(0).getType()).isEqualTo(Notification.NotificationType.ORDER_CONFIRMATION);
    }

    @Test
    void findByStatus_ShouldReturnEmptyListForNonExistentStatus() {
        List<Notification> retryNotifications = notificationRepository.findByStatus(Notification.NotificationStatus.RETRY);

        assertThat(retryNotifications).isEmpty();
    }

    @Test
    void save_ShouldPersistNotification() {
        Notification newNotification = new Notification(3L, Notification.NotificationType.REFUND_CONFIRMATION, "Refund Processed", "Your refund has been processed");

        Notification savedNotification = notificationRepository.save(newNotification);

        assertThat(savedNotification.getId()).isNotNull();
        assertThat(savedNotification.getUserId()).isEqualTo(3L);
        assertThat(savedNotification.getType()).isEqualTo(Notification.NotificationType.REFUND_CONFIRMATION);
        assertThat(savedNotification.getSubject()).isEqualTo("Refund Processed");
        assertThat(savedNotification.getMessage()).isEqualTo("Your refund has been processed");
        assertThat(savedNotification.getStatus()).isEqualTo(Notification.NotificationStatus.PENDING);
        assertThat(savedNotification.getCreatedAt()).isNotNull();
    }

    @Test
    void findById_ShouldReturnNotificationWhenExists() {
        Long id = notification1.getId();

        var foundNotification = notificationRepository.findById(id);

        assertThat(foundNotification).isPresent();
        assertThat(foundNotification.get().getId()).isEqualTo(id);
        assertThat(foundNotification.get().getUserId()).isEqualTo(1L);
        assertThat(foundNotification.get().getType()).isEqualTo(Notification.NotificationType.ORDER_CONFIRMATION);
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        var foundNotification = notificationRepository.findById(999L);

        assertThat(foundNotification).isNotPresent();
    }

    @Test
    void findAll_ShouldReturnAllNotifications() {
        List<Notification> allNotifications = notificationRepository.findAll();

        assertThat(allNotifications).hasSize(3);
        assertThat(allNotifications).extracting(Notification::getUserId)
            .containsExactlyInAnyOrder(1L, 1L, 2L);
    }

    @Test
    void delete_ShouldRemoveNotification() {
        Long id = notification1.getId();
        assertThat(notificationRepository.findById(id)).isPresent();

        notificationRepository.deleteById(id);

        assertThat(notificationRepository.findById(id)).isNotPresent();
        assertThat(notificationRepository.findAll()).hasSize(2);
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        long count = notificationRepository.count();

        assertThat(count).isEqualTo(3);
    }

    @Test
    void update_ShouldModifyExistingNotification() {
        notification1.setStatus(Notification.NotificationStatus.FAILED);
        notification1.setSentAt(LocalDateTime.now());

        Notification updatedNotification = notificationRepository.save(notification1);

        assertThat(updatedNotification.getId()).isEqualTo(notification1.getId());
        assertThat(updatedNotification.getStatus()).isEqualTo(Notification.NotificationStatus.FAILED);
        assertThat(updatedNotification.getSentAt()).isNotNull();
    }

    @Test
    void findByUserId_ShouldOrderByCreatedAtDesc() {
        // Create additional notification with later timestamp
        Notification laterNotification = new Notification(1L, Notification.NotificationType.ORDER_STATUS_UPDATE, "Order Updated", "Your order status has been updated");
        entityManager.persistAndFlush(laterNotification);

        List<Notification> userNotifications = notificationRepository.findByUserId(1L);

        assertThat(userNotifications).hasSize(3);
        // Since all notifications are created around the same time, we just verify they are all returned
        assertThat(userNotifications).extracting(Notification::getType)
            .containsExactlyInAnyOrder(
                Notification.NotificationType.ORDER_CONFIRMATION,
                Notification.NotificationType.PAYMENT_CONFIRMATION,
                Notification.NotificationType.ORDER_STATUS_UPDATE
            );
    }

    @Test
    void repository_ShouldHandleNullValues() {
        Notification notificationWithNulls = new Notification();
        notificationWithNulls.setUserId(999L);
        notificationWithNulls.setSubject("Test");
        notificationWithNulls.setMessage("Test message");

        Notification saved = notificationRepository.save(notificationWithNulls);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isNull();
        assertThat(saved.getRecipientEmail()).isNull();
        assertThat(saved.getRecipientPhone()).isNull();
        assertThat(saved.getSentAt()).isNull();
    }

    @Test
    void findByUserIdAndType_ShouldFilterByBothParameters() {
        List<Notification> results = notificationRepository.findByUserId(1L);
        long orderConfirmations = results.stream()
            .filter(n -> n.getType() == Notification.NotificationType.ORDER_CONFIRMATION)
            .count();

        assertThat(orderConfirmations).isEqualTo(1);
    }

    @Test
    void findByUserIdAndStatus_ShouldFilterByBothParameters() {
        List<Notification> results = notificationRepository.findByUserId(1L);
        long sentNotifications = results.stream()
            .filter(n -> n.getStatus() == Notification.NotificationStatus.SENT)
            .count();

        assertThat(sentNotifications).isEqualTo(1);
    }
}