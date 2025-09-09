package com.ecommerce.notificationservice.client;

import com.ecommerce.notificationservice.dto.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {
    
    private final WebClient webClient;
    
    public UserServiceClient(@Value("${services.user.url:http://localhost:8081}") String userServiceUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(userServiceUrl)
            .build();
    }
    
    public UserResponse getUserById(Long userId) {
        return webClient.get()
            .uri("/api/users/{id}", userId)
            .retrieve()
            .bodyToMono(UserResponse.class)
            .block();
    }
}