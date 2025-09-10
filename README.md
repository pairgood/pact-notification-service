# Notification Service

> **🟣 This service is highlighted in the architecture diagram below**

Email and SMS notification service for the e-commerce microservices ecosystem.

## Service Role: Consumer Only
This service consumes events from Order and Payment services to send notifications but does not produce data for other services.

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐
│   User Service  │    │ Product Service │
│   (Port 8081)   │    │   (Port 8082)   │
│                 │    │                 │
│ • Authentication│    │ • Product Catalog│
│ • User Profiles │    │ • Inventory Mgmt│
│ • JWT Tokens    │    │ • Pricing       │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          │ validates users      │ fetches products
          │                      │
          ▼                      ▼
    ┌─────────────────────────────────┐
    │        Order Service            │
    │        (Port 8083)              │
    │                                 │
    │ • Order Management              │
    │ • Order Processing              │
    │ • Consumes User & Product APIs  │
    └─────────────┬───────────────────┘
                  │
                  │ triggers payment
                  │
                  ▼
    ┌─────────────────────────────────┐
    │       Payment Service           │
    │       (Port 8084)               │
    │                                 │
    │ • Payment Processing            │
    │ • Gateway Integration           │
    │ • Refund Management             │
    └─────────────┬───────────────────┘
                  │
                  │ sends notifications
                  │
                  ▼
    ┌─────────────────────────────────┐
    │🟣  Notification Service         │
    │       (Port 8085)               │
    │                                 │
    │ • Email Notifications           │
    │ • SMS Notifications             │
    │ • Order & Payment Updates       │
    └─────────────────────────────────┘
                  │ All services send telemetry data
                  │
                  ▼
    ┌─────────────────────────────────┐
    │📊  Telemetry Service            │
    │       (Port 8086)               │
    │                                 │
    │ • Distributed Tracing           │
    │ • Service Metrics               │
    │ • Request Tracking              │
    │ • Performance Monitoring        │
    └─────────────────────────────────┘
```

## Features

- **Order Notifications**: Order confirmations, status updates, and cancellations
- **Payment Notifications**: Payment confirmations, failures, and refund notifications
- **Email Service**: Simulated email sending with realistic delays and failure rates
- **SMS Service**: Simulated SMS sending capabilities
- **Notification History**: Complete notification tracking and status
- **Multi-Channel**: Support for both email and SMS notifications
- **Failure Handling**: Graceful handling of notification delivery failures

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Email**: Spring Boot Starter Mail
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA
- **Java Version**: 17

## API Endpoints

### Order Notifications
- `POST /api/notifications/order-confirmation` - Send order confirmation
- `POST /api/notifications/order-status` - Send order status update
- `POST /api/notifications/order-cancellation` - Send order cancellation notice

### Payment Notifications
- `POST /api/notifications/payment-confirmation` - Send payment confirmation
- `POST /api/notifications/payment-failure` - Send payment failure notice
- `POST /api/notifications/refund-confirmation` - Send refund confirmation

### Notification Management
- `GET /api/notifications/{id}` - Get notification by ID
- `GET /api/notifications/user/{userId}` - Get notifications for user
- `GET /api/notifications` - Get all notifications

## Telemetry Integration

The Notification Service sends telemetry data to the Telemetry Service for monitoring notification delivery and communication performance:

### Telemetry Features
- **Request Tracing**: Tracing of notification processing workflows from receipt to delivery
- **Service Metrics**: Notification delivery metrics including success rates and processing times
- **Error Tracking**: Automatic detection and reporting of notification delivery failures
- **Channel Monitoring**: Separate tracking for email and SMS delivery channels
- **Business Metrics**: Notification-specific metrics like delivery rates by type and channel

### Traced Operations
- Notification creation and processing
- Email service integration and delivery
- SMS service integration and delivery
- Notification status updates and retry mechanisms
- Database operations (notification persistence and history)
- Service health checks and delivery channel validation

### Telemetry Configuration
The service is configured to send telemetry data to the Telemetry Service:
```yaml
telemetry:
  service:
    url: http://localhost:8086
    enabled: true
  tracing:
    sample-rate: 1.0
  metrics:
    enabled: true
    export-interval: 30s
  notifications:
    track-email-delivery: true
    track-sms-delivery: true
    track-failure-rates: true
