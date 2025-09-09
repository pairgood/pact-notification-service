package com.ecommerce.notificationservice.contracts.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.ecommerce.notificationservice.client.UserServiceClient;
import com.ecommerce.notificationservice.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "users") // Provider service name in PactMart
class UsersConsumerTest {
    
    @Pact(consumer = "notifications") // Consumer service name in PactMart
    public V4Pact getUserByIdPact(PactDslWithProvider builder) {
        return builder
            .given("user with id 1 exists")
            .uponReceiving("a request to get user by id")
            .path("/api/users/1")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers("Content-Type", "application/json")
            .body(LambdaDsl.newJsonBody(body -> body
                // Liberal validation - only validate fields actually used in NotificationService
                .numberType("id") // Used in NotificationService.java line 126: getUserById(notification.getUserId())
                .stringType("email") // Used in NotificationService.java line 128: setRecipientEmail(user.getEmail())
                .stringType("phoneNumber") // Used in NotificationService.java line 129: setRecipientPhone(user.getPhoneNumber())
                // Note: firstName, lastName not validated - we don't use these fields in notification processing
            ).build())
            .toPact(V4Pact.class);
    }
    
    @Test
    @PactTestFor(pactMethod = "getUserByIdPact")
    void testGetUserById(MockServer mockServer) {
        // Test implementation that demonstrates usage
        UserServiceClient userServiceClient = new UserServiceClient(mockServer.getUrl());
        
        UserResponse user = userServiceClient.getUserById(1L);
        
        // Verify the fields we actually use in the notification service
        assertNotNull(user);
        assertNotNull(user.getId()); // Used to correlate with notification.getUserId()
        assertNotNull(user.getEmail()); // Used to set recipient email for notifications
        assertNotNull(user.getPhoneNumber()); // Used to set recipient phone for SMS notifications
        
        assertEquals(1L, user.getId());
        assertTrue(user.getEmail().contains("@")); // Basic email validation
    }
    
    @Pact(consumer = "notifications")
    public V4Pact getUserByIdNotFoundPact(PactDslWithProvider builder) {
        return builder
            .given("user with id 999 does not exist")
            .uponReceiving("a request to get non-existent user")
            .path("/api/users/999")
            .method("GET")
            .willRespondWith()
            .status(404)
            .headers("Content-Type", "application/json")
            .body(LambdaDsl.newJsonBody(body -> body
                .stringType("error", "User not found")
                .numberType("userId", 999)
            ).build())
            .toPact(V4Pact.class);
    }
    
    @Test
    @PactTestFor(pactMethod = "getUserByIdNotFoundPact")
    void testGetUserByIdNotFound(MockServer mockServer) {
        UserServiceClient userServiceClient = new UserServiceClient(mockServer.getUrl());
        
        // This should handle the error gracefully as per NotificationService.java line 131
        assertThrows(Exception.class, () -> {
            userServiceClient.getUserById(999L);
        });
    }
}