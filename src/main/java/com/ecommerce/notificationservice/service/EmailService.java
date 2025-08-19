package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.model.Notification;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    public void sendNotification(Notification notification) {
        // Simulate email sending
        try {
            Thread.sleep(500); // Simulate email sending delay
            
            // Simulate random email failures (5% chance)
            if (Math.random() < 0.05) {
                throw new RuntimeException("Email service unavailable");
            }
            
            // In a real implementation, this would integrate with an email service like:
            // - SendGrid
            // - Amazon SES
            // - Mailgun
            // - SMTP server
            
            System.out.println("📧 Email sent successfully:");
            System.out.println("  To: User ID " + notification.getUserId());
            System.out.println("  Subject: " + notification.getSubject());
            System.out.println("  Type: " + notification.getType());
            System.out.println("  Message: " + notification.getMessage().substring(0, Math.min(50, notification.getMessage().length())) + "...");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Email sending interrupted");
        }
    }
    
    public void sendSMS(String phoneNumber, String message) {
        // Simulate SMS sending
        try {
            Thread.sleep(300); // Simulate SMS sending delay
            
            // Simulate random SMS failures (3% chance)
            if (Math.random() < 0.03) {
                throw new RuntimeException("SMS service unavailable");
            }
            
            System.out.println("📱 SMS sent successfully:");
            System.out.println("  To: " + phoneNumber);
            System.out.println("  Message: " + message);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("SMS sending interrupted");
        }
    }
}