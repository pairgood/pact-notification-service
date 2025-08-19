package com.ecommerce.notificationservice.dto;

public class OrderConfirmationRequest {
    private Long orderId;
    private Long userId;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}