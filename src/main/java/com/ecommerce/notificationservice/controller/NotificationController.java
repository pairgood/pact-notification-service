package com.ecommerce.notificationservice.controller;

import com.ecommerce.notificationservice.dto.*;
import com.ecommerce.notificationservice.model.Notification;
import com.ecommerce.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping("/order-confirmation")
    public ResponseEntity<Notification> sendOrderConfirmation(@RequestBody OrderConfirmationRequest request) {
        Notification notification = notificationService.sendOrderConfirmation(
            request.getOrderId(), request.getUserId()
        );
        return ResponseEntity.ok(notification);
    }
    
    @PostMapping("/order-status")
    public ResponseEntity<Notification> sendOrderStatusUpdate(@RequestBody OrderStatusRequest request) {
        Notification notification = notificationService.sendOrderStatusUpdate(
            request.getOrderId(), request.getUserId(), request.getStatus()
        );
        return ResponseEntity.ok(notification);
    }
    
    @PostMapping("/order-cancellation")
    public ResponseEntity<Notification> sendOrderCancellation(@RequestBody OrderCancellationRequest request) {
        Notification notification = notificationService.sendOrderCancellation(
            request.getOrderId(), request.getUserId()
        );
        return ResponseEntity.ok(notification);
    }
    
    @PostMapping("/payment-confirmation")
    public ResponseEntity<Notification> sendPaymentConfirmation(@RequestBody PaymentConfirmationRequest request) {
        Notification notification = notificationService.sendPaymentConfirmation(
            request.getPaymentId(), request.getUserId(), request.getOrderId()
        );
        return ResponseEntity.ok(notification);
    }
    
    @PostMapping("/payment-failure")
    public ResponseEntity<Notification> sendPaymentFailure(@RequestBody PaymentFailureRequest request) {
        Notification notification = notificationService.sendPaymentFailure(
            request.getPaymentId(), request.getUserId(), request.getOrderId()
        );
        return ResponseEntity.ok(notification);
    }
    
    @PostMapping("/refund-confirmation")
    public ResponseEntity<Notification> sendRefundConfirmation(@RequestBody RefundConfirmationRequest request) {
        Notification notification = notificationService.sendRefundConfirmation(
            request.getPaymentId(), request.getUserId(), request.getOrderId()
        );
        return ResponseEntity.ok(notification);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }
}