```

## Running the Service

### Prerequisites
- Java 17+
- Gradle (or use included Gradle wrapper)

### Start the Service
```bash
./gradlew bootRun
```

The service will start on **port 8085**.

### Database Access
- **H2 Console**: http://localhost:8085/h2-console
- **JDBC URL**: `jdbc:h2:mem:notificationdb`
- **Username**: `sa`
- **Password**: (empty)

## Service Dependencies

### Services That Use This Service
- **Order Service**: Sends order-related notifications
- **Payment Service**: Sends payment-related notifications

### External Dependencies
- Email service providers (simulated)
- SMS service providers (simulated)

## Data Models

### Notification Entity
```json
{
  "id": 1,
  "userId": 1,
  "type": "ORDER_CONFIRMATION",
  "subject": "Order Confirmation - Order #1",
  "message": "Thank you for your order! Your order #1 has been confirmed...",
  "status": "SENT",
  "createdAt": "2024-01-15T10:30:00",
  "sentAt": "2024-01-15T10:30:15",
  "recipientEmail": "user@example.com",
  "recipientPhone": "+1234567890"
}
```

### Notification Types
- `ORDER_CONFIRMATION` - Order placed successfully
- `ORDER_STATUS_UPDATE` - Order status changed
- `ORDER_CANCELLATION` - Order cancelled
- `PAYMENT_CONFIRMATION` - Payment processed successfully
- `PAYMENT_FAILURE` - Payment processing failed
- `REFUND_CONFIRMATION` - Refund processed
- `ACCOUNT_WELCOME` - New account created
- `PASSWORD_RESET` - Password reset request

### Notification Status
- `PENDING` - Notification created, not yet sent
- `SENT` - Notification delivered successfully
- `FAILED` - Notification delivery failed
- `RETRY` - Notification marked for retry

## Notification Simulation

The service simulates realistic notification behavior:

### Email Service
- **Processing Delay**: 500ms simulation
- **Failure Rate**: 5% random failures
- **Console Output**: Detailed email sending logs

### SMS Service  
- **Processing Delay**: 300ms simulation
- **Failure Rate**: 3% random failures
- **Console Output**: SMS delivery confirmation

## Example Usage

### Send Order Confirmation
```bash
curl -X POST http://localhost:8085/api/notifications/order-confirmation \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "userId": 1
  }'
```

### Send Payment Confirmation
```bash
curl -X POST http://localhost:8085/api/notifications/payment-confirmation \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": 1,
    "userId": 1,
    "orderId": 1
  }'
```

### Get User Notifications
```bash
curl -X GET http://localhost:8085/api/notifications/user/1
```

### Send Order Status Update
```bash
curl -X POST http://localhost:8085/api/notifications/order-status \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "userId": 1,
    "status": "SHIPPED"
  }'
```

## Console Output Examples

When notifications are sent, you'll see console output like:

```
📧 Email sent successfully:
  To: User ID 1
  Subject: Order Confirmation - Order #1
  Type: ORDER_CONFIRMATION
  Message: Thank you for your order! Your order #1 has been...

📱 SMS sent successfully:
  To: +1234567890
  Message: Your order #1 has been shipped and will arrive soon!
```

## Integration with Other Services

### Order Service Integration
The Order Service automatically sends notifications for:
- New order confirmations
- Order status updates (shipped, delivered, etc.)
- Order cancellations

### Payment Service Integration
The Payment Service automatically sends notifications for:
- Successful payment confirmations
- Payment processing failures
- Refund confirmations

## Error Handling

- **Service Unavailability**: Orders and payments continue processing even if notifications fail
- **Delivery Failures**: Failed notifications are marked for potential retry
- **Network Issues**: Graceful degradation when notification channels are unavailable

## Production Considerations

In a production environment, this service would integrate with:
- **Email Providers**: SendGrid, Amazon SES, Mailgun
- **SMS Providers**: Twilio, Amazon SNS, Nexmo
- **Message Queues**: For reliable notification delivery
- **Templates**: Email and SMS template management
- **Personalization**: User-specific notification preferences

## Pact Contract Testing

This service uses [Pact](https://pact.io/) for consumer contract testing to ensure reliable communication with external services.

### Consumer Role

This service acts as a consumer for the following external services:
- **Telemetry Service**: Sends tracing, metrics, and event data for monitoring

### Running Pact Tests

#### Consumer Tests
```bash
# Run consumer tests and generate contracts
./gradlew pactTest

