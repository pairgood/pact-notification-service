package com.ecommerce.notificationservice;

import com.ecommerce.notificationservice.dto.*;
import com.ecommerce.notificationservice.model.Notification;
import com.ecommerce.notificationservice.repository.NotificationRepository;
import com.ecommerce.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NotificationServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/notifications";
        notificationRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
        assertThat(notificationService).isNotNull();
        assertThat(notificationRepository).isNotNull();
    }

    @Test
    void sendOrderConfirmation_ShouldCreateAndReturnNotification() {
        // Given
        OrderConfirmationRequest request = new OrderConfirmationRequest();
        request.setOrderId(123L);
        request.setUserId(456L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderConfirmationRequest> httpRequest = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<Notification> response = restTemplate.postForEntity(
            baseUrl + "/order-confirmation", httpRequest, Notification.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(456L);
        assertThat(response.getBody().getType()).isEqualTo(Notification.NotificationType.ORDER_CONFIRMATION);
        assertThat(response.getBody().getSubject()).contains("Order #123");
        assertThat(response.getBody().getMessage()).contains("order #123 has been confirmed");

        // Verify persistence
        List<Notification> notificationsInDb = notificationRepository.findAll();
        assertThat(notificationsInDb).hasSize(1);
        assertThat(notificationsInDb.get(0).getUserId()).isEqualTo(456L);
    }

    @Test
    void sendOrderStatusUpdate_ShouldCreateNotificationWithStatus() {
        // Given
        OrderStatusRequest request = new OrderStatusRequest();
        request.setOrderId(789L);
        request.setUserId(321L);
        request.setStatus("SHIPPED");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderStatusRequest> httpRequest = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<Notification> response = restTemplate.postForEntity(
            baseUrl + "/order-status", httpRequest, Notification.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(321L);
        assertThat(response.getBody().getType()).isEqualTo(Notification.NotificationType.ORDER_STATUS_UPDATE);
        assertThat(response.getBody().getSubject()).contains("Order Status Update - Order #789");
        assertThat(response.getBody().getMessage()).contains("order #789 status has been updated to: SHIPPED");

        // Verify persistence
        List<Notification> notificationsInDb = notificationRepository.findAll();
        assertThat(notificationsInDb).hasSize(1);
    }

    @Test
    void sendOrderCancellation_ShouldCreateCancellationNotification() {
        // Given
        OrderCancellationRequest request = new OrderCancellationRequest();
        request.setOrderId(999L);
        request.setUserId(111L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderCancellationRequest> httpRequest = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<Notification> response = restTemplate.postForEntity(
            baseUrl + "/order-cancellation", httpRequest, Notification.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(111L);
        assertThat(response.getBody().getType()).isEqualTo(Notification.NotificationType.ORDER_CANCELLATION);
        assertThat(response.getBody().getSubject()).contains("Order Cancelled - Order #999");
        assertThat(response.getBody().getMessage()).contains("order #999 has been cancelled");
    }

    @Test
    void sendPaymentConfirmation_ShouldCreatePaymentNotification() {
        // Given
        PaymentConfirmationRequest request = new PaymentConfirmationRequest();
        request.setPaymentId(555L);
        request.setUserId(666L);
        request.setOrderId(777L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentConfirmationRequest> httpRequest = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<Notification> response = restTemplate.postForEntity(
            baseUrl + "/payment-confirmation", httpRequest, Notification.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(666L);
        assertThat(response.getBody().getType()).isEqualTo(Notification.NotificationType.PAYMENT_CONFIRMATION);
        assertThat(response.getBody().getSubject()).contains("Payment Confirmation - Order #777");
        assertThat(response.getBody().getMessage()).contains("payment has been successfully processed for order #777");
        assertThat(response.getBody().getMessage()).contains("Payment ID: 555");
    }

    @Test
    void sendPaymentFailure_ShouldCreateFailureNotification() {
        // Given
        PaymentFailureRequest request = new PaymentFailureRequest();
        request.setPaymentId(888L);
        request.setUserId(999L);
        request.setOrderId(1111L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentFailureRequest> httpRequest = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<Notification> response = restTemplate.postForEntity(
            baseUrl + "/payment-failure", httpRequest, Notification.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(999L);
        assertThat(response.getBody().getType()).isEqualTo(Notification.NotificationType.PAYMENT_FAILURE);
        assertThat(response.getBody().getSubject()).contains("Payment Failed - Order #1111");
        assertThat(response.getBody().getMessage()).contains("Payment processing failed for order #1111");
    }

    @Test
    void sendRefundConfirmation_ShouldCreateRefundNotification() {
        // Given
        RefundConfirmationRequest request = new RefundConfirmationRequest();
        request.setPaymentId(2222L);
        request.setUserId(3333L);
        request.setOrderId(4444L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RefundConfirmationRequest> httpRequest = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<Notification> response = restTemplate.postForEntity(
            baseUrl + "/refund-confirmation", httpRequest, Notification.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(3333L);
        assertThat(response.getBody().getType()).isEqualTo(Notification.NotificationType.REFUND_CONFIRMATION);
        assertThat(response.getBody().getSubject()).contains("Refund Processed - Order #4444");
        assertThat(response.getBody().getMessage()).contains("refund has been processed for order #4444");
    }

    @Test
    void getNotificationById_ShouldReturnNotification() {
        // Given - Create a notification first
        Notification notification = notificationService.sendOrderConfirmation(123L, 456L);

        // When
        ResponseEntity<Notification> response = restTemplate.getForEntity(
            baseUrl + "/" + notification.getId(), Notification.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(notification.getId());
        assertThat(response.getBody().getUserId()).isEqualTo(456L);
    }

    @Test
    void getNotificationById_ShouldReturn500ForNonExistentNotification() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/999999", String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getNotificationsByUserId_ShouldReturnUserNotifications() {
        // Given - Create notifications for different users
        notificationService.sendOrderConfirmation(123L, 456L);
        notificationService.sendPaymentConfirmation(789L, 456L, 123L);
        notificationService.sendOrderConfirmation(999L, 888L); // Different user

        // When
        ResponseEntity<List<Notification>> response = restTemplate.exchange(
            baseUrl + "/user/456",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Notification>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).allMatch(notification -> notification.getUserId().equals(456L));
    }

    @Test
    void getNotificationsByUserId_ShouldReturnEmptyListForNonExistentUser() {
        // When
        ResponseEntity<List<Notification>> response = restTemplate.exchange(
            baseUrl + "/user/999999",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Notification>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotifications() {
        // Given - Create multiple notifications
        notificationService.sendOrderConfirmation(123L, 456L);
        notificationService.sendPaymentConfirmation(789L, 888L, 123L);
        notificationService.sendOrderCancellation(999L, 111L);

        // When
        ResponseEntity<List<Notification>> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Notification>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(3);
        assertThat(response.getBody()).extracting(Notification::getType)
            .containsExactlyInAnyOrder(
                Notification.NotificationType.ORDER_CONFIRMATION,
                Notification.NotificationType.PAYMENT_CONFIRMATION,
                Notification.NotificationType.ORDER_CANCELLATION
            );
    }

    @Test
    void getAllNotifications_ShouldReturnEmptyListWhenNoNotifications() {
        // When
        ResponseEntity<List<Notification>> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Notification>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void notificationLifecycle_ShouldWorkEndToEnd() {
        // Create an order confirmation
        OrderConfirmationRequest orderRequest = new OrderConfirmationRequest();
        orderRequest.setOrderId(12345L);
        orderRequest.setUserId(67890L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Notification> orderResponse = restTemplate.postForEntity(
            baseUrl + "/order-confirmation",
            new HttpEntity<>(orderRequest, headers),
            Notification.class
        );

        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Notification orderNotification = orderResponse.getBody();
        assertThat(orderNotification).isNotNull();

        // Update order status
        OrderStatusRequest statusRequest = new OrderStatusRequest();
        statusRequest.setOrderId(12345L);
        statusRequest.setUserId(67890L);
        statusRequest.setStatus("PROCESSING");

        ResponseEntity<Notification> statusResponse = restTemplate.postForEntity(
            baseUrl + "/order-status",
            new HttpEntity<>(statusRequest, headers),
            Notification.class
        );

        assertThat(statusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Confirm payment
        PaymentConfirmationRequest paymentRequest = new PaymentConfirmationRequest();
        paymentRequest.setPaymentId(98765L);
        paymentRequest.setUserId(67890L);
        paymentRequest.setOrderId(12345L);

        ResponseEntity<Notification> paymentResponse = restTemplate.postForEntity(
            baseUrl + "/payment-confirmation",
            new HttpEntity<>(paymentRequest, headers),
            Notification.class
        );

        assertThat(paymentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify user has 3 notifications
        ResponseEntity<List<Notification>> userNotifications = restTemplate.exchange(
            baseUrl + "/user/67890",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Notification>>() {}
        );

        assertThat(userNotifications.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userNotifications.getBody()).hasSize(3);

        // Verify all notifications in database
        List<Notification> allNotifications = notificationRepository.findAll();
        assertThat(allNotifications).hasSize(3);
        assertThat(allNotifications).allMatch(notification -> notification.getUserId().equals(67890L));
    }

    @Test
    void notificationPersistence_ShouldHandleMultipleOperations() {
        // Create notifications
        notificationService.sendOrderConfirmation(1L, 100L);
        notificationService.sendOrderConfirmation(2L, 200L);
        notificationService.sendPaymentConfirmation(1L, 100L, 1L);

        // Verify database state
        assertThat(notificationRepository.count()).isEqualTo(3);

        List<Notification> user100Notifications = notificationRepository.findByUserId(100L);
        assertThat(user100Notifications).hasSize(2);

        List<Notification> user200Notifications = notificationRepository.findByUserId(200L);
        assertThat(user200Notifications).hasSize(1);

        List<Notification> orderConfirmations = notificationRepository.findByType(Notification.NotificationType.ORDER_CONFIRMATION);
        assertThat(orderConfirmations).hasSize(2);

        List<Notification> paymentConfirmations = notificationRepository.findByType(Notification.NotificationType.PAYMENT_CONFIRMATION);
        assertThat(paymentConfirmations).hasSize(1);
    }

    @Test
    void notifications_ShouldHandleEmailServiceFailures() {
        // This test verifies that notifications are still saved even if email sending fails
        // The email service has a 5% random failure rate

        int successCount = 0;
        int failureCount = 0;

        // Try multiple notifications to encounter both success and failure scenarios
        for (int i = 0; i < 20; i++) {
            Notification notification = notificationService.sendOrderConfirmation((long) i, 1L);
            if (notification.getStatus() == Notification.NotificationStatus.SENT) {
                successCount++;
            } else if (notification.getStatus() == Notification.NotificationStatus.FAILED) {
                failureCount++;
            }
        }

        // All notifications should be persisted regardless of email sending status
        assertThat(notificationRepository.count()).isEqualTo(20);
        assertThat(successCount + failureCount).isEqualTo(20);

        // With 5% failure rate, we should see both successes and likely some failures
        // Note: This is probabilistic, but with 20 attempts, we should see mostly successes
        assertThat(successCount).isGreaterThan(0);
    }
}