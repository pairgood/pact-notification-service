package com.ecommerce.notificationservice.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.ecommerce.notificationservice.telemetry.TelemetryClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.au.com.dius.pact=DEBUG"
})
class TelemetryServicePactTest {

    @Pact(consumer = "notification-service", provider = "telemetry-service")
    public V4Pact startTracePact(PactDslWithProvider builder) {
        return builder
            .given("telemetry service is running")
            .uponReceiving("a start trace event")
            .path("/api/telemetry/events")
            .method("POST")
            .headers(Map.of(
                "Content-Type", "application/json"
            ))
            .body(LambdaDsl.newJsonBody((body) -> body
                .stringType("traceId")
                .stringType("spanId")
                .stringType("serviceName", "notification-service")
                .stringType("operation")
                .stringType("eventType", "SPAN")
                .array("timestamp", timestamp -> timestamp.numberType(2025).numberType(9).numberType(10).numberType(13).numberType(9).numberType(0).numberType(123456789))
                .stringType("status", "SUCCESS")
                .stringType("httpMethod")
                .stringType("httpUrl")
                .stringType("userId")
            ).build())
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "startTracePact")
    void testStartTrace(MockServer mockServer) {
        // Arrange: Set up client with mock server URL
        TelemetryClient client = new TelemetryClient();
        ReflectionTestUtils.setField(client, "telemetryServiceUrl", mockServer.getUrl());
        ReflectionTestUtils.setField(client, "serviceName", "notification-service");
        
        // Act: Make the API call to start trace
        String traceId = client.startTrace("send_notification", "POST", "/api/notifications/order-confirmation", "user123");
        
        // Assert: Verify trace was started successfully
        assertThat(traceId).isNotNull();
        assertThat(traceId).startsWith("trace_");
    }

    @Pact(consumer = "notification-service", provider = "telemetry-service")
    public V4Pact finishTracePact(PactDslWithProvider builder) {
        return builder
            .given("telemetry service is running")
            .uponReceiving("a finish trace event")
            .path("/api/telemetry/events")
            .method("POST")
            .headers(Map.of(
                "Content-Type", "application/json"
            ))
            .body(LambdaDsl.newJsonBody((body) -> body
                .stringType("traceId")
                .stringType("spanId")
                .stringType("serviceName", "notification-service")
                .stringType("operation")
                .stringType("eventType", "SPAN")
                .array("timestamp", timestamp -> timestamp.numberType(2025).numberType(9).numberType(10).numberType(13).numberType(9).numberType(0).numberType(123456789))
                .numberType("durationMs")
                .stringType("status", "SUCCESS")
                .numberType("httpStatusCode")
                .stringType("errorMessage")
            ).build())
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "finishTracePact")
    void testFinishTrace(MockServer mockServer) {
        // Arrange: Set up client with mock server URL and trace context
        TelemetryClient client = new TelemetryClient();
        ReflectionTestUtils.setField(client, "telemetryServiceUrl", mockServer.getUrl());
        ReflectionTestUtils.setField(client, "serviceName", "notification-service");
        
        // Set up trace context
        TelemetryClient.TraceContext.setTraceId("trace_123");
        TelemetryClient.TraceContext.setSpanId("span_456");
        TelemetryClient.TraceContext.setStartTime(System.currentTimeMillis() - 1000);
        
        // Act: Make the API call to finish trace
        client.finishTrace("send_notification", 200, null);
        
        // Assert: Verify trace context was cleared
        assertThat(TelemetryClient.TraceContext.getTraceId()).isNull();
        assertThat(TelemetryClient.TraceContext.getSpanId()).isNull();
        assertThat(TelemetryClient.TraceContext.getStartTime()).isNull();
    }