# Generated contracts will be in build/pacts/
```

#### Publishing Contracts
```bash
# Publish contracts to Pactflow
./gradlew pactPublish
```

### Contract Testing Approach

This implementation follows Pact's **"Be conservative in what you send"** principle:

- Consumer tests define minimal request structures with only required fields
- Request bodies cannot contain fields not defined in the contract
- Tests validate that actual API calls match contract expectations exactly
- Mock servers reject requests with unexpected extra fields

### Contract Files

Consumer contracts are generated in:
- `build/pacts/` - Local contract files  
- Pactflow - Centralized contract storage and management

### Telemetry Service Contracts

The notification service has contract coverage for all telemetry interactions:

1. **Start Trace Events** - When notification processing begins
2. **Finish Trace Events** - When notification processing completes  
3. **Service Call Recording** - When external services are called
4. **Log Events** - Application logging and debugging

### Troubleshooting

#### Common Issues

1. **Consumer Test Failures**
   - **Extra fields in request**: Remove any fields from request body that aren't actually needed
   - **Mock server expectation mismatch**: Verify HTTP method, path, headers, and body structure
   - **Content-Type headers**: Ensure request headers match exactly what the service sends
   - **URL path parameters**: Check that path parameters are correctly formatted in the contract

2. **Contract Generation Issues**
   - **Missing @Pact annotation**: Ensure each contract method has proper annotations
   - **Invalid JSON structure**: Verify LambdaDsl body definitions match actual data structures
   - **Provider state setup**: Ensure provider state descriptions are descriptive and specific

3. **Pactflow Integration Issues**
   - **Authentication**: Verify `PACT_BROKER_TOKEN` environment variable is set
   - **Base URL**: Confirm `PACT_BROKER_BASE_URL` points to `https://pairgood.pactflow.io`
   - **Network connectivity**: Check firewall/proxy settings if publishing fails

#### Debug Commands

```bash
# Run with debug output
./gradlew pactTest --info --debug

# Run specific test class
./gradlew pactTest --tests="*TelemetryServicePactTest*"

# Generate contracts without publishing
./gradlew pactTest -x pactPublish

# Clean and regenerate contracts
./gradlew clean pactTest
```

#### Debug Logging

Add to `application-test.properties` for detailed Pact logging:
```properties
logging.level.au.com.dius.pact=DEBUG
logging.level.org.apache.http=DEBUG
```

### Contract Evolution

When external services change their APIs:

1. **New Fields in Responses**: No action needed - consumers ignore extra fields
2. **Removed Response Fields**: Update consumer tests if those fields were being used
3. **New Required Request Fields**: Update consumer tests and service code
4. **Changed Endpoints**: Update consumer contract paths and service client code

### Integration with CI/CD

Consumer contract tests run automatically on:
- **Pull Requests**: Generate and validate contracts
- **Main Branch**: Publish contracts to Pactflow for provider verification
- **Feature Branches**: Generate contracts for validation (not published)

### Manual Testing

For local development against real services:
```bash
# Test against local services (disable Pact)
./gradlew test -Dpact.verifier.disabled=true

# Test against staging services
export EXTERNAL_SERVICE_URL=https://staging.example.com
./gradlew test -Dpact.verifier.disabled=true
```

### Contract Documentation

Generated contracts document:
- **API interactions**: What endpoints this service calls
- **Request formats**: Exact structure of requests sent
- **Response expectations**: What fields this service relies on
- **Error handling**: How this service handles different response scenarios

### Timestamp Format

Note: Timestamps in telemetry events are serialized as arrays of numbers (e.g., `[2025,9,10,13,9,0,123456789]`) rather than ISO strings, due to LocalDateTime serialization behavior.

## Related Services

- **[Order Service](../order-service/README.md)**: Sends order notifications
- **[Payment Service](../payment-service/README.md)**: Sends payment notifications
- **[User Service](../user-service/README.md)**: Independent service
- **[Product Service](../product-service/README.md)**: Independent service
- **[Telemetry Service](../telemetry-service/README.md)**: Collects telemetry data from this service