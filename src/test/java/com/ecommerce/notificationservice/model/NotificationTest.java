package com.ecommerce.notificationservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void notification_ShouldCreateWithDefaultConstructor() {
        Notification notification = new Notification();
        
        assertThat(notification.getId()).isNull();
        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notification.getStatus()).isEqualTo(Notification.NotificationStatus.PENDING);
        assertThat(notification.getSentAt()).isNull();
    }

    @Test
    void notification_ShouldCreateWithParameterizedConstructor() {
        Long userId = 123L;
        Notification.NotificationType type = Notification.NotificationType.ORDER_CONFIRMATION;
        String subject = "Order Confirmed";
        String message = "Your order has been confirmed";

        Notification notification = new Notification(userId, type, subject, message);

        assertThat(notification.getUserId()).isEqualTo(userId);
        assertThat(notification.getType()).isEqualTo(type);
        assertThat(notification.getSubject()).isEqualTo(subject);
        assertThat(notification.getMessage()).isEqualTo(message);
        assertThat(notification.getStatus()).isEqualTo(Notification.NotificationStatus.PENDING);
        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notification.getSentAt()).isNull();
    }

    @Test
    void notification_ShouldSetAndGetAllFields() {
        Notification notification = new Notification();
        Long id = 1L;
        Long userId = 456L;
        Notification.NotificationType type = Notification.NotificationType.PAYMENT_CONFIRMATION;
        String subject = "Payment Confirmed";
        String message = "Your payment has been processed";
        Notification.NotificationStatus status = Notification.NotificationStatus.SENT;
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime sentAt = LocalDateTime.now();
        String recipientEmail = "user@example.com";
        String recipientPhone = "+1234567890";

        notification.setId(id);
        notification.setUserId(userId);
        notification.setType(type);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setStatus(status);
        notification.setCreatedAt(createdAt);
        notification.setSentAt(sentAt);
        notification.setRecipientEmail(recipientEmail);
        notification.setRecipientPhone(recipientPhone);

        assertThat(notification.getId()).isEqualTo(id);
        assertThat(notification.getUserId()).isEqualTo(userId);
        assertThat(notification.getType()).isEqualTo(type);
        assertThat(notification.getSubject()).isEqualTo(subject);
        assertThat(notification.getMessage()).isEqualTo(message);
        assertThat(notification.getStatus()).isEqualTo(status);
        assertThat(notification.getCreatedAt()).isEqualTo(createdAt);
        assertThat(notification.getSentAt()).isEqualTo(sentAt);
        assertThat(notification.getRecipientEmail()).isEqualTo(recipientEmail);
        assertThat(notification.getRecipientPhone()).isEqualTo(recipientPhone);
    }

    @Test
    void notificationType_ShouldHaveAllExpectedValues() {
        Notification.NotificationType[] types = Notification.NotificationType.values();
        
        assertThat(types).hasSize(8);
        assertThat(types).contains(
            Notification.NotificationType.ORDER_CONFIRMATION,
            Notification.NotificationType.ORDER_STATUS_UPDATE,
            Notification.NotificationType.ORDER_CANCELLATION,
            Notification.NotificationType.PAYMENT_CONFIRMATION,
            Notification.NotificationType.PAYMENT_FAILURE,
            Notification.NotificationType.REFUND_CONFIRMATION,
            Notification.NotificationType.ACCOUNT_WELCOME,
            Notification.NotificationType.PASSWORD_RESET
        );
    }

    @Test
    void notificationStatus_ShouldHaveAllExpectedValues() {
        Notification.NotificationStatus[] statuses = Notification.NotificationStatus.values();
        
        assertThat(statuses).hasSize(4);
        assertThat(statuses).contains(
            Notification.NotificationStatus.PENDING,
            Notification.NotificationStatus.SENT,
            Notification.NotificationStatus.FAILED,
            Notification.NotificationStatus.RETRY
        );
    }

    @Test
    void notificationType_ShouldConvertToString() {
        assertThat(Notification.NotificationType.ORDER_CONFIRMATION.toString()).isEqualTo("ORDER_CONFIRMATION");
        assertThat(Notification.NotificationType.PAYMENT_FAILURE.toString()).isEqualTo("PAYMENT_FAILURE");
    }

    @Test
    void notificationStatus_ShouldConvertToString() {
        assertThat(Notification.NotificationStatus.PENDING.toString()).isEqualTo("PENDING");
        assertThat(Notification.NotificationStatus.SENT.toString()).isEqualTo("SENT");
        assertThat(Notification.NotificationStatus.FAILED.toString()).isEqualTo("FAILED");
        assertThat(Notification.NotificationStatus.RETRY.toString()).isEqualTo("RETRY");
    }

    @Test
    void notification_ShouldCreateWithNullValues() {
        Notification notification = new Notification(null, null, null, null);
        
        assertThat(notification.getUserId()).isNull();
        assertThat(notification.getType()).isNull();
        assertThat(notification.getSubject()).isNull();
        assertThat(notification.getMessage()).isNull();
        assertThat(notification.getStatus()).isEqualTo(Notification.NotificationStatus.PENDING);
        assertThat(notification.getCreatedAt()).isNotNull();
    }

    @Test
    void notification_ShouldHandleEdgeCaseValues() {
        Notification notification = new Notification();
        
        notification.setSubject("");
        notification.setMessage("");
        notification.setRecipientEmail("");
        notification.setRecipientPhone("");
        
        assertThat(notification.getSubject()).isEqualTo("");
        assertThat(notification.getMessage()).isEqualTo("");
        assertThat(notification.getRecipientEmail()).isEqualTo("");
        assertThat(notification.getRecipientPhone()).isEqualTo("");
    }

    @Test
    void notification_ShouldHandleLongMessages() {
        String longMessage = "A".repeat(1000);
        Notification notification = new Notification(1L, Notification.NotificationType.ORDER_CONFIRMATION, "Subject", longMessage);
        
        assertThat(notification.getMessage()).hasSize(1000);
        assertThat(notification.getMessage()).isEqualTo(longMessage);
    }

    @Test
    void notification_ShouldAllowMultipleStatusTransitions() {
        Notification notification = new Notification();
        
        assertThat(notification.getStatus()).isEqualTo(Notification.NotificationStatus.PENDING);
        
        notification.setStatus(Notification.NotificationStatus.FAILED);
        assertThat(notification.getStatus()).isEqualTo(Notification.NotificationStatus.FAILED);
        
        notification.setStatus(Notification.NotificationStatus.RETRY);
        assertThat(notification.getStatus()).isEqualTo(Notification.NotificationStatus.RETRY);
        
        notification.setStatus(Notification.NotificationStatus.SENT);
        assertThat(notification.getStatus()).isEqualTo(Notification.NotificationStatus.SENT);
    }
}