    @Pact(consumer = "notification-service", provider = "telemetry-service")
    public V4Pact recordServiceCallPact(PactDslWithProvider builder) {
        return builder
            .given("telemetry service is running")
            .uponReceiving("a service call event")
            .path("/api/telemetry/events")
            .method("POST")
            .headers(Map.of(
                "Content-Type", "application/json"
            ))
            .body(LambdaDsl.newJsonBody((body) -> body
                .stringType("traceId")
                .stringType("spanId")
                .stringType("parentSpanId")
                .stringType("serviceName", "notification-service")
                .stringType("operation")
                .stringType("eventType", "SPAN")
                .array("timestamp", timestamp -> timestamp.numberType(2025).numberType(9).numberType(10).numberType(13).numberType(9).numberType(0).numberType(123456789))
                .numberType("durationMs")
                .stringType("status", "SUCCESS")
                .stringType("httpMethod")
                .stringType("httpUrl")
                .numberType("httpStatusCode")
                .stringType("metadata")
            ).build())
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "recordServiceCallPact")
    void testRecordServiceCall(MockServer mockServer) {
        // Arrange: Set up client with mock server URL and trace context
        TelemetryClient client = new TelemetryClient();
        ReflectionTestUtils.setField(client, "telemetryServiceUrl", mockServer.getUrl());
        ReflectionTestUtils.setField(client, "serviceName", "notification-service");
        
        // Set up trace context
        TelemetryClient.TraceContext.setTraceId("trace_123");
        TelemetryClient.TraceContext.setSpanId("span_456");
        
        // Act: Record a service call
        client.recordServiceCall("email-service", "send_email", "POST", "/send", 150, 200);
        
        // Assert: Verify trace context is maintained
        assertThat(TelemetryClient.TraceContext.getTraceId()).isEqualTo("trace_123");
        assertThat(TelemetryClient.TraceContext.getSpanId()).isEqualTo("span_456");
    }

    @Pact(consumer = "notification-service", provider = "telemetry-service")
    public V4Pact logEventPact(PactDslWithProvider builder) {
        return builder
            .given("telemetry service is running")
            .uponReceiving("a log event")
            .path("/api/telemetry/events")
            .method("POST")
            .headers(Map.of(
                "Content-Type", "application/json"
            ))
            .body(LambdaDsl.newJsonBody((body) -> body
                .stringType("traceId")
                .stringType("spanId")
                .stringType("serviceName", "notification-service")
                .stringType("operation")
                .stringType("eventType", "LOG")
                .array("timestamp", timestamp -> timestamp.numberType(2025).numberType(9).numberType(10).numberType(13).numberType(9).numberType(0).numberType(123456789))
                .stringType("status", "SUCCESS")
                .stringType("metadata")
            ).build())
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "logEventPact")
    void testLogEvent(MockServer mockServer) {
        // Arrange: Set up client with mock server URL and trace context
        TelemetryClient client = new TelemetryClient();
        ReflectionTestUtils.setField(client, "telemetryServiceUrl", mockServer.getUrl());
        ReflectionTestUtils.setField(client, "serviceName", "notification-service");
        
        // Set up trace context
        TelemetryClient.TraceContext.setTraceId("trace_123");
        TelemetryClient.TraceContext.setSpanId("span_456");
        
        // Act: Log an event
        client.logEvent("Notification sent successfully", "INFO");
        
        // Assert: Verify trace context is maintained
        assertThat(TelemetryClient.TraceContext.getTraceId()).isEqualTo("trace_123");
        assertThat(TelemetryClient.TraceContext.getSpanId()).isEqualTo("span_456");
    }

    @Pact(consumer = "notification-service", provider = "telemetry-service")
    public V4Pact errorScenarioPact(PactDslWithProvider builder) {
        return builder
            .given("telemetry service is running")
            .uponReceiving("a finish trace event with error")
            .path("/api/telemetry/events")
            .method("POST")
            .headers(Map.of(
                "Content-Type", "application/json"
            ))
            .body(LambdaDsl.newJsonBody((body) -> body
                .stringType("traceId")
                .stringType("spanId")
                .stringType("serviceName", "notification-service")
                .stringType("operation")
                .stringType("eventType", "SPAN")
                .array("timestamp", timestamp -> timestamp.numberType(2025).numberType(9).numberType(10).numberType(13).numberType(9).numberType(0).numberType(123456789))
                .numberType("durationMs")
                .stringType("status", "ERROR")
                .numberType("httpStatusCode")
                .stringType("errorMessage")
            ).build())
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "errorScenarioPact")
    void testFinishTraceWithError(MockServer mockServer) {
        // Arrange: Set up client with mock server URL and trace context
        TelemetryClient client = new TelemetryClient();
        ReflectionTestUtils.setField(client, "telemetryServiceUrl", mockServer.getUrl());
        ReflectionTestUtils.setField(client, "serviceName", "notification-service");
        
        // Set up trace context
        TelemetryClient.TraceContext.setTraceId("trace_123");
        TelemetryClient.TraceContext.setSpanId("span_456");
        TelemetryClient.TraceContext.setStartTime(System.currentTimeMillis() - 500);
        
        // Act: Finish trace with error
        client.finishTrace("send_notification", 500, "Email service unavailable");
        
        // Assert: Verify trace context was cleared
        assertThat(TelemetryClient.TraceContext.getTraceId()).isNull();
        assertThat(TelemetryClient.TraceContext.getSpanId()).isNull();
        assertThat(TelemetryClient.TraceContext.getStartTime()).isNull();
    }
}