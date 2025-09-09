package com.ecommerce.notificationservice.contracts.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocation;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import com.ecommerce.notificationservice.dto.UserResponse;
import com.ecommerce.notificationservice.model.Notification;
import com.ecommerce.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Provider("notifications") // Provider service name in PactMart
@PactBroker
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationsProviderTest {
    
    @LocalServerPort
    private int port;
    
    @MockBean
    private NotificationRepository notificationRepository;
    
    @MockBean
    private com.ecommerce.notificationservice.client.UserServiceClient userServiceClient;
    
    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }
    
    @TestTemplate
    @ExtendWith(PactVerificationInvocation.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
    
    @State("valid order confirmation request")
    void setupValidOrderConfirmation() {
        // Setup user service mock
        UserResponse user = new UserResponse(1L, "user@example.com", "John", "Doe", "+1234567890");
        when(userServiceClient.getUserById(1L)).thenReturn(user);
        
        // Setup test data for order confirmation scenario
        Notification notification = new Notification(
            1L,
            Notification.NotificationType.ORDER_CONFIRMATION,
            "Order Confirmation - Order #12345",
            "Thank you for your order! Your order #12345 has been confirmed and is being processed."
        );
        notification.setId(100L);
        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
    }
    
    @State("valid order status update request")
    void setupValidOrderStatusUpdate() {
        // Setup user service mock
        UserResponse user = new UserResponse(1L, "user@example.com", "John", "Doe", "+1234567890");
        when(userServiceClient.getUserById(1L)).thenReturn(user);
        
        // Setup test data for order status update scenario
        Notification notification = new Notification(
            1L,
            Notification.NotificationType.ORDER_STATUS_UPDATE,
            "Order Status Update - Order #12345",
            "Your order #12345 status has been updated to: Shipped"
        );
        notification.setId(101L);
        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
    }
    
    @State("valid payment confirmation request")
    void setupValidPaymentConfirmation() {
        // Setup user service mock
        UserResponse user = new UserResponse(1L, "user@example.com", "John", "Doe", "+1234567890");
        when(userServiceClient.getUserById(1L)).thenReturn(user);
        
        // Setup test data for payment confirmation scenario
        Notification notification = new Notification(
            1L,
            Notification.NotificationType.PAYMENT_CONFIRMATION,
            "Payment Confirmation - Order #12345",
            "Your payment has been successfully processed for order #12345. Payment ID: 67890"
        );
        notification.setId(102L);
        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
    }
    
    @State("notification with id 100 exists")
    void setupNotificationExists() {
        // Setup test data for getting notification by ID
        Notification notification = new Notification(
            1L,
            Notification.NotificationType.ORDER_CONFIRMATION,
            "Order Confirmation - Order #12345",
            "Thank you for your order!"
        );
        notification.setId(100L);
        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));
    }
    
    @State("user with id 1 has notifications")
    void setupUserHasNotifications() {
        // Setup test data for getting notifications by user ID
        Notification notification1 = new Notification(
            1L,
            Notification.NotificationType.ORDER_CONFIRMATION,
            "Order Confirmation - Order #12345",
            "Thank you for your order!"
        );
        notification1.setId(100L);
        notification1.setStatus(Notification.NotificationStatus.SENT);
        
        Notification notification2 = new Notification(
            1L,
            Notification.NotificationType.PAYMENT_CONFIRMATION,
            "Payment Confirmation - Order #12345",
            "Payment processed successfully"
        );
        notification2.setId(101L);
        notification2.setStatus(Notification.NotificationStatus.SENT);
        
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationRepository.findByUserId(1L)).thenReturn(notifications);
    }
    
    @State("there are notifications in the system")
    void setupNotificationsExist() {
        // Setup test data for getting all notifications
        Notification notification1 = new Notification(
            1L,
            Notification.NotificationType.ORDER_CONFIRMATION,
            "Order Confirmation",
            "Order confirmed"
        );
        notification1.setId(100L);
        
        Notification notification2 = new Notification(
            2L,
            Notification.NotificationType.PAYMENT_CONFIRMATION,
            "Payment Confirmation",
            "Payment processed"
        );
        notification2.setId(101L);
        
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationRepository.findAll()).thenReturn(notifications);
    }
}