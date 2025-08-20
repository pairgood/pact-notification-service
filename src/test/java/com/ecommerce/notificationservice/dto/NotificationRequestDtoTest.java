package com.ecommerce.notificationservice.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationRequestDtoTest {

    @Test
    void orderConfirmationRequest_ShouldSetAndGetAllFields() {
        OrderConfirmationRequest request = new OrderConfirmationRequest();
        Long orderId = 123L;
        Long userId = 456L;

        request.setOrderId(orderId);
        request.setUserId(userId);

        assertThat(request.getOrderId()).isEqualTo(orderId);
        assertThat(request.getUserId()).isEqualTo(userId);
    }

    @Test
    void orderConfirmationRequest_ShouldHandleNullValues() {
        OrderConfirmationRequest request = new OrderConfirmationRequest();

        request.setOrderId(null);
        request.setUserId(null);

        assertThat(request.getOrderId()).isNull();
        assertThat(request.getUserId()).isNull();
    }

    @Test
    void orderConfirmationRequest_ShouldHandleZeroValues() {
        OrderConfirmationRequest request = new OrderConfirmationRequest();

        request.setOrderId(0L);
        request.setUserId(0L);

        assertThat(request.getOrderId()).isEqualTo(0L);
        assertThat(request.getUserId()).isEqualTo(0L);
    }

    @Test
    void orderConfirmationRequest_ShouldHandleLargeValues() {
        OrderConfirmationRequest request = new OrderConfirmationRequest();

        request.setOrderId(Long.MAX_VALUE);
        request.setUserId(Long.MAX_VALUE);

        assertThat(request.getOrderId()).isEqualTo(Long.MAX_VALUE);
        assertThat(request.getUserId()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void orderStatusRequest_ShouldSetAndGetAllFields() {
        OrderStatusRequest request = new OrderStatusRequest();
        Long orderId = 789L;
        Long userId = 321L;
        String status = "SHIPPED";

        request.setOrderId(orderId);
        request.setUserId(userId);
        request.setStatus(status);

        assertThat(request.getOrderId()).isEqualTo(orderId);
        assertThat(request.getUserId()).isEqualTo(userId);
        assertThat(request.getStatus()).isEqualTo(status);
    }

    @Test
    void orderStatusRequest_ShouldHandleNullValues() {
        OrderStatusRequest request = new OrderStatusRequest();

        request.setOrderId(null);
        request.setUserId(null);
        request.setStatus(null);

        assertThat(request.getOrderId()).isNull();
        assertThat(request.getUserId()).isNull();
        assertThat(request.getStatus()).isNull();
    }

    @Test
    void orderStatusRequest_ShouldHandleEmptyStatus() {
        OrderStatusRequest request = new OrderStatusRequest();

        request.setStatus("");

        assertThat(request.getStatus()).isEqualTo("");
    }

    @Test
    void orderCancellationRequest_ShouldSetAndGetAllFields() {
        OrderCancellationRequest request = new OrderCancellationRequest();
        Long orderId = 999L;
        Long userId = 888L;

        request.setOrderId(orderId);
        request.setUserId(userId);

        assertThat(request.getOrderId()).isEqualTo(orderId);
        assertThat(request.getUserId()).isEqualTo(userId);
    }

    @Test
    void orderCancellationRequest_ShouldHandleNullValues() {
        OrderCancellationRequest request = new OrderCancellationRequest();

        request.setOrderId(null);
        request.setUserId(null);

        assertThat(request.getOrderId()).isNull();
        assertThat(request.getUserId()).isNull();
    }

    @Test
    void orderCancellationRequest_ShouldHandleNegativeValues() {
        OrderCancellationRequest request = new OrderCancellationRequest();

        request.setOrderId(-1L);
        request.setUserId(-2L);

        assertThat(request.getOrderId()).isEqualTo(-1L);
        assertThat(request.getUserId()).isEqualTo(-2L);
    }

    @Test
    void paymentConfirmationRequest_ShouldSetAndGetAllFields() {
        PaymentConfirmationRequest request = new PaymentConfirmationRequest();
        Long paymentId = 111L;
        Long userId = 222L;
        Long orderId = 333L;

        request.setPaymentId(paymentId);
        request.setUserId(userId);
        request.setOrderId(orderId);

        assertThat(request.getPaymentId()).isEqualTo(paymentId);
        assertThat(request.getUserId()).isEqualTo(userId);
        assertThat(request.getOrderId()).isEqualTo(orderId);
    }

    @Test
    void paymentConfirmationRequest_ShouldHandleNullValues() {
        PaymentConfirmationRequest request = new PaymentConfirmationRequest();

        request.setPaymentId(null);
        request.setUserId(null);
        request.setOrderId(null);

        assertThat(request.getPaymentId()).isNull();
        assertThat(request.getUserId()).isNull();
        assertThat(request.getOrderId()).isNull();
    }

    @Test
    void paymentConfirmationRequest_ShouldHandleZeroValues() {
        PaymentConfirmationRequest request = new PaymentConfirmationRequest();

        request.setPaymentId(0L);
        request.setUserId(0L);
        request.setOrderId(0L);

        assertThat(request.getPaymentId()).isEqualTo(0L);
        assertThat(request.getUserId()).isEqualTo(0L);
        assertThat(request.getOrderId()).isEqualTo(0L);
    }

    @Test
    void paymentFailureRequest_ShouldSetAndGetAllFields() {
        PaymentFailureRequest request = new PaymentFailureRequest();
        Long paymentId = 444L;
        Long userId = 555L;
        Long orderId = 666L;

        request.setPaymentId(paymentId);
        request.setUserId(userId);
        request.setOrderId(orderId);

        assertThat(request.getPaymentId()).isEqualTo(paymentId);
        assertThat(request.getUserId()).isEqualTo(userId);
        assertThat(request.getOrderId()).isEqualTo(orderId);
    }

    @Test
    void paymentFailureRequest_ShouldHandleNullValues() {
        PaymentFailureRequest request = new PaymentFailureRequest();

        request.setPaymentId(null);
        request.setUserId(null);
        request.setOrderId(null);

        assertThat(request.getPaymentId()).isNull();
        assertThat(request.getUserId()).isNull();
        assertThat(request.getOrderId()).isNull();
    }

    @Test
    void paymentFailureRequest_ShouldHandleLargeValues() {
        PaymentFailureRequest request = new PaymentFailureRequest();

        request.setPaymentId(Long.MAX_VALUE);
        request.setUserId(Long.MAX_VALUE);
        request.setOrderId(Long.MAX_VALUE);

        assertThat(request.getPaymentId()).isEqualTo(Long.MAX_VALUE);
        assertThat(request.getUserId()).isEqualTo(Long.MAX_VALUE);
        assertThat(request.getOrderId()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void refundConfirmationRequest_ShouldSetAndGetAllFields() {
        RefundConfirmationRequest request = new RefundConfirmationRequest();
        Long paymentId = 777L;
        Long userId = 888L;
        Long orderId = 999L;

        request.setPaymentId(paymentId);
        request.setUserId(userId);
        request.setOrderId(orderId);

        assertThat(request.getPaymentId()).isEqualTo(paymentId);
        assertThat(request.getUserId()).isEqualTo(userId);
        assertThat(request.getOrderId()).isEqualTo(orderId);
    }

    @Test
    void refundConfirmationRequest_ShouldHandleNullValues() {
        RefundConfirmationRequest request = new RefundConfirmationRequest();

        request.setPaymentId(null);
        request.setUserId(null);
        request.setOrderId(null);

        assertThat(request.getPaymentId()).isNull();
        assertThat(request.getUserId()).isNull();
        assertThat(request.getOrderId()).isNull();
    }

    @Test
    void refundConfirmationRequest_ShouldHandleNegativeValues() {
        RefundConfirmationRequest request = new RefundConfirmationRequest();

        request.setPaymentId(-100L);
        request.setUserId(-200L);
        request.setOrderId(-300L);

        assertThat(request.getPaymentId()).isEqualTo(-100L);
        assertThat(request.getUserId()).isEqualTo(-200L);
        assertThat(request.getOrderId()).isEqualTo(-300L);
    }

    @Test
    void orderStatusRequest_ShouldHandleLongStatus() {
        OrderStatusRequest request = new OrderStatusRequest();
        String longStatus = "A".repeat(1000);

        request.setStatus(longStatus);

        assertThat(request.getStatus()).isEqualTo(longStatus);
        assertThat(request.getStatus()).hasSize(1000);
    }

    @Test
    void orderStatusRequest_ShouldHandleSpecialCharactersInStatus() {
        OrderStatusRequest request = new OrderStatusRequest();
        String specialStatus = "STATUS_WITH_SPECIAL_CHARS_!@#$%^&*()";

        request.setStatus(specialStatus);

        assertThat(request.getStatus()).isEqualTo(specialStatus);
    }

    @Test
    void allRequests_ShouldHandleMinLongValues() {
        OrderConfirmationRequest orderRequest = new OrderConfirmationRequest();
        orderRequest.setOrderId(Long.MIN_VALUE);
        orderRequest.setUserId(Long.MIN_VALUE);

        PaymentConfirmationRequest paymentRequest = new PaymentConfirmationRequest();
        paymentRequest.setPaymentId(Long.MIN_VALUE);
        paymentRequest.setUserId(Long.MIN_VALUE);
        paymentRequest.setOrderId(Long.MIN_VALUE);

        RefundConfirmationRequest refundRequest = new RefundConfirmationRequest();
        refundRequest.setPaymentId(Long.MIN_VALUE);
        refundRequest.setUserId(Long.MIN_VALUE);
        refundRequest.setOrderId(Long.MIN_VALUE);

        assertThat(orderRequest.getOrderId()).isEqualTo(Long.MIN_VALUE);
        assertThat(orderRequest.getUserId()).isEqualTo(Long.MIN_VALUE);

        assertThat(paymentRequest.getPaymentId()).isEqualTo(Long.MIN_VALUE);
        assertThat(paymentRequest.getUserId()).isEqualTo(Long.MIN_VALUE);
        assertThat(paymentRequest.getOrderId()).isEqualTo(Long.MIN_VALUE);

        assertThat(refundRequest.getPaymentId()).isEqualTo(Long.MIN_VALUE);
        assertThat(refundRequest.getUserId()).isEqualTo(Long.MIN_VALUE);
        assertThat(refundRequest.getOrderId()).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    void allRequestTypes_ShouldBeCreatedSuccessfully() {
        // Test that all request types can be instantiated
        OrderConfirmationRequest orderConfirmation = new OrderConfirmationRequest();
        OrderStatusRequest orderStatus = new OrderStatusRequest();
        OrderCancellationRequest orderCancellation = new OrderCancellationRequest();
        PaymentConfirmationRequest paymentConfirmation = new PaymentConfirmationRequest();
        PaymentFailureRequest paymentFailure = new PaymentFailureRequest();
        RefundConfirmationRequest refundConfirmation = new RefundConfirmationRequest();

        assertThat(orderConfirmation).isNotNull();
        assertThat(orderStatus).isNotNull();
        assertThat(orderCancellation).isNotNull();
        assertThat(paymentConfirmation).isNotNull();
        assertThat(paymentFailure).isNotNull();
        assertThat(refundConfirmation).isNotNull();
    }
}